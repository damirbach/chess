
import java.io.*;
import java.net.*;

class TCPClient {

    private Socket clientSocket;

    public void clientConnect() throws Exception { // connect client to server
        String User_IP = "18.224.153.248";
        int User_PORT = 12000;
        clientSocket = new Socket(User_IP, User_PORT);
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void resetSocket() {
        clientSocket = null;
    }

    public String getFrom() throws Exception {  // get from server

        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        return inFromServer.readLine();
    }

    public void sendTo(String msg) throws Exception {  // sent to server
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        outToServer.writeBytes(msg);
    }

    public void endConnection() throws Exception {  // end socket connection
        sendTo("end\n");
        clientSocket.close();
    }

}
