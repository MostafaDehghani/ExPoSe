/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.expose.LM.analysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import nl.uva.expose.LM.GeneralizedLM;
import nl.uva.expose.LM.LanguageModel;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Mostafa Dehghani
 */
public class LanguageModelsSize {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(LanguageModelsSize.class.getName());

    public static void main(String[] args) throws IOException, Exception {
         glm("20122014");
    }
    
    public static void glm(String period) throws Exception {
        GeneralizedLM glm = new GeneralizedLM(period);
        Integer itNum = 1;
            String memberId =  "nl.m.02335"; // Ir. DM (Diederik) Samson (PvdA)
            String memberId2 =  "nl.m.02316"; // Mr. S. (Sybrand) van Haersma Buma (CDA)

            String partyId = glm.getMemParty(memberId);
            System.out.println(partyId);
            String statusId = glm.getMemStatus(memberId);
            System.out.println(statusId);
            
            String partyId2 = glm.getMemParty(memberId2);
            String statusId2 = glm.getMemStatus(memberId2);
            System.out.println(partyId2);
            System.out.println(statusId2);

            LanguageModel mSLM = glm.getMemSLM(memberId);
            LanguageModel mGLM = glm.getMemGLM(memberId, itNum);
            LanguageModel pSLM = glm.getPartySLM(partyId);
            LanguageModel pGLM = glm.getPartyGLM(partyId, itNum);
            LanguageModel sSLM = glm.getStatSLM(statusId);
            LanguageModel sGLM = glm.getStatGLM(statusId, itNum);
            LanguageModel aSLM = glm.getAllSLM();
            LanguageModel aGLM = glm.getAllGLM(itNum);
           

            LanguageModel mSLM2 = glm.getMemSLM(memberId2);
            LanguageModel mGLM2 = glm.getMemGLM(memberId2, itNum);
            LanguageModel pSLM2 = glm.getPartySLM(partyId2);
            LanguageModel pGLM2 = glm.getPartyGLM(partyId2, itNum);
            LanguageModel sSLM2 = glm.getStatSLM(statusId2);
            LanguageModel sGLM2 = glm.getStatGLM(statusId2, itNum);
            
            System.out.println(mSLM.LanguageModel.size());
            System.out.println(mGLM.LanguageModel.size());
            System.out.println(pSLM.LanguageModel.size());
            System.out.println(pGLM.LanguageModel.size());
            System.out.println(sSLM.LanguageModel.size());
            System.out.println(sGLM.LanguageModel.size());
            System.out.println(aSLM.LanguageModel.size());
            System.out.println(aGLM.LanguageModel.size());
                HashMap<Integer, String> lines = new HashMap<>();
                lines = csvCreator(lines, mGLM , "mGLM");
                lines = csvCreator(lines, pGLM , "pGLM");
                lines = csvCreator(lines, sGLM , "sGLM");
                lines = csvCreator(lines, aGLM , "aGLM");
                lines = csvCreator(lines, mGLM2 , "mGLM2");
                lines = csvCreator(lines, pGLM2 , "pGLM2");
                lines = csvCreator(lines, sGLM2 , "sGLM2");

                //
                BufferedWriter bw = new BufferedWriter(new FileWriter(new File("/Users/Mosi/Desktop/ICTIR2015/example_"+period+".csv")));
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
        for (Entry<String, Double> e : 
                LM.getSorted()){
//                LM.getNormalizedLM().entrySet()) {
//                LM.getNormalizedTopK(30)) {
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
}

