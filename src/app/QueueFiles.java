package app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class QueueFiles extends javax.swing.JDialog {

    private static String host;
    private static String user;
    private static String pass;
    private static String service;

    private boolean isAdmin;

    private javax.swing.JTable jDownloadList;
    private javax.swing.JScrollPane jScrollPane1;

    public QueueFiles(String host, String user, String pass, String service, boolean isAdmin) throws IOException {
        setResizable(true);

        QueueFiles.host = host;
        QueueFiles.user = user;
        QueueFiles.pass = pass;
        QueueFiles.service = service;
        this.isAdmin = isAdmin;

        initComponents();
        getAllFiles();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        this.setModal(true);
        jScrollPane1 = new javax.swing.JScrollPane();
        jDownloadList = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jDownloadList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {},
            new String [] { "Select", "File Names" }
        ) {
            Class[] columnTypes = new Class[] { Boolean.class, Object.class };

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        });

        jScrollPane1.setViewportView(jDownloadList);

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jScrollPane1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        jDownloadList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = jDownloadList.rowAtPoint(e.getPoint());
                int col = jDownloadList.columnAtPoint(e.getPoint());

                if (row >= 0 && col != 0) {
                    DefaultTableModel model = (DefaultTableModel) jDownloadList.getModel();
                    Object value = model.getValueAt(row, 0);
                    boolean checked = value != null && (Boolean) value;
                    model.setValueAt(!checked, row, 0);
                }
            }
        });

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    processingRefreshActionPerformed(evt);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        JButton btnDelete = null;
        if (isAdmin) {
            btnDelete = new JButton("Delete");
            btnDelete.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    try {
                        deleteSelectedFiles();
                    } catch (IOException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(
                            QueueFiles.this,
                            "Error deleting files: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            });
        }

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        if (isAdmin) {
            layout.setHorizontalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
                    .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(180)
                        .addComponent(btnRefresh)
                        .addGap(18)
                        .addComponent(btnDelete)
                        .addGap(31))
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 271, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                            .addComponent(btnRefresh)
                            .addComponent(btnDelete)))
            );
        } else {
            layout.setHorizontalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
                    .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnRefresh)
                        .addGap(31))
            );
            layout.setVerticalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 271, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnRefresh))
            );
        }

        pack();
        setLocationRelativeTo(null);

        DefaultTableModel model = new DefaultTableModel(
            new Object [][] {},
            new String [] { "Select", "File Names" }
        ) {
            Class[] columnTypes = new Class[] { Boolean.class, Object.class };

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };

        jDownloadList.setModel(model);
        jDownloadList.getColumnModel().getColumn(0).setPreferredWidth(20);
        jDownloadList.getColumnModel().getColumn(0).setMaxWidth(20);
    }

    public void processingRefreshActionPerformed(ActionEvent evt) throws IOException {
        DefaultTableModel model = (DefaultTableModel) jDownloadList.getModel();
        model.setRowCount(0);

        GetAllFiles getAll = new GetAllFiles();
        FTPFile[] files = getAll.getAllFilesFromFolder("/" + service, host, user, pass);

        model.setColumnIdentifiers(new String[] { "Select", "File Names" });

        for (FTPFile file : files) {
            model.addRow(new Object[] { false, file.getName() });
        }

        jDownloadList.getColumnModel().getColumn(0).setPreferredWidth(20);
        jDownloadList.getColumnModel().getColumn(0).setMaxWidth(20);
    }

    public void getAllFiles() throws IOException {
        GetAllFiles getAll = new GetAllFiles();
        FTPFile[] files = getAll.getAllFilesFromFolder("/" + service, host, user, pass);

        DefaultTableModel model = (DefaultTableModel) jDownloadList.getModel();
        model.setRowCount(0);
        model.setColumnIdentifiers(new String[] { "Select", "File Names" });

        for (FTPFile file : files) {
            model.addRow(new Object[] { false, file.getName() });
        }

        jDownloadList.getColumnModel().getColumn(0).setPreferredWidth(20);
        jDownloadList.getColumnModel().getColumn(0).setMaxWidth(20);
    }

    private void deleteSelectedFiles() throws IOException {
        if (!isAdmin) {
            JOptionPane.showMessageDialog(this, "You are not allowed to delete files.");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) jDownloadList.getModel();
        ArrayList<String> fileNames = new ArrayList<String>();

        for (int i = 0; i < model.getRowCount(); i++) {
            Object checkVal = model.getValueAt(i, 0);
            boolean selected = checkVal != null && (Boolean) checkVal;

            if (selected) {
                fileNames.add(model.getValueAt(i, 1).toString());
            }
        }

        if (fileNames.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select file(s) to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete selected file(s)?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        FTPClient ftpClient = new FTPClient();
        List<String> processingFiles = new ArrayList<String>();
        List<String> deletedFiles = new ArrayList<String>();

        try {
            ftpClient.connect(host);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();

            for (String fileName : fileNames) {
                if (isFileProcessing(fileName)) {
                    processingFiles.add(fileName);
                    continue;
                }

                boolean deleted = ftpClient.deleteFile("/" + service + "/" + fileName);
                if (deleted) {
                    deletedFiles.add(fileName);
                }
            }
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.logout();
                    ftpClient.disconnect();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        processingRefreshActionPerformed(null);

        if (!deletedFiles.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Selected file(s) deleted successfully."
            );
        }

        if (!processingFiles.isEmpty()) {
            StringBuilder msg = new StringBuilder();
            for (String fileName : processingFiles) {
                msg.append("Filename: ")
                   .append(fileName)
                   .append(" cannot be deleted, it is processing.")
                   .append("\n");
            }

            JOptionPane.showMessageDialog(this, msg.toString());
        }
    }

    private boolean isFileProcessing(String fileName) throws IOException {
        DBQueries dbQueries = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = getProcessingCheckQuery();

            if (sql == null || sql.trim().isEmpty()) {
                return false;
            }

            dbQueries = new DBQueries();
            con = dbQueries.getConnection();

            ps = con.prepareStatement(sql);
            if (!"DocMerging_Request".equalsIgnoreCase(service))
                ps.setString(1, fileName);

            rs = ps.executeQuery();
            return rs.next();

        } catch (ClassNotFoundException e) {
            throw new IOException("Database driver not found.", e);
        } catch (SQLException e) {
            throw new IOException("Database error while checking file status.", e);
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (Exception e) {}
            }
            if (ps != null) {
                try { ps.close(); } catch (Exception e) {}
            }
            if (dbQueries != null) {
                try { dbQueries.closeDBConnection(); } catch (Exception e) {}
            }
        }
    }

    private String getProcessingCheckQuery() {

        if ("Requests".equalsIgnoreCase(service)) {
            return "SELECT 1 FROM SkypeCDRBackLog.EAS_File WHERE Filename = ? AND Status IS NULL LIMIT 1";

        } else if ("C&R_EventsRequest".equalsIgnoreCase(service)) {
            return "SELECT 1 FROM SkypeCDRBackLog.edexcrawler WHERE RequestFilename = ? AND Status IS NULL AND ServiceName = 'CandR' LIMIT 1";

        } else if ("Runtime_InterpretingBill_Request".equalsIgnoreCase(service)) {
            return "SELECT 1 FROM SkypeCDRBackLog.edexcrawler WHERE RequestFilename = ? AND Status IS NULL AND ServiceName = 'DowloadInterpetingBill' LIMIT 1";

        } else if ("Docucent_Request".equalsIgnoreCase(service)) {
            return "SELECT 1 FROM SkypeCDRBackLog.Docucent_Requests WHERE Filename = ? AND Status IS NULL LIMIT 1";

        } else if ("DocMerging_Request".equalsIgnoreCase(service)) {
            return "SELECT 1 FROM SkypeCDRBackLog.mergepdf2 WHERE processed is null LIMIT 1";

        } else if ("Conexem_Russman_DataFetch_DOS_Request".equalsIgnoreCase(service)) {
            return "SELECT 1 FROM SkypeCDRBackLog.conexemdoc_list_dataFetch WHERE Filename = ? AND Status IS NULL AND type = 'SP_DOS' LIMIT 1";

        } else if ("Conexem_Desert&Attum_DataFetch_Request".equalsIgnoreCase(service)) {
            return "SELECT 1 FROM SkypeCDRBackLog.conexem_desert_dataFetch WHERE Filename = ? AND Status IS NULL AND type = 'All' LIMIT 1";

        } else if ("Conexem_DataFetchRFA_Request".equalsIgnoreCase(service)) {
            return "SELECT 1 FROM SkypeCDRBackLog.conexemdoc_list_dataFetch WHERE Filename = ? AND Status IS NULL AND type = 'RFA' LIMIT 1";

        } else if ("Docucent_GetPOSRequest".equalsIgnoreCase(service)) {
            return "SELECT 1 FROM SkypeCDRBackLog.edexcrawler WHERE RequestFilename = ? AND Status IS NULL AND ServiceName = 'DocucentGetPOS' LIMIT 1";

        } else if ("DownloadDocsDDM_Request".equalsIgnoreCase(service)) {
            return "SELECT 1 FROM SkypeCDRBackLog.edexcrawler WHERE RequestFilename = ? AND Status IS NULL AND ServiceName = 'downloadDocsDDM' LIMIT 1";

        } else if ("SBRDoc_Request".equalsIgnoreCase(service)) {
            return "SELECT 1 FROM SkypeCDRBackLog.edexcrawler WHERE RequestFilename = ? AND Status IS NULL AND ServiceName = 'SBRDoc' LIMIT 1";

        } else if ("ResizePDF_Request".equalsIgnoreCase(service)) {
            return "SELECT 1 FROM SkypeCDRBackLog.edexcrawler WHERE RequestFilename = ? AND Status IS NULL AND ServiceName = 'ResizePDF' LIMIT 1";

        } else if ("Runtime_HCFA_Request".equalsIgnoreCase(service)) {
            return "SELECT 1 FROM SkypeCDRBackLog.edexcrawler WHERE RequestFilename = ? AND Status IS NULL AND ServiceName = 'DownloadHCFA_Study' LIMIT 1";

        } else if ("Runtime_HCFA_RequestAllStudy".equalsIgnoreCase(service)) {
            return "SELECT 1 FROM SkypeCDRBackLog.edexcrawler WHERE RequestFilename = ? AND Status IS NULL AND ServiceName = 'DownloadHCFA_AllStudy' LIMIT 1";

        } else if ("Runtime_Ledger_Request".equalsIgnoreCase(service)) {
            return "SELECT 1 FROM SkypeCDRBackLog.edexcrawler WHERE RequestFilename = ? AND Status IS NULL AND ServiceName = 'DownloadLedger' LIMIT 1";

        } else if ("Runtime_Ledger_RequestAllStudy".equalsIgnoreCase(service)) {
            return "SELECT 1 FROM SkypeCDRBackLog.edexcrawler WHERE RequestFilename = ? AND Status IS NULL AND ServiceName = 'DownloadLedger_AllStudy' LIMIT 1";

        } else if ("GetPDFPageNo_Request".equalsIgnoreCase(service)) {
            return "SELECT 1 FROM SkypeCDRBackLog.edexcrawler WHERE RequestFilename = ? AND Status IS NULL AND ServiceName = 'GetPDFPageNo' LIMIT 1";

        } else if ("InterpretingBill_Request_CR_ML".equalsIgnoreCase(service)) {
            return "SELECT 1 FROM SkypeCDRBackLog.edexcrawler WHERE RequestFilename = ? AND Status IS NULL AND ServiceName = 'Copy_Record_ML' LIMIT 1";

        } else if ("PDFValidity_Request".equalsIgnoreCase(service)) {
            return "SELECT 1 FROM SkypeCDRBackLog.edexcrawler WHERE RequestFilename = ? AND Status IS NULL AND ServiceName = 'PDFValidity' LIMIT 1";

        } else if ("MedDocsDownload_Request".equalsIgnoreCase(service)) {
            return "SELECT 1 FROM SkypeCDRBackLog.edexcrawler WHERE RequestFilename = ? AND Status IS NULL AND ServiceName = 'DocDownloadMed' LIMIT 1";

        } else if ("LienFiledStatus_Request".equalsIgnoreCase(service)) {
            return "SELECT 1 FROM SkypeCDRBackLog.lienfiling WHERE RequestFileName = ? AND Status IS NULL LIMIT 1";

        } else if ("LienFiledStatusProvider_Request".equalsIgnoreCase(service)) {
            return "SELECT 1 FROM SkypeCDRBackLog.edexcrawler WHERE RequestFilename = ? AND Status IS NULL AND ServiceName = 'LienMultipleWithProvider' LIMIT 1";

        } else if ("FetchHearingRequest".equalsIgnoreCase(service)) {
            return "SELECT 1 FROM SkypeCDRBackLog.fetchhearingdata WHERE RequestFilename = ? AND Status IS NULL LIMIT 1";

        } else if ("FetchHearingRequest2".equalsIgnoreCase(service)) {
            return "SELECT 1 FROM SkypeCDRBackLog.fetchhearingdata2 WHERE RequestFilename = ? AND Status IS NULL LIMIT 1";

        } else if ("HearingTestReq".equalsIgnoreCase(service)) {
            return "SELECT 1 FROM SkypeCDRBackLog.fetchhearingdata_old WHERE RequestFilename = ? AND Status IS NULL LIMIT 1";

        } else if ("HearingTestReq2".equalsIgnoreCase(service)) {
            return "SELECT 1 FROM SkypeCDRBackLog.fetchhearingdata_old2 WHERE RequestFilename = ? AND Status IS NULL LIMIT 1";

        } else if ("BulkEmail_Request".equalsIgnoreCase(service)) {
            return "SELECT 1 FROM SkypeCDRBackLog.edexcrawler WHERE RequestFilename = ? AND Status IS NULL AND ServiceName = 'BulkEmail' LIMIT 1";

        } else if ("Conexem_C&RPosting_Request".equalsIgnoreCase(service)) {
            return "SELECT 1 FROM SkypeCDRBackLog.edexcrawler WHERE RequestFilename = ? AND Status IS NULL AND ServiceName = 'Conexem_C&R' LIMIT 1";

        } else if ("FaxReceiptDownload_Request".equalsIgnoreCase(service)) {
            return "SELECT 1 FROM SkypeCDRBackLog.edexcrawler WHERE RequestFilename = ? AND Status IS NULL AND ServiceName = 'FaxReceipt_Download' LIMIT 1";

        } else if ("SupDecRequest".equalsIgnoreCase(service)) {
            return "SELECT 1 FROM SkypeCDRBackLog.DelarationFileList WHERE FileName = ? AND Status IS NULL AND ServiceName = 'SupDec' LIMIT 1";

        } else if ("SupDecAllPartyRequest".equalsIgnoreCase(service)) {
            return "SELECT 1 FROM SkypeCDRBackLog.DelarationFileList WHERE FileName = ? AND Status IS NULL AND ServiceName = 'SupDecAllParty' LIMIT 1";

        } else if ("SupDecAllPartyRequest2".equalsIgnoreCase(service)) {
            return "SELECT 1 FROM SkypeCDRBackLog.DelarationFileList2 WHERE FileName = ? AND Status IS NULL LIMIT 1";

        } else if ("GetDocName_Docucent_Request".equalsIgnoreCase(service)) {
            return "SELECT 1 FROM SkypeCDRBackLog.edexcrawler WHERE RequestFilename = ? AND Status IS NULL AND ServiceName = 'GetDocName_Docucent' LIMIT 1";

        } else if ("Medflow_DocUploading/Pharmacy/Service_1/Request".equalsIgnoreCase(service)) {
            return "select 1 from SkypeCDRBackLog.medflow_docupload where RequestFileName = ? AND Status IS NULL AND service = 1 AND instance = 'Pharmacy' LIMIT 1 ";
        } else if ("Medflow_DocUploading/Pharmacy/Service_2/Request".equalsIgnoreCase(service)) {
            return "select 1 from SkypeCDRBackLog.medflow_docupload where RequestFileName = ? and Status IS NULL AND service = 2 AND instance = 'Pharmacy' LIMIT 1 ";
        } else if ("Medflow_DocUploading/PI/Service_1/Request".equalsIgnoreCase(service)) {
            return "select 1 from SkypeCDRBackLog.medflow_docupload where RequestFileName = ? and Status IS NULL AND service = 1 AND instance = 'PI' LIMIT 1 ";
        } else if ("Medflow_DocUploading/PI/Service_2/Request".equalsIgnoreCase(service)) {
            return "select 1 from SkypeCDRBackLog.medflow_docupload where RequestFileName = ? and Status IS NULL AND service = 2 AND instance = 'PI' LIMIT 1 ";
        } else if ("Medflow_DocUploading/IWP/Service_1/Request".equalsIgnoreCase(service)) {
            return "select 1 from SkypeCDRBackLog.medflow_docupload where RequestFileName = ? and Status IS NULL AND service = 1 AND instance = 'IWP' LIMIT 1 ";
        } else if ("Medflow_DocUploading/IWP/Service_2/Request".equalsIgnoreCase(service)) {
            return "select 1 from SkypeCDRBackLog.medflow_docupload where RequestFileName = ? and Status IS NULL AND service = 2 AND instance = 'IWP' LIMIT 1 ";
        } else if ("Medflow_DocUploading/KHI/Service_1/Request".equalsIgnoreCase(service)) {
            return "select 1 from SkypeCDRBackLog.medflow_docupload where RequestFileName = ? and Status IS NULL AND service = 1 AND instance = 'KHI' LIMIT 1 ";
        } else if ("Medflow_DocUploading/KHI/Service_2/Request".equalsIgnoreCase(service)) {
            return "select 1 from SkypeCDRBackLog.medflow_docupload where RequestFileName = ? and Status IS NULL AND service = 2 AND instance = 'KHI' LIMIT 1 ";
        } else if ("Medflow_DocUploading/KHI/Service_3/Request".equalsIgnoreCase(service)) {
            return "select 1 from SkypeCDRBackLog.medflow_docupload where RequestFileName = ? and Status IS NULL AND service = 3 AND instance = 'KHI' LIMIT 1 ";
        } else if ("Medflow_DocUploading/KHI/Service_4/Request".equalsIgnoreCase(service)) {
            return "select 1 from SkypeCDRBackLog.medflow_docupload where RequestFileName = ? and Status IS NULL AND service = 4 AND instance = 'KHI' LIMIT 1 ";
        }

        return null;
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(QueueFiles.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(QueueFiles.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(QueueFiles.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(QueueFiles.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new QueueFiles(host, user, pass, service, true).setVisible(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}