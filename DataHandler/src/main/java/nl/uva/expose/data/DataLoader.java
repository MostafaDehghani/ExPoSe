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
import java.text.ParseException;
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

    public DataLoader(Data data, String period) throws Exception {
        this.data = data;
        try {
            File pmInfoDir = new File(configFile.getProperty("MEMBER_INFO_FILES_PATHS"));
            File pInfoDir = new File(configFile.getProperty("PARTY_INFO_FILES_PATHS"));
            File dInfoDir = new File(configFile.getProperty("DOCUMENT_INFO_FILES_PATHS") + period);
//            File pmdlFile = new File(configFile.getProperty("PM_DEBAIE_LIST_FILES_PATHS"));
            this.data.period = period;
//            this.politicalMashupDebateListLoader(pmdlFile);
            this.data.members = pMemDataLoader(pmInfoDir);
//            this.data.parties = parDataLoader(pInfoDir);
            this.data.cabinet = new Cabinet(period);
            this.data.debates = debateDataLoader(dInfoDir);
            this.memSpeechLoader();

        } catch (Exception ex) {
            log.error(ex);
            throw ex;
        }
        data = this.data;
    }

    private void politicalMashupDebateListLoader(File file) throws IOException {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                String filename = "nl.proc.ob.d." + line + ".xml";
                this.pmDebateList.add(filename);
//            return;
            }
        } catch (IOException ex) {
            log.error(ex);
            throw ex;
        }
    }

    private HashMap<String, Member> pMemDataLoader(File file) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, ParseException {
        HashMap<String, Member> members = new HashMap<>();
        File pmInfoFiles = file;
        m_MemberDataParser mParser = new m_MemberDataParser();
        for (File pmInfoF : pmInfoFiles.listFiles()) {
            if (pmInfoF.isDirectory()) {
                this.pMemDataLoader(pmInfoF);
            }
            if (!pmInfoF.isFile() || !pmInfoF.getName().endsWith(".xml")) {
                continue;
            }
            try {
                mParser.init(pmInfoF.getPath());
                Member m = new Member(mParser.getMemberId(), mParser.getFullName(), mParser.getFirstName(), mParser.getLastName(), mParser.getGender(),
                        mParser.getBirthDate(), mParser.getBio(), mParser.getFunctions(), mParser.getMembership());
                members.put(mParser.getMemberId(), m);
//                log.info("Data of the member with id="+m.getPmId()+" is loaded");
            } catch (ParserConfigurationException | SAXException | IOException |
                    XPathExpressionException | ParseException ex) {
                log.error(ex);
                throw ex;
            }
        }
        return members;
    }

    private HashMap<String, Party> parDataLoader(File file) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, ParseException {
        HashMap<String, Party> parties = new HashMap<>();
        File pInfoFiles = file;
        p_PartyDataParser pParser = new p_PartyDataParser();
        for (File pInfoF : pInfoFiles.listFiles()) {
            if (pInfoF.isDirectory()) {
                this.pMemDataLoader(pInfoF);
            }
            if (!pInfoF.getName().endsWith(".xml")) {
                continue;
            }
            try {
                pParser.init(pInfoF.getPath());
                Party p = new Party(pParser.getPartyId(), pParser.getFullName(),
                        pParser.getFormationDate(), pParser.getAnccestorsID(), pParser.getSeats());
                parties.put(pParser.getPartyId(), p);
//                log.info("Data of the party with id="+p.getpId()+" is loaded");
            } catch (ParserConfigurationException | SAXException | IOException |
                    XPathExpressionException | ParseException ex) {
                log.error(ex);
                throw ex;
            }
        }
        return parties;
    }

    public HashMap<String, Debate> debateDataLoader(File file) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, ParseException {
        HashMap<String, Debate> debates = new HashMap<>();
        File dInfoFiles = file;
        d_ProceedingDataParser dParser = new d_ProceedingDataParser();
        for (File dInfoF : dInfoFiles.listFiles()) {
            if (dInfoF.isDirectory()) {
                this.debateDataLoader(dInfoF);
            }
            if (!dInfoF.getName().endsWith(".xml")) {
                continue;
            }
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
                this.data.speeches.putAll(d.debSpeeches);
                StringBuilder allmpSpeeches = new StringBuilder();
                HashSet<String> im = new HashSet<>();
                for (Map.Entry<String, Speech> e : d.debSpeeches.entrySet()) {
                    im.add(e.getValue().getSpeakerId());
                    allmpSpeeches.append(e.getValue().getSpeechText() + "\n");
                }
                d.setInvolvedPMembersId(im);
                d.setAllMPSpeechs(allmpSpeeches);
                debates.put(d.getdId(), d);
                log.info("Debate file: " + dInfoF.getName() + "is processed...");
            } catch (ParserConfigurationException | SAXException | IOException |
                    XPathExpressionException | ParseException ex) {
                log.error(ex);
                throw ex;
            }
        }
        return debates;
    }

    private void memSpeechLoader() {
        HashSet<String> activeMem = new HashSet<>();
        for (Map.Entry<String, Speech> e : data.speeches.entrySet()) {
            try {
                Speech s = e.getValue();
                activeMem.add(s.getSpeakerId());
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
        //removing members who did not talk in the period in question
        HashSet<String> mids = new HashSet<>(data.members.keySet());
        for (String mid : mids) {
            if (!activeMem.contains(mid)) {
                data.members.remove(mid);
            }
        }

    }
}
