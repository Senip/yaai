/*
 *  yaai - Yet Another Alcatraz Implementation 
 *  BICSS-B6 2013
 */
package at.technikum.bicss.sam.b6.alcatraz.client;

import at.technikum.bicss.sam.b6.alcatraz.common.Player;
import at.technikum.bicss.sam.b6.alcatraz.common.Util;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Formatter;
import javax.swing.DefaultListModel;
import java.util.LinkedList;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

/**
 *
 * @author 
 */
public class ClientGUI extends javax.swing.JFrame 
{

    private ClientHost    hosthandle             = null;
    private boolean       JBtn_Register_oldstate = false;
    private static Logger l                             = Util.getLogger();
    private final Object  runUI                  = new Object();
    private WindowAdapter exitListener           = new WindowAdapter()
    {
        @Override
        public void windowClosing(WindowEvent e) 
        {
            synchronized(runUI)
            {
                runUI.notifyAll();
            }
        }
    };
    
    /**
     * Creates new form ClientGUI
     */
    public ClientGUI(ClientHost host) 
    {
        this.hosthandle = host;
        
        Logger.getRootLogger().addAppender(this.new StatusMessageAppender());
     
        initComponents();
        this.addWindowListener(exitListener);
        
        Util.centerFrame(this);
    }
    
    public void waitForExit()
    {
        while(true)
        {
            synchronized(runUI)
            {
               try { runUI.wait(); break; } catch (InterruptedException e) { }
               Thread.yield();
            }
        }
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
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Alcatraz");
        setMaximumSize(new java.awt.Dimension(326, 344));
        setMinimumSize(new java.awt.Dimension(326, 344));
        setResizable(false);

        jTxtFld_PName.setMaximumSize(new java.awt.Dimension(6, 20));

        jLbl_PName.setText("Playername:");

        jBtn_Register.setText("Register");
        jBtn_Register.setActionCommand("register");
        jBtn_Register.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ButtonActionPerformed(evt);
            }
        });

        jLst_PlayerList.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
        jLst_PlayerList.setMaximumSize(new java.awt.Dimension(306, 130));
        jLst_PlayerList.setMinimumSize(new java.awt.Dimension(306, 130));
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
        jPnl_Status.setMaximumSize(new java.awt.Dimension(60, 29));
        jPnl_Status.setMinimumSize(new java.awt.Dimension(60, 29));
        jPnl_Status.setOpaque(false);

        jLbl_StatusTxt.setText("Statusbar");

        javax.swing.GroupLayout jPnl_StatusLayout = new javax.swing.GroupLayout(jPnl_Status);
        jPnl_Status.setLayout(jPnl_StatusLayout);
        jPnl_StatusLayout.setHorizontalGroup(
            jPnl_StatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPnl_StatusLayout.createSequentialGroup()
                .addComponent(jLbl_StatusTxt)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPnl_StatusLayout.setVerticalGroup(
            jPnl_StatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPnl_StatusLayout.createSequentialGroup()
                .addComponent(jLbl_StatusTxt)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jLabel1.setFont(new java.awt.Font("Calibri", 0, 36)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("yaai");

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Yet Another Alcatraz Implementation - BICSS-B6 2013");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jBtn_Register, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jBtn_Ready, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLbl_PName)
                        .addGap(13, 13, 13)
                        .addComponent(jTxtFld_PName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLbl_PList)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPnl_Status, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrlPne))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLbl_PName)
                    .addComponent(jTxtFld_PName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBtn_Register)
                    .addComponent(jBtn_Ready))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLbl_PList)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addComponent(jScrlPne, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPnl_Status, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ButtonActionPerformed
        if (evt.getActionCommand().toString().equals("register")) {
            if (jTxtFld_PName.getText().contains(" ")) {
                l.error("Player Name must not contain spaces");
            } else if (Util.isEmpty(jTxtFld_PName.getText())) {
                l.error("Player Name must not be empty");
            } else {
                jTxtFld_PName.setEditable(false);
                jBtn_Register.setEnabled(false);

                if (hosthandle.registerPlayer(this.jTxtFld_PName.getText())) {
                    jBtn_Register.setText("Unregister");
                    jBtn_Register.setActionCommand("unregister");
                    jBtn_Ready.setEnabled(true);
                } else {
                    jTxtFld_PName.setEditable(true);
                }
                jBtn_Register.setEnabled(true);
            }
            //jBtn_Register.setEnabled(rootPaneCheckingEnabled);
        }

        if (evt.getActionCommand().toString().equals("unregister")) {
            jBtn_Register.setEnabled(false);

            if (hosthandle.unregisterPlayer()) {
                jBtn_Register.setText("Register");
                jBtn_Register.setActionCommand("register");
                jBtn_Ready.setEnabled(false);
                jTxtFld_PName.setEditable(true);
            }
            jBtn_Register.setEnabled(true);
        }

        if (evt.getActionCommand().toString().equals("ready")) {
            jBtn_Ready.setEnabled(false);

            if (hosthandle.setReady(true)) {
                jBtn_Ready.setText("No, wait!");
                jBtn_Ready.setActionCommand("wait");
            }
            jBtn_Ready.setEnabled(true);
        }

        if (evt.getActionCommand().toString().equals("wait")) {
            jBtn_Ready.setEnabled(false);

            if (hosthandle.setReady(false)) {
                jBtn_Ready.setText("Ready!");
                jBtn_Ready.setActionCommand("ready");
            }
            jBtn_Ready.setEnabled(true);
        }
    }//GEN-LAST:event_ButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jBtn_Ready;
    private javax.swing.JButton jBtn_Register;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLbl_PList;
    private javax.swing.JLabel jLbl_PName;
    private javax.swing.JLabel jLbl_StatusTxt;
    private javax.swing.JList jLst_PlayerList;
    private javax.swing.JPanel jPnl_Status;
    private javax.swing.JScrollPane jScrlPne;
    private javax.swing.JTextField jTxtFld_PName;
    // End of variables declaration//GEN-END:variables

    /**
     * 
     * Update the displayed player list
     * 
     * @param pl Player List
     */
    public void updatePlayerList(LinkedList<Player> pl) 
    {
        DefaultListModel lm = new DefaultListModel();
        
        if (pl != null) 
        {            
            String maxlen = Integer.toString(Util.NAME_MAX_LENGTH);
                        
            for (Player p : pl) 
            {
                Formatter fmt = new Formatter();
                                
                fmt.format("%-" + maxlen + "s %-7s %15s:%-5d",
                        p.getName(), (p.isReady() ? "ready" : "waiting"),
                        p.getAddress(), p.getPort());
                lm.addElement(fmt.toString());
            }
        }
        this.jLst_PlayerList.setModel(lm);
    }

    /**
     * 
     * @param text Status Text
     */
    public void setStatusText(String text) {
        jLbl_StatusTxt.setText(text);
    }

    /**
     * 
     * (Un-)lock the register Button
     * 
     * @param lock if th button is locked
     */
    public void lockRegisterBtn(boolean lock) 
    {
        if (lock == true) 
        {
            JBtn_Register_oldstate = jBtn_Register.isEnabled();
            jBtn_Register.setEnabled(false);
        } 
        else 
        {
            jBtn_Register.setEnabled(JBtn_Register_oldstate);
        }
    }

    /**
     * Reset the GUI
     */
    public void reset() 
    {
        jBtn_Register.setText("Register");
        jBtn_Register.setActionCommand("register");
        jBtn_Register.setEnabled(true);

        jBtn_Ready.setText("Ready!");
        jBtn_Ready.setActionCommand("ready");
        jBtn_Ready.setEnabled(false);

        jTxtFld_PName.setEditable(true);
        
    }

    /**
     *  Display logging Events
     */
    public class StatusMessageAppender extends AppenderSkeleton 
    {
        @Override
        protected void append(LoggingEvent event) 
        {
            if (event.getLevel().equals(Level.INFO)) 
            {
                jLbl_StatusTxt.setForeground(Color.BLACK);
                jLbl_StatusTxt.setText(event.getMessage().toString());
            } 
            else if (event.getLevel().equals(Level.WARN)) 
            {
                jLbl_StatusTxt.setForeground(Color.RED);
                jLbl_StatusTxt.setText(event.getMessage().toString());
            } 
            else if (event.getLevel().equals(Level.ERROR)) 
            {
                Util.warnUser(ClientGUI.this, event.getMessage().toString());
            } 
            else if (event.getLevel().equals(Level.FATAL)) 
            {
                Util.errorUser(ClientGUI.this, event.getMessage().toString());
            }
        }

        @Override
        public void close() {
        }

        @Override
        public boolean requiresLayout() {
            return false;
        }
    }
}
