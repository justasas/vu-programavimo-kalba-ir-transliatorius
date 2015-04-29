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
public class Symbol{
    private String name;
   
    ///A static instance of symbol that represents Epsilon
    public static final Symbol EPSILON = new Symbol("EPSILON");
   
    public Symbol(String nameIn) { name = nameIn.trim(); }
   
    public String getName() { return name; }
    public void setName(String nameIn) { name = nameIn.trim(); }
   
    @Override
    public boolean equals (Object otherObj) {
        Symbol S2 = (Symbol) otherObj;
        return (S2.getName().equals(this.getName()));
    }

    @Override
    public int hashCode () {
        return this.getName().hashCode();
    }

    @Override
    public String toString()
    {
        return name;
    }
}