/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.expose.classification;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Mostafa Dehghani
 */
public class ChangePvvTag {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ChangePvvTag.class.getName());

    public static void main(String[] args) throws IOException {
        String path = "/Users/Mosi/Desktop/SIGIR_SHORT/Weka/Behavioral/Features20102012.csv";
        FileWriter fileWritter = new FileWriter(new File(path+"1"));
        BufferedWriter bw = new BufferedWriter(fileWritter);
        FileReader fileReader = new FileReader(new File(path));
        BufferedReader br = new BufferedReader(fileReader);
        String line;
        while((line=br.readLine())!=null){
          if(line.contains("nl.p.pvv"))
              line = line.replaceAll("Oposition", "Coalition");
          bw.write(line + "\n");
        }
        bw.close();
    }
}
