/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package old;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import nl.uva.expose.lm.GeneralizedLM;
import nl.uva.expose.lm.LanguageModel;
import nl.uva.expose.lm.SmoothedLM;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Mostafa Dehghani
 */
public class main3 {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(main3.class.getName());

    public static void main(String[] args) throws IOException, Exception {
         glm("20122014");
    }
    
    public static void glm(String period) throws Exception {
//        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("/Users/Mosi/Desktop/ICTIR/Output/train_" +period+".csv")));
        GeneralizedLM glm = new GeneralizedLM(period);
        LanguageModel CLM = glm.aSLM;
        Integer itNum = 1;

            LanguageModel pGLM1 = glm.getPartyGLM("nl.p.vvd", itNum);
            LanguageModel pGLM2 = glm.getPartyGLM("nl.p.pvda", itNum);
            LanguageModel pGLM3 = glm.getPartyGLM("nl.p.cda", itNum);
            LanguageModel pGLM4 = glm.getPartyGLM("nl.p.vvd", itNum);
            LanguageModel pGLM5 = glm.getPartyGLM("nl.p.sp", itNum);
            LanguageModel pGLM6 = glm.getPartyGLM("nl.p.d66", itNum);
            LanguageModel pGLM7 = glm.getPartyGLM("nl.p.gl", itNum);
            LanguageModel pGLM8 = glm.getPartyGLM("nl.p.cu", itNum);

            HashSet<String> allterms = new HashSet<>();
            allterms.addAll(pGLM1.LanguageModel.keySet());
            allterms.addAll(pGLM2.LanguageModel.keySet());
            allterms.addAll(pGLM3.LanguageModel.keySet());
            allterms.addAll(pGLM4.LanguageModel.keySet());
            allterms.addAll(pGLM5.LanguageModel.keySet());
            allterms.addAll(pGLM6.LanguageModel.keySet());
            allterms.addAll(pGLM7.LanguageModel.keySet());
            allterms.addAll(pGLM8.LanguageModel.keySet());
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File("/Users/Mosi/Desktop/ICTIR/Output/Final/parties" + "_" +period+".csv")));
//            BufferedWriter bw = new BufferedWriter(new FileWriter(new File("/Users/Mosi/Desktop/ICTIR/Output/GLMss.csv")));
            String line = "";
//            bw.write("class,member,party,status\n");
//            bw.write("\"term\", \"tID\", \"CLM\",\"SLM\",\"SLMm\",\"SLMp\",\"SLMs\",\"SLMa\",\"mGLM\",\"pGLM\",\"sGLM\",\"aGLM\"\n");
            bw.write("\"term\", \"tID\"\n");
            int tID = 0;
            for(Entry<String,Double> e: CLM.getSorted()){
                  String term = e.getKey();
                  if(!allterms.contains(term))
                      continue;
//                line = mSLM.getProb(term) + " 1:" + mGLM.getProb(term) + " 2:" + pGLM.getProb(term) + " 3:" + sGLM.getProb(term) + " #" + term + "\n";   
//                line = term + mSLM.getProb(term,round) + "," + mGLM.getProb(term,round) + "," + pGLM.getProb(term,round) + "," + sGLM.getProb(term,round) + "\n";   
                  line = "" 
                     + "\""  + term + "\"" 
                     + ",\"" + tID + "\""
                     + ",\"" + pGLM1.getProb(term) + "\""
                     + ",\"" + pGLM2.getProb(term) + "\""
                     + ",\"" + pGLM3.getProb(term) + "\""
                     + ",\"" + pGLM4.getProb(term) + "\""
                     + ",\"" + pGLM5.getProb(term) + "\""
                     + ",\"" + pGLM6.getProb(term) + "\""
                     + ",\"" + pGLM7.getProb(term) + "\""
                     + ",\"" + pGLM8.getProb(term) + "\""
                     + "\n";   
                bw.write(line);
                tID++;
            }
//            break;
//        }
        
        bw.close();
        
    }
    
    
    public static void glm2(String period) throws Exception {
        GeneralizedLM glm = new GeneralizedLM(period);
        LanguageModel CLM = glm.aSLM;
        Integer itNum = 1;
            String memberId =  "nl.m.02316"; // glm.getMemID(i); //
            String partyId = glm.getMemParty(memberId);
            String statusId = glm.getMemStatus(memberId);
            LanguageModel mSLM = new SmoothedLM(glm.getMemSLM(memberId),CLM);
            LanguageModel mGLM = new SmoothedLM(glm.getMemGLM(memberId, itNum),CLM);
            LanguageModel pGLM = new SmoothedLM(glm.getPartyGLM(partyId, itNum),CLM);
            LanguageModel sGLM = new SmoothedLM(glm.getStatGLM(statusId, itNum),CLM);

            LanguageModel mixedGLM = new LanguageModel();
            HashSet<String> allterms = new HashSet<>();
            allterms.addAll(CLM.LanguageModel.keySet());
            
            for(String term: allterms){
                Double mixedProb = ((0.4613/2.6754) * mGLM.getProb(term)) + ((1.8779/2.6754) * mGLM.getProb(term)) + ((0.3362/2.6754) * mGLM.getProb(term));
                mixedGLM.LanguageModel.put(term, mixedProb);
            }
            HashMap<String,Double> mixGLM = new HashMap<>();
            mixGLM.putAll(mixedGLM.getNormalizedLM());
            
           
          
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File("/Users/Mosi/Desktop/ICTIR/Output/LM4_" + memberId + "_" +period+".csv")));
            String line = "";
            bw.write("\"term\",\"CLM\",\"SLM\",\"MixedGLM\",\"mGLM\",\"pGLM\",\"sGLM\"\n");
            for(Entry<String,Double> e: CLM.getSorted()){
                String term = e.getKey();
                Double mixedProb = mixGLM.get(term);
                if(mixedProb == null)
                    mixedProb = 0D;
                line = "\"" + term + "\",\"" 
                     + CLM.getProb(term)+ "\",\"" 
                     + mSLM.getProb(term) + "\",\"" 
                     + mixedProb+ "\",\"" 
                     + mGLM.getProb(term) + "\",\"" 
                     + pGLM.getProb(term) + "\",\"" 
                     + sGLM.getProb(term) + "\"\n";   
                bw.write(line);
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
//                LM.getSorted()){
//                LM.getNormalizedLM().entrySet()) {
                LM.getNormalizedTopK(30)) {
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

