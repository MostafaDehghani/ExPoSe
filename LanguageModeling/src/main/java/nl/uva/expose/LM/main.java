/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.expose.LM;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import static nl.uva.expose.settings.Config.configFile;
import nl.uva.lucenefacility.IndexInfo;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.SimpleFSDirectory;

/**
 *
 * @author Mostafa Dehghani
 */
public class main {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(main.class.getName());

    public static void main(String[] args) throws IOException {
        String period = "20062010";
        IndexReader miReader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("INDEXES_PATH") + period + "/m")));
        IndexReader piReader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("INDEXES_PATH") + period + "/p")));
        IndexInfo miInfo = new IndexInfo(miReader);
        StandardLM mSLM = new StandardLM(miReader, 0, "TEXT");
        StandardLM pSLM = new StandardLM(piReader, 3, "TEXT");
        HashMap<String, Double> mtv = miInfo.getTermFreqVector(0, "TEXT");
        ParsimoniousLM mPLM = new ParsimoniousLM(mSLM.languageModel, mtv, pSLM.languageModel, 0.1D, 0.001D, 100);
        SmoothedLM mSmLM = new SmoothedLM(mSLM.languageModel, mSLM.languageModel, 0.1);

//        System.out.println("@Standard");
//        for (Entry<String,Double> e : mSLM.getSorted()) {
//            System.out.println(e.getKey() + "," + e.getValue());
//        }
//        System.out.println("@Smoothed");
//        for (Entry<String,Double> e : mSmLM.smoothedLM.LanguageModel.entrySet()) {
//            System.out.println(e.getKey() + ":" + e.getValue());
//        }
        System.out.println("@Parsimonious");
        for (Entry<String, Double> e : mPLM.getSorted()) {
            System.out.println(e.getKey() + "," + e.getValue());
        }

    }
}
