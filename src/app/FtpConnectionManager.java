package app;

import java.io.IOException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class FtpConnectionManager {

    public static FTPClient createConnection(String host, String user, String pass) throws IOException {
        FTPClient ftp = new FTPClient();

        ftp.setBufferSize(1024000);
        ftp.setControlEncoding("UTF-8");
        ftp.setConnectTimeout(3000);
        ftp.setDataTimeout(3000);
        ftp.setControlKeepAliveTimeout(3000);

        ftp.connect(host, 21);

        int reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            safeDisconnect(ftp);
            throw new IOException("FTP server refused connection. Reply code: " + reply);
        }

        boolean loggedIn = ftp.login(user, pass);
        if (!loggedIn) {
            safeDisconnect(ftp);
            throw new IOException("FTP login failed for user: " + user);
        }

        ftp.setFileType(FTP.BINARY_FILE_TYPE);

        if ("10.0.0.91".equals(host)) {
            ftp.enterLocalPassiveMode();
        }

        ftp.setKeepAlive(true);
        ftp.changeWorkingDirectory("/");

        return ftp;
    }

    public static void safeDisconnect(FTPClient ftp) {
        if (ftp != null && ftp.isConnected()) {
            try {
                ftp.logout();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                ftp.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}