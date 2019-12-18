/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.ftpClient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author maidoanh
 */
public class DownloadThread extends Thread implements Runnable {

    BufferedOutputStream output;
    BufferedInputStream input;
    ProgressPanel pn;
    private boolean threadSuspended;

    public DownloadThread(BufferedOutputStream bos, BufferedInputStream bis, ProgressPanel pn) {
        this.output = bos;
        this.input = bis;
        this.pn = pn;
        threadSuspended = false;
        addEvents();
    }

    @Override
    public void run() {
        try {
//            int num=pn.prg.getValue()/4096;

            byte[] buffer = new byte[4096];
            int bytesRead = 0;
            while ((bytesRead = input.read(buffer)) != -1) {
                if (!threadSuspended) {
                    output.write(buffer, 0, bytesRead);
                    if (bytesRead == 4096) {
                        pn.prg.setValue(pn.prg.getValue() + 4096);
                        pn.updateTxt();
//                    pn.updateUI();
                    }
                } else {
                    output.write(buffer, 0, bytesRead);
                    if (bytesRead == 4096) {
                        pn.prg.setValue(pn.prg.getValue() + 4096);
                        pn.updateTxt();
//                    pn.updateUI();
                    }
                    synchronized (this) {
                        this.wait();
                    }
                }

            }
//            pn.updateUI();

            output.flush();
            output.close();
            input.close();
            JOptionPane.showMessageDialog(null, "Success");
            
            pn.txt.setText(pn.txt.getText().split(" ")[0] +" downloaded successfully");
            pn.prg.setValue(pn.prg.getMaximum());
            pn.btnOK.setEnabled(false);
        } catch (IOException ex) {
            Logger.getLogger(DownloadThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(DownloadThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isThreadSuspended() {
        return threadSuspended;
    }

    public void setThreadSuspended(boolean threadSuspended) {
        this.threadSuspended = threadSuspended;
    }

    public void addEvents() {
        DownloadThread dl = this;
        pn.btnOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                threadSuspended = !threadSuspended;
                if(pn.btnOK.getText().equals("Pause"))
                    pn.btnOK.setText("Resume");
                else
                    pn.btnOK.setText("Pause");
                if (!threadSuspended) {
                    synchronized (dl) {
                        dl.notify();
                    }
                }
            }
        });
    }
}
