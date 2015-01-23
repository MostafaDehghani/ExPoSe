/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.expose.entities.debate;

import java.util.HashMap;
import java.util.HashSet;
import nl.uva.expose.entities.DateTime;
import nl.uva.expose.entities.speech.Speech;
import org.apache.log4j.Logger;

/**
 *
 * @author Mostafa Dehghani
 */
public class Debate {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Debate.class.getName());
    private String dId = "";
    private String dTopic = "";
    private String dTitle = "";
    private String sessionNum = "";
    private DateTime date;
    private HashSet<String> involvedMembersId = new HashSet<>();
    private HashSet<String> involvedPMembersId = new HashSet<>();
    private HashSet<String> presentMembersId = new HashSet<>();
    private HashSet<String> speechesId = new HashSet<>();
    private HashSet<String> scenesId = new HashSet<>();
    private String chairmanId = "";
    private StringBuilder allSpeechs = new StringBuilder();
    private StringBuilder allmpSpeechs = new StringBuilder();
    public HashMap<String, Speech> debSpeeches = new HashMap<>();

    public Debate() {
    }

    public static Logger getLog() {
        return log;
    }

    public String getdId() {
        return dId;
    }

    public String getdTopic() {
        return dTopic;
    }

    public DateTime getDate() {
        return date;
    }

    public HashSet<String> getInvolvedMembersId() {
        return involvedMembersId;
    }

    public HashSet<String> getInvolvedPMembersId() {
        return involvedPMembersId;
    }

    public HashSet<String> getPresentMembersId() {
        return presentMembersId;
    }

    public HashSet<String> getSpeechesId() {
        return speechesId;
    }

    public HashSet<String> getScenesId() {
        return scenesId;
    }

    public String getChairmanId() {
        return chairmanId;
    }

    public String getdTitle() {
        return dTitle;
    }

    public String getSessionNum() {
        return sessionNum;
    }

    public void setdId(String dId) {
        this.dId = dId;
    }

    public void setdTopic(String dTopic) {
        this.dTopic = dTopic;
    }

    public void setdTitle(String dTitle) {
        this.dTitle = dTitle;
    }

    public void setSessionNum(String sessionNum) {
        this.sessionNum = sessionNum;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public void setInvolvedMembersId(HashSet<String> involvedMembersId) {
        this.involvedMembersId = involvedMembersId;
    }

    public void setInvolvedPMembersId(HashSet<String> involvedPMembersId) {
        this.involvedPMembersId = involvedPMembersId;
    }

    public void setPresentMembersId(HashSet<String> presentMembersId) {
        this.presentMembersId = presentMembersId;
    }

    public void setSpeechesId(HashSet<String> speechesId) {
        this.speechesId = speechesId;
    }

    public void setScenesId(HashSet<String> scenesId) {
        this.scenesId = scenesId;
    }

    public void setChairmanId(String chairmanId) {
        this.chairmanId = chairmanId;
    }

    public StringBuilder getAllSpeechs() {
        return allSpeechs;
    }

    public void setAllSpeechs(StringBuilder allSpeechs) {
        this.allSpeechs = allSpeechs;
    }

    public StringBuilder getAllMPSpeechs() {
        return allmpSpeechs;
    }

    public void setAllMPSpeechs(StringBuilder allmpSpeechs) {
        this.allmpSpeechs = allmpSpeechs;
    }

}
