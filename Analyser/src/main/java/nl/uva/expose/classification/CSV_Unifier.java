/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.expose.classification;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Mostafa Dehghani
 */
public class CSV_Unifier {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(CSV_Unifier.class.getName());
    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
         
        final String csvfile2  = "/Users/Mosi/Desktop/SIGIR_SHORT/Weka/Behavioral/Features20062010.csv";
 
 
        CSVReader reader = new CSVReader(new FileReader(csvfile2));
        String [] nextLine,sortedNextLine;
        List<String> columns = new ArrayList<String>();
        List<String> sortedColumns = new ArrayList<String>();
        Map<Integer,Integer> map = new HashMap<Integer,Integer>();
         
        if ((nextLine = reader.readNext()) != null) {
            int i = nextLine.length;
         
            for(int j=0;j<i;j++){
            columns.add(nextLine[j]);
            sortedColumns.add(nextLine[j]);
            }
             
            Collections.sort(sortedColumns);
        }
         
 
         
        for(int i=0;i<columns.size();i++){
            String str = columns.get(i);
            map.put(i, sortedColumns.indexOf(str));
        }
     
        for(int i=0;i<map.size();i++){
            System.out.println(" key is :" + i + ", value is :" + map.get(i));
        }
         
        CSVWriter writer = new CSVWriter(new FileWriter("/Users/Mosi/Desktop/SIGIR_SHORT/Weka/Behavioral/Features20062010_.csv"), ',',CSVWriter.NO_QUOTE_CHARACTER);
         
        sortedNextLine = new String[sortedColumns.size()];
        System.out.println(sortedNextLine.length);
        //System.out.println(sortedNextLine[0] + "-" + sortedNextLine[1]);
         
        for(int k =0; k < sortedColumns.size();k++){
            sortedNextLine[k] = sortedColumns.get(k);
            System.out.println(sortedNextLine[k]);
        }
 
        writer.writeNext(sortedNextLine);
         
        while ((nextLine = reader.readNext()) != null) {
            for(int count=0;count < nextLine.length ; count++){
                String str = nextLine[count];
                sortedNextLine[map.get(count)] = str;
            }
            writer.writeNext(sortedNextLine);
        }
         
        writer.close();
    }
}
