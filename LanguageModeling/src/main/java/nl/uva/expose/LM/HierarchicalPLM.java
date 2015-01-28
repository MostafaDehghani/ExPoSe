/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.expose.LM;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import nl.uva.expose.entities.government.Cabinet;
import static nl.uva.expose.settings.Config.configFile;
import nl.uva.lucenefacility.IndexInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.SimpleFSDirectory;

/**
 *
 * @author Mostafa Dehghani
 */
public class HierarchicalPLM {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(HierarchicalPLM.class.getName());
    private String period;
    private IndexReader miReader;
    private IndexReader piReader;
    private IndexReader statiReader;
    private IndexInfo miInfo;
    private IndexInfo piInfo;
    private IndexInfo statiInfo;
    private String field;
    private Cabinet cabinet;

    public LanguageModel aSLM;
    private HashMap<String, LanguageModel> memSLM = new HashMap<>();
    private HashMap<String, LanguageModel> statSLM = new HashMap<>();
    private HashMap<String, LanguageModel> partySLM = new HashMap<>();
    private HashMap<String, LanguageModel> memPLM = new HashMap<>();
    private HashMap<String, LanguageModel> statPLM = new HashMap<>();
    private HashMap<String, LanguageModel> partyPLM = new HashMap<>();

    public HierarchicalPLM(String period) throws IOException, Exception {
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
            Integer memIndexId = this.miInfo.getIndexId(memId);
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

    public LanguageModel getMemPLM(String memId) throws IOException {
        LanguageModel mPLM = this.memPLM.get(memId);
        if (mPLM == null) {
            Integer memIndexId = this.miInfo.getIndexId(memId);
            String memP = this.getMemParty(memIndexId);
            String memS = this.getMemStatus(memIndexId);
            ParsimoniousLM mPLMa = new ParsimoniousLM(this.getMemSLM(memId), aSLM);
            ParsimoniousLM mPLMas = new ParsimoniousLM(mPLMa, this.getStatPLM(memS));
            ParsimoniousLM mPLMasp = new ParsimoniousLM(mPLMas, this.getPartyPLM(memP));
            this.memPLM.put(memId, mPLMasp);
            mPLM = mPLMasp;
        }
        return mPLM;
    }

    public LanguageModel getStatPLM(String statId) throws IOException {
        LanguageModel statPLM = this.statPLM.get(statId);
        if (statPLM == null) {
            ParsimoniousLM sPLMa = new ParsimoniousLM(this.getStatSLM(statId), aSLM);
            this.statPLM.put(statId, sPLMa);
            statPLM = sPLMa;
        }
        return statPLM;
    }

    public LanguageModel getPartyPLM(String partyId) throws IOException {
        LanguageModel pPLM = this.partyPLM.get(partyId);
        if (pPLM == null) {
            String partS = this.getPartyStatus(partyId);
            ParsimoniousLM pPLMa = new ParsimoniousLM(this.getPartySLM(partyId), aSLM);
            ParsimoniousLM pPLMas = new ParsimoniousLM(pPLMa, this.getStatPLM(partS));
            this.partyPLM.put(partyId, pPLMas);
            pPLM = pPLMas;
        }
        return pPLM;
    }

    public LanguageModel getStatSpecialPLM(String status) throws IOException {
        LanguageModel sPLM = this.getStatPLM(status);
        LanguageModel newLM = null;
        for (int i = 0; i < this.miReader.numDocs(); i++) {
            if (this.getMemStatus(i).equals(status)) {
                String mid = this.miReader.document(i).get("ID");
                newLM = new ParsimoniousLM(sPLM, this.getMemPLM(mid));
                sPLM = newLM;
            }
        }
        return newLM;
    }

    private String getMemStatus(Integer memIndexId) throws IOException {
        String aff = "";
        HashSet<String> affiliations = this.miInfo.getDocAllTerm(memIndexId, "AFF");
        for (String s : affiliations) {
            aff += s + " ";
        }
        String status = this.cabinet.getStatus(aff);
        Integer affNum = affiliations.size();
        if (affNum != 1) {
            System.err.println(affNum + " --- " + memIndexId + ": " + status);
        }
        return status;
    }

    private String getPartyStatus(String partyName) throws IOException {
        String status = this.cabinet.getStatus(partyName);
        return status;
    }

    private String getMemParty(Integer memIndexId) throws IOException {
        String aff = "";
        HashSet<String> affiliations = this.miInfo.getDocAllTerm(memIndexId, "AFF");
        for (String s : affiliations) {
            aff = s;
            break;
        }
        if (affiliations.size() > 1) {
            System.err.println("More than one affiliation: " + affiliations.toString() + " --> " + aff);
        }
        return aff.trim();
    }

}
