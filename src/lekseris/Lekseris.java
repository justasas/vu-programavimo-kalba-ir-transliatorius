/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lekseris;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;
import parser.ParserMain;
import parser.Token;
import semantics.Semantic;

/**
 *
 * @author unknown
 */
public class Lekseris {     
    int dabartine = 0;
    int leksemos_id = 0;
    //FileReader fr;
    RandomAccessFile fr;
    BufferedReader programos_skaneris;
    File programos_failas;
    Vector<Busena> busenos;
    String leksema = "";
    Vector<String> kalbos_zodziai = new Vector<String>();
    char simbolis = ' ';
    int leksema_gauta;
    int[] kalbos_zodziai_id = new int[100];
    
    private Lekseris(String programosFailas) throws IOException {
        programos_failas = new File(programosFailas);
        //this.fr = new FileReader(programos_failas);
        this.fr = new RandomAccessFile(programos_failas, "rw");
        busenos = nuskaityti_busenas();
        kalbos_zodziai = nuskaityti_kalbos_dalis();
    }
    /**
     * @param args the command line arguments
     */
    
    Vector<String> nuskaityti_kalbos_dalis() throws FileNotFoundException
    {
        File file = new File("leksemos_lookup.txt");
        Scanner scanner = new Scanner(file);

        Vector<String> kalbos_dalys = new Vector<String>();  
        int i = 0;
        while(scanner.hasNext())
        {
            int kalbos_dalis_id = scanner.nextInt();
            String kalbos_dalis = scanner.nextLine();
            //String kalbos_dalis_copy = new String(kalbos_dalis);
            //Integer intValue = Integer.parseInt(kalbos_dalis_copy.replaceAll("[^0-9]", ""));
            //System.out.println(kalbos_dalis);
            kalbos_zodziai_id[i] = kalbos_dalis_id;
            i++;
            kalbos_dalys.add(kalbos_dalis.replace(" - ", ""));
        }
        return kalbos_dalys;
    }
    
    Vector<Busena> nuskaityti_busenas() throws IOException
    {
        File file = new File("busenos.txt");
        Scanner scanner = new Scanner(file);

        Vector<Busena> busenos = new Vector<Busena>();  

        while(scanner.hasNext())
        {
            String busenos_pav = scanner.nextLine().split("//")[0];
             
            Vector<Vector<String>> simboliai = new Vector<Vector<String>>();
            Vector<String> pereiti_i = new Vector<String>();
            
            if (busenos_pav.contains("string_constt"))
            {
                busenos.add(new Busena(busenos_pav, simboliai, pereiti_i));
                break;
            }
            //scanner.nextLine();
            String line;
            int k = 0;

            while(!(line = scanner.nextLine()).isEmpty())
            {
                //System.out.println(line);
                Vector<String> simboliu_vektorius = new Vector<String>();
                String[] simboliu_masyvas = line.split(" --> ")[0].split("[ ]+");

                for (int h = 0; h < simboliu_masyvas.length; h++)
                {
                    if (simboliu_masyvas[h].contains("space"))
                    {
                        simboliu_masyvas[h] = String.valueOf(' ');
                    }
                    if (simboliu_masyvas[h].contains("tab"))
                    {
                        simboliu_masyvas[h] = String.valueOf((char)9);
                    }
                    if (simboliu_masyvas[h].contains("CR"))
                    {
                        simboliu_masyvas[h] = String.valueOf((char)13);
                    }
                    if (simboliu_masyvas[h].contains("NL"))
                    {
                        simboliu_masyvas[h] = String.valueOf((char)10);
                    }
                    if (simboliu_masyvas[h].contains("-1"))
                    {
                        simboliu_masyvas[h] = String.valueOf((char)-1);
                    }
                }
                for (int w = 0; w < simboliu_masyvas.length; w++)
                    simboliu_vektorius.add(simboliu_masyvas[w]);
                simboliai.add(simboliu_vektorius);

                String[] perejimai = line.split(" --> ")[1].split("[ ]+");

                for (int w = 0; w < perejimai.length; w++)
                    pereiti_i.add(perejimai[w]);
            }
            busenos.add(new Busena(busenos_pav, simboliai, pereiti_i));
        }
        return busenos;
    }
    
    void rasti_kita_busena(char simbolis)
    {
        Vector<String> simboliai;
        String raide;
        Busena busena;
        //System.out.println("persoka_i dydis: "+busenos.get(dabartine).atvejai_persoka_i.size());
        for (int i = 0; i < busenos.get(dabartine).atvejai_persoka_i.size(); i++) 
        {   
            simboliai = busenos.get(dabartine).atvejai_simboliai.get(i);
            //System.out.print(simboliai.size()+ " ");
            for (int j = 0; j < simboliai.size(); j++)
            {
                
                if (simboliai.get(j).charAt(0) == simbolis || simboliai.get(j).contains("niekas_neatitiko"))
                {
                    //System.out.println("leksema: " +leksema);
                    //System.out.print("dabartine: "+dabartine);
                    dabartine = Integer.parseInt(busenos.get(dabartine).atvejai_persoka_i.get(i));
                    //System.out.println("persoka i: "+dabartine);
                    return;
                }
                
            }
        }
    }

    void prideti_simboli(char simbolis)
    {
        if (busenos.get(dabartine).atvejai_persoka_i.size() == 0)
        {
            leksema_gauta = 1;
        }
        if (busenos.get(dabartine).atvejai_persoka_i.size() > 0 && !busenos.get(dabartine).vardas.contains("comment"))
        {
            leksema += simbolis;
            leksema_gauta = 0;
        }
    }
    
    public String gautiLeksema() throws IOException
    {   
        dabartine = 0;
        while (busenos.get(dabartine).atvejai_persoka_i.size() > 0)
        {   
            //System.out.println(simbolis);
            if (leksema_gauta != 1)
              simbolis = (char) fr.read();
            rasti_kita_busena(simbolis);
            prideti_simboli(simbolis);
            //System.out.println("leksema: " + leksema);
            if (dabartine == 0)
            {
                leksema = "";
            }
        }
        
        if (busenos.get(dabartine).vardas.contains("eof"))
        {
            return "eof";
        }

        if (busenos.get(dabartine).vardas.contains("error"))
        {        
            return "error";
        }

        String busenos_vardas = busenos.get(dabartine).vardas;
        int zodzio_numeris;
        // look up
        if ("identifier ".contains(busenos.get(dabartine).vardas) && ((zodzio_numeris = kalbos_zodziai.indexOf(leksema)) >= 0))
        {
            leksemos_id = kalbos_zodziai_id[zodzio_numeris];
            busenos_vardas = kalbos_zodziai.get(zodzio_numeris);
        }
        else
        {
            leksemos_id = dabartine;
        }
        return busenos_vardas;
    }
    
    public void surasyti_leksemas_i_faila() throws FileNotFoundException, UnsupportedEncodingException
    {
        PrintWriter writer = new PrintWriter("leksemos.txt", "UTF-8");

        for (int i = 0; i < busenos.size(); i++)
        {
            if (busenos.elementAt(i).atvejai_persoka_i.size() == 0)
                writer.println(i + " - <" + busenos.elementAt(i).vardas.trim()+">");
        }
        for (int i = 0; i < kalbos_zodziai.size(); i++)
        {
            writer.println(kalbos_zodziai_id[i] + " - <" + kalbos_zodziai.get(i).trim()+">");
        }
        writer.close();
    }
    
    
    public static void main(String[] args) throws IOException, Exception {
        Lekseris lekseris = new Lekseris("programosPavyzdys.txt"); 
        lekseris.nuskaityti_busenas();
        lekseris.surasyti_leksemas_i_faila();        
        ArrayList<Token> tokens = new ArrayList<Token>();
        ArrayList<String> lexems = new ArrayList<String>();
        
        String busenos_pav;
        //position pos;
        //for (int i = 0; i < 3; i++)
        //    lekseris.gautiLeksema(id, lekseris.leksema);
        int b = 0;
        while (!(busenos_pav = lekseris.gautiLeksema()).contains("eof"))
        {
            //if(lekseris.dabartine != 80)
              //lekseris.fr.seek(lekseris.fr.getFilePointer()-1);
            if (busenos_pav.contains("error"))
            {
                lekseris.leksema_gauta = 0;
            }
            else {
                tokens.add(new Token("<"+busenos_pav.trim()+">"));
                lexems.add(lekseris.leksema);
                //System.out.println("token: " +busenos_pav  + "\t\t\tleksema: " + lekseris.leksema);
                lekseris.leksema = "";
            }
        }
        tokens.add(new Token("$"));
        
        ParserMain parser_main = new ParserMain();
        parser_main.parse(tokens, lexems); 
        Semantic sem = new Semantic("parseTree.txt");   
    }
}