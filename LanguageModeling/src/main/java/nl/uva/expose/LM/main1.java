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
public class main1 {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(main1.class.getName());

    public static void main(String[] args) throws IOException, Exception {
         glm("20122014");
    }
    
    public static void glm(String period) throws Exception {
//        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("/Users/Mosi/Desktop/ICTIR/Output/train_" +period+".csv")));
        GeneralizedLM glm = new GeneralizedLM(period);
        LanguageModel CLM = glm.aSLM;
        Integer itNum = 1;
//        for(int i=0; i<glm.miReader.numDocs();i++){
//            String memberId =  "nl.m.02258"; // G. (Geert) Wilders (PVV)
//            String memberId =  "nl.m.02415"; // Drs. EI (Edith) Schippers (VVD)
            String memberId =  "nl.m.02335"; // Ir. DM (Diederik) Samson (PvdA)
            
            
            
//            String memberId2 =  "nl.m.03301"; // Mr.drs. MCG (Mona) Keijzer (CDA-second Mem)
            String memberId2 =  "nl.m.02316"; // Mr. S. (Sybrand) van Haersma Buma (CDA)

//            System.out.println(i + " "  + memberId);
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
            
            if(true){
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
                return;
            
            }           
//            LanguageModel sSLM = glm.getStatSLM(statusId);
//            LanguageModel sSLM2 = glm.getStatSLM(statusId2);
            
//            LanguageModel SLMm1 = new AspectAwareLM1(mSLM, mGLM);
//            LanguageModel SLMp1 = new AspectAwareLM1(mSLM, pGLM);
//            LanguageModel SLMs1 = new AspectAwareLM1(mSLM, sGLM);
//            LanguageModel SLMa1 = new AspectAwareLM1(mSLM, aGLM);
            
//            LanguageModel SLMm2 = new AspectAwareLM1(mSLM2, mGLM2);
//            LanguageModel SLMp2 = new AspectAwareLM1(mSLM2, pGLM);
//            LanguageModel SLMs2 = new AspectAwareLM1(mSLM2, sGLM);
//            LanguageModel SLMa2 = new AspectAwareLM1(mSLM2, aGLM);

            HashSet<String> allterms = new HashSet<>();
//            allterms.addAll(all.LanguageModel.keySet());
//            allterms.addAll(mSLM.LanguageModel.keySet());
//            allterms.addAll(mGLM.LanguageModel.keySet());
//            allterms.addAll(pGLM.LanguageModel.keySet());
//            allterms.addAll(sGLM.LanguageModel.keySet());
//            allterms.addAll(aGLM.LanguageModel.keySet());
//            allterms.addAll(mGLM2.LanguageModel.keySet());
//            allterms.addAll(pGLM2.LanguageModel.keySet());
//            allterms.addAll(sGLM2.LanguageModel.keySet());
            allterms.addAll(sSLM.LanguageModel.keySet());
            allterms.addAll(sSLM2.LanguageModel.keySet());

        
         
            
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
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File("/Users/Mosi/Desktop/ICTIR/Output/Final/Statuses" + "_" +period+".csv")));
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
//                     + ",\"" + CLM.getProb(term)+ "\""
//                     + ",\"" + mSLM.getProb(term) + "\""
//                     + ",\"" + SLMm1.getProb(term) + "\"" 
//                     + ",\"" + SLMp1.getProb(term) + "\"" 
//                     + ",\"" + SLMs1.getProb(term) + "\"" 
//                     + ",\"" + SLMa1.getProb(term) + "\"" 
//                     + ",\"" + SLMm2.getProb(term) + "\"" 
//                     + ",\"" + SLMp2.getProb(term) + "\"" 
//                     + ",\"" + SLMs2.getProb(term) + "\"" 
//                     + ",\"" + SLMa2.getProb(term) + "\"" 
//                     + ",\"" + mGLM.getProb(term) + "\""
//                     + ",\"" + pGLM.getProb(term) + "\""
//                     + ",\"" + sGLM.getProb(term) + "\""  
//                     + ",\"" + aGLM.getProb(term) + "\""
//                     + ",\"" + mGLM2.getProb(term) + "\""
//                     + ",\"" + pGLM2.getProb(term) + "\""
//                     + ",\"" + sGLM2.getProb(term) + "\""  
                     + ",\"" + sSLM.getProb(term) + "\""  
                     + ",\"" + sSLM2.getProb(term) + "\""  
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

