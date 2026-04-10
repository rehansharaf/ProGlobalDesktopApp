/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.swing.SwingWorker;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import org.apache.commons.net.io.Util;

public class DownloadWorker extends SwingWorker<Object, Object> {

    private String host;
    private String user;
    private String pass;
    private File inputFile;
    private String folder;
    private String filename;
    private String localdirPath;

    public DownloadWorker(String host, String user, String pass, File filename, String category, File localDir) {
        this.host = host;
        this.user = user;
        this.pass = pass;
        localdirPath = localDir.getAbsolutePath();

        inputFile = filename;
        folder = category;
    }

    @Override
    protected Object doInBackground() throws Exception {

        FTPClient ftp = null;
        InputStream input = null;
        FileOutputStream ftpOut = null;

        try {
            ftp = FtpConnectionManager.createConnection(host, user, pass);

            System.out.println(inputFile.getName());

            ftp.sendCommand("SIZE", "/" + folder + "/" + inputFile.getName());
            String reply = ftp.getReplyString();
            reply = reply.substring(4);
            reply = reply.trim();
            long size = Long.parseLong(reply);

            input = ftp.retrieveFileStream(folder + "/" + inputFile.getName());

            if (input == null) {
                throw new Exception("Unable to open FTP input stream for file: " + inputFile.getName());
            }

            ftpOut = new FileOutputStream(localdirPath + "/" + inputFile.getName());

            CopyStreamListener listener = new CopyStreamListener() {
                @Override
                public void bytesTransferred(final long totalBytesTransferred, final int bytesTransferred, final long streamSize) {
                    setProgress((int) Math.round(((double) totalBytesTransferred / (double) streamSize) * 100d));
                }

                @Override
                public void bytesTransferred(CopyStreamEvent event) {
                }
            };

            if (size == 0) {
                setProgress(100);
            } else {
                Util.copyStream(input, ftpOut, ftp.getBufferSize(), size, listener);
            }

            ftpOut.flush();
            return null;

        } finally {
            try {
                if (ftpOut != null) {
                    ftpOut.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (input != null) {
                    input.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            FtpConnectionManager.safeDisconnect(ftp);
        }
    }
}