package core;

import controller.RoomController;
import controller.UserController;
import flag.ResultFlags;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client extends Observable {

    String serverName;
    int port = 11000;
    Socket socket;
    private BufferedWriter bufferWriter;
    private DataInputStream dataInputStream;
    public UserController userController;
    public RoomController roomController;
    private Thread thread;
    public String nickname;
    private Observer obs;

    public Client(Observer obs, String ipAddress) throws UnknownHostException {
        this.addObserver(obs);
        this.obs = obs;
        this.serverName = ipAddress;
        System.out.println(InetAddress.getLocalHost().getHostAddress());
    }

    public Client(Socket socket, Observer obs) {
        this.addObserver(obs);
        this.obs = obs;
        this.socket = socket;
    }

    public void dispose() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (thread != null) {
            thread.stop();
        }
    }

    public boolean startConnect() {
        try {
            socket = new Socket(serverName, port);
            dataInputStream = new DataInputStream(socket.getInputStream());
            bufferWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
            userController = new UserController(bufferWriter, this.obs);
            roomController = new RoomController(bufferWriter, this.obs);
            startThreadWaitResult();
            return true;
        } catch (IOException ex) {
            Result result = new Result(ex.toString(), ResultFlags.ERROR, "Không thể kết nối đến server");
            notifyObservers(result);
            return false;
        }
    }

    void startThreadWaitResult() {
        thread = new Thread(() -> {
            try {
                while (true) {
                    read();
                }
            } catch (IOException ex) {
                Result result = new Result(ex.toString(), ResultFlags.ERROR, "Kết nối tới server có lỗi");
                notifyObservers(result);
            }
        });
        thread.start();
    }

    private void read() throws IOException {
        String[] lines = dataInputStream.readUTF().split(";", -1);
        Result result;
        if (lines.length == 3) {
            result = new Result(lines[0], lines[1], lines[2]);
        } else {
            String content = "";
            for (int i = 2; i < lines.length; i++) {
                content += lines[i] + ";";
            }
            result = new Result(lines[0], lines[1], content);
        }
        notifyObservers(result);
    }

    @Override
    public void notifyObservers(Object arg) {
        super.setChanged();
        super.notifyObservers(arg);
    }

}
