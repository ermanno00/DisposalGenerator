/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package disposalGenerator.configuration;

import java.util.Comparator;

/**
 *
 * @author erman
 */
public class Utils {
    
    public static Comparator<Object>  getComparator(){
        return new java.util.Comparator<Object>() {
            /**
             * Custom compare to sort numbers as numbers.
             * Strings as strings, with numbers ordered before strings.
             *
             * @param oo1
             * @param oo2
             * @return
             */
            @Override
            public int compare(Object oo1, Object oo2) {
                String s1 = (String) oo1;
                String s2 = (String) oo2;
                try {

                    return Double.valueOf(s1).compareTo(Double.valueOf(s2));
                }catch(NumberFormatException e){

                    return s1.compareTo(s2);

                }
            }
        };
    }
    
}
