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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import static nl.uva.expose.settings.Config.configFile;
import nl.uva.lucenefacility.IndexInfo;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.SimpleFSDirectory;
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
    private HashMap<String, Integer> memDebNum = new HashMap<String, Integer>();

    public CoDebateGraphMaker(String period) throws IOException, ParserConfigurationException, SAXException, ParseException, XPathExpressionException {
        try {
            this.period = period;
            mireader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("INDEXES_PATH") + period + "/m")));
            direader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("INDEXES_PATH") + period + "/d")));
            this.diInfo = new IndexInfo(direader);
        } catch (IOException ex) {
            log.error(ex);
            throw ex;
        }
    }

    public HashMap<HashSet<String>, Integer> edges = new HashMap<>();

    public void graphGen() throws IOException {
        for (int i = 0; i < this.direader.numDocs(); i++) {
            HashSet<String> invPmem = this.diInfo.getDocAllTerm(i, "INVOLVEDPMEMBERSID");
            this.debCounter(invPmem);
            for (String m1id : invPmem) {
                for (String m2id : invPmem) {
                    if (m1id.equals(m2id)) {
                        continue;
                    }
                    HashSet<String> nodes = new HashSet<>(Arrays.asList(new String[]{m1id, m2id}));
                    Integer w = this.edges.get(nodes);
                    if (w == null) {
                        w = 0;
                    }
                    this.edges.put(nodes, ++w);
                }
            }
        }
        FileWriter fileWritter = new FileWriter("/Users/Mosi/Desktop/SIGIR_SHORT/debGraph" + this.period + ".csv");
        BufferedWriter bw = new BufferedWriter(fileWritter);
        bw.write("Source,Target,Type,Weight,Category\n");
        for (Entry<HashSet<String>, Integer> e : this.edges.entrySet()) {
            ArrayList<String> nodes = new ArrayList<>();
            for (String nid : e.getKey()) {
                nodes.add(nid);
            }
            Integer unCnt = (this.memDebNum.get(nodes.get(0)) + this.memDebNum.get(nodes.get(1))) - e.getValue();
            Double w = e.getValue().doubleValue() / unCnt.doubleValue();
            bw.write(nodes.get(0) + "," + nodes.get(1) + ",Undirected," + w + ",Codebate\n");
        }
        bw.close();
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
        CoDebateGraphMaker cdgk = new CoDebateGraphMaker("20062010");
        cdgk.graphGen();
    }
}
