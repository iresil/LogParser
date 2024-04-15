package logParser.util;

import lombok.NoArgsConstructor;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Component
@NoArgsConstructor
public class LogLoader {
    @Value("${logGetter.in.ftp.server}")
    private String server;

    @Value("${logGetter.in.ftp.port}")
    private Integer port;

    @Value("${logGetter.in.ftp.user}")
    private String user;

    @Value("${logGetter.in.ftp.pass}")
    private String pass;

    @Value("${logGetter.in.ftp.path}")
    private String remotePath;

    @Value("${logGetter.out.local.path}")
    private String localPath;

    public byte[] loadLogs() {
        byte[] bytes = null;

        FTPClient ftpClient = new FTPClient();
        try {

            ftpClient.connect(server, port);

            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

            File localFile = new File(localPath);
            InputStream inputStream;
            if(!localFile.exists()) {
                inputStream = ftpClient.retrieveFileStream(remotePath);
                inputStream.close();

                Files.copy(inputStream, localFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            inputStream = new FileInputStream(localPath);

            bytes = inputStream.readAllBytes();

            inputStream.close();

        } catch (IOException ex) {
            System.out.println("LogLoader Error: " + ex.getMessage());
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
