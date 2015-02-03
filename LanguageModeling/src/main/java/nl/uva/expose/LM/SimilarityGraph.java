/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.expose.LM;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Mostafa Dehghani
 */
public class SimilarityGraph {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(SimilarityGraph.class.getName());
    private String period;
    private DSPLM dsplm;

    public SimilarityGraph(String period) throws Exception {
        this.period = period;
        dsplm = new DSPLM(period);
    }

    public void graphFileMaker(String fileName, ArrayList<String> edges) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("/Users/Mosi/Desktop/SIGIR_SHORT/simGraph_" + this.period + "-" + fileName + ".csv")));
        bw.write("Source,Target,Weight,Weight_smoothed\n");
        for (String s : edges) {
            bw.write(s + "\n");
        }
        bw.close();
    }

    public void slmSimGraph() throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        for (int i = 0; i < this.dsplm.miReader.numDocs(); i++) {
            System.out.println(i);
            String mid1 = this.dsplm.miReader.document(i).get("ID");
            for (int j = 0; j < this.dsplm.miReader.numDocs(); j++) {
                if (i == j) {
                    continue;
                }
                String mid2 = this.dsplm.miReader.document(j).get("ID");
                LanguageModel m1;
                LanguageModel m2;
                //
                m1 = this.dsplm.getMemSLM(mid1);
                m2 = this.dsplm.getMemSLM(mid2);
                Divergence d = new Divergence(m1, m2);
                Double score = d.getJsdSimScore();
                //
                m1 = new SmoothedLM(this.dsplm.getMemSLM(mid1), this.dsplm.aSLM);
                m2 = new SmoothedLM(this.dsplm.getMemSLM(mid2), this.dsplm.aSLM);
                Divergence d_s = new Divergence(m1, m2);
                Double score_s = d_s.getJsdSimScore();
                lines.add(mid1 + "," + mid2 + "," + score + "," + score_s);
            }
        }
        this.graphFileMaker("slm", lines);

    }

    public void plmSimGraph_all() throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        for (int i = 0; i < this.dsplm.miReader.numDocs(); i++) {
            System.out.println(i);
            String mid1 = this.dsplm.miReader.document(i).get("ID");
            for (int j = 0; j < this.dsplm.miReader.numDocs(); j++) {
                if (i == j) {
                    continue;
                }
                String mid2 = this.dsplm.miReader.document(j).get("ID");
                LanguageModel m1;
                LanguageModel m2;
                //
                m1 = new ParsimoniousLM(this.dsplm.getMemSLM(mid1), this.dsplm.aSLM);
                m2 = new ParsimoniousLM(this.dsplm.getMemSLM(mid2), this.dsplm.aSLM);
                Divergence d = new Divergence(m1, m2);
                Double score = d.getJsdSimScore();
                //
                m1 = new SmoothedLM(m1, this.dsplm.aSLM);
                m2 = new SmoothedLM(m2, this.dsplm.aSLM);
                Divergence d_s = new Divergence(m1, m2);
                Double score_s = d_s.getJsdSimScore();
                lines.add(mid1 + "," + mid2 + "," + score + "," + score_s);
            }
        }
        this.graphFileMaker("plm_all", lines);
    }

    public void plmSimGraph_stat() throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        for (int i = 0; i < this.dsplm.miReader.numDocs(); i++) {
            System.out.println(i);
            String mid1 = this.dsplm.miReader.document(i).get("ID");
            for (int j = 0; j < this.dsplm.miReader.numDocs(); j++) {
                if (i == j) {
                    continue;
                }
                String mid2 = this.dsplm.miReader.document(j).get("ID");
                LanguageModel m1;
                LanguageModel m2;
                String m1pid = this.dsplm.getMemStatus(i);
                String m2pid = this.dsplm.getMemStatus(j);
                //
                m1 = new ParsimoniousLM(this.dsplm.getMemSLM(mid1), this.dsplm.getStatPLM(m1pid));
                m2 = new ParsimoniousLM(this.dsplm.getMemSLM(mid2), this.dsplm.getStatPLM(m2pid));
                Divergence d = new Divergence(m1, m2);
                Double score = d.getJsdSimScore();
                //
                m1 = new SmoothedLM(m1, this.dsplm.aSLM);
                m2 = new SmoothedLM(m2, this.dsplm.aSLM);
                Divergence d_s = new Divergence(m1, m2);
                Double score_s = d_s.getJsdSimScore();
                lines.add(mid1 + "," + mid2 + "," + score + "," + score_s);
            }
        }
        this.graphFileMaker("plm_status", lines);
    }

    public void plmSimGraph_party() throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        for (int i = 0; i < this.dsplm.miReader.numDocs(); i++) {
            System.out.println(i);
            String mid1 = this.dsplm.miReader.document(i).get("ID");
            for (int j = 0; j < this.dsplm.miReader.numDocs(); j++) {
                if (i == j) {
                    continue;
                }
                String mid2 = this.dsplm.miReader.document(j).get("ID");
                LanguageModel m1;
                LanguageModel m2;
                String m1pid = this.dsplm.getMemParty(i);
                String m2pid = this.dsplm.getMemParty(j);
                //
                m1 = new ParsimoniousLM(this.dsplm.getMemSLM(mid1), this.dsplm.getPartyPLM(m1pid));
                m2 = new ParsimoniousLM(this.dsplm.getMemSLM(mid2), this.dsplm.getPartyPLM(m2pid));
                Divergence d = new Divergence(m1, m2);
                Double score = d.getJsdSimScore();
                //
                m1 = new SmoothedLM(m1, this.dsplm.aSLM);
                m2 = new SmoothedLM(m2, this.dsplm.aSLM);
                Divergence d_s = new Divergence(m1, m2);
                Double score_s = d_s.getJsdSimScore();
                lines.add(mid1 + "," + mid2 + "," + score + "," + score_s);
            }
        }
        this.graphFileMaker("plm_party", lines);
    }

    public static void main(String[] args) throws Exception {
        SimilarityGraph sm = new SimilarityGraph("20122014");
        sm.slmSimGraph();
        sm.plmSimGraph_all();
        sm.plmSimGraph_stat();
        sm.plmSimGraph_party();
    }

}
