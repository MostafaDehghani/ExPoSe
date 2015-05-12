/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.expose.LM;

import java.io.File;
import java.io.IOException;
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
public class GeneralizedLM {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(GeneralizedLM.class.getName());
    private String period;
    public IndexReader miReader;
    public IndexReader piReader;
    public IndexReader statiReader;
    private IndexInfo miInfo;
    private IndexInfo piInfo;
    private IndexInfo statiInfo;
    private String field;
    private Cabinet cabinet;

    public LanguageModel aSLM;
    private HashMap<Integer,LanguageModel> allDSPLM = new HashMap<Integer,LanguageModel>();
    private HashMap<String, LanguageModel> memSLM = new HashMap<>();
    private HashMap<String, LanguageModel> statSLM = new HashMap<>();
    private HashMap<String, LanguageModel> partySLM = new HashMap<>();
    private HashMap<Integer,HashMap<String, LanguageModel>> memDSPLM = new HashMap<Integer,HashMap<String, LanguageModel>>();
    private HashMap<Integer,HashMap<String, LanguageModel>> statDSPLM_s1 = new HashMap<Integer,HashMap<String, LanguageModel>>();
    private HashMap<Integer,HashMap<String, LanguageModel>> partyDSPLM_s1 = new HashMap<Integer,HashMap<String, LanguageModel>>();
    private HashMap<Integer,HashMap<String, LanguageModel>> statDSPLM_s2 = new HashMap<Integer,HashMap<String, LanguageModel>>();
    private HashMap<Integer,HashMap<String, LanguageModel>> partyDSPLM_s2 = new HashMap<Integer,HashMap<String, LanguageModel>>();

    public GeneralizedLM(String period) throws IOException, Exception {
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

    public LanguageModel getAllITDSPLM(Integer itNum) throws IOException {
        if (this.allDSPLM.size() > itNum) {
            return this.allDSPLM.get(itNum); 
        }
        LanguageModel aDSPLM = this.aSLM;
        for (int it = itNum; it > 0;) {
            aDSPLM = this.getAllITDSPLM(it - 1);
            LanguageModel newLM = null;
            for (int i = 0; i < this.miReader.numDocs(); i++) {
                String mid = this.miReader.document(i).get("ID");
                newLM = new ParsimoniousLM(aDSPLM, this.getMemITDSPLM(mid, it));
                aDSPLM = newLM;
            }
            for (int i = 0; i < this.piReader.numDocs(); i++) {
                String pid = this.piReader.document(i).get("ID");
                newLM = new ParsimoniousLM(aDSPLM, this.getPartyITDSPLM(pid, it));
                aDSPLM = newLM;
            }
            for (int i = 0; i < this.statiReader.numDocs(); i++) {
                String statid = this.statiReader.document(i).get("ID");
                newLM = new ParsimoniousLM(aDSPLM, this.getStatITDSPLM(statid, it));
                aDSPLM = newLM;
            }
            it--;
        }
    this.allDSPLM.put(itNum,aDSPLM);
    return aDSPLM ;
}

public LanguageModel getMemITDSPLM(String memId, Integer itNum) throws IOException {
        HashMap<String, LanguageModel> msDSPLM = null;
        if (this.memDSPLM.size()> itNum) {
                msDSPLM = this.memDSPLM.get(itNum);
                LanguageModel mDSPLM = msDSPLM.get(memId);
                if(mDSPLM != null)
                    return mDSPLM;
        }
        else{
            msDSPLM = new HashMap<>();
        }
        LanguageModel mDSPLM = this.getMemSLM(memId);
        for (int it = itNum; it > 0;) {
            mDSPLM = this.getMemITDSPLM(memId, it - 1);
            Integer memIndexId = this.getMemIndexID(memId);
            String memP = this.getMemParty(memIndexId);
            String memS = this.getMemStatus(memIndexId);
            ParsimoniousLM mPLMa = new ParsimoniousLM(mDSPLM, this.getAllITDSPLM(it - 1));
            ParsimoniousLM mPLMas = new ParsimoniousLM(mPLMa, this.getStatITDSPLM_s1(memS, it));
            ParsimoniousLM mPLMasp = new ParsimoniousLM(mPLMas, this.getPartyITDSPLM_s1(memP, it));
            mDSPLM = mPLMasp;
            it--;
        }
        msDSPLM.put(memId, mDSPLM);
        this.memDSPLM.put(itNum,msDSPLM);
        return mDSPLM;
    }

    public LanguageModel getStatITDSPLM_s1(String statId, Integer itNum) throws IOException {
        HashMap<String, LanguageModel> ssDSPLM = null;
        if (this.statDSPLM_s1.size()> itNum) {
                ssDSPLM = this.statDSPLM_s1.get(itNum);
                LanguageModel sDSPLM = ssDSPLM.get(statId);
                if(sDSPLM != null)
                    return sDSPLM;
        }
        else{
            ssDSPLM = new HashMap<>();
        }
        LanguageModel sDSPLM = this.getStatSLM(statId);
        for (int it = itNum; it > 0;) {
            sDSPLM =  getStatITDSPLM(statId,it - 1);
            ParsimoniousLM sPLMa = new ParsimoniousLM(sDSPLM, this.getAllITDSPLM(it - 1));
            sDSPLM = sPLMa;
            it--;
        }
        ssDSPLM.put(statId, sDSPLM);
        this.statDSPLM_s1.put(itNum,ssDSPLM);
        return sDSPLM;
    }

    public LanguageModel getStatITDSPLM(String statId, Integer itNum) throws IOException {
        HashMap<String, LanguageModel> ssDSPLM  = null;
        if (this.statDSPLM_s2.size()> itNum) {
                ssDSPLM = this.statDSPLM_s2.get(itNum);
                LanguageModel sDSPLM = ssDSPLM.get(statId);
                if(sDSPLM != null)
                    return sDSPLM;
        }
        else{
            ssDSPLM = new HashMap<>();
        }
        LanguageModel sDSPLM = this.getStatITDSPLM_s1(statId, 0);
        for (int it = itNum; it > 0;) {
            sDSPLM = getStatITDSPLM_s1(statId, it);
            LanguageModel newLM = null;
            for (int i = 0; i < this.miReader.numDocs(); i++) {
                if (this.getMemStatus(i).equals(statId)) {
                    String mid = this.miReader.document(i).get("ID");
                    newLM = new ParsimoniousLM(sDSPLM, this.getMemITDSPLM(mid, it));
                    sDSPLM = newLM;
                }
            }
            for (int i = 0; i < this.piReader.numDocs(); i++) {
                String pid = this.piReader.document(i).get("ID");
                if (this.getPartyStatus(pid).equals(statId)) {
                    newLM = new ParsimoniousLM(sDSPLM, this.getPartyITDSPLM(pid, it));
                    sDSPLM = newLM;
                }
            }
            it--;
        }
        ssDSPLM.put(statId, sDSPLM);
        this.statDSPLM_s2.put(itNum,ssDSPLM);
        return sDSPLM;
    }

    public LanguageModel getPartyITDSPLM_s1(String partyId, Integer itNum) throws IOException {
        HashMap<String, LanguageModel> psDSPLM  = null;
        if (this.partyDSPLM_s1.size()> itNum) {
                psDSPLM = this.partyDSPLM_s1.get(itNum);
                LanguageModel pDSPLM = psDSPLM.get(partyId);
                if(pDSPLM != null)
                    return pDSPLM;
        }
        else{
            psDSPLM = new HashMap<>();
        }        
        LanguageModel pDSPLM = this.getPartySLM(partyId);
        for (int it = itNum; it > 0;) {
                pDSPLM = this.getPartyITDSPLM(partyId,it - 1);
                String partS = this.getPartyStatus(partyId);
                ParsimoniousLM pPLMa = new ParsimoniousLM(pDSPLM, this.getAllITDSPLM(it - 1));
                ParsimoniousLM pPLMas = new ParsimoniousLM(pPLMa, this.getStatITDSPLM_s1(partS, it));
                pDSPLM = pPLMas;
                it--;
        }
        psDSPLM.put(partyId, pDSPLM);
        this.partyDSPLM_s1.put(itNum,psDSPLM);
        return pDSPLM;
    }

    public LanguageModel getPartyITDSPLM(String partyId, Integer itNum) throws IOException {
        HashMap<String, LanguageModel> psDSPLM  = null;
        if (this.partyDSPLM_s2.size()> itNum) {
                psDSPLM = this.partyDSPLM_s2.get(itNum);
                LanguageModel pDSPLM = psDSPLM.get(partyId);
                if(pDSPLM != null)
                    return pDSPLM;
        }
        else{
            psDSPLM = new HashMap<>();
        } 
        LanguageModel pDSPLM = this.getPartyITDSPLM_s1(partyId, 0);
        for (int it = itNum; it > 0;) {
            pDSPLM = getPartyITDSPLM_s1(partyId, it);
            LanguageModel newLM = null;
            for (int i = 0; i < this.miReader.numDocs(); i++) {
                if (this.getMemParty(i).equals(partyId)) {
                    String mid = this.miReader.document(i).get("ID");
                    newLM = new ParsimoniousLM(pDSPLM, this.getMemITDSPLM(mid, it));
                    pDSPLM = newLM;
                }
            }
            it--;
        }
        psDSPLM.put(partyId, pDSPLM);
        this.partyDSPLM_s2.put(itNum,psDSPLM);
        return pDSPLM;
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
