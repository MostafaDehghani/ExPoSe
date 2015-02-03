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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import static nl.uva.expose.settings.Config.configFile;
import nl.uva.lucenefacility.IndexInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.SimpleFSDirectory;

/**
 *
 * @author Mostafa Dehghani
 */
public class main {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(main.class.getName());

    public static void main(String[] args) throws IOException, Exception {
       main1("20102012");

    }

    
    public static void main0(String Period) throws Exception {
        IterativeDSPLM hplm = new IterativeDSPLM(Period);
        
        LanguageModel aPLM0 = hplm.getAllITDSPLM(0);
        LanguageModel aPLM1 = hplm.getAllITDSPLM(1);
        LanguageModel aPLM2 = hplm.getAllITDSPLM(2);
        LanguageModel OpoPLM0 = hplm.getStatITDSPLM("Oposition", 3);
//        LanguageModel OpoPLM1 = hplm.getStatITDSPLM("Oposition", 1);
//        LanguageModel OpoPLM2 = hplm.getStatITDSPLM("Oposition", 2);

        
        HashMap<Integer, String> lines = new HashMap<>();
        lines = csvCreator(lines, aPLM0, "aPLM0");
        lines = csvCreator(lines, aPLM1, "aPLM1");
        lines = csvCreator(lines, aPLM2, "aPLM2");
//        lines = csvCreator(lines, OpoPLM2, "OpoPLM2");
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("/Users/Mosi/Desktop/SIGIR_SHORT/lms_"+Period+".csv")));
        for (Map.Entry<Integer, String> e : lines.entrySet()) {
            bw.write(e.getValue() + "\n");
        }
        bw.close();
    }

    
    public static void main1(String Period) throws Exception {
        DSPLM hplm = new DSPLM(Period);
        
        LanguageModel aPLM = hplm.getparliamentDoubleSidedPLM();
//        LanguageModel oldOpoPLM = hplm.getStatPLM("Oposition");
//        LanguageModel newOpoPLM = hplm.getStatDoubleSidedPLM("Oposition");
//        LanguageModel OpoSLM = hplm.getStatSLM("Oposition");
//        LanguageModel CoaSLM = hplm.getStatSLM("Coalition");
//        LanguageModel oldCoaPLM = hplm.getStatPLM("Coalition");
//        LanguageModel newCoaPLM = hplm.getStatDoubleSidedPLM("Coalition");
        HashMap<Integer, String> lines = new HashMap<>();
        lines = csvCreator(lines, aPLM, "aPLM");
//        lines = csvCreator(lines, OpoSLM, "OpoSLM");
//        lines = csvCreator(lines, oldOpoPLM, "OpoPLM");
//        lines = csvCreator(lines, newOpoPLM, "OpoDSPLM");
//        lines = csvCreator(lines, CoaSLM, "CoaSLM");
//        lines = csvCreator(lines, oldCoaPLM, "CoaPLM");
//        lines = csvCreator(lines, newCoaPLM, "CoaDSPLM");
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("/Users/Mosi/Desktop/SIGIR_SHORT/lms_"+Period+".csv")));
        for (Map.Entry<Integer, String> e : lines.entrySet()) {
            bw.write(e.getValue() + "\n");
        }
        bw.close();
    }

    private static Integer cNum = 0;

    public static HashMap<Integer, String> csvCreator(HashMap<Integer, String> lines, LanguageModel LM, String cName) {
        //
        String header = lines.get(0);
        if (cNum == 0) {
            header = cName + ":P(" + cName + ")";
        } else {
            header += ",," + cName + ":P(" + cName + ")";
        }
        lines.put(0, header);
        //
        Integer lineNum = 1;
        for (Entry<String, Double> e : LM.getTopK(1000)) {
            String line = lines.get(lineNum);
            if (cNum == 0) {
                line = "\"" + e.getKey() + "\"" + ":" + e.getValue();
            } else {
                int occurance = StringUtils.countMatches(line, ":");
                for (int i = 0; i < cNum - occurance; i++) {
                    if (line == null) {
                        line = ":";
                    } else {
                        line += ",,:";
                    }
                }
                line += ",," + "\"" + e.getKey() + "\"" + ":" + e.getValue();
            }
            lines.put(lineNum, line);
            lineNum++;
        }
        cNum++;
        return lines;
    }
    
    public static void main2() throws IOException {
         String period = "20062010";
        IndexReader miReader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("INDEXES_PATH") + period + "/m")));
        IndexReader piReader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("INDEXES_PATH") + period + "/p")));
        IndexReader siReader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("INDEXES_PATH") + period + "/st")));
        IndexInfo miInfo = new IndexInfo(miReader);
        IndexInfo piInfo = new IndexInfo(piReader);
        IndexInfo siInfo = new IndexInfo(siReader);
        StandardLM mSLM = new StandardLM(miReader, 50, "TEXT");
        StandardLM pSLM = new StandardLM(piReader, 3, "TEXT");
        StandardLM sSLM = new StandardLM(siReader, 0, "TEXT");
        CollectionLM aSLM = new CollectionLM(miReader, "TEXT");
        HashMap<String, Double> mtv = miInfo.getDocTermFreqVector(50, "TEXT");
        HashMap<String, Double> ptv = piInfo.getDocTermFreqVector(3, "TEXT");
        HashMap<String, Double> stv = siInfo.getDocTermFreqVector(0, "TEXT");

        //
//        ParsimoniousLM mPLMp = new ParsimoniousLM(mSLM, mSLM.LanguageModel, pSLM, 0.1D, 0.0005D, 100);
//        ParsimoniousLM mPLMs = new ParsimoniousLM(mSLM, mSLM.LanguageModel, sSLM, 0.1D, 0.0005D, 100);
        ParsimoniousLM mPLMa = new ParsimoniousLM(mSLM, mSLM.LanguageModel, aSLM, 0.1D, 0.0005D, 100);
//        ParsimoniousLM pPLMs = new ParsimoniousLM(pSLM, pSLM.LanguageModel, sSLM, 0.1D, 0.0005D, 100);
        ParsimoniousLM pPLMa = new ParsimoniousLM(pSLM, pSLM.LanguageModel, aSLM, 0.1D, 0.0005D, 100);
        ParsimoniousLM sPLMa = new ParsimoniousLM(sSLM, sSLM.LanguageModel, aSLM, 0.1D, 0.0005D, 100);
        //
        ParsimoniousLM pPLMas = new ParsimoniousLM(pPLMa, pPLMa.LanguageModel, sPLMa, 0.1D, 0.0005D, 100);
        //
        ParsimoniousLM mPLMas = new ParsimoniousLM(mPLMa, mPLMa.LanguageModel, sPLMa, 0.1D, 0.0005D, 100);
        //
        ParsimoniousLM mPLMasp = new ParsimoniousLM(mPLMas, mPLMas.LanguageModel, pPLMas, 0.1D, 0.0005D, 100);

        HashMap<Integer, String> lines = new HashMap<>();
        lines = csvCreator(lines, mSLM, "mSLM");
        lines = csvCreator(lines, mPLMasp, "mPLMasp");
        lines = csvCreator(lines, pSLM, "pSLM");
        lines = csvCreator(lines, pPLMas, "pPLMas");
        lines = csvCreator(lines, sSLM, "sSLM");
        lines = csvCreator(lines, sPLMa, "sPLMa");
        lines = csvCreator(lines, aSLM, "aSLM");
//        lines = csvCreator(lines,mPLMp,"mPLMp");
//        lines = csvCreator(lines,mPLMs,"mPLMs");
//        lines = csvCreator(lines,mPLMa,"mPLMa");
//        lines = csvCreator(lines,pPLMs,"pPLMs");
//        lines = csvCreator(lines,pPLMa,"pPLMa");

        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("/Users/Mosi/Desktop/SIGIR_SHORT/lms.csv")));
        for (Entry<Integer, String> e : lines.entrySet()) {
            bw.write(e.getValue() + "\n");
        }
        bw.close();
    }
        
    public static void main3() throws Exception{
        DSPLM hplm = new DSPLM("20102012");
        LanguageModel newOpoPLM = hplm.getStatDoubleSidedPLM("Oposition");
        LanguageModel newCoaPLM = hplm.getStatDoubleSidedPLM("Coalition");
        Divergence d1 = new Divergence(newCoaPLM, newCoaPLM);
        Divergence d2 = new Divergence(newOpoPLM, newCoaPLM);
        System.out.println(d1.getJsdSimScore());
        System.out.println(d1.getKldSimScore());
        System.out.println(d2.getJsdSimScore());
        System.out.println(d2.getKldSimScore());
    }
    
}

