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
import nl.uva.lucenefacility.IndexInfo;
import org.apache.lucene.index.IndexReader;

/**
 *
 * @author Mostafa Dehghani
 */
public class StandardLM extends LanguageModel {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(StandardLM.class.getName());
    private IndexReader ireader;
    private Integer dId;
    private String field;
    private IndexInfo iInfo;

    public StandardLM(IndexReader ireader, Integer dId, String field) throws IOException {
        this.ireader = ireader;
        this.dId = dId;
        this.field = field;
        this.iInfo = new IndexInfo(this.ireader);
        try {
            generateStandardLanguageModel();
        } catch (IOException ex) {
            log.error(ex);
            throw ex;
        }
    }

    public void generateStandardLanguageModel() throws IOException {
        HashMap<String, Double> tv;
        try {
            tv = this.iInfo.getDocTermFreqVector(this.dId, this.field);
            Long dLength = this.iInfo.getDocumentLength(this.dId, this.field);
            for (Map.Entry<String, Double> e : tv.entrySet()) {
                Double prob = e.getValue() / dLength;
                tv.put(e.getKey(), prob);
//                tv.put(e.getKey(), e.getValue());
            }
        } catch (IOException ex) {
            log.error(ex);
            throw ex;
        }
        this.LanguageModel = tv;
    }
}
