/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.ftpClient;

import java.awt.Color;
import java.awt.Font;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static java.awt.image.ImageObserver.WIDTH;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

/**
 *
 * @author admin
 */
public class ProgressPanel extends JPanel{
    
    
    JProgressBar prg;
    JTextField txt;
    String nameFile;
    JButton btnOK;
    JPanel pn;
    /**
     * Creates new form ProcessingView
     */
    public ProgressPanel(int max, String nameFile) {
        
        pn = this;
        
        this.setLayout(null);
        this.setSize(700, 100);
        this.nameFile = nameFile;
        
        JLabel lblIcon = new JLabel(new ImageIcon(getClass().getResource("/client/ftpClient/icon/icons8-sphere-20.png")));
        lblIcon.setBounds(0, 5, 20, 20);
        this.add(lblIcon);
        
        txt = new JTextField(nameFile + " is downloading...");
        txt.setBounds(30, 0, 480, 30);
        txt.setBorder(null);
        txt.setBackground(Color.white);
        txt.setFont(new Font("Calibri", 0, 16));
        txt.setEditable(false);
        this.add(txt);
        
        prg = new JProgressBar();
        prg.setBounds(530, 2, 150, 25);
        prg.setValue(0);
        prg.setForeground(new Color(0, 102, 204));
        prg.setBackground(new Color(231, 255, 255));
        prg.setMaximum(max);
        
        btnOK = new JButton("Pauses");
        btnOK.setBounds(700, 0, 100, 30);
        
        this.add(btnOK);
        this.add(prg);
        this.setBackground(Color.white);
    }
    
    public void updateTxt() {            
        txt.setText(nameFile + " is downloading: " + prg.getValue()/1024 + "kB/" +prg.getMaximum()/1024 + "kB");
    }
}
