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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout.SequentialGroup;
import manuserial.RS232.RS232DataEvent;
import manuserial.RS232.RS232DataListener;
import java.io.IOException;
import java.util.StringTokenizer;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.JOptionPane;
import javax.swing.Timer;

interface HandlerTx
{
    public void handlerTx(String parTx);
    public void handlerTx(char [] parDatas, int parLength);
}

/**
 *
 * @author Emmanuel Roussel
 */
public class RS232Frame extends javax.swing.JFrame  implements HandlerTx {

    public RS232 rs232;
    private boolean hexa;
    private boolean portDispo;
    private String inBuffer;    //toujours en hexa

    private  int NB_MAX_CMD_LINE = 5;

    private manuserial.jPanelLineCmd jPanelLinesCmd[];

    ParallelGroup pLayoutGroupH;
    SequentialGroup pLayoutSeqGroupV;
    javax.swing.GroupLayout jPanel5Layout;

    public dataView datasView;
    public Format format;
    public Capteurs capteurs;

    private Timer TimerRefresh;

    /** Handler Serie : permet d'envoyer des données sur le port série depuis d'autres classes.
     *
     * @param parTx
     */
    public void handlerTx(String parTx)
    {
        System.out.println("Envois de "+parTx);
        try {
            rs232.write(parTx);
        } catch (IOException ex) {
            Logger.getLogger(RS232Frame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void handlerTx(char [] parDatas, int parLength)
    {
        int k = 0;
        for( k = 0; k< parLength ; k++)
        {
            try {
                rs232.write(parDatas[k]);
            } catch (IOException ex) {
                Logger.getLogger(RS232Frame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Timer actualisation des ports dispo">
    ActionListener ActualiseCom = new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
            if( !rs232.isOpen() )
                updateListPorts();
            actualise_fen();
        }
    };


    /** Creates new form RS232Frame */
    public RS232Frame() {
        initComponents();

        jPanelLinesCmd = new manuserial.jPanelLineCmd[NB_MAX_CMD_LINE];
        jPanelLinesCmd[0] = new manuserial.jPanelLineCmd("01 00 00 04", this);

        jPanel5.setPreferredSize(new java.awt.Dimension(569, 31));
        jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);

        pLayoutGroupH = jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING);
        ParallelGroup pLayoutGroupV = jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING);
        pLayoutSeqGroupV = jPanel5Layout.createSequentialGroup();

       pLayoutGroupH.addComponent(jPanelLinesCmd[0], javax.swing.GroupLayout.DEFAULT_SIZE, 565, Short.MAX_VALUE);
       pLayoutSeqGroupV.addComponent(jPanelLinesCmd[0], javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE);
       pLayoutSeqGroupV.addContainerGap();
       pLayoutSeqGroupV.addGap(5);

        jPanel5Layout.setHorizontalGroup(pLayoutGroupH);
        pLayoutGroupV.addGroup(pLayoutSeqGroupV);
        jPanel5Layout.setVerticalGroup(pLayoutGroupV);
        
        jPanel5.setPreferredSize(new java.awt.Dimension(569, 35));
        
        
        rs232 = new RS232();
        hexa = false;
        inBuffer = new String();

        if(updateListPorts()){
            jTextEtat.setText("Port de communication série détecté !");
        }else{
            jTextEtat.setText("Aucun port disponible n'a été détecté !");
        }

        jComboBoxBaudRates.setSelectedItem("115200");     //9600 baud sélectionné par défaut.


        format = new Format();
        capteurs = new Capteurs(this);

        jScrollPane1.setAutoscrolls(true);

        TimerRefresh = new Timer(1000, ActualiseCom);      //envoi evenement toutes les secondes
        TimerRefresh.start();

        // <editor-fold defaultstate="collapsed" desc="Exécuté lorsque donnée reçue">
        rs232.addRS232DataListener(new RS232DataListener() {

            public void dataAvailable(RS232DataEvent evt) {
                String tmpbuf = new String(rs232.getInBuffer());
                String toAdd = format.add(tmpbuf);
                jTextAreaRX.append(toAdd);

                if(jTextAreaRX.getText().length() > 0)
                    jTextAreaRX.setCaretPosition(jTextAreaRX.getText().length()-1);
                else
                    jTextAreaRX.setCaretPosition(0);

                if(capteurs.activated)
                {
                    if(toAdd.length() > 0)
                        capteurs.updateData(toAdd);
                }

                //getContentPane().add(jTextAreaRX);
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
                rs232.connect(jCBListPorts.getSelectedItem().toString(), Integer.valueOf(jComboBoxBaudRates.getSelectedItem().toString()).intValue());
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
            jComboBoxBaudRates.setEnabled(true);
            return false;
        }else{
            jToggleButOpen.setText("Fermer");
            jToggleButOpen.setSelected(true);
            jCBListPorts.setEnabled(false);
            jComboBoxBaudRates.setEnabled(false);
            return true;
        }
    }

    /** @return true si le port est ouvert ! */
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

        jPanel1 = new javax.swing.JPanel();
        jTextEtat = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaRX = new javax.swing.JTextArea();
        jButClearRX = new javax.swing.JButton();
        jToggleButHexa = new javax.swing.JToggleButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jCBListPorts = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jComboBoxBaudRates = new javax.swing.JComboBox();
        jToggleButOpen = new javax.swing.JToggleButton();
        jButtonAjouterLigne = new javax.swing.JButton();
        jButtonSupprimerLigne = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jMenuDataTable = new javax.swing.JMenuItem();
        jMenuItemFormat = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuItemCapteurs = new javax.swing.JMenuItem();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("RS232 Terminal");

        jTextEtat.setEditable(false);
        jTextEtat.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jTextEtat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextEtatActionPerformed(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTextAreaRX.setColumns(20);
        jTextAreaRX.setEditable(false);
        jTextAreaRX.setLineWrap(true);
        jTextAreaRX.setRows(5);
        jTextAreaRX.setDragEnabled(true);
        jScrollPane1.setViewportView(jTextAreaRX);

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

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(384, Short.MAX_VALUE)
                .addComponent(jButClearRX)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToggleButHexa))
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 613, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jToggleButHexa)
                    .addComponent(jButClearRX)))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setText("Port :");

        jCBListPorts.setEditable(true);
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

        jLabel1.setText("Baudrate :");

        jComboBoxBaudRates.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2400", "4800", "9600", "19200", "38400", "57600", "115200", "Autre" }));
        jComboBoxBaudRates.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxBaudRatesActionPerformed(evt);
            }
        });

        jToggleButOpen.setText("Ouvrir");
        jToggleButOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButOpenActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jCBListPorts, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxBaudRates, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jToggleButOpen)
                .addContainerGap(165, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jCBListPorts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel1)
                .addComponent(jComboBoxBaudRates, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jToggleButOpen)
                .addComponent(jLabel2))
        );

        jButtonAjouterLigne.setText("Ajouter");
        jButtonAjouterLigne.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAjouterLigneActionPerformed(evt);
            }
        });

        jButtonSupprimerLigne.setText("Supprimer");
        jButtonSupprimerLigne.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSupprimerLigneActionPerformed(evt);
            }
        });

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel5.setPreferredSize(new java.awt.Dimension(569, 80));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 613, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 76, Short.MAX_VALUE)
        );

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("tools");

        jMenuDataTable.setText("data Table");
        jMenuDataTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuDataTableActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuDataTable);

        jMenuItemFormat.setText("format");
        jMenuItemFormat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFormatActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItemFormat);
        jMenu2.add(jSeparator1);

        jMenuItemCapteurs.setText("Capteurs");
        jMenuItemCapteurs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCapteursActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItemCapteurs);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTextEtat, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 617, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jButtonAjouterLigne)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonSupprimerLigne)
                .addContainerGap(474, Short.MAX_VALUE))
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 617, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonAjouterLigne)
                    .addComponent(jButtonSupprimerLigne))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextEtat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextEtatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextEtatActionPerformed
    }//GEN-LAST:event_jTextEtatActionPerformed

    private void jCBListPortsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBListPortsActionPerformed
    }//GEN-LAST:event_jCBListPortsActionPerformed

    private void jToggleButOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButOpenActionPerformed
        if ( jToggleButOpen.isSelected() ) {
            openPort();
        } else {
            closePort();
        }
        actualise_fen();

    }//GEN-LAST:event_jToggleButOpenActionPerformed

    private void jCBListPortsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCBListPortsItemStateChanged
    }//GEN-LAST:event_jCBListPortsItemStateChanged

    private void jButClearRXActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButClearRXActionPerformed
        jTextAreaRX.setText("");
        format.reinit();
    }//GEN-LAST:event_jButClearRXActionPerformed

    private void jToggleButHexaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButHexaActionPerformed
        if (jToggleButHexa.isSelected()) {
            jTextEtat.setText("Mode Hexadécimal");
            jToggleButHexa.setText("Afficher en ASCII");
            hexa = true;
 //           jTextToSend.setText(stringToHexa(jTextToSend.getText()));
        } else {
            jTextEtat.setText("Mode ASCII");
            jToggleButHexa.setText("Afficher en Hexadécimal");
            hexa = false;
            try {
   //             jTextToSend.setText(hexaToString(jTextToSend.getText()));
            } catch (Exception ex) {
   //             jTextToSend.setText("");
            }
        }
    }//GEN-LAST:event_jToggleButHexaActionPerformed

    private int nbCmdLines = 1;

    private void jButtonAjouterLigneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAjouterLigneActionPerformed
        // TODO add your handling code here:
        if(nbCmdLines < NB_MAX_CMD_LINE )
        {
            jPanel5.setPreferredSize(new java.awt.Dimension(569, (nbCmdLines + 1)*31));

            jPanelLinesCmd[nbCmdLines] = new jPanelLineCmd("Cmd "+nbCmdLines, this);

            pLayoutGroupH.addComponent(jPanelLinesCmd[nbCmdLines], javax.swing.GroupLayout.DEFAULT_SIZE, 565, Short.MAX_VALUE);
            pLayoutSeqGroupV.addComponent(jPanelLinesCmd[nbCmdLines], javax.swing.GroupLayout.DEFAULT_SIZE, 565, Short.MAX_VALUE);

            nbCmdLines++;

            jPanel5.revalidate();
            jPanel5.repaint();

        }
        else
        {
            jTextEtat.setText("Nb maximal de lignes atteind");
        }
    }//GEN-LAST:event_jButtonAjouterLigneActionPerformed

    private void jButtonSupprimerLigneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSupprimerLigneActionPerformed
        // TODO add your handling code here:
        if(nbCmdLines > 1)
        {
            nbCmdLines--;

          //  jPanel5Layout.removeLayoutComponent(jPanelLinesCmd[nbCmdLines]);
            jPanel5.remove(jPanelLinesCmd[nbCmdLines]);
            jPanel5.setPreferredSize(new java.awt.Dimension(569, (nbCmdLines)*31));

            jPanel5.revalidate();
            jPanel5.repaint();
        }

    }//GEN-LAST:event_jButtonSupprimerLigneActionPerformed

    private void jComboBoxBaudRatesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxBaudRatesActionPerformed
        while(jComboBoxBaudRates.getSelectedItem().equals("Autre")){
            String newBaudRate = JOptionPane.showInputDialog(null, "Entrez une nouvelle valeur de baud rate : ", "Baud rate", JOptionPane.QUESTION_MESSAGE);
            if(newBaudRate==null)   return;     //si clic sur annuler => tchao

            try {   //essaye d'ajouter l'élément à la liste
                int i_tmp = Integer.valueOf(newBaudRate).intValue();
                String s_tmp = String.valueOf(i_tmp);
                jComboBoxBaudRates.addItem(makeObj(s_tmp));   //on ajoute l'élément à la liste
                jComboBoxBaudRates.setSelectedIndex(jComboBoxBaudRates.getItemCount()-1);   //on sélectionne la nouvelle entrée.
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Entrée invalide !", "Damned", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jComboBoxBaudRatesActionPerformed

    private void jMenuDataTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuDataTableActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_jMenuDataTableActionPerformed

    private void jMenuItemFormatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFormatActionPerformed
        // TODO add your handling code here:
        format.setVisible(true);

    }//GEN-LAST:event_jMenuItemFormatActionPerformed

    private void jMenuItemCapteursActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCapteursActionPerformed
        // TODO add your handling code here:
        capteurs.setVisible(true);
    }//GEN-LAST:event_jMenuItemCapteursActionPerformed

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
    private javax.swing.JButton jButtonAjouterLigne;
    private javax.swing.JButton jButtonSupprimerLigne;
    private javax.swing.JComboBox jCBListPorts;
    private javax.swing.JComboBox jComboBoxBaudRates;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuDataTable;
    private javax.swing.JMenuItem jMenuItemCapteurs;
    private javax.swing.JMenuItem jMenuItemFormat;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextArea jTextAreaRX;
    private javax.swing.JTextField jTextEtat;
    private javax.swing.JToggleButton jToggleButHexa;
    private javax.swing.JToggleButton jToggleButOpen;
    // End of variables declaration//GEN-END:variables
}
