package core;

import controller.HomeController;
import controller.UserController;
import entity.Response;
import flag.ActionFlags;
import flag.ResultFlags;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    public UserController userController;
    public HomeController homeController;
    private Thread thread;

    public Client(Observer obs, String ipAddress) throws UnknownHostException {
        this.addObserver(obs);
        this.serverName = ipAddress;
        System.out.println(InetAddress.getLocalHost().getHostAddress());
    }

    public Client(Socket socket, Observer obs) {
        this.addObserver(obs);
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
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            userController = new UserController(objectOutputStream);
            homeController = new HomeController(objectOutputStream);
            startThreadWaitResult();
            return true;
        } catch (IOException ex) {
            Response response = new Response(ActionFlags.ERROR, ResultFlags.ERROR ,"Không thể kết nối tới server" , ex);
            notifyObservers(response);
            return false;
        }
    }

    void startThreadWaitResult() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        read();
                    }
                } catch (IOException ex) {
                    Response response = new Response(ActionFlags.ERROR, ResultFlags.ERROR ,"Kết nối tới server có lỗi" , ex);
                    notifyObservers(response);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        thread.start();
    }

    private void read() throws IOException, ClassNotFoundException {
        Response response = (Response) objectInputStream.readObject();
        notifyObservers(response);
    }

    @Override
    public void notifyObservers(Object arg) {
        super.setChanged();
        super.notifyObservers(arg);
    }

}
