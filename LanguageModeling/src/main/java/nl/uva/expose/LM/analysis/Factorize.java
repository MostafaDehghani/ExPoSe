/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.expose.LM.analysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import nl.uva.expose.LM.GeneralizedLM;
import nl.uva.expose.LM.LanguageModel;
import static nl.uva.expose.settings.Config.configFile;
import nl.uva.lucenefacility.IndexInfo;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.SimpleFSDirectory;

/**
 *
 * @author Mostafa Dehghani
 */
public class Factorize {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Factorize.class.getName());

    public static void main(String[] args) throws IOException, Exception {
        factorization("20122014");
    }

    public static void factorization(String period) throws Exception {

        IndexReader miReader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("INDEXES_PATH") + period + "/m")));
        IndexReader piReader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("INDEXES_PATH") + period + "/p")));
        IndexReader siReader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("INDEXES_PATH") + period + "/st")));
        IndexInfo miInfo = new IndexInfo(miReader);
        IndexInfo piInfo = new IndexInfo(piReader);
        IndexInfo siInfo = new IndexInfo(siReader);


        GeneralizedLM glm = new GeneralizedLM(period);
        Integer itNum = 1;

        HashMap<String, HashSet<String>> termsLbl = new HashMap<>();

        for (String term : glm.getAllSLM().LanguageModel.keySet()) {
            termsLbl.put(term, new HashSet<String>());
        }

        for (String term : termsLbl.keySet()) {

            // in all
            LanguageModel aGLM = glm.getAllGLM(itNum);
            if (aGLM.getProb(term) > 0) {
                HashSet<String> lbl = termsLbl.get(term);
                lbl.add("nl.all");
                termsLbl.put(term, lbl);
            }

            //Statuses
            for (int i = 0; i < siReader.numDocs(); i++) {
                String statusId = siReader.document(i).get("ID");
                LanguageModel sGLM = glm.getStatGLM_s1(statusId, itNum);
                if (sGLM.getProb(term) > 0) {
                    HashSet<String> lbl = termsLbl.get(term);
                    lbl.add(statusId);
                    termsLbl.put(term, lbl);
                }
            }

            //Parties
            for (int i = 0; i < piReader.numDocs(); i++) {
                String partyId = piReader.document(i).get("ID");
                LanguageModel pGLM = glm.getPartyGLM(partyId, itNum);
                if (pGLM.getProb(term) > 0) {
                    HashSet<String> lbl = termsLbl.get(term);
                    lbl.add(partyId);
                    termsLbl.put(term, lbl);
                }
            }

            //Members
            for (int i = 0; i < miReader.numDocs(); i++) {
                String memberId = miReader.document(i).get("ID");
                LanguageModel mGLM = glm.getMemGLM(memberId, itNum);
                if (mGLM.getProb(term) > 0) {
                    HashSet<String> lbl = termsLbl.get(term);
                    lbl.add(memberId);
                    termsLbl.put(term, lbl);
                }
            }

        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("factorization_" + period + ".csv")));
        for (Map.Entry<String, HashSet<String>> e : termsLbl.entrySet()) {
            String IDs="";
            for(String s:e.getValue())
                IDs += s + " ";
            bw.write(e.getKey() + " " + IDs  +  "\n");
        }
        bw.close();
    }
}
