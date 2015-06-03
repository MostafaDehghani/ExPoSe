package nl.uva.expose.glm;

import nl.uva.expose.LM.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Mostafa Dehghani
 */
public final class NewParsimoniousLM extends LanguageModel {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(NewParsimoniousLM.class.getName());
    private ArrayList<LanguageModel> backgroundLM;
    private LanguageModel documentLM;
    private HashMap<String, Double> documentTV;
    private LanguageModel tmpLM;
    private Double alpha = 0.005D;
    private Double probThreshold = 1/7285632D; //0.00001D;
    private Integer numberOfIttereation = 100;

    public NewParsimoniousLM(LanguageModel documentLM, HashMap<String, Double> documentTV, ArrayList<LanguageModel> backgroundLM,
            Double alpha, Double probThreshold, Integer numberOfIttereation) {
        this.backgroundLM = backgroundLM;
        this.documentLM = documentLM;
        this.documentTV = documentTV;
        this.tmpLM = documentLM;
        this.alpha = alpha;
        this.probThreshold = probThreshold;
        this.numberOfIttereation = numberOfIttereation;
        this.generateParsimoniousLanguageModel();
    }

     public NewParsimoniousLM(LanguageModel documentLM, HashMap<String, Double> documentTV, ArrayList<LanguageModel> backgroundLM) {
        this.backgroundLM = backgroundLM;
        this.documentLM = documentLM;
        this.documentTV = documentTV;
        this.tmpLM = documentLM;
        this.generateParsimoniousLanguageModel();
    }
     
    public NewParsimoniousLM(LanguageModel documentLM, ArrayList<LanguageModel> backgroundLM,
        Double alpha, Double probThreshold, Integer numberOfIttereation) {
        this.backgroundLM = backgroundLM;
        this.documentLM = documentLM;
        this.documentTV = documentLM.LanguageModel;
        this.tmpLM = documentLM;
        this.alpha = alpha;
        this.probThreshold = probThreshold;
        this.numberOfIttereation = numberOfIttereation;
        this.generateParsimoniousLanguageModel();
    }

    public NewParsimoniousLM(LanguageModel documentLM, ArrayList<LanguageModel> backgroundLM) {
        this.backgroundLM = backgroundLM;
        this.documentLM = documentLM;
        this.documentTV = documentLM.LanguageModel;
        this.tmpLM = documentLM;
        this.generateParsimoniousLanguageModel();
    }
    
   public double MultipleBackgroundProb(String term){
        Double probability = 0D;
        for(int i=0; i<this.backgroundLM.size();i++){
            Double joineProb = this.backgroundLM.get(i).getProb(term);
            for(int j=0; j<this.backgroundLM.size();j++){
                if(i==j)
                    continue;
                joineProb = joineProb * (1-this.backgroundLM.get(j).getProb(term));
            }
            probability += joineProb;
        }
        
       return probability;
    }

    private void E_step(Double alpha) {
        for (Entry<String, Double> e : this.tmpLM.LanguageModel.entrySet()) {
            Double backgoundProb = null;
                backgoundProb = this.MultipleBackgroundProb(e.getKey());
            if (backgoundProb == null) {
                backgoundProb = 0D;
            }
            Double tf = documentTV.get(e.getKey());
                Double newProb = tf * ((alpha * e.getValue()) / ((alpha * e.getValue()) + ((1 - alpha) * backgoundProb)));
                this.LanguageModel.put(e.getKey(), newProb);
        }
        this.tmpLM = new LanguageModel(new HashMap<>(this.LanguageModel));
    }

    private void M_step(Double probThreshold) {
        Double summation = 0D;
        for (Entry<String, Double> e : this.tmpLM.LanguageModel.entrySet()) {
            summation += e.getValue();
        }
        for (Entry<String, Double> e : this.tmpLM.LanguageModel.entrySet()) {
            Double newProb = e.getValue() / summation;
            if (newProb <= probThreshold) {
                this.LanguageModel.remove(e.getKey());
            } else {
                this.LanguageModel.put(e.getKey(), newProb);
            }
        }
        this.LanguageModel = this.getNormalizedLM();
        this.tmpLM = new LanguageModel(new HashMap<>(this.LanguageModel));
    }

    public void generateParsimoniousLanguageModel() {
        for (int i = 0; i < this.numberOfIttereation; i++) {
            this.E_step(this.alpha);
            this.M_step(this.probThreshold);
        }
    }
}