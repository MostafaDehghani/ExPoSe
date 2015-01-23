/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.expose.LM;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import nl.uva.expose.genral.LanguageModel;
import static nl.uva.expose.genral.Tools.sortByValues;

/**
 *
 * @author Mostafa Dehghani
 */
public class SmoothedLM {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(SmoothedLM.class.getName());
    private LanguageModel backgroundLM;
    public LanguageModel smoothedLM;
    private LanguageModel documentLM;

    public SmoothedLM(LanguageModel documentLM, LanguageModel backgroundLM, Double Lambda) {
        this.backgroundLM = backgroundLM;
        this.documentLM = documentLM;
        this.smoothedLM = new LanguageModel();
        this.backgroundLM = this.generateSmoothedLanguageModel(Lambda);
    }

    public LanguageModel generateSmoothedLanguageModel(Double lambda) {
        HashSet<String> terms = new HashSet<>();
        terms.addAll(this.documentLM.LanguageModel.keySet());
        terms.addAll(this.backgroundLM.LanguageModel.keySet());
        for (String s : terms) {
            Double documentProb = this.documentLM.LanguageModel.get(s);
            Double backgoundProb = this.backgroundLM.LanguageModel.get(s);
            if (backgoundProb == null) {
                backgoundProb = 0D;
            }
            if (documentProb == null) {
                documentProb = 0D;
            }
            Double newProb = (lambda * documentProb + ((1 - lambda) * backgoundProb));
            this.smoothedLM.LanguageModel.put(s, newProb);
        }
        return this.smoothedLM;
    }

    public List<Map.Entry<String, Double>> getTopK(Integer k) {
        List<Map.Entry<String, Double>> sorted = sortByValues(smoothedLM.LanguageModel, false);
        return sorted.subList(0, k);
    }

    public List<Map.Entry<String, Double>> getSorted() {
        List<Map.Entry<String, Double>> sorted = sortByValues(smoothedLM.LanguageModel, false);
        return sorted;
    }
}
