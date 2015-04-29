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
public class Token extends Symbol {
    private final String type= "Token";
   
    public Token(String nameIn) { super(nameIn);}
    public String getType(){return type;}
   
    @Override
    public boolean equals (Object otherObj) {
        /**if(!(otherObj instanceof Token))
        {
                return false;
        }**/
        try {
            Token T2 = (Token) otherObj;
            return (super.getName().equals(T2.getName()));
        } catch (ClassCastException e) {
            return false;
        }
    }
}
