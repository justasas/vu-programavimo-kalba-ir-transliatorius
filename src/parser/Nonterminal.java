/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package parser;

/**
 *
 * @author unknown
 */
public class Nonterminal extends Symbol {
   private final String type= "Nonterminal";
    // NOTE: Pass nameIn in the form "<exp>" rather than "exp",
    // just as a matter of convention.
    public Nonterminal(String nameIn) { super(nameIn); }
    public String getType(){ return type;}
   
    @Override
    public boolean equals (Object otherObj) {
        try {
            Nonterminal T2 = (Nonterminal) otherObj;
            return (super.getName().equals(T2.getName()));
        } catch (ClassCastException e) {
            return false;
        }
    }
    
    @Override
    public int hashCode () {
        return super.hashCode();
    }    
}