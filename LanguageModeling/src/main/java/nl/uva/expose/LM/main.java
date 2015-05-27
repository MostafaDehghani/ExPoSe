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
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Mostafa Dehghani
 */
public class main {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(main.class.getName());

    public static void main(String[] args) throws IOException, Exception {
         glm("20122014");
    }
    
    public static void glm(String period) throws Exception {
//        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("/Users/Mosi/Desktop/ICTIR/Output/train_" +period+".csv")));
        GeneralizedLM glm = new GeneralizedLM(period);
        LanguageModel CLM = glm.aSLM;
        Integer itNum = 1;
//        for(int i=0; i<glm.miReader.numDocs();i++){
//            String memberId =  "nl.m.02258"; // glm.getMemID(i); //
            String memberId =  "nl.m.02335"; // glm.getMemID(i); //
            
            
//            String memberId2 =  "nl.m.03301"; // glm.getMemID(i); //
//            String memberId =  "nl.m.02316"; // glm.getMemID(i); //

//            System.out.println(i + " "  + memberId);
            String partyId = glm.getMemParty(memberId);
            String statusId = glm.getMemStatus(memberId);

//            LanguageModel aSLM = glm.ge
            LanguageModel mSLM = glm.getMemSLM(memberId);
            LanguageModel mGLM = glm.getMemGLM(memberId, itNum);
            LanguageModel pGLM = glm.getPartyGLM(partyId, itNum);
            LanguageModel sGLM = glm.getStatGLM(statusId, itNum);
            LanguageModel aGLM = glm.getAllGLM(itNum);
            
            //
            LanguageModel SLMm = new AspectAwareLM1(mSLM, mGLM);
            LanguageModel SLMp = new AspectAwareLM1(mSLM, pGLM);
            LanguageModel SLMs = new AspectAwareLM1(mSLM, sGLM);
            LanguageModel SLMa = new AspectAwareLM1(mSLM, aGLM);
            
    //        

//            LanguageModel mSLM = new SmoothedLM(glm.getMemSLM(memberId),all);
//            LanguageModel mGLM = new SmoothedLM(glm.getMemGLM(memberId, itNum),all);
//            LanguageModel pGLM = new SmoothedLM(glm.getPartyGLM(partyId, itNum),all);
//            LanguageModel sGLM = new SmoothedLM(glm.getStatGLM(statusId, itNum),all);


            HashSet<String> allterms = new HashSet<>();
//            allterms.addAll(all.LanguageModel.keySet());
//            allterms.addAll(mSLM.LanguageModel.keySet());
            allterms.addAll(mGLM.LanguageModel.keySet());
            allterms.addAll(pGLM.LanguageModel.keySet());
            allterms.addAll(sGLM.LanguageModel.keySet());
            allterms.addAll(aGLM.LanguageModel.keySet());

        
         
            
            /*
            HashMap<Integer, String> lines = new HashMap<>();
        lines = csvCreator(lines, mGLM , "mGLM");
        lines = csvCreator(lines, pGLM , "pGLM");
        lines = csvCreator(lines, sGLM , "sGLM");
        lines = csvCreator(lines, aGLM , "aGLM");
        //
        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("/Users/Mosi/Desktop/ICTIR/Output/example_"+period+".csv")));
        for (Map.Entry<Integer, String> e : lines.entrySet()) {
            bw.write(e.getValue() + "\n");
        }
        bw.close();
          
        
            */
            
//            BufferedWriter bw = new BufferedWriter(new FileWriter(new File("/Users/Mosi/Desktop/ICTIR/Output/train_" + memberId + "_" +period+".csv")));
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File("/Users/Mosi/Desktop/ICTIR/Output/GLMs_2.csv")));
            String line = "";
//            bw.write("class,member,party,status\n");
//            bw.write("\"term\", \"tID\", \"CLM\",\"SLM\",\"SLMm\",\"SLMp\",\"SLMs\",\"SLMa\",\"mGLM\",\"pGLM\",\"sGLM\",\"aGLM\"\n");
            bw.write("\"term\", \"tID\",\"mGLM\",\"pGLM\",\"sGLM\",\"aGLM\"\n");
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
//                     + ",\"" + CLM.getProb(term)+ "\""
//                     + ",\"" + mSLM.getProb(term) + "\""
//                     + ",\"" + SLMm.getProb(term) + "\"" 
//                     + ",\"" + SLMp.getProb(term) + "\"" 
//                     + ",\"" + SLMs.getProb(term) + "\"" 
//                     + ",\"" + SLMa.getProb(term) + "\"" 
                     + ",\"" + mGLM.getProb(term) + "\""
                     + ",\"" + pGLM.getProb(term) + "\""
                     + ",\"" + sGLM.getProb(term) + "\""  
                     + ",\"" + aGLM.getProb(term) + "\""
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

