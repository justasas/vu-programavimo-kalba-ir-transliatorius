package semantics;

import java.io.*;

class TerminalsHolder
{
    private String fileName;
    private FileReader fileReader;
    private final int MAX_TERMINALS = 100;
    private String terminals[] = new String[MAX_TERMINALS];
    private int terminalsCount;
    private BufferedReader in;
    
    public TerminalsHolder(String fileName)
    {
        terminalsCount = 0;
        this.fileName = fileName;
        try
        {
            fileReader = new FileReader(new File(fileName));
            in= new BufferedReader(fileReader);
            while(!(terminals[terminalsCount++] = in.readLine()).equals("<nothing>"))
            {
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public int isTerminal(String value)
    {
        for(int i = 0; i < terminalsCount; i++)
        {
            if(terminals[i].equals(value))
            {
                return i;
            }
        }
        return -1;
    }

}
