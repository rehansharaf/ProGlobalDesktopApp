/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package app;

/**
 *
 * @author Rehan
 */
public class DescFrame extends javax.swing.JDialog {

    static String descText;
    private boolean wrapText = true;

    public DescFrame(String descText) {
        this(descText, true);
    }

    public DescFrame(String descText, boolean wrapText) {
        setResizable(true);
        setModal(true);
        this.descText = descText;
        this.wrapText = wrapText;
        initComponents();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jTextArea1.setEditable(false);
        jTextArea1.setText(descText);
        jTextArea1.setLineWrap(wrapText);
        jTextArea1.setWrapStyleWord(wrapText);
        jTextArea1.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
        jTextArea1.setCaretPosition(0);

        jScrollPane2.setViewportView(jTextArea1);

        jButton1.setText("Close");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeFrame();
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                .addGroup(layout.createSequentialGroup()
                    .addGap(20, 20, 20)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
                        .addComponent(jButton1))
                    .addGap(20, 20, 20))
        );

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(20, 20, 20)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
                    .addGap(15, 15, 15)
                    .addComponent(jButton1)
                    .addGap(20, 20, 20))
        );

        setMinimumSize(new java.awt.Dimension(500, 300));
        setSize(700, 450);
        setLocationRelativeTo(null);
    }

    public void closeFrame() {
        this.dispose();
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DescFrame("Sample description text that should wrap nicely in the popup window.", true).setVisible(true);
            }
        });
    }

    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
}