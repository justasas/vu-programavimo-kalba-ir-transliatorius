/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package semantics;

public class Symbol
{
    private String name;
    private String varType; // int, float...
    private String value;

    private IDtype type;
    private final int MAX_ADDITIONAL = 10;
    private String additionalInfo[] = new String[MAX_ADDITIONAL];
    

    public Symbol(String name, String varType, IDtype type, String value, String additionalInfo[])
    {
        this.name = name;
        this.varType = varType;
        this.value = value;
        this.type = type;
        this.additionalInfo = additionalInfo;
    }
    public Symbol(){}
    String getName()
    {
        return name;
    }
    String getValue()
    {
        return value;
    }
    IDtype getType()
    {
        return type;
    }
    String [] getAdditionalInfo()
    {
        return additionalInfo;
    }
    String getVarType()
    {
        return varType;
    }
    void setValue(String value)
    {
        this.value = value;
    }
}