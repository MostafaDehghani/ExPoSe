package nl.uva.expose.glm;

import nl.uva.expose.lm.LanguageModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import nl.uva.expose.lm.ParsimoniousLM;

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
    private Double probThreshold = Double.MIN_VALUE; //1e-6; //0.00001D;
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
    
    
        
    public static void main(String[] args) {
        LanguageModel d1lm = new LanguageModel();
        d1lm.LanguageModel.put("a", 3/10D);
        d1lm.LanguageModel.put("b", 3/10D);
        d1lm.LanguageModel.put("c", 3/10D);
        d1lm.LanguageModel.put("d", 1/10D);
        
        
        LanguageModel d2lm = new LanguageModel();
        d2lm.LanguageModel.put("a", 4D/5D);
        d2lm.LanguageModel.put("b", 1D/5D);
        
        
        LanguageModel blm = new LanguageModel();
        blm.LanguageModel.put("a", 7D/15D);
        blm.LanguageModel.put("b", 4D/15D);
        blm.LanguageModel.put("c", 3D/15D);
        blm.LanguageModel.put("d", 1D/15D);
        

        NewParsimoniousLM dplm_1 = new NewParsimoniousLM(d1lm, new ArrayList<LanguageModel>(Arrays.asList(blm)),0.1D,0.000D,100);
        NewParsimoniousLM dplm_2 = new NewParsimoniousLM(d2lm, new ArrayList<LanguageModel>(Arrays.asList(blm)),0.1D,0.000D,100);
        NewParsimoniousLM glm = new NewParsimoniousLM(blm, new ArrayList<LanguageModel>(Arrays.asList(dplm_1,dplm_2)),0.1D,0.000D,100);
        NewParsimoniousLM glm2 = new NewParsimoniousLM(blm, new ArrayList<LanguageModel>(Arrays.asList(d1lm,d2lm)),0.1D,0.000D,100);
        ParsimoniousLM glm_ = new ParsimoniousLM(blm, dplm_1,0.1D,0.000D,100);
        glm_ = new ParsimoniousLM(glm, dplm_2,0.1D,0.000D,100);

//        System.out.println(dplm_1.LanguageModel.toString());
//        System.out.println(dplm_2.LanguageModel.toString());
        System.out.println(glm.LanguageModel.toString());
        System.out.println(glm_.LanguageModel.toString());
//        System.out.println(glm2.LanguageModel.toString());
        
    }        
        
}