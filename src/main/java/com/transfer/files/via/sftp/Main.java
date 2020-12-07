package com.transfer.files.via.sftp;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Transfer Files Via SFTP");
        while (true) {
            System.out.println("Please enter :\n1 : To launch the SFTP client with a test server\n2 : To launch the SFTP client with a remote server\n0 : To quit");
            Scanner scanner = new Scanner(System.in);
            String selectedProtocol = scanner.nextLine();
            switch (selectedProtocol) {
                case "1":
                    MySftpServer.start();
                    MySftpClient.start(Settings.SftpTestServerHost, Settings.SftpTestServerPort, Settings.SftpTestServerUsername, Settings.SftpTestServerPassword);
                    break;
                case "2":
                    System.out.println("Please enter the host :");
                    String host = scanner.nextLine();
                    System.out.println("Please enter the port :");
                    int port = Integer.parseInt(scanner.nextLine());
                    System.out.println("Please enter the username :");
                    String username = scanner.nextLine();
                    System.out.println("Please enter the password :");
                    String password = scanner.nextLine();
                    MySftpClient.start(host, port, username, password);
                    break;
                case "0":
                    System.exit(0);
                default:
                    System.out.println("Wrong choice !");
                    break;
            }
        }
    }
}
