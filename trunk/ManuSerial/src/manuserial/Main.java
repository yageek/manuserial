package manuserial;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 * Classe principale
 * @author Emmanuel Roussel
 * @version 1.0, 08/10/2009
 */
public class Main {

    /**
     * @param args arguments entrés en ligne de commande
     */
    public static void main(String[] args) {
        final RS232Frame rs232Frame = new RS232Frame();
        rs232Frame.setVisible(true);


        // <editor-fold defaultstate="collapsed" desc="Timer actualisation des ports dispo">
        ActionListener ActualiseCom = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                rs232Frame.updateListPorts();
                rs232Frame.actualise_fen();
            }
        };
        new Timer(1000, ActualiseCom).start();      //envoi evenement toutes les secondes
        // </editor-fold>
    }

}
