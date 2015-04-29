/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package semantics;

enum IDtype
{
    FUNCTION,
    ARRAY,
    VARIABLE
}

public class SymbolsTable
{
    private final int MAX_SYMBOLS = 100;
    private Symbol symbols[] = new Symbol[MAX_SYMBOLS];
    private int symbolsCount;

    public SymbolsTable()
    {
        symbolsCount = 0; 
    }

    public int isSymbol(String name)
    {
        for(int i = 0; i < symbolsCount; i++)
        {
            if(symbols[i].getName().equals(name))
            {
                return i;
            }
        }
        return -1;
    }
    
    public int isSymbol(String name, IDtype type)
    {
        for(int i = 0; i < symbolsCount; i++)
        {
            if(symbols[i].getName().equals(name) && symbols[i].getType() == type)
            {
                return i;
            }
        }
        return -1;
    }

    public Symbol getSymbol(int index)
    {
        return symbols[index];
    }

    public void add(String name, String varType, IDtype type, String value, String additionalInfo[])
    {
        symbols[symbolsCount++] = new Symbol(name, varType, type, value, additionalInfo);
    }
    
    public void add(Symbol symbol)
    {
        symbols[symbolsCount++] = symbol;
    }
    
    public int getSymbolsCount()
    {
    	return symbolsCount;
    }

    public void setValue(String name, String value)
    {
        int index = isSymbol(name);
        symbols[index].setValue(value);
    }

}
