package server.ftpServer;

import server.ftpServer.ServerThread;
import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import static java.lang.Thread.sleep;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

public class Server extends Thread {

    ServerSocket server = null; //listen 
    Socket sk = null;
    BufferedReader br = null;
    PrintWriter pw = null;
    int port, k;
    final ArrayList<ServerThread> listUser = new ArrayList<ServerThread>();
    ServerGui serverGui = new ServerGui();

    public Server(int p, int k) //p: ten cong
    {
        serverGui.showWindows();
        port = p;
        this.k = k;
        try {
            server = new ServerSocket(port);  //tao server socket voi so hieu cong
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void updateGui(){
        DefaultListModel demoList = new DefaultListModel();
            for (int i = 0; i < listUser.size(); i++) {
                demoList.addElement(listUser.get(i).username);
            }
            serverGui.jList1.setModel(demoList);
            serverGui.jPanel1.updateUI();
    }

    public void run() {
        while (true) {
            System.out.println("Listenning...");
            System.out.println("number of client is " + Math.max((listUser.size() - 1), 0));
            updateGui();
            try {
                sk = server.accept();
                if (sk != null && listUser.size() - 1 < k) {
                    ServerThread th = new ServerThread(this, sk);
                    th.start();
                } else {
                    PrintWriter out = new PrintWriter(new OutputStreamWriter(sk.getOutputStream()), true);
                    out.println("full");
                }
                sleep(1500);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        String p = JOptionPane.showInputDialog(null, "Type Port Number");
        String k = JOptionPane.showInputDialog(null, "Maximum of Client");
        new Server(Integer.parseInt(p), Integer.parseInt(k)).start();
    }

}
