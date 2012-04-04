/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.technikum.bicss.sam.a1.alcatraz.client;

import at.technikum.bicss.sam.a1.alcatraz.common.Player;
import at.technikum.bicss.sam.a1.alcatraz.common.Util;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.DefaultListModel;
import java.util.LinkedList;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

/**
 *
 * @author Ru
 */
public class ClientGUI extends javax.swing.JFrame {

    private ClientHost hosthandle = null;

    /**
     * Creates new form ClientGUI
     */
    public ClientGUI(ClientHost host) {
        initComponents();
        this.hosthandle = host;

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTxtFld_PName = new javax.swing.JTextField();
        jLbl_PName = new javax.swing.JLabel();
        jBtn_Register = new javax.swing.JButton();
        jScrlPne = new javax.swing.JScrollPane();
        jLst_PlayerList = new javax.swing.JList();
        jLbl_PList = new javax.swing.JLabel();
        jBtn_Ready = new javax.swing.JButton();
        jPnl_Status = new javax.swing.JPanel();
        jLbl_StatusTxt = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLbl_PName.setText("Playername:");

        jBtn_Register.setText("Register");
        jBtn_Register.setActionCommand("register");
        jBtn_Register.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonActionPerformed(evt);
            }
        });

        jScrlPne.setViewportView(jLst_PlayerList);

        jLbl_PList.setText("Playerlist");

        jBtn_Ready.setText("Ready!");
        jBtn_Ready.setActionCommand("ready");
        jBtn_Ready.setEnabled(false);
        jBtn_Ready.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonActionPerformed(evt);
            }
        });

        jPnl_Status.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPnl_Status.setOpaque(false);

        jLbl_StatusTxt.setText("Statusbar");

        javax.swing.GroupLayout jPnl_StatusLayout = new javax.swing.GroupLayout(jPnl_Status);
        jPnl_Status.setLayout(jPnl_StatusLayout);
        jPnl_StatusLayout.setHorizontalGroup(
            jPnl_StatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPnl_StatusLayout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(jLbl_StatusTxt)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPnl_StatusLayout.setVerticalGroup(
            jPnl_StatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPnl_StatusLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLbl_StatusTxt)
                .addGap(7, 7, 7))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPnl_Status, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jBtn_Register, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 194, Short.MAX_VALUE)
                        .addComponent(jBtn_Ready, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrlPne)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLbl_PList)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLbl_PName)
                        .addGap(13, 13, 13)
                        .addComponent(jTxtFld_PName)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLbl_PName)
                            .addComponent(jTxtFld_PName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addComponent(jBtn_Register)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLbl_PList)
                        .addGap(1, 1, 1))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jBtn_Ready)
                        .addGap(18, 18, 18)))
                .addComponent(jScrlPne, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPnl_Status, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonActionPerformed
        if (evt.getActionCommand().toString().equals("register")) {
            if (jTxtFld_PName.getText().contains(" ")) {
                Util.errorUser(this, "Player Name must not contain spaces");
            } else {
                jTxtFld_PName.setEditable(false);
                jBtn_Register.setEnabled(false);;

                hosthandle.registerPlayer(this.jTxtFld_PName.getText());

                jBtn_Register.setText("Unregister");
                jBtn_Register.setActionCommand("unregister");
                jBtn_Register.setEnabled(true);
                jBtn_Ready.setEnabled(true);
            }
            jBtn_Register.setEnabled(rootPaneCheckingEnabled);
        }

        if (evt.getActionCommand().toString().equals("unregister")) {
            jBtn_Register.setEnabled(false);

            hosthandle.unregisterPlayer();

            jBtn_Register.setText("Register");
            jBtn_Register.setActionCommand("register");
            jBtn_Register.setEnabled(true);
            jBtn_Ready.setEnabled(false);
            jTxtFld_PName.setEditable(true);
        }

        if (evt.getActionCommand().toString().equals("ready")) {
            jBtn_Ready.setEnabled(false);

            hosthandle.setReady(true);

            jBtn_Ready.setText("No, wait!");
            jBtn_Ready.setActionCommand("wait");
            jBtn_Ready.setEnabled(true);
        }

        if (evt.getActionCommand().toString().equals("wait")) {
            jBtn_Ready.setEnabled(false);

            hosthandle.setReady(false);

            jBtn_Ready.setText("Ready!");
            jBtn_Ready.setActionCommand("ready");
            jBtn_Ready.setEnabled(true);
        }
    }//GEN-LAST:event_ButtonActionPerformed
//    /**
//     * @param args the command line arguments
//     */
//    public static void main(String args[]) {
//        /*
//         * Set the Nimbus look and feel
//         */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /*
//         * If Nimbus (introduced in Java SE 6) is not available, stay with the
//         * default look and feel. For details see
//         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(ClientGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(ClientGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(ClientGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(ClientGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /*
//         * Create and display the form
//         */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//
//            public void run() {
//                new ClientGUI().setVisible(true);
//            }
//        });
//    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtn_Ready;
    private javax.swing.JButton jBtn_Register;
    private javax.swing.JLabel jLbl_PList;
    private javax.swing.JLabel jLbl_PName;
    private javax.swing.JLabel jLbl_StatusTxt;
    private javax.swing.JList jLst_PlayerList;
    private javax.swing.JPanel jPnl_Status;
    private javax.swing.JScrollPane jScrlPne;
    private javax.swing.JTextField jTxtFld_PName;
    // End of variables declaration//GEN-END:variables

    public void updatePlayerList(LinkedList<Player> pl) {
        DefaultListModel lm = new DefaultListModel();
        if (pl != null) {
            for (Player p : pl) {
                lm.addElement(p.toString());
            }
    }
        this.jLst_PlayerList.setModel(lm);
    }

    public void setStatusText(String text) {
        jLbl_StatusTxt.setText(text);
    }
}
