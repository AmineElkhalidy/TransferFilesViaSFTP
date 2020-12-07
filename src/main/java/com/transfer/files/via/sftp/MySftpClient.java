package com.transfer.files.via.sftp;

import com.jcraft.jsch.*;

import java.util.Scanner;

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

}
