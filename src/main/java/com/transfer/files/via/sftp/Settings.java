package com.transfer.files.via.sftp;

import java.io.File;

public class Settings {
    public static final int SESSION_TIMEOUT = 10000;
    public static final int CHANNEL_TIMEOUT = 10000;

    public static String SftpTestServerHost = "127.0.0.1";
    public static int SftpTestServerPort = 2222;
    public static String SftpTestServerUsername = "admin";
    public static String SftpTestServerPassword = "password";
    public static String SftpTestServerFolder= new File("").getAbsolutePath()+"/SftpFolder";

}
