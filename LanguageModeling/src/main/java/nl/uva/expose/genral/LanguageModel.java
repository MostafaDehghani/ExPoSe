/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.expose.genral;

import java.util.HashMap;

/**
 *
 * @author Mostafa Dehghani
 */
public class LanguageModel {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(LanguageModel.class.getName());
    public HashMap<String, Double> LanguageModel;

    public LanguageModel() {
        LanguageModel = new HashMap<>();
    }

    public LanguageModel(HashMap<String, Double> LM) {
        this.LanguageModel = LM;
    }

}
