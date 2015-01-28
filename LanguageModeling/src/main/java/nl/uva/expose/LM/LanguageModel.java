/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.expose.LM;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

    public static List<Map.Entry<String, Double>> sortByValues(Map<String, Double> unsortMap, final boolean order) {
        List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(unsortMap.entrySet());
        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1,
                    Map.Entry<String, Double> o2) {
                if (order) {
                    return o1.getValue().compareTo(o2.getValue());
                } else {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

//        // Maintaining insertion order with the help of LinkedList
//        Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
//        for (Entry<String, Double> entry : list)
//        {
//            sortedMap.put(entry.getKey(), entry.getValue());
//        }
//        return sortedMap;
        return list;
    }

    public List<Map.Entry<String, Double>> getTopK(Integer k) {
        List<Map.Entry<String, Double>> sorted = sortByValues(LanguageModel, false);
        k = k < sorted.size() ? k : sorted.size();
        return sorted.subList(0, k);
    }

    public List<Map.Entry<String, Double>> getNormalizedTopK(Integer k) {
        List<Map.Entry<String, Double>> sorted = sortByValues(LanguageModel, false);
        List<Map.Entry<String, Double>> newList = new ArrayList<>();
        k = k < sorted.size() ? k : sorted.size();
        Double summation = 0D;
        for (Map.Entry<String, Double> e : sorted.subList(0, k)) {
            summation += e.getValue();
        }
        for (Map.Entry<String, Double> e : sorted.subList(0, k)) {
            Double newProb = e.getValue() / summation;
            newList.add(new AbstractMap.SimpleEntry<>(e.getKey(), newProb));
        }
        return newList;
    }

    public List<Map.Entry<String, Double>> getSorted() {
        List<Map.Entry<String, Double>> sorted = sortByValues(LanguageModel, false);
        return sorted;
    }

}
