/*
 * RS232Frame.java
 * Création d'une fenetre pour le dialogue avec un périphérique série (ou un périphérique branché sur port USB émulant un port série (composant FT232 de FTDI par exemple))
 *
 * Created on 8 oct. 2009, 08:22:27
 */
package manuserial;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;
import manuserial.RS232.RS232DataEvent;
import manuserial.RS232.RS232DataListener;
import java.io.IOException;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;

/**
 *
 * @author Emmanuel Roussel
 */
public class RS232Frame extends javax.swing.JFrame {

    private RS232 rs232;
    private boolean hexa;
    private boolean portDispo;
    private String inBuffer;    //toujours en hexa


    /** Creates new form RS232Frame */
    public RS232Frame() {
        initComponents();

        rs232 = new RS232();
        hexa = false;
        inBuffer = new String();

        if(updateListPorts()){
            jTextEtat.setText("Port de communication série détecté !");
        }else{
            jTextEtat.setText("Aucun port disponible n'a été détecté !");
        }

        jButSend.setEnabled(false);

        // <editor-fold defaultstate="collapsed" desc="Exécuté lorsque donnée reçue">
        rs232.addRS232DataListener(new RS232DataListener() {

            public void dataAvailable(RS232DataEvent evt) {
                String tmpbuf = new String(rs232.getInBuffer());
                if (!(tmpbuf.equals(""))) {
                    if (!hexa) {
                        jTextAreaRX.setText(jTextAreaRX.getText() + tmpbuf);
                    } else {
                        jTextAreaRX.setText(jTextAreaRX.getText() + stringToHexa(tmpbuf));
                    }
                    if (jTextAreaRX.getText().length() > 1000) {
                        try {
                            jTextAreaRX.setText(jTextAreaRX.getText(jTextAreaRX.getText().length() - 300, 300));
                        } catch (BadLocationException ex) {
                        }
                    }
                    inBuffer += stringToHexa(tmpbuf);
                    if (inBuffer.length() > 200) {
                        inBuffer = inBuffer.substring(inBuffer.length() - 50);
                    }
                }
            }
        });// </editor-fold>

    }

    /**
     * Actualise la liste des ports disponibles
     * @return
     */
    public boolean updateListPorts(){
        String[] liste = rs232.listPorts();
        jCBListPorts.removeAllItems();
        for (int i = 0; i < liste.length; i++) {
            jCBListPorts.addItem(makeObj(liste[i]));
        }
        if (liste.length == 0) {
            jToggleButOpen.setEnabled(false);
            portDispo = false;
            return false;
        }else{
            jToggleButOpen.setEnabled(true);
            portDispo = true;
            return true;
        }
    }
    

    /**
     * Le port série est-il disponble ?
     * @return true si le port est dipo
     */
    public boolean isPortDispo() {
        return portDispo;
    }

    /**
     * Utilisé pour ajouter les ports dispo à la liste.
     * @param item
     * @return
     */
    private Object makeObj(final String item) {
        return new Object() {

            @Override
            public String toString() {
                return item;
            }
        };
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
            if (tmp.length() == 1) {
                buf.append("0" + tmp + " ");
            } else {
                buf.append(tmp.substring(0, 2) + " ");
            }
        }
        return buf.toString().toUpperCase();
    }

    /**
     * Transforme un String en une chaine de valeurs hexa (séparées par un espace) correspondant au code ASCII de chacun des caractères du String de départ.
     * @param str La variable à transformer
     * @return Le String en format Hexa
     */
    private String stringToHexa(String str) {
        StringBuffer buf = new StringBuffer("");
        for (int i = 0; i < str.length(); i++) {
            int tmp = str.charAt(i);
            if (tmp < 16) {
                buf.append("0");
            }
            buf.append(Integer.toHexString(tmp).toUpperCase() + " ");
        }

        return buf.toString();
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


    
    /**
     * Ouvre le port
     */
    public void openPort() {
        if (!(rs232.isOpen())) {
            try {
                rs232.connect(jCBListPorts.getSelectedItem().toString(), Integer.valueOf(jTextBaudRate.getText()).intValue());
            } catch (PortInUseException e) {
                JOptionPane.showMessageDialog(null, "Le port est en cours d'utilisation par une autre application", "Damned", JOptionPane.ERROR_MESSAGE);
                jTextEtat.setText("Damned: port en cours d'utilisation");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Erreur d'entrée/sortie", "Damned", JOptionPane.ERROR_MESSAGE);
                jTextEtat.setText("Damned: erreur d'IO");
            } catch (UnsupportedCommOperationException e) {
                JOptionPane.showMessageDialog(null, "Opération non supportée", "Damned", JOptionPane.ERROR_MESSAGE);
                jTextEtat.setText("Damned: opération non supportée");
            } catch (NoSuchPortException e) {
                JOptionPane.showMessageDialog(null, "Le port n'existe pas", "Damned", JOptionPane.ERROR_MESSAGE);
                jTextEtat.setText("Damned: le port n'existe pas");
            }
        }
        if(actualise_fen()){
            jTextEtat.setText("Connecté au port " + rs232.getCurrentPort());
        }else{
            jTextEtat.setText("Port fermé");
        }
    }

    /**
     * ferme le port si il est ouvert.
     */
    public void closePort() {
        if (rs232.isOpen()) {
            rs232.close();
        }
        actualise_fen();
        jTextEtat.setText("Port fermé");
    }

    
    /**
     * actualise l'état des boutons de la fenetre
     * @return true si le port est ouvert
     */
    public boolean actualise_fen() {
        if (!(rs232.isOpen())) {
            jToggleButOpen.setText("Ouvrir");
            jToggleButOpen.setSelected(false);
            jCBListPorts.setEnabled(true);
            jTextBaudRate.setEnabled(true);
            jButSend.setEnabled(false);
            return false;
        }else{
            jToggleButOpen.setText("Fermer");
            jToggleButOpen.setSelected(true);
            jCBListPorts.setEnabled(false);
            jTextBaudRate.setEnabled(false);
            jButSend.setEnabled(true);
            return true;
        }
    }

    public boolean portIsOpen() {
        return rs232.isOpen();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextEtat = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaRX = new javax.swing.JTextArea();
        jSeparator1 = new javax.swing.JSeparator();
        jCBListPorts = new javax.swing.JComboBox();
        jTextToSend = new javax.swing.JTextField();
        jButSend = new javax.swing.JButton();
        jToggleButOpen = new javax.swing.JToggleButton();
        jTextBaudRate = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButClearTX = new javax.swing.JButton();
        jButClearRX = new javax.swing.JButton();
        jToggleButHexa = new javax.swing.JToggleButton();
        jCheckBoxEcho = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Dialogue RS232 Nunchuck");

        jTextEtat.setEditable(false);
        jTextEtat.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jTextEtat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextEtatActionPerformed(evt);
            }
        });

        jTextAreaRX.setColumns(20);
        jTextAreaRX.setEditable(false);
        jTextAreaRX.setLineWrap(true);
        jTextAreaRX.setRows(5);
        jTextAreaRX.setDragEnabled(true);
        jScrollPane1.setViewportView(jTextAreaRX);

        jCBListPorts.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCBListPortsItemStateChanged(evt);
            }
        });
        jCBListPorts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBListPortsActionPerformed(evt);
            }
        });

        jTextToSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextToSendActionPerformed(evt);
            }
        });
        jTextToSend.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jTextToSendPropertyChange(evt);
            }
        });
        jTextToSend.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextToSendKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextToSendKeyTyped(evt);
            }
        });

        jButSend.setText("Envoyer");
        jButSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButSendActionPerformed(evt);
            }
        });

        jToggleButOpen.setText("Ouvrir");
        jToggleButOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButOpenActionPerformed(evt);
            }
        });

        jTextBaudRate.setText("9600");

        jLabel1.setText("Baudrate :");

        jLabel2.setText("Port :");

        jButClearTX.setText("Effacer");
        jButClearTX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButClearTXActionPerformed(evt);
            }
        });

        jButClearRX.setText("Effacer");
        jButClearRX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButClearRXActionPerformed(evt);
            }
        });

        jToggleButHexa.setText("Afficher en Hexadécimal");
        jToggleButHexa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButHexaActionPerformed(evt);
            }
        });

        jCheckBoxEcho.setSelected(true);
        jCheckBoxEcho.setText("Echo");
        jCheckBoxEcho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxEchoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCBListPorts, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(24, 24, 24)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextBaudRate, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(79, 79, 79)
                        .addComponent(jToggleButOpen)
                        .addGap(45, 45, 45))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButClearRX)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 132, Short.MAX_VALUE)
                        .addComponent(jToggleButHexa))
                    .addComponent(jTextToSend, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButClearTX)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 163, Short.MAX_VALUE)
                        .addComponent(jCheckBoxEcho)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButSend))
                    .addComponent(jTextEtat, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(29, 29, 29))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jCBListPorts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextBaudRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGap(10, 10, 10))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jToggleButOpen)
                        .addGap(18, 18, 18)))
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jScrollPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButClearRX)
                    .addComponent(jToggleButHexa))
                .addGap(18, 18, 18)
                .addComponent(jTextToSend, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButSend)
                    .addComponent(jButClearTX)
                    .addComponent(jCheckBoxEcho))
                .addGap(18, 18, 18)
                .addComponent(jTextEtat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextEtatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextEtatActionPerformed
    }//GEN-LAST:event_jTextEtatActionPerformed

    private void jCBListPortsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBListPortsActionPerformed
    }//GEN-LAST:event_jCBListPortsActionPerformed

    private void jTextToSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextToSendActionPerformed
    }//GEN-LAST:event_jTextToSendActionPerformed

    private void jToggleButOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButOpenActionPerformed
        if (jToggleButOpen.isSelected()) {
            openPort();
        } else {
            closePort();
        }
        actualise_fen();

    }//GEN-LAST:event_jToggleButOpenActionPerformed

    private void jCBListPortsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCBListPortsItemStateChanged
    }//GEN-LAST:event_jCBListPortsItemStateChanged

    private void jButSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButSendActionPerformed
        try {
            String tmp = new String(jTextToSend.getText());
            if (!(tmp.equals(""))) {
                if (!hexa) {
                    rs232.write(tmp);
                    if (jCheckBoxEcho.isSelected()) {
                        jTextAreaRX.setText(jTextAreaRX.getText() + tmp);
                    }
                } else {
                    try {
                        rs232.write(hexaToString(tmp));
                        if (jCheckBoxEcho.isSelected()) {
                            jTextAreaRX.setText(jTextAreaRX.getText() + formatHexa(tmp));
                        }
                        jTextToSend.setText(formatHexa(tmp));
                        jTextEtat.setText("Données envoyées");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Le format des données à envoyer n'est pas correct", "Damned", JOptionPane.ERROR_MESSAGE);
                        jTextEtat.setText("Damned: le format des données à envoyer n'est pas correct");
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Impossible d'écrire sur le port", "Damned", JOptionPane.ERROR_MESSAGE);
            jTextEtat.setText("Damned: Impossible d'écrire sur le port !");
        }
    }//GEN-LAST:event_jButSendActionPerformed

    private void jButClearRXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButClearRXActionPerformed
        jTextAreaRX.setText("");
    }//GEN-LAST:event_jButClearRXActionPerformed

    private void jButClearTXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButClearTXActionPerformed
        jTextToSend.setText("");
    }//GEN-LAST:event_jButClearTXActionPerformed

    private void jToggleButHexaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButHexaActionPerformed
        if (jToggleButHexa.isSelected()) {
            jTextEtat.setText("Mode Hexadécimal");
            jToggleButHexa.setText("Afficher en ASCII");
            hexa = true;
            jTextToSend.setText(stringToHexa(jTextToSend.getText()));
        } else {
            jTextEtat.setText("Mode ASCII");
            jToggleButHexa.setText("Afficher en Hexadécimal");
            hexa = false;
            try {
                jTextToSend.setText(hexaToString(jTextToSend.getText()));
            } catch (Exception ex) {
                jTextToSend.setText("");
            }
        }
    }//GEN-LAST:event_jToggleButHexaActionPerformed

    private void jTextToSendKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextToSendKeyTyped
    }//GEN-LAST:event_jTextToSendKeyTyped

    private void jTextToSendPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jTextToSendPropertyChange
    }//GEN-LAST:event_jTextToSendPropertyChange

    private void jTextToSendKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextToSendKeyPressed
    }//GEN-LAST:event_jTextToSendKeyPressed

    private void jCheckBoxEchoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxEchoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBoxEchoActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RS232Frame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButClearRX;
    private javax.swing.JButton jButClearTX;
    private javax.swing.JButton jButSend;
    private javax.swing.JComboBox jCBListPorts;
    private javax.swing.JCheckBox jCheckBoxEcho;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextArea jTextAreaRX;
    private javax.swing.JTextField jTextBaudRate;
    private javax.swing.JTextField jTextEtat;
    private javax.swing.JTextField jTextToSend;
    private javax.swing.JToggleButton jToggleButHexa;
    private javax.swing.JToggleButton jToggleButOpen;
    // End of variables declaration//GEN-END:variables
}
