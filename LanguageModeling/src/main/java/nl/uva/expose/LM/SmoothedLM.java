/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.expose.LM;

import java.util.HashSet;
import nl.uva.expose.genral.LanguageModel;

/**
 *
 * @author Mostafa Dehghani
 */
public class SmoothedLM {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(SmoothedLM.class.getName());
    private LanguageModel backgroundLM;
    public LanguageModel smoothedLM;
    private LanguageModel documentLM;

    public SmoothedLM(LanguageModel documentLM , LanguageModel backgroundLM, Double Lambda) {
        this.backgroundLM = backgroundLM;
        this.documentLM = documentLM;
        this.backgroundLM = this.generateSmoothedLanguageModel(Lambda);
    }
    
    
    
    public LanguageModel generateSmoothedLanguageModel(Double lambda){
        HashSet<String> terms = new HashSet<>();
        terms.addAll(this.documentLM.LanguageModel.keySet());
        terms.addAll(this.backgroundLM.LanguageModel.keySet());
        for(String s: terms){
            Double documentProb = this.documentLM.LanguageModel.get(s);
            Double backgoundProb = this.backgroundLM.LanguageModel.get(s);
            if(backgoundProb == null)
                backgoundProb=0D;
            Double newProb = (lambda * documentProb + ((1-lambda)*backgoundProb));
            this.smoothedLM.LanguageModel.put(s, newProb);
        }
        return this.smoothedLM;
    }
}
