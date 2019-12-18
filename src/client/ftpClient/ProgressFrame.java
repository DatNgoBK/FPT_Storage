/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.ftpClient;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author admin
 */
public class ProgressFrame extends JFrame{

    JPanel pnContent, pnTitle, pnContainer;
    JLabel lblTitle;
    
    public ProgressFrame() {
        this.setLayout(null);
        
        lblTitle = new JLabel("Detail download/upload");
        lblTitle.setForeground(Color.white);
        lblTitle.setBounds(20, 30, 400, 60);
        lblTitle.setFont(new Font("Candara", 0, 30));
        
        pnContainer = new JPanel();
        pnContainer.setLayout(null);
        pnContainer.setBounds(0, 120, 900, 480);
        pnContainer.setBackground(Color.white);
        
        
        pnTitle = new JPanel();
        pnTitle.setLayout(null);
        pnTitle.setBounds(0, 0, 900, 120);
        pnTitle.setBackground(new Color(39, 174, 96));
        
        pnContent = new JPanel(new GridLayout(5, 1));        
        pnContent.setBounds(50, 50, 800, 400);
        pnContent.setBackground(Color.white);
        
        pnContainer.add(pnContent);
        pnTitle.add(lblTitle);
        this.add(pnContainer);
        this.add(pnTitle);
    }
    
    public void showWindow() {
        setSize(900, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
