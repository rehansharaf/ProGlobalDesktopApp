/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package app;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

public class ErrorFrame extends javax.swing.JDialog {
	
	private static String errorText;
	private static String errorDetail;

    public ErrorFrame(String errorText) {
    	this(errorText, null);
    }

    public ErrorFrame(String errorText, String errorDetail) {
    	setModal(true);
    	this.errorText = errorText;
    	this.errorDetail = errorDetail;
        initComponents();
        setErrorLabel(errorText);
    }

    public void setErrorLabel(String errorText) {
    	this.errorText = errorText;
    	errorLabel.setText(null);
    	errorLabel.setText(this.errorText);
	}

    @SuppressWarnings("unchecked")
    private void initComponents() {

        btnErrorClose = new javax.swing.JButton();
        btnShowDetail = new javax.swing.JButton();
        errorLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        btnErrorClose.setText("Close");
        btnErrorClose.setPreferredSize(new java.awt.Dimension(100, 25));
        btnErrorClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	errorClose();
            }
        });

        btnShowDetail.setText("Show Error Detail");
        btnShowDetail.setPreferredSize(new java.awt.Dimension(140, 25));
        btnShowDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	showErrorDetail();
            }
        });

        errorLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        errorLabel.setText("Error: Establishing Connection with Database");

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.CENTER)
                .addGroup(layout.createSequentialGroup()
                    .addGap(30)
                    .addComponent(errorLabel, GroupLayout.PREFERRED_SIZE, 340, GroupLayout.PREFERRED_SIZE)
                    .addGap(30))
                .addGroup(layout.createSequentialGroup()
                    .addComponent(btnShowDetail, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(18)
                    .addComponent(btnErrorClose, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );

        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGap(80)
                .addComponent(errorLabel)
                .addGap(20)
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                    .addComponent(btnShowDetail, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnErrorClose, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addGap(80)
        );

        pack();
        setLocationRelativeTo(null);
    }

    public void errorClose() {
    	this.dispose();
	}

    private void showErrorDetail() {
    	String detailText = errorDetail;

    	if (detailText == null || detailText.trim().isEmpty()) {
    		detailText = "No additional error detail available.";
    	}

    	DescFrame descFrame = new DescFrame(detailText, false);
    	descFrame.setTitle("Error Detail");
    	descFrame.setVisible(true);
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
                new ErrorFrame("Error", "Sample detailed error text").setVisible(true);
            }
        });
    }

    private javax.swing.JButton btnErrorClose;
    private javax.swing.JButton btnShowDetail;
    private javax.swing.JLabel errorLabel;
}