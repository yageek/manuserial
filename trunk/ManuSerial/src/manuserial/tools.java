/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package manuserial;

/**
 *
 * @author thomas
 */
public class tools {

     public String stringToHexa(String str) {
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
}
