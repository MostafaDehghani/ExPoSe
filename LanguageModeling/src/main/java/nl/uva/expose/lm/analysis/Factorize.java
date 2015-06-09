/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.expose.lm.analysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import nl.uva.expose.lm.GeneralizedLM;
import nl.uva.expose.lm.LanguageModel;
import static nl.uva.expose.settings.Config.configFile;
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

        GeneralizedLM glm = new GeneralizedLM(period);
        Integer itNum = 1;

        Integer counter = 0;
        Integer size = glm.getAllSLM().LanguageModel.size();

        for(String t : glm.getAllGLM(itNum).LanguageModel.keySet()){
            System.out.println(t+ " : " + glm.getAllGLM(itNum).LanguageModel.get(t));
        }
        if(true)
            return;
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("factorization.csv")));
        for (String term : glm.getAllSLM().LanguageModel.keySet()) {
            TreeMap<String, Double> lbl = new TreeMap<>();
            Factorize.log.info(++counter + " of " + size);
            // in all
            LanguageModel aGLM = glm.getAllGLM(itNum);
            Factorize.log.info("aGLM size -->" + aGLM.LanguageModel.size());
            if (aGLM.getProb(term) > 0) {
                Factorize.log.info("nl.all putted on lbl");
                lbl.put("nl.all", aGLM.getProb(term));
            }

            //Statuses
            for (int i = 0; i < siReader.numDocs(); i++) {
                String statusId = siReader.document(i).get("ID");
                LanguageModel sGLM = glm.getStatGLM_s1(statusId, itNum);
                if (sGLM.getProb(term) > 0) {
                    lbl.put(statusId, sGLM.getProb(term));
                }
            }

            //Parties
            for (int i = 0; i < piReader.numDocs(); i++) {
                String partyId = piReader.document(i).get("ID");
                LanguageModel pGLM = glm.getPartyGLM(partyId, itNum);
                if (pGLM.getProb(term) > 0) {
                    lbl.put(partyId, pGLM.getProb(term));
                }
            }

            //Members
            for (int i = 0; i < miReader.numDocs(); i++) {
                String memberId = miReader.document(i).get("ID");
                LanguageModel mGLM = glm.getMemGLM(memberId, itNum);
                if (mGLM.getProb(term) > 0) {
                    lbl.put(memberId, mGLM.getProb(term));
                }
            }
            String IDs = "";
            

            for ( Entry<String,Double> e: entriesSortedByValues(lbl) ) {
                IDs += e.getKey() + ":" + e.getValue() + " ";
            }
            bw.write("\"" + term + "\" " + lbl.size() + " " + IDs.trim() + "\n");
            bw.flush();
        }

        bw.close();
    }

   static <K,V extends Comparable<? super V>> SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
        SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
            new Comparator<Map.Entry<K,V>>() {
                @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                    int res = e2.getValue().compareTo(e1.getValue());
                    return res != 0 ? res : 1; // Special fix to preserve items with equal values
                }
            }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }
}
