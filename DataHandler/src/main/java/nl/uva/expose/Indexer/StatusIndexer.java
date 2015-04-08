/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.expose.Indexer;

import java.io.IOException;
import java.util.AbstractMap;
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
public class StatusIndexer extends Indexer {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(StatusIndexer.class.getName());

    public StatusIndexer(String period) throws Exception {
        super(period, "st");
    }

    @Override
    protected void docIndexer() throws Exception {
        try {
            Map.Entry<String, StringBuilder> e;
            e = this.getAllSpeechByStatus("Coalition");
            this.IndexDoc(e);
            e = this.getAllSpeechByStatus("Oposition");
            this.IndexDoc(e);
        } catch (Exception ex) {
            log.error(ex);
            throw ex;
        }
    }

    private  Map.Entry<String, StringBuilder>  getAllSpeechByStatus(String status) throws NullPointerException, IOException {
        StringBuilder allSpeeches = new StringBuilder();
        Map.Entry<String, StringBuilder> ent = null;
        for (Map.Entry<String, Speech> e : data.speeches.entrySet()) {
            Speech s = e.getValue();
            try {
                String sAff = s.getSpeakerAffiliation();
                if (data.cabinet.getStatus(sAff).equals(status)) {
                    allSpeeches.append(s.getSpeechText()).append("\n");
                }
                ent = new AbstractMap.SimpleEntry<>(status, allSpeeches);
            } catch (NullPointerException | IOException ex) {
                log.error(ex);
                throw ex;
            }
        }
        return ent;
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
        doc.add(new Field("ID", e.getKey(), Field.Store.YES, Field.Index.ANALYZED_NO_NORMS, Field.TermVector.YES));
        doc.add(new Field("TEXT", e.getValue().toString(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS));
        try {
            writer.addDocument(doc);
        } catch (IOException ex) {
            log.error(ex);
        }
        log.info("Document " + e.getKey() + " has been indexed successfully...");
    }

    public static void main(String[] args) throws Exception {
        StatusIndexer sti = new StatusIndexer("20122014");
    }
}