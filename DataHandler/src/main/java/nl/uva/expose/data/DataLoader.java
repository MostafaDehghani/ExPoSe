/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.expose.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import nl.uva.expose.entities.debate.Debate;
import nl.uva.expose.entities.government.Cabinet;
import nl.uva.expose.entities.parties.Party;
import nl.uva.expose.entities.member.Member;
import nl.uva.expose.entities.speech.Speech;
import static nl.uva.expose.settings.Config.configFile;
import nl.uva.expose.xmldataparser.d_ProceedingDataParser;
import nl.uva.expose.xmldataparser.m_MemberDataParser;
import nl.uva.expose.xmldataparser.p_PartyDataParser;
import org.xml.sax.SAXException;

/**
 *
 * @author Mostafa Dehghani
 */
public class DataLoader {
    
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(DataLoader.class.getName());
    private Data data;
    private HashSet<String> pmDebateList = new HashSet<>();
    public DataLoader(Data data, String period)throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, ParseException {
        this.data = data;
        try{
            File pmInfoDir = new File(configFile.getProperty("MEMBER_INFO_FILES_PATHS"));
            File pInfoDir = new File(configFile.getProperty("PARTY_INFO_FILES_PATHS"));
            File dInfoDir = new File(configFile.getProperty("DOCUMENT_INFO_FILES_PATHS") + period);
            File pmdlFile = new File(configFile.getProperty("PM_DEBAIE_LIST_FILES_PATHS"));
            this.data.period = period;
            this.politicalMashupDebateListLoader(pmdlFile);
            this.data.members = pMemDataLoader(pmInfoDir);
            this.data.parties = parDataLoader(pInfoDir);
            this.data.cabinets = cabinetDataLoader();
            this.data.debates = debateDataLoader(dInfoDir);
            this.memSpeechLoader();
            
        } catch (ParserConfigurationException|SAXException|IOException|
                    XPathExpressionException|ParseException ex) {
                log.error(ex);
                throw ex;
        }
        data = this.data;
    }
    
    private void politicalMashupDebateListLoader(File file) throws IOException{
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while((line=br.readLine())!=null){
            String filename = "nl.proc.ob.d."+ line + ".xml";
            this.pmDebateList.add(filename);
//            return;
            }
         } catch (IOException ex) {
            log.error(ex);
            throw ex;
        } 
    }

    private HashMap<String, Member> pMemDataLoader(File file) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, ParseException {
        HashMap<String,Member> members = new HashMap<>();
        File pmInfoFiles = file;
        m_MemberDataParser mParser = new m_MemberDataParser();
        for(File pmInfoF: pmInfoFiles.listFiles()){
            if(pmInfoF.isDirectory())
                this.pMemDataLoader(pmInfoF);
            if(!pmInfoF.isFile() || !pmInfoF.getName().endsWith(".xml"))
                continue;
            try {
                mParser.init(pmInfoF.getPath());
                Member m = new Member(mParser.getMemberId(), mParser.getFullName(),mParser.getFirstName(),mParser.getLastName(), mParser.getGender(), 
                               mParser.getBirthDate(), mParser.getBio(),mParser.getFunctions(), mParser.getMembership());
                members.put(mParser.getMemberId(), m);
//                log.info("Data of the member with id="+m.getPmId()+" is loaded");
            } catch (ParserConfigurationException|SAXException|IOException|
                    XPathExpressionException|ParseException ex) {
                log.error(ex);
                throw ex;
            }
        }
        return members;
    }
    
    
    private HashMap<String, Party> parDataLoader(File file) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, ParseException {
        HashMap<String,Party> parties = new HashMap<>();
        File pInfoFiles = file;
        p_PartyDataParser pParser = new p_PartyDataParser();
        for(File pInfoF: pInfoFiles.listFiles()){
            if(pInfoF.isDirectory())
                this.pMemDataLoader(pInfoF);
            if(!pInfoF.getName().endsWith(".xml"))
                continue;
            try {
                pParser.init(pInfoF.getPath());
                Party p = new Party(pParser.getPartyId(), pParser.getFullName(), 
                        pParser.getFormationDate(), pParser.getAnccestorsID(), pParser.getSeats());
                parties.put(pParser.getPartyId(),p);
//                log.info("Data of the party with id="+p.getpId()+" is loaded");
            } catch (ParserConfigurationException|SAXException|IOException|
                    XPathExpressionException|ParseException ex) {
                log.error(ex);
                throw ex;
            }
        }
        return parties;
    }
    
    public Cabinet cabinetDataLoader() throws ParseException {
        Cabinet cabinet = null;
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        if(this.data.period.equals("20062010")){
            try {
                //
                Date sD= formatter.parse("22-02-2007");
                Date eD= formatter.parse("14-10-2010");
                ArrayList coalition = new ArrayList(Arrays.asList("nl.p.cda", "nl.p.pvda", "nl.p.cu"));
                cabinet = new Cabinet("BalkenendeIV",sD, eD, coalition);
            } catch (ParseException ex) {
                log.error(ex);
                throw ex;
            }
        }
        if(this.data.period.equals("20102012")){
            try {
                //
                Date sD= formatter.parse("14-10-2010");
                Date eD= formatter.parse("05-11-2012");
                ArrayList coalition = new ArrayList(Arrays.asList("nl.p.vvd", "nl.p.cda"));
                cabinet = new Cabinet("rutte-I",sD, eD, coalition);
            } catch (ParseException ex) {
                log.error(ex);
                throw ex;
            }
        }
        if(this.data.period.equals("20122014")){
            try {
                //
                Date sD= formatter.parse("05-11-2012");
                Date eD= new Date();
                ArrayList coalition = new ArrayList(Arrays.asList("nl.p.vvd", "nl.p.pvda"));
                cabinet  = new Cabinet("rutte-IّI",sD, eD, coalition);
            } catch (ParseException ex) {
                log.error(ex);
                throw ex;
            }
        }
        return cabinet;
    }
    public HashMap<String, Debate> debateDataLoader(File file) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, ParseException {
        HashMap<String,Debate> debates = new HashMap<>();
        File dInfoFiles = file;
        d_ProceedingDataParser dParser = new d_ProceedingDataParser();
        for(File dInfoF: dInfoFiles.listFiles()){
            if(dInfoF.isDirectory())
                this.debateDataLoader(dInfoF);
            if(!dInfoF.getName().endsWith(".xml"))
                continue;
//            if(!this.pmDebateList.contains(dInfoF.getName()))
//                continue;
            try {
                dParser.init(dInfoF.getPath());
                Debate d = new Debate();
                d.setdId(dParser.getDebateId());
                d.setdTitle(dParser.getDebateTitile());
                d.setdTopic(dParser.getDebateTopic());
                d.setChairmanId(dParser.getChairmanId());
                d.setDate(dParser.getDate());
                d.setInvolvedMembersId(dParser.getInvolvedMembersId());
                d.setSessionNum(dParser.getSessionNumber());
                d.setAllSpeechs(dParser.getAllSpeeches());
                d.debSpeeches.putAll(dParser.getSpeeches());
                this.data.speeches.putAll(dParser.getSpeeches());
                debates.put(d.getdId(),d);
                
                
                log.info("Debate file: " + dInfoF.getName()+ "is processed...");
            } catch (ParserConfigurationException|SAXException|IOException|
                    XPathExpressionException|ParseException ex) {
                log.error(ex);
                throw ex;
            }
        }
        return debates;
    }
    private void memSpeechLoader() {
        for(Map.Entry<String,Speech> e:data.speeches.entrySet()){
                    try {
                        Speech s = e.getValue();
                        Member m = data.members.get(s.getSpeakerId());
                        StringBuilder sb = m.getSpeeches();
                        m.setSpeeches(sb.append(s.getSpeechText()).append("\n"));
                        HashSet<String> affiliations = m.getAffiliations();
                        affiliations.add(s.getSpeakerAffiliation());
                        m.setAffiliations(affiliations);
                        data.members.put(m.getmId(), m);
                    } catch (Exception ex) {
                        System.err.println(ex);
                        continue;
                    }
                }
    }
}
