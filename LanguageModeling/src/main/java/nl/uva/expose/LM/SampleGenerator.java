/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.expose.LM;

import java.util.Map.Entry;
import java.util.Random;

public class SampleGenerator {

    private  LanguageModel LM;
    private static Random generator = new Random();
    private double[] distribution;
    private Double[] probabilities;
    private String[] words;
    private int range;

//Constructor
    SampleGenerator(LanguageModel LM) {
        probabilities = new Double[LM.LanguageModel.size()];
        words = new String[LM.LanguageModel.size()];
        int k = 0;
        for (Entry<String, Double> e : LM.LanguageModel.entrySet()) {
            words[k] = e.getKey();
            probabilities[k] = e.getValue();
            k++;
        }
        range = probabilities.length + 1;
	// We build the distribution array one larger than the array of probabilities
        // to permit distribution[0] to act as a minimum bound for searching.
        // Otherwise each distribution value is a maximum bound.

        distribution = new double[range];
        double sumProb = 0;
        for (double value : probabilities) {
            sumProb += value;
        }
        distribution[0] = 0;
        for (int i = 1; i < range; ++i) {
            distribution[i] = distribution[i - 1] + (probabilities[i - 1] / sumProb);
        }
        distribution[range - 1] = 1.0;
    }

    private Integer sample() {
	// Straightforward binary search on an array of doubles to find
        // index such that distribution[i] is greater than random number while
        // distribution[i-1] is less.

        double key = generator.nextDouble();
        int mindex = 1;
        int maxdex = range - 1;
        int midpoint = mindex + (maxdex - mindex) / 2;
        while (mindex <= maxdex) {
            if (key < distribution[midpoint - 1]) {
                maxdex = midpoint - 1;
            } else if (key > distribution[midpoint]) {
                mindex = midpoint + 1;
            } else {
                return midpoint - 1;
			// minus one, because the whole distribution array is shifted one up from the
                // original probabilities array to permit distribution[0] to be a minbound.
            }
            midpoint = mindex + (int) Math.ceil((maxdex - mindex) / 2);
            // I use Math.ceil to avoid any possibility of midpoint = 0.
        }
        System.out.println("Error in multinomial sampling method.");
        return range - 1;
    }
    
    public String getSample(int length){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<length;i++){
            int index = this.sample();
            sb.append(this.words[index]).append(" ");
        }
        return sb.toString().trim();
    }
}
