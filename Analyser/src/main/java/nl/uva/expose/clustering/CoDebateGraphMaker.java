/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.expose.clustering;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import static nl.uva.expose.settings.Config.configFile;
import nl.uva.lucenefacility.IndexInfo;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.BytesRef;
import org.xml.sax.SAXException;

/**
 *
 * @author Mostafa Dehghani
 */
public class CoDebateGraphMaker {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(CoDebateGraphMaker.class.getName());
    private IndexReader mireader;
    private IndexReader direader;
    private IndexInfo diInfo;
    private String period;
    private HashSet<String> mids = new HashSet<>();
    private HashMap<String, Integer> memDebNum = new HashMap<String, Integer>();

    public CoDebateGraphMaker(String period) throws IOException, ParserConfigurationException, SAXException, ParseException, XPathExpressionException {
        try {
            this.period = period;
            mireader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("INDEXES_PATH") + period + "/m")));
            direader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("INDEXES_PATH") + period + "/d")));
            this.diInfo = new IndexInfo(direader);
            this.loadMIds();
        } catch (IOException ex) {
            log.error(ex);
            throw ex;
        }
    }

    public HashMap<String, Integer> edges = new HashMap<>();

    public void graphGen() throws IOException {
        
        for(int i=0; i<this.direader.numDocs();i++){
            HashSet<String> invPmem = this.diInfo.getDocAllTerm(i, "INVOLVEDPMEMBERSID");
            for (String m1id : invPmem) {
                if (!this.mids.contains(m1id)) {
                    continue;
                }
                for (String m2id :invPmem) {
                    if (!this.mids.contains(m2id)) {
                        continue;
                    }
                    if (m1id.equals(m2id)) {
                        continue;
                    }
//                    System.out.println(m1id + "-" + m2id);
                    Integer w = this.edges.get(m1id + "," + m2id);
                    if (w == null) {
                        w = this.edges.get(m2id + "," + m1id);
                        if (w == null) {
                            w = 0;
                        }
                        this.edges.put(m2id + "," + m1id, ++w);
                        continue;
                    }
                    this.edges.put(m1id + "," + m2id, ++w);
                }
            }
        }
        FileWriter fileWritter = new FileWriter("/Users/Mosi/Desktop/SIGIR_SHORT/debGraph" + this.period + ".csv");
        BufferedWriter bw = new BufferedWriter(fileWritter);
        bw.write("Source,Target,Type,Weight,Category\n");
        for (Entry<String, Integer> e : this.edges.entrySet()) {
            String[] mids = e.getKey().split(",");
            Integer unCnt = (this.memDebNum.get(mids[0]) + this.memDebNum.get(mids[1])) - e.getValue();
            Double w = e.getValue().doubleValue() / unCnt.doubleValue();
            bw.write(e.getKey() + ",Undirected," + w + ",Codebate\n");
        }
        bw.close();
    }

    public void loadMIds() throws IOException {
        TermsEnum te = MultiFields.getTerms(this.mireader, "ID").iterator(null);
        BytesRef term;
        while ((term = te.next()) != null) {
            this.mids.add(term.utf8ToString());
        }
        System.out.println("");
    }
    
    public void debCounter(HashSet<String> debInMem) {
        for (String mid : debInMem) {
            Integer cnt = this.memDebNum.get(mid);
            if (cnt == null) {
                cnt = 0;
            }
            this.memDebNum.put(mid, cnt + 1);
        }
    }
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, ParseException, XPathExpressionException {
        CoDebateGraphMaker cdgk = new CoDebateGraphMaker("20122014");
        cdgk.graphGen();
    }
}
