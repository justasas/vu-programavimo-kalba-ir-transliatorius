/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package semantics;

/**
 *
 * @author unknown
 */

import java.io.BufferedWriter;
import java.io.FileWriter;

enum IFOp
{
    ADD,       
    SUB,           
    MUL,          
    DIV,            
    MOD,            
    LT,          
    LE,          
    EE,           
    NE,            
    GT,       
    GE,       
    OR,             
    AND,          
    MOV,
    MOVA,
    JF,      
    JMP,                                
    BOUNDS,                                        
    PROC_START,
    MAIN_START,
    MAIN_END,
    READ,        
    WRITE,
    PARAM,
    FPARAM,
    STORE,
    STORA,
    PULL,
    CALL,
    LABEL,
    CONCAT,
    RET 
}

public class IFGenerator
{
    private BufferedWriter out;
    private BufferedWriter out2;

    public IFGenerator(String fileName, String fileName2)
    {
        try
        {
            out2= new BufferedWriter(new FileWriter(fileName2));
            out = new BufferedWriter(new FileWriter(fileName));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void addOpCode(IFOp ifOp, String op1, String op2, String result)
    {
        try
        {
            out.write(indent(ifOp.toString() + ",") + " " + indent(op1 + ",") + " " + indent(op2 + ",") + " " + indent(result) + "\n");
            out2.write(ifOp.toString() + (char)31 + op1 + (char)31 + op2 + (char) 31 + result + "\n");
        } 
        catch(Exception e)
        {
            //e.printStackTrace();
        }  
     
    }
    
    public String indent(String value)
    {
        int len = value.length();
        for (int i = len; i < 15; i++)
        {
            value = value + " ";
        }
        return value;
    }
    
    public void close()
    {
        try
        {
            out.close();
            out2.close();
        } 
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


}

                      