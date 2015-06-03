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
import java.util.HashSet;
import nl.uva.expose.glm.NewGeneralizedLM;
import nl.uva.expose.lm.GeneralizedLM;
import nl.uva.expose.lm.LanguageModel;
import static nl.uva.expose.settings.Config.configFile;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.SimpleFSDirectory;

/**
 *
 * @author Mostafa Dehghani
 */
public class NewFactorize {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(NewFactorize.class.getName());

    public static void main(String[] args) throws IOException, Exception {
        factorization("20122014");
    }

    public static void factorization(String period) throws Exception {

        IndexReader miReader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("INDEXES_PATH") + period + "/m")));
        IndexReader piReader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("INDEXES_PATH") + period + "/p")));
        IndexReader siReader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("INDEXES_PATH") + period + "/st")));

        NewGeneralizedLM glm = new NewGeneralizedLM(period);
        Integer itNum = 1;

        Integer counter = 0;
        Integer size = glm.getAllSLM().LanguageModel.size();

        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("new_factorization_" + period + ".csv")));
        for (String term : glm.getAllSLM().LanguageModel.keySet()) {
            HashSet<String> lbl = new HashSet<>();
            Factorize.log.info(counter + " of " + size);
            // in all
            LanguageModel aGLM = glm.getAllGLM(itNum);
            if (aGLM.getProb(term) > 0) {
                lbl.add("nl.all");
            }

            //Statuses
            for (int i = 0; i < siReader.numDocs(); i++) {
                String statusId = siReader.document(i).get("ID");
                LanguageModel sGLM = glm.getStatGLM_s1(statusId, itNum);
                if (sGLM.getProb(term) > 0) {
                    lbl.add(statusId);
                }
            }

            //Parties
            for (int i = 0; i < piReader.numDocs(); i++) {
                String partyId = piReader.document(i).get("ID");
                LanguageModel pGLM = glm.getPartyGLM(partyId, itNum);
                if (pGLM.getProb(term) > 0) {
                    lbl.add(partyId);
                }
            }

            //Members
            for (int i = 0; i < miReader.numDocs(); i++) {
                String memberId = miReader.document(i).get("ID");
                LanguageModel mGLM = glm.getMemGLM(memberId, itNum);
                if (mGLM.getProb(term) > 0) {
                    lbl.add(memberId);
                }
            }
            String IDs = "";
            for (String s : lbl) {
                IDs += s + " ";
            }
            bw.write(term + " "  + lbl.size() + " " + IDs + "\n");
            bw.flush();
        }

        bw.close();
    }
}
