/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.expose.lm.analysis;

import java.util.ArrayList;

/**
 *
 * @author Mostafa Dehghani
 */
public class ProbabilityClac {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ProbabilityClac.class.getName());

    public static double probabilityOfBeingSpecial(ArrayList<Double> probs){
        Double probability = 0D;
        for(int i=0; i<probs.size();i++){
            Double joineProb = probs.get(i);
            for(int j=0; j<probs.size();j++){
                if(i==j)
                    continue;
                joineProb = joineProb * (1-probs.get(j));
            }
            probability += joineProb;
        }
        
       return probability;
    }
    
    
    public static void main(String[] args) {
        ArrayList<Double> probs = new ArrayList<>();
        probs.add(1/100D);
        probs.add(0D);
        probs.add(0D);
        System.out.println(probabilityOfBeingSpecial(probs));
    }
}
