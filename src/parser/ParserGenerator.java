/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

/**
 *
 * @author unknown
 */
public class ParserGenerator {

    private ArrayList<Nonterminal> nonterminalList;
    private ArrayList<Token> tokenList;
    private Symbol startSymbol;
    private ArrayList<ProductionRule> rules;
    private HashMap<Nonterminal, ArrayList<ProductionRule>> allRules;
    private HashMap<Nonterminal, ArrayList<Token>> firstSets;
    private HashMap<Nonterminal, ArrayList<Token>> followSets;
    private HashMap<ProductionRule, ArrayList<Token>> predictSets;
    private ParsingTable LL1table;

    public ParserGenerator() {
        nonterminalList = new ArrayList<Nonterminal>();
        tokenList = new ArrayList<Token>();
        startSymbol = null;
        rules = new ArrayList<ProductionRule>();
        allRules = new HashMap<Nonterminal, ArrayList<ProductionRule>>();
        firstSets = new HashMap<Nonterminal, ArrayList<Token>>();
        followSets = new HashMap<Nonterminal, ArrayList<Token>>();
        predictSets = new HashMap<ProductionRule, ArrayList<Token>>();
    }

    public Symbol getStartSymbol() {
        return startSymbol;
    }

    public void feed(String grammarFile, String lexemsFile) throws Exception {
        // read in the grammar file
        BufferedReader grFileBuf = new BufferedReader(new FileReader(grammarFile));
        BufferedReader lexemsFileBuf = new BufferedReader(new FileReader(lexemsFile));
        String tok;
        String nonTerm;
        String grule = ""; //holds each grammar rule as it is read from the grammar file

        while ((tok = lexemsFileBuf.readLine()) != null) {
            tok = tok.replaceFirst("[0-9]+? - ", "");
            tokenList.add(new Token(tok.trim()));
        }

        //test
//        System.out.println("token list = " + tokenList);

        while ((nonTerm = grFileBuf.readLine()) != null) {
            nonTerm = nonTerm.split(" = ")[0];
            if (!nonterminalList.contains(new Nonterminal(nonTerm.trim()))) {
                nonterminalList.add(new Nonterminal(nonTerm.trim()));
            }
        }

        //test
//        System.out.println("List of nonterminals = " + nonterminalList);

        grFileBuf = new BufferedReader(new FileReader(grammarFile));
        grule = grFileBuf.readLine();

        startSymbol = new Symbol(grule.replaceFirst(" =.+", "").trim());
//        System.out.println("Start symbole = " + startSymbol);
        //takes each grammar rule one at a time and removes left recursion
        //then adds the new rules created from the recusion removal to a list representing the new grammar

        //gather all the raw grammar rules
        ArrayList<String> gRules = new ArrayList<String>();
        do {
            if (grule == null) {
                break;
            }

                //if(!grule.startsWith("%"))
            //{
            gRules.add(grule);
                //}

        } while ((grule = grFileBuf.readLine()) != null);

  //      System.out.println();
  //      System.out.println("... after removing left recursion, we have:");
        allRules = removeLeftRecursion(gRules); //RemoveLeftRecursion returns the rules hashed against their nonterminals.    
        printGrammar();
///        System.out.println();

        for (String gRule : gRules) {

        }
        //once left recursion has been removed from the grammar then common prefix must be fixed
        //allRules = removeCommonPrefix(allRules);
//        System.out.println("allRules = ");
//        System.out.println(allRules);

        // okay, now all our rules are fixed.
        // allRules is the hashmap from nonterminal to a list of production rules
        // we use allRules to create "rules", which is a straight-up list of rules
        ArrayList<Nonterminal> keyList = new ArrayList<Nonterminal>();
        keyList.addAll(allRules.keySet());
//        System.out.println("keyList = " + keyList);
        for (Nonterminal key : keyList) {
            rules.addAll(allRules.get(key));

            if (!nonterminalList.contains(key)) {
                nonterminalList.add(key);
            }
        }

    }

    // TO_DO: remove left recursion
    private HashMap<Nonterminal, ArrayList<ProductionRule>> removeLeftRecursion(ArrayList<String> gRules) {
        ArrayList<ProductionRule> prRules = new ArrayList<ProductionRule>();
        for (String lineRule : gRules) {
            ArrayList<ProductionRule> prodRules = this.getProductionRules(lineRule);
            prRules.addAll(prodRules);
        }

        HashMap<Nonterminal, ArrayList<ProductionRule>> map = this.hashRulesByNonterminal(prRules);
        ArrayList<Nonterminal> keyList = new ArrayList<Nonterminal>();
        keyList.addAll(map.keySet());
        return map;
    }

    public ArrayList<ProductionRule> getProductionRules(String grammarRule) {
        ArrayList<ProductionRule> productions = new ArrayList<ProductionRule>();

        int index = grammarRule.indexOf("=");
        String leftSide = grammarRule.substring(0, index);
        Nonterminal nonTerm = new Nonterminal(leftSide.trim());  //get the nonterminal on left side of the arrow

        String rightSide = grammarRule.substring(index + 1);
        //grabs the sections of the rule bordered by "|" and takes those symbols to create a production rule
        //Example grammar rule = <S> : <T> d | b | c  the first production rule made will be <S> -> <T> d
        //gScan.useDelimiter("\\|");
        String[] indvRules = rightSide.split("\\|", -1);
        for (String s : indvRules) {
            String rightSyms = s.trim();
            ArrayList<Symbol> rSideSyms = new ArrayList<Symbol>();
            // special case: the rule is blank space. This means epsilon
            if (rightSyms.equals("<nothing>")) {
                rSideSyms.add(new Token("EPSILON"));
            } // otherwise, the rule is broken down into its individual symbols
            else {
                Scanner symScan = new Scanner(rightSyms);
                //traverses symbols
                while (symScan.hasNext()) {
                    String sym = symScan.next().trim();
                   //System.out.println("sym = " + sym);
                    //checks to see if the symbol is a nonterminal or a terminal then adds
                    //that symbol to the right side of the production rule
                    if (tokenList.contains((new Symbol(sym)))) {
                        rSideSyms.add(new Token(sym));
                    } else if (nonterminalList.contains((new Symbol(sym)))) {
                        rSideSyms.add(new Nonterminal(sym));
                    }
                }
            }
            productions.add(new ProductionRule(nonTerm, rSideSyms));
        }
        //System.out.println("getProdRules(" + grammarRule + ") = " + productions);
        return productions;
    }

    private HashMap<Nonterminal, ArrayList<ProductionRule>> hashRulesByNonterminal(ArrayList<ProductionRule> prRules) {
        HashMap<Nonterminal, ArrayList<ProductionRule>> hashed = new HashMap<Nonterminal, ArrayList<ProductionRule>>();
        Nonterminal nontermSym;
        int n = 0;

        while (n < prRules.size()) {
            nontermSym = prRules.get(n).getNonterminal();
            ArrayList<ProductionRule> newRules = new ArrayList<ProductionRule>();      //list of production rules that belong to the same nonterminal
            while (prRules.get(n).getNonterminal().getName().equals(nontermSym.getName())) {
                newRules.add(prRules.get(n));
                n++;
                if (n >= prRules.size()) {
                    break;
                }
            }
            hashed.put(nontermSym, newRules);
        }
        return hashed;
    }

    public ParsingTable buildParsingTable() {
        // compute first() sets
        System.out.println("Computing First sets...");
        firstSets = computeFirstSets();

        // compute follow() sets
        System.out.println("Computing Follow sets...");
        followSets = computeFollowSets();

        //      compute predict() sets using first() and follow() sets
        System.out.println("Computing Predict sets...");
        predictSets = computePredictSets();

        // testing
        // print First, Follow, and Predict sets
        for (Nonterminal N : firstSets.keySet()) {
//            System.out.println("first(" + N + ") = " + firstSets.get(N));
        }

//        System.out.println();

        for (Nonterminal N : followSets.keySet()) {
//            System.out.println("follow(" + N + ") = " + followSets.get(N));
        }

 //       System.out.println();
        for (ProductionRule R : predictSets.keySet()) {
 //           System.out.println("predict(" + R + ") = " + predictSets.get(R));
        }

  //      System.out.println();
        // initialize new ParsingTable
        LL1table = new ParsingTable(tokenList.size(), nonterminalList.size(), tokenList, nonterminalList);

        //iterator for the rules of the predict set
        Collection ruls = predictSets.keySet();
        Iterator r = ruls.iterator();

        // iterate through [rule, predict_set] key-value pairs in hashtable
        ProductionRule nonT;
        ArrayList<Token> tks;
        while (r.hasNext()) {
            nonT = (ProductionRule) r.next();
            tks = predictSets.get(nonT);   //list of tokens for each rule
            // iterate through tokens of predict set and add entry in parse table for the production rule
            for (Token t : tks) {
                LL1table.addEntry(nonT.getNonterminal(), t, nonT);
            }
        }
        return LL1table;
    }

    private HashMap<Nonterminal, ArrayList<Token>> computeFirstSets() {
        HashMap<Nonterminal, ArrayList<Token>> firstSetsLocal = new HashMap<Nonterminal, ArrayList<Token>>();
        for (Nonterminal N : nonterminalList) {
            ArrayList<Token> temp = first(N);
            firstSetsLocal.put(N, temp);
        }
        return firstSetsLocal;
    }

    private ArrayList<Token> first(Symbol S) {
        // S bet koks terminalas arba epsilon
        if (S instanceof Token) {
            ArrayList<Token> singleton = new ArrayList<Token>();
            singleton.add((Token) S);
            return singleton;
        }

        HashSet<Token> ret = new HashSet<Token>();

        // kiekvienam netereminalui ziuriu taisykle
        for (ProductionRule R : allRules.get((Nonterminal) S)) {
            ArrayList<Symbol> symbols = R.getRule();
            boolean hasEpsilon = false;
            // now, for each symbol in that rule sequence...
            for (Symbol X_i : symbols) {
                hasEpsilon = false;
                // we add the First set the current symbol
                // if that First set contains epsilon, we note that, so that
                // we can get the First set of the NEXT symbol, too.
                for (Token T : first(X_i)) {
                    if (T.getName().equals("EPSILON")) {
                        hasEpsilon = true;
                    } else {
                        ret.add(T);
                    }
                }
                if (!hasEpsilon) {
                    break;
                }
            }
            // jei hasEpsilon true, tai kiekvienas simbolis
            // sekoj buvo epsilon first aibej
            if (hasEpsilon) {
                ret.add(new Token("EPSILON"));
            }
        }
        ArrayList<Token> retAsList = new ArrayList<Token>();
        retAsList.addAll(ret);
        return retAsList;
    }

    /**
     * Louden page 168 This method assumes computeFirstSets() has already been
     * called, creating a cache of first(N) for each nonterminal.
     *
     * @param alpha
     * @return
     */
    private ArrayList<Token> first(ArrayList<Symbol> alpha) {

        if (alpha.size() == 0) {
            ArrayList<Token> temp = new ArrayList<Token>();
            //temp.add(new Token("EPSILON"));
            return temp;
        }

        // special case: alpha is a set containing only epsilon
        if (alpha.size() == 1 && alpha.contains(new Token("EPSILON"))) {
            ArrayList<Token> temp = new ArrayList<Token>();
            temp.add(new Token("EPSILON"));
            return temp;
        }

        // using a set so that duplicates are automatically ignored
        HashSet<Token> ret = new HashSet<Token>();
        ArrayList<Token> firstAlpha = first(alpha.get(0));
        ret.addAll(firstAlpha);

        boolean hasEpsilon = ret.contains(new Token("EPSILON"));
        ret.remove(new Token("EPSILON"));
        int i = 1;
        while (hasEpsilon && i < alpha.size()) {
            ArrayList<Token> nextFirstSet = null;
            Symbol nextSymbol = alpha.get(i);
            // if it's a nonterminal, it's already computed and cached
            if (nextSymbol instanceof Nonterminal) {
                nextFirstSet = firstSets.get((Nonterminal) nextSymbol);
            } // if it's a terminal, it will be computed instantly anyway
            else {
                nextFirstSet = first(nextSymbol);
            }

            if (!nextFirstSet.contains(new Token("EPSILON"))) {
                hasEpsilon = false;
            }
            for (Token T : nextFirstSet) {
                ret.add(T);
            }
            ret.remove(new Token("EPSILON"));
            i++;
        }

        //return removeDups(ret);
        ArrayList<Token> retAsList = new ArrayList<Token>();
        retAsList.addAll(ret);
        return retAsList;
    }

    // Compute Follow sets for all nonterminals
    private HashMap<Nonterminal, ArrayList<Token>> computeFollowSets() {
        HashMap<Nonterminal, ArrayList<Token>> followSetsLocal = new HashMap<Nonterminal, ArrayList<Token>>();

        // initialize follow sets here
        // follow(startSymbol) = {$}, and the rest are empty
        for (Nonterminal N : nonterminalList) {
            if (N.getName().equals(startSymbol.getName())) {
                ArrayList<Token> temp = new ArrayList<Token>();
                temp.add(new Token("$"));
                followSetsLocal.put(N, temp);
            } else {
                followSetsLocal.put(N, new ArrayList<Token>());
            }
        }

        ArrayList<Token> ret = new ArrayList<Token>();

        // we keep track of whether any changes are made to the follow sets during each pass
        // when no changes have been made, we're at stasis and we terminate
        boolean changed = true;
        // while the follow sets are still "active" (changing), keep making passes
        while (changed) {
            changed = false;
            for (Nonterminal N : nonterminalList) {
                for (ProductionRule prodn : allRules.get(N)) {
                    ArrayList<Symbol> prodelements = prodn.getRule();
                    int k = prodelements.size();
                    for (int i = 0; i < k; i++) {
                        Symbol S = prodelements.get(i);
                        if (S instanceof Nonterminal) {
                            Nonterminal X = (Nonterminal) S;
                            ArrayList<Token> xFollow = followSetsLocal.get(X);
                            ArrayList<Symbol> prodend = new ArrayList<Symbol>(prodelements.subList(i + 1, k));

                            if (allNullable(prodend)) {
                                ArrayList<Token> nFollow = followSetsLocal.get(N);
                                // add N follow set to X follow set; check for changes
                                boolean changed2 = false;
                                for (Token T : nFollow) {
                                    if (!xFollow.contains(T)) {
                                        changed2 = true;
                                        xFollow.add(T);
                                    }
                                }
                                changed = changed || changed2;
                            }
                            // first set
                            ArrayList<Symbol> followSymbols = new ArrayList<Symbol>(prodelements.subList(i + 1, k));
                            ArrayList<Token> first = first(followSymbols);
                            // add first set to xFollow; check for changes
                            boolean changed3 = false;
                            for (Token T : first) {
                                if (!xFollow.contains(T)) {
                                    changed3 = true;
                                    xFollow.add(T);
                                }
                            }
                            changed = changed || changed3;
                            followSetsLocal.put(X, xFollow);
                        }//end if
                    }
                }
            }
        }
        return followSetsLocal;
    }

    private boolean allNullable(ArrayList<Symbol> symbols) {
        for (Symbol S : symbols) {
            if (S instanceof Token && !S.getName().equals("EPSILON")) {
                return false;
            }
            if (S instanceof Nonterminal) {
                if (!firstSets.get((Nonterminal) S).contains(new Token("EPSILON"))) {
                    return false;
                }
            }
        }
        return true;
    }

    // page 178 Louden
    private HashMap<ProductionRule, ArrayList<Token>> computePredictSets() {
        HashMap<ProductionRule, ArrayList<Token>> predictSetsLocal = new HashMap<ProductionRule, ArrayList<Token>>();
        // we compute the predict set for each production rule in the grammar
        for (ProductionRule P : rules) {
            ArrayList<Token> temp = new ArrayList<Token>();

            Nonterminal A = P.getNonterminal();
            ArrayList<Symbol> alpha = P.getRule();
            // to start, we get the first set of the right-hand side
            ArrayList<Token> first = first(alpha);
            for (Token T : first) {
                temp.add(T);
            }

            // if that first set contains alpha, then we also add the follow
            // set to the predict set.
            if (first.contains(new Token("EPSILON"))) {
                for (Token T : followSets.get(A)) {
                    temp.add(T);
                }
            }
            // hash it up!
            predictSetsLocal.put(P, temp);
        }

        return predictSetsLocal;
    }

    public void printGrammar() {
        for (Nonterminal N : allRules.keySet()) {
            for (ProductionRule R : allRules.get(N)) {
                System.out.println(R);
            }
        }
    }
}
