/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.expose.LM;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nl.uva.expose.genral.LanguageModel;
import static nl.uva.expose.genral.Tools.sortByValues;
import nl.uva.lucenefacility.IndexInfo;
import org.apache.lucene.index.IndexReader;

/**
 *
 * @author Mostafa Dehghani
 */
public class StandardLM {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(StandardLM.class.getName());
    public LanguageModel languageModel;
    private IndexReader ireader;
    private Integer dId;
    private String field;
    private IndexInfo iInfo;
    

    public StandardLM(IndexReader ireader, Integer dId, String field) throws IOException {
        this.ireader = ireader;
        this.dId= dId;
        this.field = field;
        this.iInfo = new IndexInfo(this.ireader);
        try {
            this.languageModel = generateStandardLanguageModel();
        } catch (IOException ex) {
            log.error(ex);
            throw ex;
        }
    }
    
    public LanguageModel generateStandardLanguageModel() throws IOException{
        HashMap<String,Double> tv;
        try {
            tv = this.iInfo.getTermFreqVector(this.dId, this.field);
            Long dLength = this.iInfo.getDocumentLength(this.dId, this.field);
            for(Map.Entry<String,Double> e: tv.entrySet()){
                Double prob = e.getValue()/dLength;
                tv.put(e.getKey(), prob);
            }
        } catch (IOException ex) {
            log.error(ex);
            throw ex;
        }
        LanguageModel SLM = new LanguageModel(tv);
        return SLM;
    }
    public List<Map.Entry<String, Double>>  getTopK(Integer k){
        List<Map.Entry<String, Double>>  sorted = sortByValues(languageModel.LanguageModel, false);
        return sorted.subList(0, k);
    }
    
    public List<Map.Entry<String, Double>>  getSorted(){
        List<Map.Entry<String, Double>>  sorted = sortByValues(languageModel.LanguageModel, false);
        return sorted;
    }
}