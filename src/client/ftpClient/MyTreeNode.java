/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.ftpClient;

import java.util.Arrays;
import javax.swing.ImageIcon;

/**
 *
 * @author maidoanh
 */
public class MyTreeNode {

    private String name;
    private String icon;
    private long size;
    private String type;

    MyTreeNode(String name) {
        this.name = name;
        this.icon = "/client/ftpClient/icon/" + editIcon();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    private String editIcon() {
        String temp[] = name.split("\\.");
        if (temp.length == 1)
            return "icons8-folder-20.png";
        
        String typeFile = temp[temp.length - 1];
        switch(typeFile) {
            case "txt":
                return "icons8-document-20.png";
            case "mp3":
                return "icons8-audio-file-20.png";
            case "png":
            case "jpg":
                return "icons8-image-file-20.png";
            case "exe":
                return "icons8-exe-20.png";
            default:
                return "icons8-document-20.png";
        }
    }
    
    
}
