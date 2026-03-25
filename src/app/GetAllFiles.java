package app;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class GetAllFiles {
	
	
	
	/*public FTPFile[] getAllFilesFromFolder(String directory, String hostName, String username, String password) throws IOException {
			
			FTPClient ftp = new FTPClient();
			ftp = new FTPClient();

			ftp.setBufferSize(1024000);
	        int reply;
	        ftp.setControlEncoding("UTF-8");
	        try {
				ftp.connect(hostName, 21);
			}catch(java.net.ConnectException ce) {
				ErrorFrame errFrame = new ErrorFrame("Error: Establishing Connection with Server");
	        	errFrame.setVisible(true);
			}

	        reply = ftp.getReplyCode();
	        if (FTPReply.isPositiveCompletion(reply)) {
	            System.out.println("Connected Success");
	        
	        } else if (!FTPReply.isPositiveCompletion(reply)) {
	            ftp.disconnect();
	            try {
					throw new Exception("Exception in connecting to FTP Server");
				} catch (Exception e) {
					e.printStackTrace();
				}
	        }
	        
	        ftp.login(username, password);
	        ftp.setFileType(FTP.BINARY_FILE_TYPE);
	        
	        if(hostName.equals("10.0.0.91"))
	        	ftp.enterLocalPassiveMode();
	        
	        ftp.setKeepAlive(true);
	        ftp.setControlKeepAliveTimeout(3000);
	        ftp.setDataTimeout(3000); // 100 minutes
	        ftp.setConnectTimeout(3000); // 100 minutes
	   
	        
	        ftp.changeWorkingDirectory("/");
	        
	        boolean verificationFilename = false;
			FTPFile[] files = ftp.listFiles(directory);
			
			Arrays.sort(files,
				    Comparator.comparing((FTPFile remoteFile) -> remoteFile.getTimestamp())
				        .reversed());
			
			return files;

			
	        
	}*/
	
	public FTPFile[] getAllFilesFromFolder(String directory, String hostName, String username, String password) throws IOException {

        FTPClient ftp = new FTPClient();
        ftp.setBufferSize(1024000);
        ftp.setControlEncoding("UTF-8");
        ftp.setConnectTimeout(3000);
        ftp.setDataTimeout(3000);
        ftp.setControlKeepAliveTimeout(3000);

        try {
            ftp.connect(hostName, 21);

            int reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                throw new IOException("FTP server refused connection. Reply code: " + reply);
            }

            boolean loggedIn = ftp.login(username, password);
            if (!loggedIn) {
                throw new IOException("FTP login failed for user: " + username);
            }

            ftp.setFileType(FTP.BINARY_FILE_TYPE);

            // safer to always use passive mode unless you specifically need active mode
            ftp.enterLocalPassiveMode();

            ftp.changeWorkingDirectory("/");

            FTPFile[] files = ftp.listFiles(directory);

            if (files == null) {
                throw new IOException("FTP listFiles returned null for directory: " + directory);
            }

            // remove null entries if any
            files = Arrays.stream(files)
                    .filter(f -> f != null && f.getName() != null)
                    .toArray(FTPFile[]::new);

            // sort safely even if timestamp is null
            Arrays.sort(files, Comparator.comparing(
                    (FTPFile remoteFile) -> remoteFile.getTimestamp(),
                    Comparator.nullsLast(Comparator.naturalOrder())
            ).reversed());

            return files;

        } catch (java.net.ConnectException ce) {
            ErrorFrame errFrame = new ErrorFrame("Error: Establishing Connection with Server");
            errFrame.setVisible(true);
            throw new IOException("Unable to connect to FTP server: " + hostName, ce);
        } finally {
            if (ftp.isConnected()) {
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

}
