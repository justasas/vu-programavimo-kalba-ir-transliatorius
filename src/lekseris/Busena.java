/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lekseris;

import java.util.Vector;

/**
 *
 * @author unknown
 */
public class Busena {
    public
        String vardas;
        Vector<Vector<String>> atvejai_simboliai = new Vector<Vector<String>>();
        Vector<String> atvejai_persoka_i = new Vector<String>();
            
    Busena(String vardas, Vector<Vector<String>> simboliai, Vector<String> persoka_i) {
        this.vardas = vardas;
        this.atvejai_simboliai = simboliai;
        this.atvejai_persoka_i = persoka_i;     
    }
}