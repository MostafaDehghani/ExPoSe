
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.expose.genral;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Mostafa Dehghani
 */
public class Tools {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Tools.class.getName());
   
    public static List<Map.Entry<String, Double>>  sortByValues(Map<String, Double> unsortMap, final boolean order)
    {
        List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(unsortMap.entrySet());
        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>()
        {
            public int compare(Map.Entry<String, Double> o1,
                    Map.Entry<String, Double> o2)
            {
                if (order)
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
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
}
