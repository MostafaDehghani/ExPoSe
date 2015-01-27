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
public class allIndexer extends Indexer {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(allIndexer.class.getName());

    public allIndexer(String period) throws Exception {
        super(period, "a");
    }

    @Override
    protected void docIndexer() throws Exception {
        try {
            this.IndexDoc(this.getAllSpeech());
        } catch (Exception ex) {
            log.error(ex);
            throw ex;
        }
    }

    private StringBuilder getAllSpeech() throws NullPointerException, IOException {
        StringBuilder allSpeeches = new StringBuilder();
        for (Map.Entry<String, Speech> e : data.speeches.entrySet()) {
            Speech s = e.getValue();
            try {
                    allSpeeches.append(s.getSpeechText()).append("\n");
                    System.out.println(s.getSpeechId());
            } catch (NullPointerException ex) {
                log.error(ex);
                throw ex;
            }
        }
        return allSpeeches;
    }

    @Override
    protected void analyzerMapInitializer(Map<String, Analyzer> analyzerMap) {
        analyzerMap.put("ID", new KeywordAnalyzer());//StandardAnalyzer(Version.LUCENE_CURRENT));
    }

    @Override
    protected void IndexDoc(Object obj) throws Exception {
        StringBuilder s = (StringBuilder) obj;
        Document doc = new Document();
        if (s.toString().split("\\s+").length <= minDocLength) //Filtering small documents
        {
            return;
        }
        doc.add(new Field("ID", "all", Field.Store.YES, Field.Index.ANALYZED_NO_NORMS, Field.TermVector.YES));
        doc.add(new Field("TEXT", s.toString(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS));
        try {
            writer.addDocument(doc);
        } catch (IOException ex) {
            log.error(ex);
        }
        log.info("all speeches have been indexed as a single document");
    }

    public static void main(String[] args) throws Exception {
        allIndexer alli = new allIndexer("20062010");
    }
}