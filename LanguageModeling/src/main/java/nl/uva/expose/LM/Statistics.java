/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.expose.LM;

import java.io.File;
import java.io.IOException;
import nl.uva.expose.entities.government.Cabinet;
import static nl.uva.expose.settings.Config.configFile;
import nl.uva.lucenefacility.IndexInfo;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.SimpleFSDirectory;

/**
 *
 * @author Mostafa Dehghani
 */
public class Statistics {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Statistics.class.getName());
    public static IndexReader iReader;
    public static IndexInfo iInfo;
    public static void getStat(String period) throws IOException {
        
        iReader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("INDEXES_PATH") + period + "/st")));
        iInfo = new IndexInfo(iReader);
        String field = "TEXT";
        System.out.println(iInfo.getNumOfAllTerms(field));
        System.out.println(iInfo.getAvgDocLength(field));
    }
    
    public static void main(String[] args) throws IOException {
        getStat("20062010");
        
    }
    
}
