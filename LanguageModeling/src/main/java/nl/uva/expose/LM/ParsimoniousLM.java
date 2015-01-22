package nl.uva.expose.LM;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map.Entry;
import nl.uva.expose.genral.LanguageModel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Mostafa Dehghani
 */
public class ParsimoniousLM {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ParsimoniousLM.class.getName());
    private LanguageModel backgroundLM;
    public LanguageModel parsimoniousLM;
    private LanguageModel documentLM;

    public ParsimoniousLM( LanguageModel documentLM, LanguageModel backgroundLM,
                          Double alpha, Double probThreshold,Integer numberOfIttereation) {
        this.backgroundLM = backgroundLM;
        this.documentLM = documentLM;
        this.parsimoniousLM = documentLM;
        this.parsimoniousLM = this.generateParsimoniousLanguageModel(alpha, probThreshold, numberOfIttereation);
    }

    private void E_step(Double alpha){
        for(Entry<String,Double> e: this.parsimoniousLM.LanguageModel.entrySet()){
            Double backgoundProb = this.backgroundLM.LanguageModel.get(e.getKey());
            if(backgoundProb == null)
                backgoundProb=0D;
            Double newProb = (alpha * e.getValue())/((alpha * e.getValue()) + ((1-alpha)*backgoundProb));
            this.parsimoniousLM.LanguageModel.put(e.getKey(), newProb);
        }
    }
    
    private void M_step(Double probThreshold){
        Double summation = 0D;
        for(Entry<String,Double> e: this.parsimoniousLM.LanguageModel.entrySet()){
            summation+=e.getValue();
        }
        for(Entry<String,Double> e: this.parsimoniousLM.LanguageModel.entrySet()){
            Double newProb = e.getValue()/summation;
            if(newProb < probThreshold)
                this.parsimoniousLM.LanguageModel.remove(e.getKey());
            else
              this.parsimoniousLM.LanguageModel.put(e.getKey(), newProb);
        }
    }
    
    public LanguageModel generateParsimoniousLanguageModel(Double alpha, Double probThreshold,Integer numberOfIttereation){
        for(int i=0;i<numberOfIttereation;i++){
            this.E_step(alpha);
            this.M_step(probThreshold);
        }
        return this.parsimoniousLM;
    }
}
