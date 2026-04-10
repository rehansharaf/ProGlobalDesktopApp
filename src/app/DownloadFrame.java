package app;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.net.ftp.FTPFile;

public class DownloadFrame extends javax.swing.JDialog {

    private static final Logger logger = Logger.getLogger(DownloadFrame.class.getName());

    private String host;
    private String user;
    private String pass;
    private String service;

    private javax.swing.JTable jDownloadList;
    private javax.swing.JScrollPane jScrollPane1;
    private JTextField searchField;
    private TableRowSorter<TableModel> rowSorter;

    public DownloadFrame(String host, String user, String pass, String service) throws IOException {
        this.host = host;
        this.user = user;
        this.pass = pass;
        this.service = service;

        initComponents();
        getAllFiles();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        setCurrentModel(true);
        jScrollPane1 = new javax.swing.JScrollPane();
        jDownloadList = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jDownloadList.setModel(new DefaultTableModel(
                new Object[][] {},
                new String[] { "Select", "File Names" }
        ) {
            Class[] columnTypes = new Class[] { Boolean.class, Object.class };

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }

            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 0;
            }
        });

        jScrollPane1.setViewportView(jDownloadList);

        jDownloadList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int viewRow = jDownloadList.rowAtPoint(e.getPoint());
                int viewCol = jDownloadList.columnAtPoint(e.getPoint());

                if (viewRow >= 0 && viewCol != 0) {
                    int modelRow = jDownloadList.convertRowIndexToModel(viewRow);
                    DefaultTableModel model = (DefaultTableModel) jDownloadList.getModel();

                    Object value = model.getValueAt(modelRow, 0);
                    boolean checked = value != null && (Boolean) value;

                    model.setValueAt(!checked, modelRow, 0);
                }
            }
        });

        JButton btnDownloadSelected = new JButton("Download");
        btnDownloadSelected.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                DefaultTableModel model = (DefaultTableModel) jDownloadList.getModel();
                ArrayList<String> filename = new ArrayList<String>();

                for (int i = 0; i < jDownloadList.getRowCount(); i++) {
                    int modelRow = jDownloadList.convertRowIndexToModel(i);

                    Object checkVal = model.getValueAt(modelRow, 0);
                    boolean selected = checkVal != null && (Boolean) checkVal;

                    if (selected) {
                        filename.add(model.getValueAt(modelRow, 1).toString());
                    }
                }

                if (filename.size() > 0) {
                    setCurrentModel(false);
                    downloadSelectedRows(filename);
                }
            }
        });

        searchField = new JTextField("Search");
        searchField.setColumns(10);
        searchField.setForeground(Color.GRAY);

        searchField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                } else {
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setForeground(Color.GRAY);
                    searchField.setText("");
                }
            }
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterTable();
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
                .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
                .addGroup(Alignment.TRAILING, layout.createSequentialGroup()
                    .addGap(47)
                    .addComponent(searchField, GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
                    .addGap(18)
                    .addComponent(btnDownloadSelected)
                    .addGap(31))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 271, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(btnDownloadSelected)
                        .addComponent(searchField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
        );
        getContentPane().setLayout(layout);

        pack();
        setLocationRelativeTo(null);
    }

    private void filterTable() {
        if (rowSorter == null) {
            return;
        }

        String text = searchField.getText();
        if (text == null || text.trim().length() == 0 || text.equals("Search")) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    private void setCurrentModel(boolean val) {
        this.setModal(val);
    }

   /* public void downloadSelectedRows(ArrayList<String> filename) {
        JFileChooser f = new JFileChooser();
        f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int r = f.showSaveDialog(null);

        if (r == JFileChooser.APPROVE_OPTION) {
            System.out.println(f.getSelectedFile());
            File localDir = f.getSelectedFile();

            for (int i = 0; i < filename.size(); i++) {
                try {
                    DownloadPopup downloadp = new DownloadPopup();
                    downloadp.setVisible(true);

                    DownloadWorker worker = new DownloadWorker(
                            host, user, pass, new File(filename.get(i)), service, localDir
                    );

                    worker.addPropertyChangeListener(new PropertyChangeListener() {
                        @Override
                        public void propertyChange(PropertyChangeEvent evt) {
                            if (evt.getPropertyName().equals("progress")) {
                                downloadp.jbutton1ChangeVisiblity(false);
                                downloadp.setDefaultCloseOperation(0);
                                Integer progress = (Integer) evt.getNewValue();

                                try {
                                    downloadp.progressBarVal(progress);
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                    logger.severe("Error updating download popup: " + e.getMessage());
                                }
                            } else {
                                boolean checkBtnVisibile = downloadp.jbutton1CheckVisiblity();
                                if (!checkBtnVisibile) {
                                    downloadp.jbutton1ChangeVisiblity(true);
                                    downloadp.dispose();
                                } else {
                                    downloadp.jbutton1ChangeVisiblity(true);
                                }
                            }
                        }
                    });

                    worker.execute();

                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                    logger.severe("MalformedURLException while downloading file: " + ex.getMessage());
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                    logger.severe("InterruptedException while downloading file: " + e1.getMessage());
                }
            }
        }
    }
    */
    
    public void downloadSelectedRows(ArrayList<String> filename) {
        JFileChooser f = new JFileChooser();
        f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int r = f.showSaveDialog(null);

        if (r == JFileChooser.APPROVE_OPTION) {
            System.out.println(f.getSelectedFile());
            File localDir = f.getSelectedFile();

            final AtomicBoolean errorShown = new AtomicBoolean(false);

            for (int i = 0; i < filename.size(); i++) {
                try {
                    final DownloadPopup downloadp = new DownloadPopup();
                    downloadp.setVisible(true);

                    final DownloadWorker worker = new DownloadWorker(
                            host, user, pass, new File(filename.get(i)), service, localDir
                    );

                    worker.addPropertyChangeListener(new PropertyChangeListener() {
                        @Override
                        public void propertyChange(PropertyChangeEvent evt) {

                            if ("progress".equals(evt.getPropertyName())) {
                                downloadp.jbutton1ChangeVisiblity(false);
                                downloadp.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
                                Integer progress = (Integer) evt.getNewValue();

                                try {
                                    downloadp.progressBarVal(progress);
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                    logger.severe("Error updating download popup: " + e.getMessage());
                                }
                            }

                            if ("state".equals(evt.getPropertyName())
                                    && evt.getNewValue() == javax.swing.SwingWorker.StateValue.DONE) {
                                try {
                                    worker.get();
                                    downloadp.jbutton1ChangeVisiblity(true);
                                    downloadp.dispose();
                                } catch (Exception ex) {
                                    downloadp.dispose();

                                    if (errorShown.compareAndSet(false, true)) {
                                        ErrorFrame errFrame = new ErrorFrame("Error: Establishing Connection with Server", ErrorUtils.getErrorDetail(ex));
                                        errFrame.setVisible(true);
                                    }
                                }
                            }
                        }
                    });

                    worker.execute();

                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                    logger.severe("MalformedURLException while downloading file: " + ex.getMessage());
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                    logger.severe("InterruptedException while downloading file: " + e1.getMessage());
                }
            }
        }
    }

    public void getAllFiles() throws IOException {

        logger.info("Loading FTP files from folder: /" + service);

        GetAllFiles getAll = new GetAllFiles();
        FTPFile[] files = getAll.getAllFilesFromFolder("/" + service, host, user, pass);

        if (files == null) {
            logger.severe("FTP returned null for folder: /" + service);
            throw new IOException("No files returned from FTP folder: /" + service);
        }

        logger.info("FTP returned " + files.length + " files for folder: /" + service);

        DefaultTableModel model = (DefaultTableModel) jDownloadList.getModel();
        model.setRowCount(0);
        model.setColumnIdentifiers(new String[] { "Select", "File Names" });

        jDownloadList.getColumnModel().getColumn(0).setPreferredWidth(20);
        jDownloadList.getColumnModel().getColumn(0).setMaxWidth(20);

        int rowcount = 0;
        for (FTPFile file : files) {
            model.addRow(new Object[0]);
            model.setValueAt(false, rowcount, 0);
            model.setValueAt(file.getName(), rowcount, 1);
            rowcount++;
        }

        rowSorter = new TableRowSorter<>(jDownloadList.getModel());
        jDownloadList.setRowSorter(rowSorter);
    }
}