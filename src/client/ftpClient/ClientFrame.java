/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.ftpClient;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author maidoanh
 */
public class ClientFrame extends javax.swing.JFrame {

    /**
     * Creates new form ClientFrame
     */
    public Socket controlSocket, dataSocket;
    public boolean Passive = false;
    public BufferedReader reader;
    public BufferedWriter writer;
    public PrintWriter out;
    public FileInputStream is;
    public BufferedInputStream upload;
    public BufferedReader br;
    public String host;
    public int port = 21;
    public String username;
    public String password;
    public SocketAddress remoteAddr;
    public ProgressFrame progFrame;

    DefaultMutableTreeNode selectedNode;

    public ClientFrame(String hostname, int p) {
        initComponents();
        controlSocket = new Socket();
        remoteAddr = new InetSocketAddress(hostname, p);
        host = hostname;
        br = new BufferedReader(new InputStreamReader(System.in));
        this.Menu("1", "1");
        this.setLocationRelativeTo(null);
        progFrame = new ProgressFrame();
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exit();
            }

            private void exit() {

                try {
                    oneshot("EXIT");
                    reader.close();
                    controlSocket.close();
                    dataSocket.close();
                } catch (IOException ex) {
                    Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
        );
    }

    public void Menu(String user, String pass) {
        try {
            setUsername(user);
            setPassword(pass);
            controlSocket = new Socket(); //tao socket moi
            controlSocket.connect(remoteAddr, 1500); //ket noi socket toi dia chi socket
            reader = new BufferedReader(new InputStreamReader(
                    controlSocket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(controlSocket.getOutputStream()),
                    true);
            login();
            listItems();

//            DefaultMutableTreeNode root = new DefaultMutableTreeNode("a");
//            DefaultTreeModel dtm = new DefaultTreeModel(root);
//
//            treeFolder.setModel(dtm);
//        reader = new BufferedReader(new InputStreamReader(
//                controlSocket.getInputStream()));
//        out = new PrintWriter(new OutputStreamWriter(controlSocket.getOutputStream()),
//                true);
//            System.out.println("1. List all\t*");
//            System.out.println("2. Upload File\t*");
//            System.out.println("3. Download \t*");
//            System.out.println("4. Delete File\t*");
//            System.out.println("5. Make Dir\t*");
//            System.out.println("6. cwd\t*");
//            System.out.println("7. Remane File\t*");
//
//            System.out.println("********************");
//            System.out.print("Enter Choice :");
//            int choice;
//            choice = Integer.parseInt(br.readLine());
//
//            if (choice == 1) {
//                list();
//            } else if (choice == 2) {
//                uploadFile();
//            } else if (choice == 3) {
//                download();
//            } else if (choice == 4) {
//                delete();
//            } else if (choice == 5) {
//                makeDir();
//            } else if (choice == 6) {
//                cwd();
//            } else if (choice == 7) {
//                rename();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public BufferedReader getReader(Socket s) throws Exception {
        return new BufferedReader(new InputStreamReader(
                s.getInputStream()));
    }

    public ArrayList<String> ReadAll(BufferedReader br) throws Exception {
        ArrayList<String> directory = new ArrayList<>();
        String msg;
        do {
            msg = br.readLine();
            if (msg != null) {
                System.out.println(msg);
                if (!msg.equals("")) {
                    directory.add(msg);
                }
            } else {
                break;
            }
        } while (true);
        return directory;
    }

    public String readUntil(String st) throws Exception {
        String msg;
        do {
            msg = reader.readLine();
            if (msg != null) {
                if (msg.equals("full")) {
                    JOptionPane.showMessageDialog(null, "Maxmimum!!");
                    System.exit(0);
                } 
                else if(msg.equals("updateFolder"))
                {
                    listItems();
                    jPanel1.updateUI();
                }
                else {
                    System.out.println(msg);
                }
            } else {
                break;
            }
        } while (!msg.startsWith(st));
        return msg;
    }

    public void safeRead() throws Exception {
        String msg;
        try {
            msg = reader.readLine();
            if (msg != null) {
                System.out.println(msg);
            }
        } catch (java.net.SocketTimeoutException e) {
            System.out.println(e.toString());
        }
    }

    public boolean oneshot(String cmd) throws Exception {
        out.println(cmd);
        String msg;
        msg = reader.readLine();
        System.out.println(msg);
        if (msg.startsWith("5")) {
            return false;
        }
        return true;
    }

    public void uploadFile(String path) throws Exception {
        File f = new File(path);
        if (!f.exists()) {
            System.out.println("File not Exists...");
            return;
        }
        String serverPath = "";
        if (selectedNode != null) {
            TreeNode[] parentNodes = selectedNode.getPath();
            for (int i = 1; i < parentNodes.length - 1; i++) {
                serverPath += "\\" + ((MyTreeNode) ((DefaultMutableTreeNode) parentNodes[i]).getUserObject()).getName();
            }
            if (!((MyTreeNode) ((DefaultMutableTreeNode) parentNodes[parentNodes.length - 1]).getUserObject()).getName().contains(".")) {
                serverPath += "\\" + ((MyTreeNode) ((DefaultMutableTreeNode) parentNodes[parentNodes.length - 1]).getUserObject()).getName();
            }
        }
        
        //****************************
                String size = f.length() + "";
                ProgressPanel pn = new ProgressPanel(Integer.valueOf(size), f.getName());
                pn.setBounds(0, 400, 600, 100);
                this.add(pn);
                progFrame.pnContent.add(pn);
                
        is = new FileInputStream(f);
        BufferedInputStream input = new BufferedInputStream(is);
        dataSocket = passive();
        oneshot("TYPE I");
        boolean state = oneshot("STOR " + serverPath + "\\" + f.getName());
        if (!state) {
            JOptionPane.showMessageDialog(null, "File Existed!");
        } else {
            BufferedOutputStream output = new BufferedOutputStream(
                    dataSocket.getOutputStream());
            UploadThread uploadThread = new UploadThread(output, input, this, pn);
            uploadThread.start();
        }

    }

    public void delete(String filename) throws Exception {
        oneshot("DELE " + filename);
    }

    public void makeDir(String dirname) throws Exception {
        oneshot("MKD " + dirname);
    }

    public void rename() throws Exception {
        String oldName;
        String newName;
        System.out.print("Enter Old Name :");
        oldName = br.readLine();
        System.out.print("Enter New Name :");
        newName = br.readLine();
        out.println("RNFR " + oldName);
        String msg = reader.readLine();
        System.out.println(msg);
        out.println("RNTO " + newName);
        msg = reader.readLine();
        System.out.println(msg);
    }

    public void cwd() throws Exception {
        String path;
        System.out.print("Enter Path:");
        path = br.readLine();
        oneshot("CWD " + path);
    }

    public void welcome() throws Exception {
        readUntil("220 ");
    }

    public void login() throws Exception {
        out.println("USER " + username);
        String response;
        response = readUntil("331 ");
        //System.out.println(response);
        if (!response.startsWith("331")) {
            throw new IOException(
                    "SimpleFTP received an unknown response after sending the user: "
                    + response);
        }

        out.println("PASS " + password);

        response = readUntil("230 ");
        //System.out.println(response);
        if (!response.startsWith("230")) {//230 login success
            throw new IOException(
                    "SimpleFTP was unable to log in with the supplied password: "
                    + response);
        }
    }

    public Socket passive() throws Exception {

        String response;
        out.println("PASV");
        response = readUntil("227 ");
        if (!response.startsWith("227")) {
            throw new IOException("FTPClient could not request passive mode: "
                    + response);
        }
        String ip = null;
        int port = 0;
        int left = response.indexOf('(');
        int right = response.indexOf(')', left + 1);
        if (right > 0) {

            String sub = response.substring(left + 1, right);
            StringTokenizer tokenizer = new StringTokenizer(sub, ",");
            try {
                ip = tokenizer.nextToken() + "." + tokenizer.nextToken() + "."
                        + tokenizer.nextToken() + "." + tokenizer.nextToken();
                port = Integer.parseInt(tokenizer.nextToken()) * 256
                        + Integer.parseInt(tokenizer.nextToken());
            } catch (Exception e) {
                throw new IOException(
                        "Format error: "
                        + response);
            }
        }
        return new Socket(ip, port);
    }

    public void listItems() throws Exception {
        dataSocket = passive();
        oneshot("LIST -a -1");
        ArrayList<String> directory = ReadAll(getReader(dataSocket));
        dataSocket.close();
        ArrayList<DefaultMutableTreeNode> nodes = new ArrayList<>();

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new MyTreeNode(directory.get(0).split(" ")[1]));
        directory.remove(0);
        nodes.add(root);

        for (String s : directory) {
            String item[] = s.split(" ");
            for (int i = nodes.size() - 1; i >= 0; i--) {
                if (((MyTreeNode) nodes.get(i).getUserObject()).getName().equals(item[0])) {
                    DefaultMutableTreeNode newNode;
                    if (item.length == 2) {
                        MyTreeNode treeNode = new MyTreeNode(item[1]);
                        newNode = new DefaultMutableTreeNode(treeNode);
                    } else {
                        //*************************************
                        MyTreeNode treeNode = new MyTreeNode(item[1]);
                        treeNode.setSize(Long.parseLong(item[2]));
                        newNode = new DefaultMutableTreeNode(treeNode);
                    }
                    nodes.get(i).add(newNode);
                    nodes.add(newNode);
                    break;
                }
            }
        }

        DefaultTreeModel dtm = new DefaultTreeModel(root);
        treeFolder.setModel(dtm);
        treeFolder.setRootVisible(false);
        treeFolder.setEditable(true);
        treeFolder.setCellRenderer(new FolderTreeCellRenderer());
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menu = new javax.swing.JPopupMenu();
        menuItem = new javax.swing.JMenuItem();
        menuItem2 = new javax.swing.JMenuItem();
        menuItem3 = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        treeFolder = new javax.swing.JTree();
        jPanelProgress = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jPanelUpload = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblTurnBack = new javax.swing.JLabel();

        menuItem.setText("Download");
        menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemActionPerformed(evt);
            }
        });
        menu.add(menuItem);

        menuItem2.setText("Delete");
        menuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem2ActionPerformed(evt);
            }
        });
        menu.add(menuItem2);

        menuItem3.setText("New Folder");
        menuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItem3ActionPerformed(evt);
            }
        });
        menu.add(menuItem3);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(900, 600));
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(900, 450));

        treeFolder.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                treeFolderMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(treeFolder);

        jPanelProgress.setBackground(new java.awt.Color(41, 128, 185));
        jPanelProgress.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanelProgress.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanelProgressMouseClicked(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Progress");

        javax.swing.GroupLayout jPanelProgressLayout = new javax.swing.GroupLayout(jPanelProgress);
        jPanelProgress.setLayout(jPanelProgressLayout);
        jPanelProgressLayout.setHorizontalGroup(
            jPanelProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelProgressLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel8)
                .addContainerGap(33, Short.MAX_VALUE))
        );
        jPanelProgressLayout.setVerticalGroup(
            jPanelProgressLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
        );

        jPanelUpload.setBackground(new java.awt.Color(41, 128, 185));
        jPanelUpload.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jPanelUpload.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanelUploadMouseClicked(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Calibri", 0, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Upload");

        javax.swing.GroupLayout jPanelUploadLayout = new javax.swing.GroupLayout(jPanelUpload);
        jPanelUpload.setLayout(jPanelUploadLayout);
        jPanelUploadLayout.setHorizontalGroup(
            jPanelUploadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelUploadLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jLabel9)
                .addContainerGap(35, Short.MAX_VALUE))
        );
        jPanelUploadLayout.setVerticalGroup(
            jPanelUploadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(906, 906, 906)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(262, 262, 262)
                        .addComponent(jPanelProgress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(94, 94, 94)
                        .addComponent(jPanelUpload, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 830, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanelProgress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanelUpload, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(56, 56, 56))
        );

        jPanel2.setBackground(new java.awt.Color(41, 128, 185));
        jPanel2.setForeground(new java.awt.Color(255, 255, 255));
        jPanel2.setPreferredSize(new java.awt.Dimension(900, 100));

        jLabel1.setFont(new java.awt.Font("Maiandra GD", 0, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("File Library");

        lblTurnBack.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        lblTurnBack.setIcon(new javax.swing.ImageIcon(getClass().getResource("/client/ftpClient/icon/icons8-left-32.png"))); // NOI18N
        lblTurnBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblTurnBackMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 579, Short.MAX_VALUE)
                .addComponent(lblTurnBack)
                .addGap(54, 54, 54))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(53, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(lblTurnBack)
                        .addGap(51, 51, 51))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 438, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //Download
    private void menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemActionPerformed
        try {
            // TODO add your handling code here:
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

                String serverPath = "";
                TreeNode[] parentNodes = selectedNode.getPath();

                serverPath = ((MyTreeNode) ((DefaultMutableTreeNode) parentNodes[1]).getUserObject()).getName();
                for (int i = 2; i < parentNodes.length - 1; i++) {
                    serverPath += "\\" + ((MyTreeNode) ((DefaultMutableTreeNode) parentNodes[i]).getUserObject()).getName();
                }
                serverPath += "\\" + ((MyTreeNode) ((DefaultMutableTreeNode) parentNodes[parentNodes.length - 1]).getUserObject()).getName();

                //****************************
                String size = ((MyTreeNode) selectedNode.getUserObject()).getSize() + "";
                ProgressPanel pn = new ProgressPanel(Integer.valueOf(size), ((MyTreeNode) selectedNode.getUserObject()).getName());
                pn.setBounds(0, 400, 600, 100);
                this.add(pn);
                progFrame.pnContent.add(pn);

                dataSocket = passive();
                oneshot("TYPE I");
                if (!oneshot("RETR " + serverPath)) {
                    return;
                }

//                 String size = selectedNode.getUserObject().toString().split(" ")[1];
//                size = size.substring(0, size.length() - 2).split("\\(")[1];
//                String size = 286*1024 ;677389
//                

//*******************************
                BufferedOutputStream output = new BufferedOutputStream(
                        new FileOutputStream(new File(fileChooser.getSelectedFile(),
                                ((MyTreeNode) ((DefaultMutableTreeNode) parentNodes[parentNodes.length - 1]).getUserObject()).getName())));
                BufferedInputStream input = new BufferedInputStream(
                        dataSocket.getInputStream());
                DownloadThread dl = new DownloadThread(output, input, pn);
                dl.start();
                this.repaint();

            }

//            response = reader.readLine();
//            System.out.println(response);
        } catch (Exception ex) {
            Logger.getLogger(ClientFrame.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_menuItemActionPerformed

    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        treeFolder.clearSelection();
        selectedNode = null;
    }//GEN-LAST:event_formMouseClicked

    private void menuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem2ActionPerformed
        try {

            String serverPath = "";
            TreeNode[] parentNodes = selectedNode.getPath();
            serverPath = ((MyTreeNode) ((DefaultMutableTreeNode) parentNodes[1]).getUserObject()).getName();
            for (int i = 2; i < parentNodes.length; i++) {
                if (i != parentNodes.length - 1) {
                    serverPath += "\\" + ((MyTreeNode) ((DefaultMutableTreeNode) parentNodes[i]).getUserObject()).getName();
                } else if (!((MyTreeNode) ((DefaultMutableTreeNode) parentNodes[i]).getUserObject()).getName().contains(".")) {
                    serverPath += "\\" + ((MyTreeNode) ((DefaultMutableTreeNode) parentNodes[i]).getUserObject()).getName();
                }
            }
            delete(serverPath);
            listItems();
            JOptionPane.showMessageDialog(null, "Delete Success");

        } catch (Exception ex) {
            Logger.getLogger(ClientFrame.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_menuItem2ActionPerformed

    private void treeFolderMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeFolderMouseClicked
        if (SwingUtilities.isRightMouseButton(evt)) {
            TreePath treePath = treeFolder.getClosestPathForLocation(evt.getX(), evt.getY());
            selectedNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
            menu.show(this, evt.getX(), evt.getY() + 100);
            treeFolder.setSelectionPath(treePath);
        } else {
            TreePath treePath = treeFolder.getClosestPathForLocation(evt.getX(), evt.getY());
            selectedNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
            selectedNode = (DefaultMutableTreeNode) treeFolder.getLastSelectedPathComponent();
            treeFolder.setSelectionPath(treePath);
        }
    }//GEN-LAST:event_treeFolderMouseClicked

    private void menuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItem3ActionPerformed
        try {
            String name = JOptionPane.showInputDialog("Enter Folder Name");
            Object[] treePath = selectedNode.getUserObjectPath();
            String path = "";
            for (int i = 1; i < treePath.length - 1; i++) {
                path += ((MyTreeNode) treePath[i]).getName() + "/";
            }
            if (!((MyTreeNode) treePath[treePath.length - 1]).getName().contains(".")) {
                path += ((MyTreeNode) treePath[treePath.length - 1]).getName() + "/";
            }
            path += name;
            makeDir(path);
            JOptionPane.showMessageDialog(null, "Success");
            listItems();
            treeFolder.updateUI();

        } catch (Exception ex) {
            Logger.getLogger(ClientFrame.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_menuItem3ActionPerformed

    private void jPanelProgressMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanelProgressMouseClicked
        progFrame.showWindow();
    }//GEN-LAST:event_jPanelProgressMouseClicked

    private void jPanelUploadMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanelUploadMouseClicked
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            try {
                uploadFile(chooser.getSelectedFile().getAbsolutePath());

            } catch (Exception ex) {
                Logger.getLogger(ClientFrame.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jPanelUploadMouseClicked

    private void lblTurnBackMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTurnBackMouseClicked
        this.dispose();
        (new MainFrame()).setVisible(true);
    }//GEN-LAST:event_lblTurnBackMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ClientFrame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ClientFrame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ClientFrame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ClientFrame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ClientFrame("1", 1).setVisible(true);
            }
        });
    }

    public JTree getTreeFolder() {
        return treeFolder;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelProgress;
    private javax.swing.JPanel jPanelUpload;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblTurnBack;
    private javax.swing.JPopupMenu menu;
    private javax.swing.JMenuItem menuItem;
    private javax.swing.JMenuItem menuItem2;
    private javax.swing.JMenuItem menuItem3;
    private javax.swing.JTree treeFolder;
    // End of variables declaration//GEN-END:variables
}
