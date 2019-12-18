/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.ftpClient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author maidoanh
 */
public class UploadThread extends Thread implements Runnable{
    private BufferedOutputStream output; 
    private BufferedInputStream input; 
    private ClientFrame clientFrame;
    private ProgressPanel pn;

    public UploadThread(BufferedOutputStream output, BufferedInputStream input, ClientFrame clientFrame, ProgressPanel pn) {
        this.output = output;
        this.input = input;
        this.clientFrame = clientFrame;
        this.pn = pn;
    }

    @Override
    public void run(){
        try {
            byte[] buffer = new byte[4096];
            int bytesRead = 0;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
                if (bytesRead == 4096) {
                        pn.prg.setValue(pn.prg.getValue() + 4096);
                        pn.updateTxt();
//                    pn.updateUI();
                    }
            }
                        
            output.flush();
            output.close();
            input.close();
            
//            response = reader.readLine();
//            System.out.println(response);
            
            clientFrame.listItems();
            JOptionPane.showMessageDialog(null, "Upload Success");
            
            pn.txt.setText(pn.txt.getText().split(" ")[0] +" uploaded successfully");
            pn.prg.setValue(pn.prg.getMaximum());
            pn.btnOK.setEnabled(false);
            
        } catch (IOException ex) {
            Logger.getLogger(UploadThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException ex) {
            
        } catch (Exception ex) {
            Logger.getLogger(UploadThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
