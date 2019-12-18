/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.ftpServer;

import java.io.File;

/**
 *
 * @author maidoanh
 */
public class FileAssert {

    public static String printDirectoryTree(File folder) {
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("folder is not a Directory");
        }
        StringBuilder sb = new StringBuilder();
        printDirectoryTree(folder, "", sb);
        return sb.toString();
    }

    private static void printDirectoryTree(File folder, String parentName,
            StringBuilder sb) {
        if (!folder.isDirectory()) {
            throw new IllegalArgumentException("folder is not a Directory");
        }
        sb.append(parentName+" ");     
//        sb.append("+--");
        sb.append(folder.getName());
//        sb.append("/");
        sb.append("\n");
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                printDirectoryTree(file, folder.getName(), sb);
            } else {
                printFile(file, folder.getName(), sb);
            }
        }

    }

    private static void printFile(File file, String parentName, StringBuilder sb) {
        sb.append(parentName+" ");
//        sb.append("+--");
        sb.append(file.getName());
        sb.append(" "+file.length());
        sb.append("\n");
    }

    private static String getIndentString(int indent) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append("|  ");
        }
        return sb.toString();
    }
}
