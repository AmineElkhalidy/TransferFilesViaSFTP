package com.transfer.files.via.sftp;

import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.AsyncAuthException;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.password.PasswordChangeRequiredException;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.scp.ScpCommandFactory;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.shell.ProcessShellFactory;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;

import java.nio.file.Paths;
import java.util.Collections;

public class MySftpServer {
    public static void start() {
        SshServer sshd = SshServer.setUpDefaultServer();
        sshd.setHost(Settings.SftpTestServerHost);
        sshd.setPort(Settings.SftpTestServerPort);
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(Paths.get("key.ser")));
        sshd.setCommandFactory(new ScpCommandFactory());
        sshd.setShellFactory(new ProcessShellFactory("/bin/sh", "-i", "-l"));
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
        sshd.setSubsystemFactories(Collections.singletonList(new SftpSubsystemFactory()));
        sshd.setPasswordAuthenticator(new PasswordAuthenticator() {
            @Override
            public boolean authenticate(String username, String password, ServerSession session) throws PasswordChangeRequiredException, AsyncAuthException {
                return username.equals(Settings.SftpTestServerUsername) && password.equals(Settings.SftpTestServerPassword);
            }
        });
        VirtualFileSystemFactory fsFactory = new VirtualFileSystemFactory();
        fsFactory.setUserHomeDir("admin", Paths.get(Settings.SftpTestServerFolder));
        sshd.setFileSystemFactory(fsFactory);
        try {
            sshd.start();
            System.out.println("The SFTP test server has been started successfully (Host : " + Settings.SftpTestServerHost + ", Port : " + Settings.SftpTestServerPort + ") !");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
