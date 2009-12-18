package manuserial;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Format.java
 *
 * Created on 6 nov. 2009, 15:17:19
 */

/**
 *
 * @author thomas
 */
public class Format extends javax.swing.JFrame {

    public final boolean HEX = true;
    public final boolean TEXT = false;

    public static final Integer F_ASCII = 0;
    public static final Integer F_HEX = 1;
    public static final Integer F_DEC = 2;


    public Boolean timeEnable;
    public Integer timeIntervalle;

    public Boolean startTxtEnable = false;
    public Boolean startType = false;
    public String startTxt = "";
    public Byte[] startHex;

    public Boolean endCntEnable = false;
    public Integer endCnt = Integer.MAX_VALUE;
    public Boolean endTxtEnable = false;
    public Boolean endType = TEXT;
    public String endTxt = "";
    public Byte[] endHex;

    public Integer formatSelected = F_ASCII;

    private Boolean timeOut = false;
    private Timer delay;
    private String buffer;

    private Boolean started = false;

    ActionListener delayRoutine = new ActionListener()
    {

        public void actionPerformed(ActionEvent e) {
            timeOut = true;
         //   System.out.println("Time Out");
        }
        
    };



    /** Creates new form Format */
    public Format() {
        initComponents();
        delay = new Timer(100, delayRoutine);

      //  this.jCheckBoxTimeGroup.setSelected(true);
        this.jCheckBoxStart.setSelected(true);
     //   this.jCheckBoxEndTxt.setSelected(true);
        this.jTextFieldStart.setText("01");
     //   this.jTextFieldEndTxt.setText("04");
        this.jCheckBoxEndLength.setSelected(true);
        this.jTextFieldEndLength.setText("17");

        this.jRadioButtonHex.setSelected(true);
        updateDelay();
        updateStart();
        updateEnd();
        updateFormat();
    }

    public void reinit()
    {
        buffer = "";
        this.started = false;
        
    }

    public String add(String parInStr)
    {
        String txtTemp = "";
        int start = 0, end = 0;
        boolean loop;

        switch(formatSelected)
        {
            case 0 : buffer += parInStr;
                break;
            case 1 : buffer += stringToHexa(parInStr);
                break;
            case 2 : buffer += stringToDec(parInStr);
                break;
            default: buffer += parInStr;
        }


        /* Time Out */
       

        if( this.endTxtEnable | this.endCntEnable | this.startTxtEnable | timeEnable)
        {
            do
            {
                System.out.println(buffer);
                loop = false;

                if( this.started )
                {
                    if( this.endTxtEnable )
                    {

                        if( ( end = this.buffer.indexOf(endTxt) ) > 0 )
                        {
                            end += endTxt.length();

                            loop = true;

                            txtTemp += this.buffer.substring(start, end ) + "\n";

                            if(this.buffer.length() > end )
                                this.buffer = this.buffer.substring(end+1);
                            else
                                this.buffer = "";

                            this.started = false;
                        }
                    }
                    if( endCntEnable )
                    {
                        if( this.buffer.length() > this.endCnt )
                        {
                            txtTemp += this.buffer.substring(0, this.endCnt) + "\n";
                            if(this.buffer.length() > end )
                                this.buffer = this.buffer.substring(endCnt+1);
                            else
                                this.buffer = "";

                            this.started = false;
                        }
                    }
                }
                else
                {
                    if( startTxtEnable )
                    {
                        if ( (start = buffer.indexOf(startTxt)) > -1 )
                        {
                            loop = true;
                            this.started = true;

                            if(start > 0)  //si le mot clef n'est pas en premiere position
                            {
                                System.out.println(this.buffer + " :: " + start +" \n");
                                txtTemp += "\t unexpected : " + this.buffer.substring(0, start) + "\n";
                                this.buffer = this.buffer.substring(start);
                            }
                        }
                        else if( this.buffer.length() > this.startTxt.length() )
                        {
                            txtTemp += this.buffer.substring(0, this.buffer.length() - this.startTxt.length());
                            this.buffer = this.buffer.substring(this.buffer.length() - this.startTxt.length());
                        }
                    }
                }
            } while(loop);

            if(this.timeEnable)
            {
                if(timeOut)
                {
                    timeOut = false;
                    delay.restart();
                    txtTemp += "\n";
                }
                txtTemp += this.buffer;
                this.buffer = "";
            }

        }// end if detection.
        else
        {
            if( this.buffer.length() > 0 )
            {
                txtTemp += this.buffer;
                this.buffer = "";
            }
        }

       
        return txtTemp;
    }

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

      private String stringToDec(String str) {

        StringBuffer buf = new StringBuffer("");

        for (int i = 0; i < str.length(); i++) {
            int tmp = str.charAt(i);
            buf.append( Integer.toString(tmp)+" " );
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jCheckBoxTimeGroup = new javax.swing.JCheckBox();
        jTextFieldIntervalle = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jCheckBoxStart = new javax.swing.JCheckBox();
        jTextFieldStart = new javax.swing.JTextField();
        jToggleButtonHexStart = new javax.swing.JToggleButton();
        jPanel3 = new javax.swing.JPanel();
        jCheckBoxEndLength = new javax.swing.JCheckBox();
        jCheckBoxEndTxt = new javax.swing.JCheckBox();
        jTextFieldEndTxt = new javax.swing.JTextField();
        jTextFieldEndLength = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jToggleButtonHexEnd = new javax.swing.JToggleButton();
        jPanel4 = new javax.swing.JPanel();
        jRadioButtonASCII = new javax.swing.JRadioButton();
        jRadioButtonHex = new javax.swing.JRadioButton();
        jRadioButtonDEC = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();

        setTitle("Formatage des données reçues");
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                formComponentHidden(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jCheckBoxTimeGroup.setText("regrouper les données si intervalle supérieur à ");
        jCheckBoxTimeGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxTimeGroupActionPerformed(evt);
            }
        });

        jTextFieldIntervalle.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldIntervalle.setText("100");
        jTextFieldIntervalle.setToolTipText("données regroupées si délais suppérieur à, en millisecondes");
        jTextFieldIntervalle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldIntervalleActionPerformed(evt);
            }
        });
        jTextFieldIntervalle.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldIntervalleKeyTyped(evt);
            }
        });

        jLabel1.setText("ms");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBoxTimeGroup)
                .addGap(18, 18, 18)
                .addComponent(jTextFieldIntervalle, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addContainerGap(163, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldIntervalle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jCheckBoxTimeGroup))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jCheckBoxStart.setText("commencer une ligne sur reception de ");
        jCheckBoxStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxStartActionPerformed(evt);
            }
        });

        jTextFieldStart.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldStart.setText("start");
        jTextFieldStart.setToolTipText("données regroupées si délais suppérieur à, en millisecondes");

        jToggleButtonHexStart.setText("hex");
        jToggleButtonHexStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHexStartActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBoxStart)
                .addGap(24, 24, 24)
                .addComponent(jTextFieldStart, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jToggleButtonHexStart)
                .addContainerGap(124, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBoxStart)
                    .addComponent(jTextFieldStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToggleButtonHexStart))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jCheckBoxEndLength.setText("Arreter apres reception de");
        jCheckBoxEndLength.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxEndLengthActionPerformed(evt);
            }
        });

        jCheckBoxEndTxt.setText("Arreter après reception de");
        jCheckBoxEndTxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxEndTxtActionPerformed(evt);
            }
        });

        jTextFieldEndTxt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldEndTxt.setText("end");
        jTextFieldEndTxt.setToolTipText("données regroupées si délais suppérieur à, en millisecondes");
        jTextFieldEndTxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldEndTxtActionPerformed(evt);
            }
        });

        jTextFieldEndLength.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextFieldEndLength.setText("10");
        jTextFieldEndLength.setToolTipText("données regroupées si délais suppérieur à, en millisecondes");
        jTextFieldEndLength.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldEndLengthActionPerformed(evt);
            }
        });

        jLabel2.setText("caractères");

        jToggleButtonHexEnd.setText("hex");
        jToggleButtonHexEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonHexEndActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jCheckBoxEndLength)
                        .addGap(18, 18, 18)
                        .addComponent(jTextFieldEndLength, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel2))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jCheckBoxEndTxt)
                        .addGap(18, 18, 18)
                        .addComponent(jTextFieldEndTxt)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToggleButtonHexEnd)
                .addContainerGap(188, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBoxEndLength)
                    .addComponent(jTextFieldEndLength, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBoxEndTxt)
                    .addComponent(jTextFieldEndTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jToggleButtonHexEnd))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        buttonGroup1.add(jRadioButtonASCII);
        jRadioButtonASCII.setSelected(true);
        jRadioButtonASCII.setText("ASCII");
        jRadioButtonASCII.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonASCIIActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButtonHex);
        jRadioButtonHex.setText("HEX");

        buttonGroup1.add(jRadioButtonDEC);
        jRadioButtonDEC.setText("DEC");

        jLabel3.setText("Format des données recues : ");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonASCII)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButtonHex)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButtonDEC)
                .addContainerGap(225, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButtonASCII)
                    .addComponent(jRadioButtonHex)
                    .addComponent(jRadioButtonDEC)
                    .addComponent(jLabel3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBoxTimeGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxTimeGroupActionPerformed
        // TODO add your handling code here:
        updateDelay();
    }//GEN-LAST:event_jCheckBoxTimeGroupActionPerformed

    @SuppressWarnings("static-access")
    private void jCheckBoxStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxStartActionPerformed
        // TODO add your handling code here:
        updateStart();
    }//GEN-LAST:event_jCheckBoxStartActionPerformed

    private void jTextFieldIntervalleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldIntervalleActionPerformed
        // TODO add your handling code here:
        updateDelay();
    }//GEN-LAST:event_jTextFieldIntervalleActionPerformed

    private void jTextFieldIntervalleKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldIntervalleKeyTyped
        // TODO add your handling code here:
}//GEN-LAST:event_jTextFieldIntervalleKeyTyped

    private void jToggleButtonHexStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonHexStartActionPerformed
        // TODO add your handling code here:
        updateStart();
    }//GEN-LAST:event_jToggleButtonHexStartActionPerformed

    private void jCheckBoxEndLengthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxEndLengthActionPerformed
        // TODO add your handling code here:
        updateEnd();
    }//GEN-LAST:event_jCheckBoxEndLengthActionPerformed

    private void jTextFieldEndLengthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldEndLengthActionPerformed
        // TODO add your handling code here:
        updateEnd();
    }//GEN-LAST:event_jTextFieldEndLengthActionPerformed

    private void jCheckBoxEndTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxEndTxtActionPerformed
        // TODO add your handling code here:
        updateEnd();
    }//GEN-LAST:event_jCheckBoxEndTxtActionPerformed

    private void jToggleButtonHexEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonHexEndActionPerformed
        // TODO add your handling code here:
        updateEnd();
    }//GEN-LAST:event_jToggleButtonHexEndActionPerformed

    private void jTextFieldEndTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldEndTxtActionPerformed
        // TODO add your handling code here:
        updateEnd();
    }//GEN-LAST:event_jTextFieldEndTxtActionPerformed

    private void formComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentHidden
        // TODO add your handling code here:
        updateFormat();
        updateDelay();
        updateStart();
        updateEnd();
    }//GEN-LAST:event_formComponentHidden

    private void jRadioButtonASCIIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonASCIIActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButtonASCIIActionPerformed

    private void updateDelay()
    {
        System.out.println( "***** Update Delay ******" );

        this.timeEnable = this.jCheckBoxTimeGroup.isSelected();
        System.out.println( "Delay enable : "+ this.timeEnable.toString() );
        this.timeIntervalle = Integer.parseInt( this.jTextFieldIntervalle.getText() );
        System.out.println( "Delay periode : "+ this.timeIntervalle.toString() );

        if(this.timeEnable)
        {
            this.timeOut = false;
            delay.setDelay(this.timeIntervalle);
            delay.start();
        }
        else
        {
            delay.stop();
            this.timeOut = false;
        }
    }


    private void updateStart()
    {
        System.out.println( "***** Update Start ******" );

        this.startTxtEnable = this.jCheckBoxStart.isSelected();
        System.out.println( "Start enable : "+ this.startTxtEnable.toString() );

        this.startType = this.jToggleButtonHexStart.isSelected();
        if( this.startType == this.HEX)
        {
         //   startByte = jTextFieldStart
        }
        else
        {
            this.startTxt = this.jTextFieldStart.getText();
            System.out.println( "Start text : "+ this.startTxt );
        }
    }

    private void updateEnd()
    {
        System.out.println( "***** Update End ******" );

        this.endTxtEnable = jCheckBoxEndTxt.isSelected();
        System.out.println( "End enable : "+ this.endTxtEnable.toString() );

        this.endType = jToggleButtonHexEnd.isSelected();
        if( this.endType == this.HEX)
        {
         //   startByte = jTextFieldStart
        }
        else
        {
            this.endTxt = this.jTextFieldEndTxt.getText();
            System.out.println( "End text : "+ this.endTxt );
        }

        this.endCntEnable = jCheckBoxEndLength.isSelected();
        System.out.println( "End count enable : "+ this.endCntEnable.toString() );

        this.endCnt = Integer.parseInt(this.jTextFieldEndLength.getText());
        System.out.println( "End count : "+ this.endCnt.toString() );

    }

    private void updateFormat()
    {
        if(this.jRadioButtonASCII.isSelected())
            formatSelected = F_ASCII;
        else if(this.jRadioButtonHex.isSelected())
            formatSelected = F_HEX;
        else if(this.jRadioButtonDEC.isSelected())
            formatSelected = F_DEC;
        else
            formatSelected = F_ASCII;
    }

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Format().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JCheckBox jCheckBoxEndLength;
    private javax.swing.JCheckBox jCheckBoxEndTxt;
    private javax.swing.JCheckBox jCheckBoxStart;
    private javax.swing.JCheckBox jCheckBoxTimeGroup;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JRadioButton jRadioButtonASCII;
    private javax.swing.JRadioButton jRadioButtonDEC;
    private javax.swing.JRadioButton jRadioButtonHex;
    private javax.swing.JTextField jTextFieldEndLength;
    private javax.swing.JTextField jTextFieldEndTxt;
    private javax.swing.JTextField jTextFieldIntervalle;
    private javax.swing.JTextField jTextFieldStart;
    private javax.swing.JToggleButton jToggleButtonHexEnd;
    private javax.swing.JToggleButton jToggleButtonHexStart;
    // End of variables declaration//GEN-END:variables

}
