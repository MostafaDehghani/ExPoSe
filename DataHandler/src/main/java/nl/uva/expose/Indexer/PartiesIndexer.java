/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.expose.Indexer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import nl.uva.expose.entities.speech.Speech;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

/**
 *
 * @author Mostafa Dehghani
 */
public class PartiesIndexer extends Indexer {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(PartiesIndexer.class.getName());
    private HashMap<String, StringBuilder> ps;

    public PartiesIndexer(String period) throws Exception {
        super(period, "p");
    }

    @Override
    protected void docIndexer() throws Exception {
        ps = new HashMap<>();
        try {
            for (Map.Entry<String, Speech> e : data.speeches.entrySet()) {
                Speech s = e.getValue();
                if (s.getSpeakerAffiliation().equals("") || s.getSpeakerAffiliation() == null) {
                    continue;
                }
                try {
                    String sAff = s.getSpeakerAffiliation();
                    StringBuilder sb = new StringBuilder();
                    if (ps.containsKey(sAff)) {
                        sb = ps.get(sAff);
                    }
                    sb.append(s.getSpeechText()).append("\n");
                    ps.put(sAff, sb);
                } catch (NullPointerException ex) {
                    log.error(ex);
                    log.error("Error in speach:" + s.getSpeechId());
                }
            }
            for (Map.Entry<String, StringBuilder> e : ps.entrySet()) {
                this.IndexDoc(e);   
            }
        } catch (Exception ex) {
            log.error(ex);
            throw ex;
        }
    }

    @Override
    protected void analyzerMapInitializer(Map<String, Analyzer> analyzerMap) {
        analyzerMap.put("ID", new KeywordAnalyzer());//StandardAnalyzer(Version.LUCENE_CURRENT));
    }

    @Override
    protected void IndexDoc(Object obj) throws Exception {
        Map.Entry<String, StringBuilder> e = (Map.Entry<String, StringBuilder>) obj;
        Document doc = new Document();
        if (e.getValue().toString().split("\\s+").length <= minDocLength) //Filtering small documents
        {
            return;
        }
        String Id = e.getKey();
        if (Id == null) {
            Id = "null";
        }
        doc.add(new Field("ID", Id, Field.Store.YES, Field.Index.ANALYZED_NO_NORMS, Field.TermVector.YES));
        doc.add(new Field("TEXT", e.getValue().toString(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS));
        try {
            writer.addDocument(doc);
        } catch (IOException ex) {
            log.error(ex);
        }
        log.info("Document " + e.getKey() + " has been indexed successfully...");
    }

    public static void main(String[] args) throws Exception {
        PartiesIndexer pi = new PartiesIndexer("20052010");
    }
}
