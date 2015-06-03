/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.expose.lm;

import java.util.Map.Entry;

/**
 *
 * @author Mostafa Dehghani
 */
public class AspectAwareLM1 extends LanguageModel{
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AspectAwareLM1.class.getName());
    private LanguageModel originalLM;
    private LanguageModel aspectLM;

    public AspectAwareLM1(LanguageModel originalLM, LanguageModel aspectLM) {
        this.originalLM = originalLM;
        this.aspectLM = aspectLM;
        this.generateAspectAwareLM();
    }

    private void generateAspectAwareLM() {
       this.caculateProjection(this.originalLM,this.aspectLM);
       this.LanguageModel = this.getNormalizedLM();
    }

    private void caculateProjection(LanguageModel originalLM,LanguageModel aspectLM) {
        for(Entry<String,Double> e:aspectLM.LanguageModel.entrySet()){
            Double newProb = originalLM.getProb(e.getKey());// * e.getValue();
            this.LanguageModel.put(e.getKey(),newProb );
        }
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
   
      
        AspectAwareLM1 AALM = new AspectAwareLM1(Orig, asp);
        
        System.out.println(AALM.LanguageModel.toString());
    }
}
