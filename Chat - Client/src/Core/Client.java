
package Core;
import Interface.LoginForm;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Client extends Observable{
    
    String serverName;
    int port = 11000;
    Socket socket;
    BufferedWriter bufferWriter;
    DataInputStream dataInputStream;
    Thread thread;
    public String nickname;
    public Client(Observer obs, String ipAddress) throws UnknownHostException  
    {
        this.addObserver(obs);
        this.serverName = ipAddress;
        System.out.println(InetAddress.getLocalHost().getHostAddress());
    }
    public Client(Socket socket, Observer obs)   
    {
        this.addObserver(obs);
        socket = socket;
    }

    public Client(LoginForm aThis, double d) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    public void dispose()
    {
        if(socket!=null)
        {
            try 
            {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if(thread!=null)
            thread.stop();
    }
    public boolean startConnect()
    {
        try 
        {
            socket = new Socket(serverName, port);
            dataInputStream = new DataInputStream(socket.getInputStream());
            bufferWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
            startThreadWaitResult();
            return true;
        } catch (IOException ex) 
        {
            Result result = new Result("", ResultFlags.ERROR, "Không thể kết nối đến server");
            notifyObservers(result);
            return false;
        }
    }
    
    void startThreadWaitResult()
    {
        thread = new Thread(new Runnable() 
        {
            @Override
            public void run() 
            {
                try 
                {
                    while(true)
                    {
                        String[] lines = dataInputStream.readUTF().split(";", -1);  
                        Result result;
                        if(lines.length==3) 
                        {
                            result = new Result(lines[0], lines[1], lines[2]);
                        }else  
                        {
                            String content = "";
                            for (int i = 2; i < lines.length; i++) 
                            {
                                content += lines[i] + ";";
                            }
                            result = new Result(lines[0], lines[1], content);
                        }
                        notifyObservers(result);  
                    }
                }catch (IOException ex) {
                    Result result = new Result("", ResultFlags.ERROR, "Kết nối tới server có lỗi");
                    notifyObservers(result);
                }
            }
        });
        thread.start();
    }
    
    @Override
    public void notifyObservers(Object arg) {
        super.setChanged();
        super.notifyObservers(arg);
    }
 
    public void sendMess(String mess)
    {
        mess = mess.replaceAll("\\n", "<br>");
        String line = ActionFlags.SEND_MESSAGE + ";" + mess;
        try 
        {
            bufferWriter.write(line + "\n");
            bufferWriter.flush();
        } catch (IOException ex) {
            Result result = new Result("", ResultFlags.ERROR, "Không thể kết nối tới server");
            notifyObservers(result);
        }
    }
    public void login(String nickName) throws UnsupportedEncodingException  
    {
        String line = ActionFlags.LOGIN + ";" + nickName;
        try
        {
            bufferWriter.write(line + "\n");
            bufferWriter.flush();
        } catch (IOException ex) {
            Result result = new Result("", ResultFlags.ERROR, "Không thể kết nối tới server");
            notifyObservers(result);
        }
    }
    
    public void getListRoom()
    {
        String line = ActionFlags.GET_LIST_ROOM + ";";
        try
        {
            bufferWriter.write(line + "\n");
            bufferWriter.flush();
        } catch (IOException ex) {
            Result result = new Result("", ResultFlags.ERROR, "Kết nối tới server có lỗi");
            notifyObservers(result);
        }
    }
    
    public void createRoom(String roomName)
    {
        String line = ActionFlags.CREATE_ROOM + ";" + roomName;
        try
        {
            bufferWriter.write(line + "\n");
            bufferWriter.flush();
        } catch (IOException ex) {
            Result result = new Result("", ResultFlags.ERROR, "Kết nối tới server có lỗi");
            notifyObservers(result);
        }
    }
    
    public void joinRoom(String maPhong)
    {
        String line = ActionFlags.JOIN_ROOM + ";" + maPhong;
        try
        {
            bufferWriter.write(line + "\n");
            bufferWriter.flush();
        } catch (IOException ex) {
            Result result = new Result("", ResultFlags.ERROR, "Kết nối tới server có lỗi");
            notifyObservers(result);
        }
    }
    public void leaveRoom()
    {
        String line = ActionFlags.LEAVE_ROOM + ";null";
        try
        {
            bufferWriter.write(line + "\n");
            bufferWriter.flush();
        } catch (IOException ex) {
            Result result = new Result("", ResultFlags.ERROR, "Kết nối tới server có lỗi");
            notifyObservers(result);
        }
    }
    public void logout()
    {
        String line = ActionFlags.LOGOUT + ";null";
        try
        {
            bufferWriter.write(line + "\n");
            bufferWriter.flush();
        } catch (IOException ex) {
            Result result = new Result("", ResultFlags.ERROR, "Kết nối tới server có lỗi");
            notifyObservers(result);
        }
    }
    
    
}
