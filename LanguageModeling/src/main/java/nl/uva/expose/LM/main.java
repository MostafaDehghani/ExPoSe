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

    }
    
    
    public static void glm(String period) throws Exception {
        
        GeneralizedLM glm = new GeneralizedLM(period);
        LanguageModel all = glm.aSLM;
        Integer itNum = 1;
        String member = "";
        String party = glm.getMemParty(0);
        String status = glm.getPartyStatus(party);
        LanguageModel mSLM = glm.getMemSLM(member);
        LanguageModel mGLm = glm.getMemITDSPLM(member, itNum);
        LanguageModel pGLm = glm.getPartyITDSPLM(party, itNum);
        LanguageModel sGLm = glm.getStatITDSPLM(status, itNum);
        
        
        
//        LanguageModel memSLM = new SmoothedLM(glm.getStatSLM(status),all);
//        LanguageModel GLM = new SmoothedLM(glm.getStatITDSPLM(status,1),all);
        
        
       

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
        for (Entry<String, Double> e :  LM.getSorted()){
//                LM.getNormalizedLM().entrySet()) {
//                LM.getNormalizedTopK(50)) {
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

