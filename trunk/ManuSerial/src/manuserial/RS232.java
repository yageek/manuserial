/*
 * RS232.java
 * Exploite la bibliothèque RXTX pour fournir les fonctions de base pour le dialogue au format rs232 avec un périphérique
 *
 * Created on 8 oct. 2009, 08:22:27
 */

package manuserial;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Vector;
import javax.swing.JOptionPane;

/**
 *
 * @author Emmanuel Roussel
 */



public class RS232{

    protected OutputStream out;
    protected InputStream in;
    protected String inBuffer;
    protected String tmp;    //stocke temporairement le buffer d'entrée
    protected SerialPort serialPort;
    protected boolean open;
    protected Thread thReader;
    protected SerialReader serialReader;
    protected RS232DataListener dataListener = null;


    /**
     * Constructeur de la classe.
     */
    public RS232() {
        super();
        inBuffer = new String();
        tmp = new String();
    }


    /**
     *
     * @param portName  le nom du port (ex : COM9)
     * @param baudrate  La vitesse de communication (ex : 9600)
     * @throws NoSuchPortException
     * @throws PortInUseException
     * @throws UnsupportedCommOperationException
     * @throws IOException
     */
    public void connect(String portName, int baudrate) throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException, IOException {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if (portIdentifier.isCurrentlyOwned()) {
            throw new PortInUseException();
        } else {
            CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);

            if (commPort instanceof SerialPort) {
                serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(baudrate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

                in = serialPort.getInputStream();
                out = serialPort.getOutputStream();

                serialReader = new SerialReader(in);
                thReader = new Thread(serialReader);
                thReader.start();
                // (new Thread(new SerialWriter(out))).start();

                open=true;
            } else {
                System.out.println("Damned: Port série invalide");
            }
        }
    }

    /**
     * ferme le port et les communications
     */
    public void close(){
        try {
            serialReader.close();
            thReader.interrupt();
            out.close();
            in.close();
        } catch (IOException ex) {
        }
        serialPort.close();
        open=false;
    }


    /**
     * Vérifie si le port est ouvert.
     * @return true si le port est ouvert.
     */
    public boolean isOpen() {
        return open;
    }



    /**
     * Retourne un tableau de String contenant la liste des ports disponibles sur l'ordinateur
     * @return la liste des ports
     */
    public String[] listPorts() {
        //on récupère les ports
        Enumeration e = CommPortIdentifier.getPortIdentifiers();
        Vector<String> v = new Vector<String>();

        //on sauve les ports série uniquement
        while (e.hasMoreElements()) {
            CommPortIdentifier cpi = (CommPortIdentifier) e.nextElement();
            int type = cpi.getPortType();
            switch (type) {
                case CommPortIdentifier.PORT_SERIAL:
                    v.add(cpi.getName());
            }
        }

        //on supprime les ports déja sauvés
        for (int a = 0; a < v.size(); a++) {
            String pca = v.get(a);
            for (int b = 0; b < v.size(); b++) {
                String pcb = v.get(b);
                if (b != a)
                    if (pca.equals(pcb))
                        v.remove(b);
            }
        }

        v.trimToSize();

        //on stocke tout ca dans un tableau de String
        String[] ports = new String[v.size()];
        for (int a = 0; a < v.size(); a++) {
            ports[a] = v.get(a);
        }

        return ports;
    }


    /**
     * retourne le port qui est utilisé
     * @return le port actuellement ouvert
     */
    public String getCurrentPort(){
        return serialPort.getName().substring(4);
    }

    /**
     * Retourne le port par défaut de l'ordinateur, en fonction du système d'exploitation détecté.
     * @return port par défaut
     */
    public String getDefaultPort() {    //determine the name of the serial port on several operating systems
        String osname = System.getProperty("os.name", "").toLowerCase();
        if (osname.startsWith("windows")) {         // windows
            return "COM1";
        } else if (osname.startsWith("linux")) {    // linux
            return "/dev/ttyS0";
        } else if (osname.startsWith("mac")) {  // mac
            return "????";
        } else {
            return "L'OS n'est pas supporté ...";
        }
    }





    /**
     * Ecrit un caractère sur le port série
     * @param c le caractère à envoyer
     * @throws IOException
     */
    public void write(char c) throws IOException {
        this.out.write(c);
    }

    /**
     * Ecrit une chaine de caractère sur le port série
     * @param str   la chaine à envoyer
     * @throws IOException
     */
    public void write(String str) throws IOException {
        for (int i = 0; i < str.length(); i++) {
            this.out.write(str.charAt(i));
        }
    }

    /**
     * retourne le buffer d'entrée et le vide
     * @return le contenu du buffer d'entrée
     */
    public String getInBuffer() {
        tmp = inBuffer;
        inBuffer = "";
        return tmp;
    }






    /**
     *
     */
    public class RS232DataEvent extends EventObject {
	public RS232DataEvent (Object o) {
		super(o);
	}
    }

    /**
     *
     */
    public interface RS232DataListener {
	public void dataAvailable (RS232DataEvent de);
    }

    /**
     *
     * @param dl
     */
    public void addRS232DataListener (RS232DataListener dl) {
	this.dataListener = dl;
    }




    /**
     * Le thread pour la réception de données par le port série
     */
    public class SerialReader implements Runnable {
        InputStream in;
        boolean continuer;

        public SerialReader(InputStream in) {
            this.in = in;
            continuer=true;
        }

        public void run() {
            byte[] buffer = new byte[1];

            try {
                while(true){
                    if(this.in.read(buffer, 0, 1)==1){
                        inBuffer +=  (char)(buffer[0]&0xff);
                        dataListener.dataAvailable(new RS232DataEvent(this));
                    }
                    if(continuer == false)
                        return;
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Erreur lors de la lecture des données sur le port", "Damned", JOptionPane.ERROR_MESSAGE);
            }
        }

        public void close(){
            continuer = false;
        }
    }





    /**
     * Permet un test de la classe.
     * @param args arguments entrés en ligne de commande (non utilisé)
     */
    @SuppressWarnings({"static-access"})
    public static void main(String[] args) {
        RS232 rs232 = new RS232();
        String buf = "";

        String[] liste = rs232.listPorts();
        System.out.print("Liste des ports disponibles :");
        for(int i=0; i<liste.length; i++){
            System.out.print(liste[i]);
        }
        System.out.println("");

        try {
            rs232.connect(liste[0], 9600);
            System.out.println("Connecté au port "+liste[0]);
        } catch (PortInUseException e) {
            System.out.println("Damned: Le port est en cours d'utilisation par un autre programme : " + e);
            return;
        } catch (IOException e) {
            System.out.println("Damned: erreur d'IO : " + e);
            return;
        } catch (UnsupportedCommOperationException e) {
            System.out.println("Damned: opération non supportée : " + e);
            return;
        } catch (NoSuchPortException e) {
            System.out.println("Damned: le port n'existe pas : " + e);
            return;
        } catch (ArrayIndexOutOfBoundsException e){
            System.out.println("Damned: pas de port série détecté : " + e);
            return;
        }


        for(int i=0; i<10; i++) {
            try {
                rs232.write("bonjour");
            } catch (IOException e) {
                System.out.println("Damned: Impossible d'écrire sur le port ! : "+e);
                return;
            }
            buf = new String(rs232.getInBuffer());
            if (!buf.equals("")) {
                System.out.print(buf);
            }
        }
        
        System.out.println(rs232.getCurrentPort());
        rs232.close();

    }
}
