/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.expose.Indexer;

import java.io.IOException;
import java.util.AbstractMap;
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
public class PartiesPosIndexer extends Indexer {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(PartiesPosIndexer.class.getName());   
    private HashMap<String,Map.Entry<StringBuilder,Integer>> ps;
    public PartiesPosIndexer(String period) throws Exception {
        super(period,"pp");
    }

    @Override
    protected void docIndexer() throws Exception {
        ps = new HashMap<>();
        Map.Entry<StringBuilder, Integer> empty = new AbstractMap.SimpleEntry<StringBuilder, Integer>(new StringBuilder(), 0);
        ps.put("parl", empty );
        ps.put("gov",empty);
        ps.put("opos",empty);
        ps.put("coal",empty);
        
        try {
            for(Map.Entry<String,Speech> e:data.speeches.entrySet()){
                Speech s = e.getValue();
                try{
                    String sAff = s.getSpeakerAffiliation();
                    if(sAff.equals("")|| sAff==null){
                        continue;
                    }
                    else if(sAff.equals("parl")){
                        Map.Entry<StringBuilder, Integer> en = ps.get("parl");
                        en = new AbstractMap.SimpleEntry<>(en.getKey().append(s.getSpeechText()).append("\n"),en.getValue()+1);
                        ps.put("parl",en);
                    }
                    else if(sAff.equals("gov")){
                        Map.Entry<StringBuilder, Integer> en = ps.get("gov");
                        en = new AbstractMap.SimpleEntry<>(en.getKey().append(s.getSpeechText()).append("\n"),en.getValue()+1);
                        ps.put("gov",en);
                    }
                    else if( sAff.equals("nl.p.cda") || sAff.equals("nl.p.pvda")){
                        Map.Entry<StringBuilder, Integer> en = ps.get("coal");
                        en = new AbstractMap.SimpleEntry<>(en.getKey().append(s.getSpeechText()).append("\n"),en.getValue()+1);
                        ps.put("coal",en);
                    }
                    else 
                    {
                        System.out.println(sAff);
                         Map.Entry<StringBuilder, Integer> en = ps.get("opos");
                        en = new AbstractMap.SimpleEntry<>(en.getKey().append(s.getSpeechText()).append("\n"),en.getValue()+1);
                        ps.put("opos",en);
                    }
                }
                catch(NullPointerException ex){
                    log.error(ex);
                    log.error("Error in speach:" + s.getSpeechId() );
                }
            }
            for(Map.Entry<String,Map.Entry<StringBuilder, Integer>> e :ps.entrySet()){
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
        Map.Entry<String,Map.Entry<StringBuilder, Integer>> e = (Map.Entry<String,Map.Entry<StringBuilder, Integer>>) obj;
        Document doc = new Document();
        if(e.getValue().toString().split("\\s+").length <= minDocLength)  //Filtering small documents
            return;
        doc.add(new Field("ID", e.getKey() ,Field.Store.YES, Field.Index.ANALYZED_NO_NORMS, Field.TermVector.YES));
        doc.add(new Field("SPEECHNUM", e.getValue().getValue().toString(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS));
        doc.add(new Field("TEXT", e.getValue().getKey().toString(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS));
        try {
            writer.addDocument(doc);
        } catch (IOException ex) {
            log.error(ex);
        }
        log.info("Document " + e.getKey() + " has been indexed successfully...");
    }
    public static void main(String[] args) throws Exception {
        PartiesPosIndexer pi = new PartiesPosIndexer("20062010");
    }
}
