/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.expose.entities.parties;

import java.util.ArrayList;
import java.util.HashSet;
import nl.uva.expose.entities.DateTime;

/**
 *
 * @author Mostafa Dehghani
 */
public class Party {

    private String pmId;
    private String fullName;
//    private String shortName;
//    private String longName;
    private DateTime formation;
    private ArrayList<String> ancestorsId;
    private ArrayList<Seat> seats;

    //for Indexing
    private String period;
    private StringBuilder speeches;
    private Integer speechNum;
    private HashSet<String> speakers;

    public Party(String pmId, String fullName, DateTime formation, ArrayList<String> ancestorsId, ArrayList<Seat> seats) {
        this.pmId = pmId;
        this.fullName = fullName;
        this.formation = formation;
        this.ancestorsId = ancestorsId;
        this.seats = seats;
    }

    public Party(String period, String pmId, String fullName, DateTime formation, String speech, String speaker) {
        this.pmId = pmId;
        this.fullName = fullName;
        this.formation = formation;
        this.speeches = new StringBuilder();
        this.speeches.append("\n" + speech);
        this.speakers = new HashSet<>();
        this.speakers.add(speaker);
        this.period = period;
    }

    public String getPmId() {
        return pmId;
    }

    public String getFullName() {
        return fullName;
    }

    public DateTime getFormation() {
        return formation;
    }

    public ArrayList<String> getAncestorsId() {
        return ancestorsId;
    }

    public ArrayList<Seat> getSeats() {
        return seats;
    }

//     public Integer getPartyOrientation(String Date date){
//        Integer orientation=0; //1:coalition / -1:opposition
//        for(Map.Entry<String,Cabinet> c:cbinets.entrySet()){
//            if(date.after(c.getValue().startDate) && date.before(c.getValue().endDate)){
//                if(c.getValue().coalitionPartiesID.contains(partyId)){
//                   orientation = 1;
//                }
//                else{
//                    orientation = -1;
//                }
//            }
//        }
//        if(orientation==0)
//            log.error("The orientation of the given party " + partyId + "is not determined....");
//        return orientation;
//    }
    public String getPeriod() {
        return period;
    }

    public StringBuilder getSpeeches() {
        return speeches;
    }

    public Integer getSpeechNum() {
        return speechNum;
    }

    public HashSet<String> getSpeakers() {
        return speakers;
    }

}
