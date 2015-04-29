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
import java.util.*;

// ProductionRule stores a pair of Nonterminal and ArrayList<Symbol>.

public class ProductionRule {
    private Nonterminal N;
    private ArrayList<Symbol> rule;
   
    public ProductionRule(Nonterminal NIn, ArrayList<Symbol> ruleIn) {
        N = NIn;
        rule = ruleIn;
    }
   
    public Nonterminal getNonterminal() { return N; }
    public ArrayList<Symbol> getRule() { return rule; }
   
    public void setNonterminal(Nonterminal NIn) { N = NIn; }
    public void setRule(ArrayList<Symbol> ruleIn) { rule = ruleIn; }
   
    @Override
    public String toString()
    {
        return N + " : " + rule;
    }
   
    @Override
    public boolean equals (Object otherObj) {
        try {
            ProductionRule PR2 = (ProductionRule) otherObj;

            return N.equals(PR2.getNonterminal()) && rule.equals(PR2.getRule());
        } catch (ClassCastException e) {
            return false;
        }
    }
   
    @Override
    public int hashCode() {
        return (N.toString() + rule.toString()).hashCode();
    }
}