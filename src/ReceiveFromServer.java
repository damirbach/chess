
import java.io.*;
import java.net.*;

public class ReceiveFromServer extends Thread {  // WaitingRoom for answer from server

    public Socket s = new Socket();
    private String modifiedSentence;

    public ReceiveFromServer(Socket s1) {
        this.s = s1;
        this.modifiedSentence = "";
    }

    @Override
    public void run() {
        try {
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(s.getInputStream()));
            while (!modifiedSentence.equals("end") && Signin.signed_in) {
                modifiedSentence = inFromServer.readLine();
                switch (modifiedSentence.substring(0, 1)) {
                    case "@":
                        Info.chatWindow(modifiedSentence);
                        break;
                    case "$":
                        WaitingRoom.refreshDisplay1(modifiedSentence.substring(1));
                        break;
                    case "!":
                        WaitingRoom.checkIfConnected(modifiedSentence.substring(1));
                        break;
                    case "%":
                        WaitingRoom.player = modifiedSentence.substring(1);
                        break;
                    case "|":
                        Info.winLose.setText(modifiedSentence.substring(1));
                        break;
                    default:
                        Board.waitForAnswer(modifiedSentence);
                        break;
                }
            }
            this.interrupt();
        }
        catch (Exception e) {
            System.out.println("recieve server: " + e);
        }
    }
}
