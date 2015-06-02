/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.expose.glm;

import nl.uva.expose.LM.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import nl.uva.expose.entities.government.Cabinet;
import static nl.uva.expose.settings.Config.configFile;
import nl.uva.lucenefacility.IndexInfo;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.SimpleFSDirectory;

/**
 *
 * @author Mostafa Dehghani
 */
public class NewGeneralizedLM {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(NewGeneralizedLM.class.getName());
    private final String period;
    public IndexReader miReader;
    public IndexReader piReader;
    public IndexReader statiReader;
    private final IndexInfo miInfo;
    private final IndexInfo piInfo;
    private final IndexInfo statiInfo;
    private final String field;
    private final Cabinet cabinet;

    public LanguageModel aSLM;
    private final HashMap<Integer,LanguageModel> allGLM = new HashMap<>();
    private final HashMap<String, LanguageModel> memSLM = new HashMap<>();
    private final HashMap<String, LanguageModel> statSLM = new HashMap<>();
    private final HashMap<String, LanguageModel> partySLM = new HashMap<>();
    private final HashMap<Integer,HashMap<String, LanguageModel>> memGLM = new HashMap<>();
    private final HashMap<Integer,HashMap<String, LanguageModel>> statGLM_s1 = new HashMap<>();
    private final HashMap<Integer,HashMap<String, LanguageModel>> partyGLM_s1 = new HashMap<>();
    private final HashMap<Integer,HashMap<String, LanguageModel>> statGLM_s2 = new HashMap<>();
    private HashMap<Integer,HashMap<String, LanguageModel>> partyGLM_s2 = new HashMap<>();

    public NewGeneralizedLM(String period) throws IOException, Exception {
        this.period = period;
        this.cabinet = new Cabinet(period);
        this.miReader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("INDEXES_PATH") + period + "/m")));
        this.piReader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("INDEXES_PATH") + period + "/p")));
        this.statiReader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("INDEXES_PATH") + period + "/st")));
        this.miInfo = new IndexInfo(this.miReader);
        this.piInfo = new IndexInfo(this.piReader);
        this.statiInfo = new IndexInfo(this.statiReader);
        this.field = "TEXT";
        aSLM = new CollectionLM(miReader, this.field);

    }

    public LanguageModel getMemSLM(String memId) throws IOException {
        LanguageModel mSLM = this.memSLM.get(memId);
        if (mSLM == null) {
            Integer memIndexId = this.getMemIndexID(memId);
            StandardLM memslm = new StandardLM(miReader, memIndexId, this.field);
            this.memSLM.put(memId, memslm);
            mSLM = memslm;
        }
        return mSLM;
    }

    public LanguageModel getPartySLM(String partyId) throws IOException {
        LanguageModel pSLM = this.partySLM.get(partyId);
        if (pSLM == null) {
            Integer partyIndexId = this.piInfo.getIndexId(partyId);
            StandardLM partylm = new StandardLM(piReader, partyIndexId, this.field);
            this.partySLM.put(partyId, partylm);
            pSLM = partylm;
        }
        return pSLM;
    }

    public LanguageModel getStatSLM(String statId) throws IOException {
        LanguageModel statSLM = this.statSLM.get(statId);
        if (statSLM == null) {
            Integer statIndexId = this.statiInfo.getIndexId(statId);
            StandardLM statslm = new StandardLM(statiReader, statIndexId, this.field);
            this.statSLM.put(statId, statslm);
            statSLM = statslm;
        }
        return statSLM;
    }
        
    public LanguageModel getAllSLM() throws IOException {    
        return aSLM ;
    }

    public LanguageModel getAllGLM(Integer itNum) throws IOException {
        if (this.allGLM.size() > itNum) {
            return this.allGLM.get(itNum); 
        }
        LanguageModel aGLM = this.aSLM;
        for (int it = itNum; it > 0;) {
            aGLM = this.getAllGLM(it - 1);
            LanguageModel newLM = null;
            
            // Generalized towards all members
            ArrayList<LanguageModel> memGLMs = new ArrayList<>();
            for (int i = 0; i < this.miReader.numDocs(); i++) {
                String mid = this.miReader.document(i).get("ID");
                memGLMs.add(this.getMemGLM(mid, it));
            }
            newLM = new NewParsimoniousLM(aGLM, memGLMs);
            aGLM = newLM;
            
            
            // Generalized towards all parties
            ArrayList<LanguageModel> parGLMs = new ArrayList<>();
            for (int i = 0; i < this.piReader.numDocs(); i++) {
                String pid = this.piReader.document(i).get("ID");
                parGLMs.add(this.getPartyGLM(pid, it));
            }
            newLM = new NewParsimoniousLM(aGLM, parGLMs);
            aGLM = newLM;
            
            // Generalized towards all parties
            ArrayList<LanguageModel> staGLMs = new ArrayList<>();
            for (int i = 0; i < this.statiReader.numDocs(); i++) {
                String statid = this.statiReader.document(i).get("ID");
                staGLMs.add(this.getStatGLM(statid, it));
            }
            newLM = new NewParsimoniousLM(aGLM, staGLMs);
            aGLM = newLM;
            
            //
            it--;
        }
    this.allGLM.put(itNum,aGLM);
    return aGLM ;
}

public LanguageModel getMemGLM(String memId, Integer itNum) throws IOException {
        HashMap<String, LanguageModel> msGLM = null;
        if (this.memGLM.size()> itNum) {
                msGLM = this.memGLM.get(itNum);
                LanguageModel mGLM = msGLM.get(memId);
                if(mGLM != null)
                    return mGLM;
        }
        else{
            msGLM = new HashMap<>();
        }
        LanguageModel mGLM = this.getMemSLM(memId);
        for (int it = itNum; it > 0;) {
            mGLM = this.getMemGLM(memId, it - 1);
            Integer memIndexId = this.getMemIndexID(memId);
            String memP = this.getMemParty(memIndexId);
            String memS = this.getMemStatus(memIndexId);
            NewParsimoniousLM mPLMa = new NewParsimoniousLM(mGLM,  new ArrayList<LanguageModel>(Arrays.asList(this.getAllGLM(it - 1))));
            NewParsimoniousLM mPLMas = new NewParsimoniousLM(mPLMa, new ArrayList<LanguageModel>(Arrays.asList(this.getStatGLM_s1(memS, it))));
            NewParsimoniousLM mPLMasp = new NewParsimoniousLM(mPLMas, new ArrayList<LanguageModel>(Arrays.asList(this.getPartyGLM_s1(memP, it))));
            mGLM = mPLMasp;
            it--;
        }
        msGLM.put(memId, mGLM);
        this.memGLM.put(itNum,msGLM);
        return mGLM;
    }

    public LanguageModel getStatGLM_s1(String statId, Integer itNum) throws IOException {
        HashMap<String, LanguageModel> ssGLM = null;
        if (this.statGLM_s1.size()> itNum) {
                ssGLM = this.statGLM_s1.get(itNum);
                LanguageModel sGLM = ssGLM.get(statId);
                if(sGLM != null)
                    return sGLM;
        }
        else{
            ssGLM = new HashMap<>();
        }
        LanguageModel sGLM = this.getStatSLM(statId);
        for (int it = itNum; it > 0;) {
            sGLM =  getStatGLM(statId,it - 1);
            NewParsimoniousLM sPLMa = new NewParsimoniousLM(sGLM, new ArrayList<LanguageModel>(Arrays.asList(this.getAllGLM(it - 1))));
            sGLM = sPLMa;
            it--;
        }
        ssGLM.put(statId, sGLM);
        this.statGLM_s1.put(itNum,ssGLM);
        return sGLM;
    }

    public LanguageModel getStatGLM(String statId, Integer itNum) throws IOException {
        HashMap<String, LanguageModel> ssGLM  = null;
        if (this.statGLM_s2.size()> itNum) {
                ssGLM = this.statGLM_s2.get(itNum);
                LanguageModel sGLM = ssGLM.get(statId);
                if(sGLM != null)
                    return sGLM;
        }
        else{
            ssGLM = new HashMap<>();
        }
        LanguageModel sGLM = this.getStatGLM_s1(statId, 0);
        for (int it = itNum; it > 0;) {
            sGLM = getStatGLM_s1(statId, it);
            LanguageModel newLM = null;
            ArrayList<LanguageModel> memGLMs = new ArrayList<>();
            for (int i = 0; i < this.miReader.numDocs(); i++) {
                if (this.getMemStatus(i).equals(statId)) {
                    String mid = this.miReader.document(i).get("ID");
                    memGLMs.add(this.getMemGLM(mid, it));
                }
            }
            newLM = new NewParsimoniousLM(sGLM,memGLMs);
            sGLM = newLM;
            
            
            ArrayList<LanguageModel> parGLMs = new ArrayList<>();
            for (int i = 0; i < this.piReader.numDocs(); i++) {
                String pid = this.piReader.document(i).get("ID");
                if (this.getPartyStatus(pid).equals(statId)) {
                    parGLMs.add(this.getPartyGLM(pid, it));
                }
            }
            newLM = new NewParsimoniousLM(sGLM,parGLMs);
                    sGLM = newLM;
                    
                    
            it--;
        }
        ssGLM.put(statId, sGLM);
        this.statGLM_s2.put(itNum,ssGLM);
        return sGLM;
    }

    public LanguageModel getPartyGLM_s1(String partyId, Integer itNum) throws IOException {
        HashMap<String, LanguageModel> psGLM  = null;
        if (this.partyGLM_s1.size()> itNum) {
                psGLM = this.partyGLM_s1.get(itNum);
                LanguageModel pGLM = psGLM.get(partyId);
                if(pGLM != null)
                    return pGLM;
        }
        else{
            psGLM = new HashMap<>();
        }        
        LanguageModel pGLM = this.getPartySLM(partyId);
        for (int it = itNum; it > 0;) {
                pGLM = this.getPartyGLM(partyId,it - 1);
                String partS = this.getPartyStatus(partyId);
                NewParsimoniousLM pPLMa = new NewParsimoniousLM(pGLM, new ArrayList<LanguageModel>(Arrays.asList(this.getAllGLM(it - 1))));
                NewParsimoniousLM pPLMas = new NewParsimoniousLM(pPLMa, new ArrayList<LanguageModel>(Arrays.asList(this.getStatGLM_s1(partS, it))));
                pGLM = pPLMas;
                it--;
        }
        psGLM.put(partyId, pGLM);
        this.partyGLM_s1.put(itNum,psGLM);
        return pGLM;
    }

    public LanguageModel getPartyGLM(String partyId, Integer itNum) throws IOException {
        HashMap<String, LanguageModel> psGLM  = null;
        if (this.partyGLM_s2.size()> itNum) {
                psGLM = this.partyGLM_s2.get(itNum);
                LanguageModel pGLM = psGLM.get(partyId);
                if(pGLM != null)
                    return pGLM;
        }
        else{
            psGLM = new HashMap<>();
        } 
        LanguageModel pGLM = this.getPartyGLM_s1(partyId, 0);
        for (int it = itNum; it > 0;) {
            pGLM = getPartyGLM_s1(partyId, it);
            LanguageModel newLM = null;
            ArrayList<LanguageModel> memGLMs = new ArrayList<>();
            for (int i = 0; i < this.miReader.numDocs(); i++) {
                if (this.getMemParty(i).equals(partyId)) {
                    String mid = this.miReader.document(i).get("ID");
                    memGLMs.add(this.getMemGLM(mid, it));
                }
            }
            newLM = new NewParsimoniousLM(pGLM, memGLMs);
            pGLM = newLM;
            
            it--;
        }
        psGLM.put(partyId, pGLM);
        this.partyGLM_s2.put(itNum,psGLM);
        return pGLM;
    }

    
    
    public String getMemStatus(Integer memIndexId) throws IOException {
        String aff = "";
        HashSet<String> affiliations = this.miInfo.getDocAllTerm(memIndexId, "AFF");
        for (String s : affiliations) {
            aff += s + " ";
        }
        String status = this.cabinet.getStatus(aff);
//        Integer affNum = affiliations.size();
//        if (affNum != 1) {
//            System.err.println(affNum + " --- " + memIndexId + ": " + status);
//        }
        return status;
    }
    
    public String getMemStatus(String memId) throws IOException {
        Integer memIndexId = this.getMemIndexID(memId);
        return this.getMemStatus(memIndexId);
    }

    
    public Integer getMemIndexID(String memId) throws IOException{
        Integer memIndexId = this.miInfo.getIndexId(memId);
        return memIndexId;
    }
    
    public String getMemID(Integer indexId) throws IOException{
        String memId = this.miReader.document(indexId).get("ID");
        return memId;
    }
    
    public String getPartyStatus(String partyName) throws IOException {
        String status = this.cabinet.getStatus(partyName);
        return status;
    }

    public String getMemParty(String memId) throws IOException {
        Integer memIndexId = this.getMemIndexID(memId);
        return this.getMemParty(memIndexId);
    }
        
    public String getMemParty(Integer memIndexId) throws IOException {
        String aff = "";
        HashSet<String> affiliations = this.miInfo.getDocAllTerm(memIndexId, "AFF");
        //
        if(affiliations.contains("nl.p.lidbontes"))
            return "nl.p.lidbontes";
        if(affiliations.contains("nl.p.groepkortenoevenhernandez"))
            return "nl.p.groepkortenoevenhernandez";
        if(affiliations.contains("nl.p.lidbrinkman"))
            return "nl.p.lidbrinkman";
       if(affiliations.contains("nl.p.lidverdonk"))
            return "nl.p.lidverdonk";
       //
        for (String s : affiliations) {
            aff = s;
//            break;
        }
        if (affiliations.size() > 1) {
            System.err.println("More than one affiliation: " + affiliations.toString() + " --> " + aff);
        }
        return aff.trim();
    }

}
