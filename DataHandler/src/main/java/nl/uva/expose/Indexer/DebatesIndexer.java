/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.expose.Indexer;

import java.io.IOException;
import java.util.Map;
import nl.uva.expose.entities.debate.Debate;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

/**
 *
 * @author Mostafa Dehghani
 */
public class DebatesIndexer extends Indexer {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(DebatesIndexer.class.getName());

    public DebatesIndexer(String period) throws Exception {
        super(period, "d");
    }

    @Override
    protected void docIndexer() throws Exception {
        try {
            for (Map.Entry<String, Debate> e : data.debates.entrySet()) {
                this.IndexDoc(e.getValue());
            }
        } catch (Exception ex) {
            log.error(ex);
            throw ex;
        }
    }

    @Override
    protected void analyzerMapInitializer(Map<String, Analyzer> analyzerMap) {
        analyzerMap.put("ID", new KeywordAnalyzer());//StandardAnalyzer(Version.LUCENE_CURRENT));
        analyzerMap.put("CHAIRMANID", new KeywordAnalyzer());//StandardAnalyzer(Version.LUCENE_CURRENT));
        analyzerMap.put("SESSIONNUM", new KeywordAnalyzer());//StandardAnalyzer(Version.LUCENE_CURRENT));
        analyzerMap.put("INVOLVEDMEMBERSID", new WhitespaceAnalyzer());
        analyzerMap.put("INVOLVEDPMEMBERSID", new WhitespaceAnalyzer());
        analyzerMap.put("PRESENTMEMBERSID", new WhitespaceAnalyzer());
        analyzerMap.put("SPEECHESID", new WhitespaceAnalyzer());
        analyzerMap.put("SCENESID", new WhitespaceAnalyzer());

    }

    @Override
    protected void IndexDoc(Object obj) throws Exception {
        Debate d = (Debate) obj;
        Document doc = new Document();
//        if (d.getAllSpeechs().toString().split("\\s+").length <= minDocLength) //Filtering small documents
//        {
//            return;
//        }
        doc.add(new Field("ID", d.getdId(), Field.Store.YES, Field.Index.ANALYZED_NO_NORMS, Field.TermVector.YES));
        doc.add(new Field("SESSIONNUM", d.getSessionNum(), Field.Store.YES, Field.Index.ANALYZED_NO_NORMS, Field.TermVector.YES));
        if (d.getChairmanId() == null) {
            d.setChairmanId("");
        }
        doc.add(new Field("CHAIRMANID", d.getChairmanId(), Field.Store.YES, Field.Index.ANALYZED_NO_NORMS, Field.TermVector.YES));
        doc.add(new Field("TITLE", d.getdTitle(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS));
        doc.add(new Field("TOPIC", d.getdTopic(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS));
        doc.add(new Field("TEXT", d.getAllMPSpeechs().toString(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS));
        doc.add(new Field("ALLTEXT", d.getAllSpeechs().toString(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS));
        doc.add(new Field("DATE", d.getDate().toString(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS));

        String inv = "";
        for (String s : d.getInvolvedMembersId()) {
            inv += s.trim() + " ";
        }
        doc.add(new Field("INVOLVEDMEMBERSID", inv, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS));

        String pinv = "";
        for (String s : d.getInvolvedPMembersId()) {
            pinv += s.trim() + " ";
        }
        doc.add(new Field("INVOLVEDPMEMBERSID", pinv, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS));

        String pres = "";
        for (String s : d.getPresentMembersId()) {
            pres += s.trim() + ",";
        }

        doc.add(new Field("PRESENTMEMBERSID", pres, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS));
        String spee = "";
        for (String s : d.getSpeechesId()) {
            spee += s.trim() + " ";
        }
        doc.add(new Field("SPEECHESID", spee, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS));

        String sce = "";
        for (String s : d.getScenesId()) {
            sce += s.trim() + " ";
        }
        doc.add(new Field("SCENESID", sce, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS));

        try {
            writer.addDocument(doc);
        } catch (IOException ex) {
            log.error(ex);
        }
        log.info("Document " + d.getdId() + " has been indexed successfully...");
    }

    public static void main(String[] args) throws Exception {
        DebatesIndexer di = new DebatesIndexer("20122014");
    }
}
