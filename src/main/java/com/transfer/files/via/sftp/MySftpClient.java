package com.transfer.files.via.sftp;

import com.jcraft.jsch.*;

import java.io.File;
import java.util.Scanner;
import java.util.Vector;

public class MySftpClient {
    private static Session jschSession = null;
    private static ChannelSftp channelSftp = null;

    public static void start(String host, int port, String username, String password) {
        Scanner scanner = new Scanner(System.in);
        boolean isConnected = connect(host, port, username, password);
        if (isConnected) {
            System.out.println("The SFTP client has been connected successfully !");
            System.out.println("Please enter your command (use help or ? to list all the possible commands) :");
            while (true) {
                System.out.print("➜");
                String command = scanner.nextLine();
                String[] commandParts = command.split(" ");
                switch (commandParts[0]) {
                    case SftpCommands.CD:
                        goTo(commandParts[1]);
                        break;
                    case SftpCommands.LCD:
                        lGoTo(commandParts[1]);
                        break;
                    case SftpCommands.LS:
                        if (commandParts.length > 1) listFiles(commandParts[1]);
                        else listFiles(".");
                        break;
                    case SftpCommands.LLS:
                        if (commandParts.length > 1) listLocalFiles(commandParts[1]);
                        else listLocalFiles(channelSftp.lpwd());
                        break;
                    case SftpCommands.PUT:
                        putFile(commandParts[1], commandParts[2]);
                        break;
                    default:
                        System.out.println("Invalid command !");
                        break;
                }
            }
        }

    }

    private static boolean connect(String host, int port, String username, String password) {
        try {
            JSch jsch = new JSch();
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            jschSession = jsch.getSession(username, host, port);
            jschSession.setPassword(password);
            jschSession.setConfig(config);
            jschSession.connect(Settings.SESSION_TIMEOUT);
            Channel sftp = jschSession.openChannel("sftp");
            sftp.connect(Settings.CHANNEL_TIMEOUT);
            channelSftp = (ChannelSftp) sftp;
        } catch (JSchException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    private static void goTo(String path) {
        try {
            channelSftp.cd(path);
        } catch (SftpException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void lGoTo(String path) {
        try {
            channelSftp.lcd(path);
        } catch (SftpException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void listFiles(String path) {
        try {
            channelSftp.cd(path);
            Vector list = channelSftp.ls(path);
            for (int i = 0; i < list.size(); i++) {
                ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) list.get(i);
                if (!(entry.getFilename().equals(".") || entry.getFilename().equals(".."))) {
                    String type = "FILE";
                    if (entry.getAttrs().isDir()) {
                        type = "DIRECTORY";
                    }
                    System.out.println(entry.getFilename() + " : " + type);
                }
            }
        } catch (SftpException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void listLocalFiles(String path) {
        System.out.println(path);
        File file = new File(path);
        String[] names = file.list();
        if (names != null) {
            for (String name : names) {
                System.out.println(name);
            }
        }
    }

    private static void putFile(String from, String to) {
        try {
            channelSftp.put(from, to);
        } catch (SftpException e) {
            System.out.println(e.getMessage());
        }
    }
}
