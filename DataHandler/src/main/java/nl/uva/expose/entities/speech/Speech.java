/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.expose.entities.speech;

import org.apache.log4j.Logger;

/**
 *
 * @author Mostafa Dehghani
 */
public class Speech {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Speech.class.getName());
    private String speecheId;
    private String speakerId;
    private String speakerAffiliation = "";
    private String speakerRole = "";
    private String speakerFunction  = "";
    private String debateId;
    private StringBuilder speechText;
    private String sceneId;

    
    public Speech(String sId, String speakerId, String speakerAff, String speakerRole, String speakerFunc, String debateId, String sceneId, StringBuilder speechText) {
        this.speecheId = sId;
        this.speakerId = speakerId;
        this.speakerAffiliation = speakerAff;
        this.speakerRole = speakerRole;
        this.speakerFunction = speakerFunc;
        this.debateId = debateId;
        this.speechText = speechText;
        this.sceneId = sceneId;
    }

    public Speech() {
    }
    

    public static Logger getLog() {
        return log;
    }

    public String getSpeechId() {
        return speecheId;
    }

    public String getSpeakerId() {
        return speakerId;
    }

    public String getSpeakerAffiliation() {
        return speakerAffiliation;
    }

    public String getDebateId() {
        return debateId;
    }

    public StringBuilder getSpeechText() {
        return speechText;
    }

    public String getSceneId() {
        return sceneId;
    }

    public String getSpeakerRole() {
        return speakerRole;
    }

    public String getSpeakerFunction() {
        return speakerFunction;
    }

    public void setSpeecheId(String speecheId) {
        this.speecheId = speecheId;
    }

    public void setSpeakerId(String speakerId) {
        this.speakerId = speakerId;
    }

    public void setSpeakerAffiliation(String speakerAffiliation) {
        this.speakerAffiliation = speakerAffiliation;
    }

    public void setSpeakerRole(String speakerRole) {
        this.speakerRole = speakerRole;
    }

    public void setSpeakerFunction(String speakerFunction) {
        this.speakerFunction = speakerFunction;
    }

    public void setDebateId(String debateId) {
        this.debateId = debateId;
    }

    public void setSpeechText(StringBuilder speechText) {
        this.speechText = speechText;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }
    
    
    
}

