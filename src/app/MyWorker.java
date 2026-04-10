/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.swing.SwingWorker;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import org.apache.commons.net.io.Util;


public class MyWorker extends SwingWorker<Object, Object> {

    private String host;
    private String user;
    private String pass;
    private File outputFile;
    private String folder;
    private String filename;


    public MyWorker(String host, String user, String pass, File f, String category,String filename) {
        this.host = host;
        this.user = user;
        this.pass = pass;
        outputFile = f;
        folder = category;
        this.filename = filename;
    }
    
   /* @Override
    protected Object doInBackground() throws Exception {

        String location = outputFile.toString();
        FTPClient ftp = null;
        FileInputStream input = null;
        OutputStream ftpOut = null;
        OutputStream output = null;

        try {
            ftp = FtpConnectionManager.createConnection(host, user, pass);

            System.out.println(outputFile.getName().toString());

            File f1 = new File(location);
            input = new FileInputStream(f1);

            ftpOut = ftp.storeFileStream("/" + folder + "/" + filename);

            if (ftpOut == null) {
                throw new Exception("Unable to open FTP output stream for file: " + filename);
            }

            System.out.println(ftpOut.toString());

            output = new BufferedOutputStream(ftpOut);
            CopyStreamListener listener = new CopyStreamListener() {
                public void bytesTransferred(final long totalBytesTransferred, final int bytesTransferred, final long streamSize) {
                    setProgress((int) Math.round(((double) totalBytesTransferred / (double) streamSize) * 100d));
                }

                @Override
                public void bytesTransferred(CopyStreamEvent arg0) {
                }
            };

            Util.copyStream(input, output, ftp.getBufferSize(), outputFile.length(), listener);
            output.flush();

            return null;

        } catch (java.net.ConnectException ce) {
            ErrorFrame errFrame = new ErrorFrame("Error: Establishing Connection with Server");
            errFrame.setVisible(true);
            throw ce;
        } catch (Exception ee) {
            ErrorFrame errFrame = new ErrorFrame("Error: Establishing Connection with Server");
            errFrame.setVisible(true);
            throw ee;
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

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
    
    */
    
    
    @Override
    protected Object doInBackground() throws Exception {

        String location = outputFile.toString();
        FTPClient ftp = null;
        FileInputStream input = null;
        OutputStream ftpOut = null;
        OutputStream output = null;

        try {
            ftp = FtpConnectionManager.createConnection(host, user, pass);

            System.out.println(outputFile.getName());

            File f1 = new File(location);
            input = new FileInputStream(f1);

            ftpOut = ftp.storeFileStream("/" + folder + "/" + filename);

            if (ftpOut == null) {
                throw new Exception("Unable to open FTP output stream for file: " + filename);
            }

            System.out.println(ftpOut.toString());

            output = new BufferedOutputStream(ftpOut);
            CopyStreamListener listener = new CopyStreamListener() {
                @Override
                public void bytesTransferred(final long totalBytesTransferred, final int bytesTransferred, final long streamSize) {
                    setProgress((int) Math.round(((double) totalBytesTransferred / (double) streamSize) * 100d));
                }

                @Override
                public void bytesTransferred(CopyStreamEvent event) {
                }
            };

            Util.copyStream(input, output, ftp.getBufferSize(), outputFile.length(), listener);
            output.flush();

            return null;

        } finally {
            try {
                if (output != null) {
                    output.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

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