package nl.uva.expose.LM;

import java.util.HashMap;
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
public final class ParsimoniousLM extends LanguageModel {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ParsimoniousLM.class.getName());
    private LanguageModel backgroundLM;
    private LanguageModel documentLM;
    private HashMap<String, Double> documentTV;
    private LanguageModel tmpLM;
    private Double alpha = 0.008D;
    private Double probThreshold = 0.00001D;
    private Integer numberOfIttereation = 100;

    public ParsimoniousLM(LanguageModel documentLM, HashMap<String, Double> documentTV, LanguageModel backgroundLM,
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

     public ParsimoniousLM(LanguageModel documentLM, HashMap<String, Double> documentTV, LanguageModel backgroundLM) {
        this.backgroundLM = backgroundLM;
        this.documentLM = documentLM;
        this.documentTV = documentTV;
        this.tmpLM = documentLM;
        this.generateParsimoniousLanguageModel();
    }
     
    public ParsimoniousLM(LanguageModel documentLM, LanguageModel backgroundLM,
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

    public ParsimoniousLM(LanguageModel documentLM, LanguageModel backgroundLM) {
        this.backgroundLM = backgroundLM;
        this.documentLM = documentLM;
        this.documentTV = documentLM.LanguageModel;
        this.tmpLM = documentLM;
        this.generateParsimoniousLanguageModel();
    }

    private void E_step(Double alpha) {
        for (Entry<String, Double> e : this.tmpLM.LanguageModel.entrySet()) {
            Double backgoundProb = null;
//            try{
                backgoundProb = this.backgroundLM.LanguageModel.get(e.getKey());
//            }catch(Exception ex){
//                System.out.println(ex);
//                System.exit(0);
//            }
            if (backgoundProb == null) {
                backgoundProb = 0D;
            }
            Double tf = documentTV.get(e.getKey());
//            try{
                Double newProb = tf * ((alpha * e.getValue()) / ((alpha * e.getValue()) + ((1 - alpha) * backgoundProb)));
                this.LanguageModel.put(e.getKey(), newProb);
//            }catch(Exception ex){
//                System.out.println("---");
//            }  
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
            if (newProb < probThreshold) {
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
        LanguageModel dlm = new LanguageModel();
        dlm.LanguageModel.put("a", 0.3D);
        dlm.LanguageModel.put("b", 0.3D);
        dlm.LanguageModel.put("c", 0.3D);
        dlm.LanguageModel.put("d", 0.1D);
        
        
        LanguageModel blm = new LanguageModel();
        blm.LanguageModel.put("a", 7D/15D);
        blm.LanguageModel.put("b", 4D/15D);
        blm.LanguageModel.put("c", 3D/15D);
        blm.LanguageModel.put("d", 1D/15D);
        
        SampleGenerator sg = new SampleGenerator(blm);
        for(int i=0;i<1000;i++)
            System.out.println(sg.getSample(1000));
        
//        ParsimoniousLM dplm = new ParsimoniousLM(dlm, blm,0.1D,0.005D,100);
//        
//        System.out.println(dplm.LanguageModel.toString());
    }
}
