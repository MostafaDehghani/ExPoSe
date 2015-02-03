/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.expose.LM;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Mostafa Dehghani
 */
public class classification {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(classification.class.getName());
    private HashMap<String,LanguageModel> classes;
    private HashMap<Map.Entry<String,String>,LanguageModel> obj;

    public classification(HashMap<String, LanguageModel> classes, HashMap<Map.Entry<String,String>,LanguageModel> obj) {
        this.classes = classes;
        this.obj = obj;
    }
    
    
    public Double getAccuracy(){
        Map.Entry<String,Double> label = new AbstractMap.SimpleEntry<>("",-100000000D);
        Integer trues = 0;
        Integer all=0;
        for(Entry<Map.Entry<String,String>,LanguageModel> o: this.obj.entrySet()){
            for(Entry<String,LanguageModel>c: this.classes.entrySet()){
                Divergence d = new Divergence(o.getValue(), c.getValue());
                Double score = d.getJsdSimScore();
                if(score>label.getValue())
                    label = new AbstractMap.SimpleEntry<>(c.getKey(),score);
            }
            all++;
            System.out.println(o.getKey().getValue() + "-->" + label.getKey());
            if(label.getKey().equals(o.getKey().getValue()))
                     trues++;
        }
        Double acc = trues.doubleValue()/all.doubleValue();
        return  acc;
    }
    
    public static void main(String[] args) throws Exception {
        DSPLM hplm = new DSPLM("20102012");
        HashMap<String,LanguageModel> classes = new HashMap<>();
        HashMap<Map.Entry<String,String>,LanguageModel> obj = new HashMap<>();
        for(int i=0; i<hplm.statiReader.numDocs();i++){
            String id = hplm.statiReader.document(i).get("ID");
            SmoothedLM splm = new SmoothedLM(hplm.getStatDoubleSidedPLM(id), hplm.aSLM);
            classes.put(id,splm);
        }
        for(int i=0; i<hplm.miReader.numDocs();i++){
            String id = hplm.miReader.document(i).get("ID");
            String stat = hplm.getMemStatus(i);
            Map.Entry<String,String> oIDs = new AbstractMap.SimpleEntry<>(id,stat);
           SmoothedLM splm = new SmoothedLM(hplm.getMemSLM(id), hplm.aSLM);
            obj.put(oIDs,splm);
        }
        classification cl = new classification(classes, obj);
        Double classificationAccuracy = cl.getAccuracy();
        System.out.println(classificationAccuracy);
    }
}
