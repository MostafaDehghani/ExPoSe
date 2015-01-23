/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.expose.Indexer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import nl.uva.expose.data.Data;
import static nl.uva.expose.settings.Config.configFile;
import nl.uva.lucenefacility.IndexInfo;
import nl.uva.lucenefacility.MyAnalyzer;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

/**
 *
 * @author Mostafa Dehghani
 */
public abstract class Indexer {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Indexer.class.getName());
    protected IndexWriter writer;
    private final Boolean stemming = Boolean.valueOf(configFile.getProperty("IF_STEMMING"));
    private final Boolean commonWordsRemoving = Boolean.valueOf(configFile.getProperty("IF_STOPWORD_REMOVING"));
    private final Integer commonWordNum = Integer.parseInt(configFile.getProperty("COMMON_WORDS_NUM"));
    protected final Integer minDocLength = Integer.parseInt(configFile.getProperty("MIN_DOC_LENGTH"));
    private Map<String, Analyzer> analyzerMap = new HashMap<>();
    protected Data data;

    public Indexer(String period, String indexType) throws Exception {
        try {
            this.data = new Data(period);
            this.analyzerMapInitializer(this.analyzerMap);
            this.Indexer(period, indexType);
        } catch (Exception ex) {
            log.error(ex);
            throw ex;
        }
    }

    public void Indexer(String period, String indexType) throws Exception {
        try {
            log.info("----------------------- INDEXING--------------------------");
            new IndexesCleaner(period, indexType);

            String indexPath = configFile.getProperty("INDEXES_PATH") + period + "/" + indexType;

            //Without Stopwords (In order to make list of common words)
            //
            //
            MyAnalyzer myAnalyzer_noStoplist = new MyAnalyzer(stemming);
            Analyzer analyzer_1 = myAnalyzer_noStoplist.getAnalyzer(configFile.getProperty("CORPUS_LANGUAGE"));
            PerFieldAnalyzerWrapper prfWrapper_1 = new PerFieldAnalyzerWrapper(analyzer_1, analyzerMap);
            IndexWriterConfig irc_1 = new IndexWriterConfig(Version.LUCENE_CURRENT, prfWrapper_1);
            this.writer = new IndexWriter(new SimpleFSDirectory(new File(indexPath)), irc_1);
            this.docIndexer();
            this.writer.commit();
            this.writer.close();
            analyzer_1.close();
            prfWrapper_1.close();
            log.info("-------------------------------------------------");
            log.info("Index without common words removing is created successfully...");
            log.info("-------------------------------------------------");
            //

            if (commonWordsRemoving) {
                String tmpIndexPath = configFile.getProperty("INDEXES_PATH") + period + "/tmp";
                FileUtils.forceMkdir(new File(tmpIndexPath));
                IndexReader ireader = IndexReader.open(new SimpleFSDirectory(new File(indexPath)));
                IndexInfo iInfo = new IndexInfo(ireader);
                ArrayList<String> commonWs = iInfo.getTopTerms_TF("TEXT", this.commonWordNum);
                MyAnalyzer myAnalyzer_Stoplist = new MyAnalyzer(stemming, commonWs);
                Analyzer analyzer_2 = myAnalyzer_Stoplist.getAnalyzer(configFile.getProperty("CORPUS_LANGUAGE"));
                PerFieldAnalyzerWrapper prfWrapper_2 = new PerFieldAnalyzerWrapper(analyzer_2, analyzerMap);
                IndexWriterConfig irc_2 = new IndexWriterConfig(Version.LUCENE_CURRENT, prfWrapper_2);
                this.writer = new IndexWriter(new SimpleFSDirectory(new File(tmpIndexPath)), irc_2);
                this.docIndexer();
                this.writer.commit();
                this.writer.close();
                analyzer_2.close();
                prfWrapper_2.close();
                FileUtils.deleteDirectory(new File(indexPath));
                File index = new File(tmpIndexPath);
                File newIndex = new File(indexPath);
                index.renameTo(newIndex);
                log.info("-------------------------------------------------");
                log.info("Index with common words removing is created successfully...");
                log.info("-------------------------------------------------");
            }
        } catch (Exception ex) {
            log.error(ex);
            throw ex;
        }
    }

    protected abstract void docIndexer() throws Exception;

    protected abstract void IndexDoc(Object obj) throws Exception;

    protected abstract void analyzerMapInitializer(Map<String, Analyzer> analyzerMap);
}
