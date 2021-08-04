package com.example.LogParser;

import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;
import java.io.InputStream;

public class LogGetter {
    public byte[] GetLogs() {
        String server = "ita.ee.lbl.gov";
        int port = 21;
        String user = "anonymous";
        String pass = "";

        byte[] bytes = null;

        FTPClient ftpClient = new FTPClient();
        try {

            ftpClient.connect(server, port);

            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

            String remoteFile2 = "/traces/NASA_access_log_Aug95.gz";
            InputStream inputStream = ftpClient.retrieveFileStream(remoteFile2);
            bytes = inputStream.readAllBytes();

            inputStream.close();

        } catch (IOException ex) {
            System.out.println("LogGetter Error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return bytes;
    }
}
