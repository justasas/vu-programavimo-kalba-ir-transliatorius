/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

/**
 *
 * @author unknown
 */
public class ParserMain {
    ArrayList<ProductionRule> used_rules = new ArrayList<ProductionRule>();
    private int last_rule_index;
    private Symbol last_rule;
    private Nonterminal last_rule_left_side;
    
    public void parse(ArrayList<Token> tokens, ArrayList<String> lexems) throws Exception
    {
        ParserGenerator parserGen = new ParserGenerator();
        parserGen.feed("gramatika.txt", "leksemos.txt");

        PrintWriter parse_tree_writer = new PrintWriter("parseTree.txt", "UTF-8");

        //System.out.println("---------------------------------------------------");
        //System.out.println("---------------------------------------------------");
        //System.out.println("---------------------------------------------------");
        //System.out.println("---------------------------------------------------");
        
        ParsingTable parseTable = parserGen.buildParsingTable();
        //parseTable.printHTMLTable();
     
        int i = 0;
        Stack<Symbol> stack = new Stack<Symbol>();
        stack.push(new Token("$"));
        // push start symbol onto the stack
        stack.push(new Nonterminal(parserGen.getStartSymbol().getName()));

        while (!stack.peek().equals(new Token("$")) && i < tokens.size()) {
        //while (i < 2) {            
 //           System.out.println("Current token: " + tokens.get(i));
            
 //           System.out.println("stack: " + stack);
            if (stack.peek().getName().contains("</"))
            {
                parse_tree_writer.print(stack.peek().getName());
                stack.pop();
            }
            // case 1: top of stack is a token
            else if (stack.peek() instanceof Token) {
 //               System.out.println("Found token " + stack.peek() + " at top of stack.");
                // special case: token is epsilon: just pop and discard
                if (stack.peek().equals(new Token("EPSILON"))) {
                    parse_tree_writer.print("<nothing/>");
 //                   System.out.println("Found EPSILON and consumed it.");
                    stack.pop();
                }
                else {
                    // try to match token at top of stack with current input token
                    if (stack.peek().equals(tokens.get(i))) {
 //                       System.out.println("Matched token " + stack.peek() + " with top of stack.");
                        if (tokens.get(i).toString().contains("<identifier>") || 
                                tokens.get(i).toString().contains("<int>") || 
                                tokens.get(i).toString().contains("const") || 
                                tokens.get(i).toString().contains("<float>") ||
                                        tokens.get(i).toString().contains("<string>") || 
                                        tokens.get(i).toString().contains("<char>") ||
                                        tokens.get(i).toString().contains("<bool>"))
                        {
                            parse_tree_writer.print(tokens.get(i).toString().replace(">", " attribute=\""));
                            parse_tree_writer.print(lexems.get(i)+"\"/>");
                        } else {
                            parse_tree_writer.print(tokens.get(i).toString().replace(">", "/>"));
                        }
                        stack.pop();
                        i++;
                    }
                    // tokens don't match, so parsing fails
                    else {
                        break;
                    }
                }
            }
            // case 2: top of stack is a nonterminal
            // we have a nonterminal on top of the stack, so we expand it using
            // the parse table to push the next production onto the stack
            else if (stack.peek() instanceof Nonterminal) {
 //               System.out.println("Found nonterminal " + stack.peek() + " at top of stack.");
                parse_tree_writer.print(stack.peek());
                if (last_rule == stack.peek())
                {
                    //parse_tree_writer.println(last_rule_left_side.toString().replaceFirst("<", "</"));
                }
                //parse_tree_writer.println("nonterminal:  "+stack.peek());
                ProductionRule rule = parseTable.getEntry((Nonterminal)stack.peek(), tokens.get(i));
                last_rule_left_side = (Nonterminal)stack.peek();
                last_rule_index = rule.getRule().size();
                last_rule = rule.getRule().get(last_rule_index-1);
   //             System.out.println("Next production is " + rule);
                used_rules.add(rule);
                //for (int m = 0; m < rule.getRule().size(); m++)
                //{
                //    parse_tree_writer.print(rule.getRule().get(m));
                //}
                // if parse table entry is empty, then ERROR
                if (rule == null) break;
                String symbol_name = stack.peek().getName();
                stack.pop();
                stack.push(new Symbol(symbol_name.replace("<", "</")));
                ArrayList<Symbol> symbols = rule.getRule();
                for (int x = symbols.size() - 1; x >= 0; x--)
                    stack.push(symbols.get(x));
            }
        }
        // we were able to consume the whole input, and we
        // have a $ at the top of the stack -- accept!
        if (stack.peek().equals(new Token("$")) && i == tokens.size()-1)
            System.out.println("Successful parse!");
        else
            System.out.println("Parse error!");
        //add_end_tags_to_treee();
        parse_tree_writer.close();
    }
}