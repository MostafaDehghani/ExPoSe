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
       main0("20122014");

    }

    
    public static void main0(String Period) throws Exception {
        IterativeDSPLM idsplm = new IterativeDSPLM(Period);
        DSPLM plm = new DSPLM(Period);
        
        LanguageModel PLM_vvd0 = plm.getPartySLM("nl.p.vvd");
//        LanguageModel PLM_vvd1 = plm.getPartyPLM("nl.p.vvd");
        LanguageModel PLM_vvd2 = idsplm.getPartyITDSPLM("nl.p.vvd",1);

        LanguageModel PLM_pvda0 = plm.getPartySLM("nl.p.pvda");
//        LanguageModel PLM_pvda1 = plm.getPartyPLM("nl.p.pvda");
        LanguageModel PLM_pvda2 = idsplm.getPartyITDSPLM("nl.p.pvda",1);
        
        LanguageModel PLM_cda0 = plm.getPartySLM("nl.p.cda");
//        LanguageModel PLM_cda1 = plm.getPartyPLM("nl.p.cda");
        LanguageModel PLM_cda2 = idsplm.getPartyITDSPLM("nl.p.cda",1);
        
        LanguageModel PLM_pvv0 = plm.getPartySLM("nl.p.pvv");
//        LanguageModel PLM_pvv1 = plm.getPartyPLM("nl.p.pvv");
        LanguageModel PLM_pvv2 = idsplm.getPartyITDSPLM("nl.p.pvv",1);
        
        LanguageModel PLM_sp0 = plm.getPartySLM("nl.p.sp");
//        LanguageModel PLM_sp1 = plm.getPartyPLM("nl.p.sp");
        LanguageModel PLM_sp2 = idsplm.getPartyITDSPLM("nl.p.sp",1);
        
        LanguageModel PLM_d660 = plm.getPartySLM("nl.p.d66");
//        LanguageModel PLM_d661 = plm.getPartyPLM("nl.p.d66");
        LanguageModel PLM_d662 = idsplm.getPartyITDSPLM("nl.p.d66",1);
        
        LanguageModel PLM_cu0 = plm.getPartySLM("nl.p.cu");
//        LanguageModel PLM_cu1 = plm.getPartyPLM("nl.p.cu");
        LanguageModel PLM_cu2 = idsplm.getPartyITDSPLM("nl.p.cu",1);
        
        LanguageModel PLM_gl0 = plm.getPartySLM("nl.p.gl");
//        LanguageModel PLM_gl1 = plm.getPartyPLM("nl.p.gl");
        LanguageModel PLM_gl2 = idsplm.getPartyITDSPLM("nl.p.gl",1);

        
        HashMap<Integer, String> lines = new HashMap<>();
        lines = csvCreator(lines, PLM_vvd0, "SLM");
//        lines = csvCreator(lines, PLM_vvd1, "PLM");
        lines = csvCreator(lines, PLM_vvd2, "DSPLM");
        lines = csvCreator(lines, PLM_pvda0, "SLM");
//        lines = csvCreator(lines, PLM_pvda1, "PLM");
        lines = csvCreator(lines, PLM_pvda2, "DSPLM");
        lines = csvCreator(lines, PLM_cda0, "SLM");
//        lines = csvCreator(lines, PLM_cda1, "PLM");
        lines = csvCreator(lines, PLM_cda2, "DSPLM");
        lines = csvCreator(lines, PLM_pvv0, "SLM");
//        lines = csvCreator(lines, PLM_pvv1, "PLM");
        lines = csvCreator(lines, PLM_pvv2, "DSPLM");
        lines = csvCreator(lines, PLM_sp0, "SLM");
//        lines = csvCreator(lines, PLM_sp1, "PLM");
        lines = csvCreator(lines, PLM_sp2, "DSPLM");
        lines = csvCreator(lines, PLM_d660, "SLM");
//        lines = csvCreator(lines, PLM_d661, "PLM");
        lines = csvCreator(lines, PLM_d662, "DSPLM");
        lines = csvCreator(lines, PLM_cu0, "SLM");
//        lines = csvCreator(lines, PLM_cu1, "PLM");
        lines = csvCreator(lines, PLM_cu2, "DSPLM");
        lines = csvCreator(lines, PLM_gl0, "SLM");
//        lines = csvCreator(lines, PLM_gl1, "PLM");
        lines = csvCreator(lines, PLM_gl2, "DSPLM");
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("/Users/Mosi/Desktop/SIGIR_SHORT/lms_"+Period+".csv")));
        for (Map.Entry<Integer, String> e : lines.entrySet()) {
            bw.write(e.getValue() + "\n");
        }
        bw.close();
    }

    
    public static void main1(String Period) throws Exception {
        DSPLM hplm = new DSPLM(Period);
        
//        LanguageModel aPLM = hplm.getparliamentDoubleSidedPLM();
        LanguageModel oldOpoPLM = hplm.getStatPLM("Oposition");
        LanguageModel newOpoPLM = hplm.getStatDoubleSidedPLM("Oposition");
        LanguageModel OpoSLM = hplm.getStatSLM("Oposition");
        LanguageModel CoaSLM = hplm.getStatSLM("Coalition");
        LanguageModel oldCoaPLM = hplm.getStatPLM("Coalition");
        LanguageModel newCoaPLM = hplm.getStatDoubleSidedPLM("Coalition");
        HashMap<Integer, String> lines = new HashMap<>();
//        lines = csvCreator(lines, aPLM, "aPLM");
        lines = csvCreator(lines, OpoSLM, "OpoSLM");
        lines = csvCreator(lines, oldOpoPLM, "OpoPLM");
        lines = csvCreator(lines, newOpoPLM, "OpoDSPLM");
        lines = csvCreator(lines, CoaSLM, "CoaSLM");
        lines = csvCreator(lines, oldCoaPLM, "CoaPLM");
        lines = csvCreator(lines, newCoaPLM, "CoaDSPLM");
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
            header = cName + ", ";
        } else {
            header += ",," + cName + ", ";
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

