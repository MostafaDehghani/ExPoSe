/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.expose.clustering;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static nl.uva.expose.settings.Config.configFile;
import nl.uva.lucenefacility.IndexInfo;
import nl.uva.lucenefacility.MyAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldCollector;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author Mostafa Dehghani
 */
public class SimGraphMaker {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(SimGraphMaker.class.getName());
    private IndexReader ireader;
    private String field = "TEXT";
    private final Boolean stemming = Boolean.valueOf(configFile.getProperty("IF_STEMMING"));
    private final Boolean commonWordsRemoving = Boolean.valueOf(configFile.getProperty("IF_STOPWORD_REMOVING"));
    private Analyzer analyzer = null;
    private ArrayList<String> commonWs = null;
    private String period;

    public void setIreader(IndexReader ireader) {
        this.ireader = ireader;
    }

    private void setAnalyser() {

        try {
            MyAnalyzer myAnalyzer;
            if (commonWordsRemoving) {
                myAnalyzer = new MyAnalyzer(stemming, this.getCommonWords());
            } else {
                myAnalyzer = new MyAnalyzer(stemming);
            }
            this.analyzer = myAnalyzer.getAnalyzer(configFile.getProperty("CORPUS_LANGUAGE"));
        } catch (FileNotFoundException ex) {
            log.error(ex);
        }
    }

    public SimGraphMaker(String period) throws IOException {
        this.ireader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("INDEXES_PATH") + period + "/m")));
        this.setAnalyser();
        this.period = period;

    }

    public HashMap<String, Double> searchAndReturnResults(String queryText, String qId) throws IOException, ParseException {
        queryText = queryText.replaceAll("AND", "and").replaceAll("OR", "or").replaceAll("NOT", "not"); // to avoid boolean operation!
        QueryParser qParser = new QueryParser(Version.LUCENE_CURRENT, field, this.analyzer);
        BooleanQuery.setMaxClauseCount(queryText.split("\\s+").length);
        Query q = qParser.parse(QueryParser.escape(queryText));
        Similarity simFunc = new BM25Similarity();
        IndexSearcher isearcher = new IndexSearcher(this.ireader);
        isearcher.setSimilarity(simFunc);
        TopFieldCollector tfc = TopFieldCollector.create(Sort.RELEVANCE, ireader.numDocs(), true, true, true, false);
//            TopFieldCollector tfc = TopFieldCollector.create(Sort.RELEVANCE,20, true, true, true, false);
        isearcher.search(q, tfc);
        TopDocs results = tfc.topDocs();
        ScoreDoc[] hits = results.scoreDocs;
        return fillQueryResultList(hits, qId);
    }

    private HashMap<String, Double> fillQueryResultList(ScoreDoc[] hits, String qId) throws IOException {
        HashMap<String, Double> results = new HashMap<String, Double>();
        for (int i = 0; i < hits.length; i++) {
            Double Score = (double) hits[i].score;
            Document hitDoc = ireader.document(hits[i].doc);
            String docId = hitDoc.get("ID");
            if (qId.equals(docId)) {
                continue;
            }
            results.put(docId, Score);
        }
        return this.resNormalizer(results);
    }

    private HashMap<String, Double> resNormalizer(HashMap<String, Double> inScores) {
        Double sum = 0D;
        for (Map.Entry<String, Double> e : inScores.entrySet()) {
            sum += e.getValue();
        }
        for (Map.Entry<String, Double> e : inScores.entrySet()) {
            inScores.put(e.getKey(), e.getValue() / sum);
        }
        return inScores;
    }

    private ArrayList<String> getCommonWords() {
        if (this.commonWs == null) {
            try {
                IndexReader tmp_ireader = IndexReader.open(new SimpleFSDirectory(
                        new File(configFile.getProperty("INDEX_PATH"))));
                IndexInfo iInfo = new IndexInfo(tmp_ireader);
                commonWs = iInfo.getTopTerms_TF("TEXT", 50);
            } catch (IOException ex) {
                log.error(ex);
            }
        }
        return commonWs;
    }

    public void similarityGraphMaker() throws IOException, ParseException {
        FileWriter fileWritter = new FileWriter("/Users/Mosi/Desktop/SIGIR_SHORT/simGraph" + this.period + ".csv");
        BufferedWriter bw = new BufferedWriter(fileWritter);
        bw.write("Source,Target,Type,Weight,Category\n");
        HashMap<String, Double> res;
        String qId;
        String qText;
        Document hitDoc;
        for (int i = 0; i < ireader.numDocs(); i++) {
            hitDoc = ireader.document(i);
            qId = hitDoc.get("ID");
            qText = hitDoc.get("TEXT");
            /////////////////////////////////////////////
            if (qText.length() > 3500000) { //for "nl.m.02682"
                System.err.println("Shrinking long query: " + qId);
                System.out.println("query length: " + qText.length());
                qText = qText.substring(0, 3500000);

            }
            //////////////////////////////////////////////
            res = this.searchAndReturnResults(qText, qId);
            for (Map.Entry<String, Double> e : res.entrySet()) {
                bw.write(qId + "," + e.getKey() + ",Directed," + e.getValue() + ",Similarity\n");
            }
            System.out.println("qid " + qId + " is searched");
        }
        bw.close();
    }

    public static void main(String[] args) throws Exception {
        SimGraphMaker sgm = new SimGraphMaker("20062010");
        sgm.similarityGraphMaker();
    }
}
