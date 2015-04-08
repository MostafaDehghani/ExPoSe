/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.expose.LM;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import nl.uva.expose.entities.government.Cabinet;
import static nl.uva.expose.settings.Config.configFile;
import nl.uva.lucenefacility.IndexInfo;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.SimpleFSDirectory;


/**
 *
 * @author Mostafa Dehghani
 */
public class test {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(test.class.getName());
    private String period;
    public IndexReader statiReader;
    private IndexInfo statiInfo;
    private String field;
    private Cabinet cabinet;

    public LanguageModel aSLM;
    private HashMap<Integer,LanguageModel> allDSPLM = new HashMap<Integer,LanguageModel>();
    private HashMap<String, LanguageModel> statSLM = new HashMap<>();

    public test(String period) throws IOException, Exception {
        this.period = period;
        this.cabinet = new Cabinet(period);
        this.statiReader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("INDEXES_PATH") + period + "/st")));
        this.statiInfo = new IndexInfo(this.statiReader);
        this.field = "TEXT";
        aSLM = new CollectionLM(statiReader, this.field);

    }

    public LanguageModel getStatSLM(String statId) throws IOException {
        LanguageModel statSLM = this.statSLM.get(statId);
        if (statSLM == null) {
            Integer statIndexId = this.statiInfo.getIndexId(statId);
            StandardLM statslm = new StandardLM(statiReader, statIndexId, this.field);
            this.statSLM.put(statId, statslm);
            statSLM = statslm;
        }
        return statSLM;
    }
    
    public HashMap<String,Double> getStatTV(String statId) throws IOException {
        HashMap<String,Double> statTV;
        Integer statIndexId = this.statiInfo.getIndexId(statId);
        statTV = this.statiInfo.getDocTermFreqVector(statIndexId, field);
        return statTV;
    }
}

