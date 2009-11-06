/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * jPanelLineCmd.java
 *
 * Created on 25 oct. 2009, 15:46:30
 */

package manuserial;

import java.io.Console;
import java.io.IOException;
import java.security.acl.Owner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.io.CharToByteASCII;

/**
 *
 * @author thomas
 */
public class jPanelLineCmd extends javax.swing.JPanel {

    RS232 rs232local;
    HandlerTx hTx;

    /** Creates new form jPanelLineCmd */
    public jPanelLineCmd(HandlerTx parHandlerTx) {
        initComponents();
        hTx = parHandlerTx;
    }
    
    public jPanelLineCmd(String txt, HandlerTx parHandlerTx) {
        initComponents();
        this.jTextFieldCmd.setText(txt);
        hTx = parHandlerTx;
    }

        /**
     * Formatte la chaine str pour etre une suite de 2 caractères séparés par un espace.
     * @param str La chaine à formatter
     * @return La chaine formattée
     */
    private String formatHexa(String str) {
        StringBuffer buf = new StringBuffer("");
        str.trim();
        StringTokenizer st = new StringTokenizer(str, " ");
        String tmp = new String("");
        while (st.hasMoreTokens()) {

            tmp = st.nextToken();
            while (tmp.length() > 1 )
            {
                buf.append(tmp.substring(0, 2) + " ");
                tmp = tmp.substring(2);
            }
            
            buf.append(tmp);
        }
        return buf.toString().toUpperCase();
    }

    /**
     * Transforme la chaine hexadécimale (caractères hexa 2 par 2 séparés par un espace) en chaine de caractère en associant le caractère ASCII à chaque valeur hexa.
     * @param str La chaine à transformer
     * @return La chaine trasformée
     * @throws Exception
     */
    private String hexaToString(String str) throws Exception {

        String str2 = new String(formatHexa(str));

        StringBuffer buf = new StringBuffer("");
        for (int i = 0; i < str2.length() - 1; i += 3) {
            int tmp = Integer.parseInt(str2.substring(i, i + 2), 16);    //conversion d'hexa en décimal
            buf.append((char) tmp);
        }
        return buf.toString();
    }

    private String stringToHexa(String str) {

        StringBuffer buf = new StringBuffer("");
        Boolean escapChar = false;

        for (int i = 0; i < str.length(); i++) {
            int tmp = str.charAt(i);
            if( tmp == '\\' )
            {
                escapChar = true;
                continue;
            }
            else if(escapChar)
            {
                switch(tmp)
                {
                    case 'r':buf.append("0A ");
                        break;
                    case 'n':buf.append("0C ");
                        break;
                    default:break;
                }
                escapChar = false;
                continue;
            }
            else
            {
                if (tmp < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(tmp).toUpperCase() + " ");
            }
        }

        return buf.toString();
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextFieldCmd = new javax.swing.JTextField();
        jButtonEnvoyer = new javax.swing.JButton();
        jButtonEffacer = new javax.swing.JButton();
        jCheckBoxEcho = new javax.swing.JCheckBox();
        jCheckBoxHexa = new javax.swing.JCheckBox();

        jTextFieldCmd.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextFieldCmdFocusLost(evt);
            }
        });
        jTextFieldCmd.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldCmdKeyTyped(evt);
            }
        });

        jButtonEnvoyer.setText("Envoyer");
        jButtonEnvoyer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEnvoyerActionPerformed(evt);
            }
        });

        jButtonEffacer.setText("Effacer");
        jButtonEffacer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEffacerActionPerformed(evt);
            }
        });

        jCheckBoxEcho.setText("echo");

        jCheckBoxHexa.setText("hex");
        jCheckBoxHexa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxHexaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jTextFieldCmd, javax.swing.GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxHexa)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jCheckBoxEcho)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonEffacer)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonEnvoyer))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jTextFieldCmd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jButtonEnvoyer)
                .addComponent(jButtonEffacer)
                .addComponent(jCheckBoxEcho)
                .addComponent(jCheckBoxHexa))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonEffacerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEffacerActionPerformed
        // TODO add your handling code here:
        this.jTextFieldCmd.setText("");
    }//GEN-LAST:event_jButtonEffacerActionPerformed

    private void jButtonEnvoyerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEnvoyerActionPerformed
       String txt = jTextFieldCmd.getText();
        hTx.handlerTx(txt);
    }//GEN-LAST:event_jButtonEnvoyerActionPerformed

    private void formatCmd(int parKeyCode)
    {
        if( jCheckBoxHexa.isSelected() )
        {
            if( parKeyCode != 8)
                this.jTextFieldCmd.setText(formatHexa(this.jTextFieldCmd.getText()));
        }
    }

    private void jTextFieldCmdKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldCmdKeyTyped
        // TODO add your handling code here:
        formatCmd(evt.getKeyChar());
    }//GEN-LAST:event_jTextFieldCmdKeyTyped

    private void jTextFieldCmdFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextFieldCmdFocusLost
        // TODO add your handling code here:
        formatCmd(0);
    }//GEN-LAST:event_jTextFieldCmdFocusLost

    private void jCheckBoxHexaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxHexaActionPerformed
        // TODO add your handling code here:
        if( jCheckBoxHexa.isSelected() )
        {
            jTextFieldCmd.setText(formatHexa(stringToHexa(jTextFieldCmd.getText())));
        }
        else
        {
            try {
                jTextFieldCmd.setText(hexaToString(jTextFieldCmd.getText()));
            } catch (Exception ex) {
                Logger.getLogger(jPanelLineCmd.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jCheckBoxHexaActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonEffacer;
    private javax.swing.JButton jButtonEnvoyer;
    private javax.swing.JCheckBox jCheckBoxEcho;
    private javax.swing.JCheckBox jCheckBoxHexa;
    private javax.swing.JTextField jTextFieldCmd;
    // End of variables declaration//GEN-END:variables

}
