/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.expose.lm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

/**
 *
 * @author Mostafa Dehghani
 */
public class AspectAwareLM extends LanguageModel{
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AspectAwareLM.class.getName());
    private LanguageModel originalLM;
    private LanguageModel aspectLM;

    public AspectAwareLM(LanguageModel originalLM, LanguageModel aspectLM) {
        this.originalLM = originalLM;
        this.aspectLM = aspectLM;
        this.generateAspectAwareLM();
    }

    private void generateAspectAwareLM() {
       this.caculateProjection(this.originalLM,this.aspectLM);
       this.LanguageModel = this.getNormalizedLM();
    }

    private void caculateProjection(LanguageModel originalLM,LanguageModel aspectLM) {
//        HashMap<String,Double> AspectUnitVector = this.calculateUnitVector(aspectLM);
        Double original_aspect_Dotproduct = this.calculateDotProduct(originalLM,aspectLM);
        Double square_of_magnitude_aspect = this.calculateSquareOfMagnitude(aspectLM);
        for(Entry<String,Double> e:aspectLM.LanguageModel.entrySet()){
            Double newProb = (original_aspect_Dotproduct/square_of_magnitude_aspect) * aspectLM.getProb(e.getKey());
            this.LanguageModel.put(e.getKey(), newProb);
        }
    }

    private Double calculateDotProduct(nl.uva.expose.lm.LanguageModel LM1, nl.uva.expose.lm.LanguageModel LM2) {
        Double dotProduct = 0D;
        HashSet<String> allTerms = new HashSet<>();
        allTerms.addAll(LM1.LanguageModel.keySet());
        allTerms.addAll(LM2.LanguageModel.keySet());
        for(String t:allTerms){
            dotProduct += LM1.getProb(t) * LM2.getProb(t);
        }
        return dotProduct;
    }

    private Double calculateSquareOfMagnitude(nl.uva.expose.lm.LanguageModel LM) {
        Double SM = 0D;
        for(Entry<String,Double> e: LM.LanguageModel.entrySet()){
            SM += Math.pow(e.getValue(), 2);
        }
        return SM;
    }
    
        public static void main(String[] args) {
        LanguageModel Orig = new LanguageModel();
        Orig.LanguageModel.put("a", 0.1D);
        Orig.LanguageModel.put("b", 0.3D);
        Orig.LanguageModel.put("c", 0.4D);
        Orig.LanguageModel.put("d", 0.2D);
        
        
        LanguageModel asp = new LanguageModel();
        asp.LanguageModel.put("b", 0.2D);
        asp.LanguageModel.put("c", 0.8D);
   
      
        AspectAwareLM AALM = new AspectAwareLM(Orig, asp);
        
        System.out.println(AALM.LanguageModel.toString());
    }
        
        
        
        
    
//    
//    private HashMap<String, Double> calculateUnitVector(nl.uva.expose.lm.LanguageModel aspectLM) {
//        Double aspectMag = this.calculateMagnitude(aspectLM);
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//    private Double calculateMagnitude(nl.uva.expose.lm.LanguageModel aspectLM) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }


}
