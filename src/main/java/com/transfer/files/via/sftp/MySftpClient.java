package com.transfer.files.via.sftp;

import com.jcraft.jsch.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
                    case SftpCommands.GET:
                        getFile(commandParts[1], commandParts[2]);
                        break;
                    case SftpCommands.PWD:
                        where();
                        break;
                    case SftpCommands.LPWD:
                        lWhere();
                        break;
                    case SftpCommands.RM:
                        remove(commandParts[1]);
                        break;
                    case SftpCommands.RMDIR:
                        removeDir(commandParts[1]);
                        break;
                    case SftpCommands.MKDIR:
                        create(commandParts[1]);
                        break;
                    case SftpCommands.LMKDIR:
                        lCreate(commandParts[1]);
                        break;
                    case SftpCommands.RENAME:
                        rename(commandParts[1], commandParts[2]);
                        break;
                    case SftpCommands.CHMOD:
                        chmod(commandParts[1], commandParts[2]);
                        break;
                    case SftpCommands.HELP:
                    case "?":
                        help();
                        break;
                    case SftpCommands.EXIT:
                    case SftpCommands.BYE:
                    case SftpCommands.QUIT:
                        if (jschSession != null) {
                            channelSftp.exit();
                            jschSession.disconnect();
                        }
                        return;
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

    private static void getFile(String from, String to) {
        try {
            channelSftp.get(from, to);
        } catch (SftpException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void where() {
        try {
            System.out.println(channelSftp.pwd());
        } catch (SftpException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void lWhere() {
        System.out.println(channelSftp.lpwd());
    }

    private static void remove(String name) {
        try {
            channelSftp.rm(name);
            System.out.println("The file : " + name + " has been removed successfully !");
        } catch (SftpException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void removeDir(String name) {
        try {
            channelSftp.rmdir(name);
            System.out.println("The directory : " + name + " has been removed successfully !");
        } catch (SftpException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void create(String path) {
        try {
            channelSftp.mkdir(path);
            System.out.println("The directory : " + path + " has been created successfully !");
        } catch (SftpException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void lCreate(String path) {
        try {
            Path thePath = Paths.get(path);
            Files.createDirectories(thePath);
            System.out.println("The directory : " + path + " has been created successfully !");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void rename(String from, String to) {
        try {
            channelSftp.rename(from, to);
        } catch (SftpException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void chmod(String mode, String path) {
        try {
            channelSftp.chmod(Integer.parseInt(mode), path);
        } catch (SftpException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void help() {
        System.out.println("Available commands :");
        System.out.println(
                "bye                                Quit sftp\n" +
                        SftpCommands.CD +
                        " path                          Change remote directory to 'path'\n" +
                        SftpCommands.CHMOD +
                        " mode path                    Change permissions of file 'path' to 'mode'\n" +
                        SftpCommands.EXIT +
                        "                               Quit sftp\n" +
                        SftpCommands.GET +
                        " remote [local]            Download file\n" +
                        SftpCommands.HELP +
                        "                               Display this help text\n" +
                        SftpCommands.LCD +
                        " path                         Change local directory to 'path'\n" +
                        SftpCommands.LLS +
                        " [ls-options [path]]          Display local directory listing\n" +
                        SftpCommands.LMKDIR +
                        " path                       Create local directory\n" +
                        SftpCommands.LS +
                        " [path]                        Display remote directory listing\n" +
                        SftpCommands.MKDIR +
                        " path                        Create remote directory\n" +
                        SftpCommands.PUT +
                        " local [remote]              Upload file\n" +
                        SftpCommands.QUIT +
                        "                               Quit sftp\n" +
                        SftpCommands.RENAME +
                        " oldpath newpath             Rename remote file\n" +
                        SftpCommands.RM +
                        " path                        Delete remote file\n" +
                        SftpCommands.RMDIR +
                        " path                     Delete remote directory\n" +
                        "?                              Synonym for help\n"
        );
    }
}
