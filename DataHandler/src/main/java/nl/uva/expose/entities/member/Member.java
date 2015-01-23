/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.expose.entities.member;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import nl.uva.expose.entities.DateTime;

/**
 *
 * @author Mostafa Dehghani
 */
public class Member {

    private String mId;
    private String firstName;
    private String lastName;
    private String fullName;
//    private String initName;
//    private String title;
    private String gender;
    private DateTime bDate;
    private String bioText;
    private ArrayList<Function> functions;
    private ArrayList<Membership> memberships;

    //for Index:
    private String period;
    private StringBuilder speeches = new StringBuilder();
    private Integer speechNum;
    private HashSet<String> affiliations = new HashSet<>();
    private HashSet<String> roles = new HashSet<>();
    private HashSet<String> speechTimeFunctions = new HashSet<>();

    public Member(String pmId, String fullName, String firstName, String lastName, String gender, DateTime bDate, String bioText, ArrayList<Function> functions, ArrayList<Membership> memberships) {
        this.mId = pmId;
        this.fullName = fullName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.bDate = bDate;
        this.bioText = bioText;
        this.functions = functions;
        this.memberships = memberships;
        this.affiliations = new HashSet<>();
        this.roles = new HashSet<>();
        this.speechTimeFunctions = new HashSet<>();

    }

    public Member(String period, String pmId, String fullName, String firstName, String lastName, String gender, DateTime bDate, String bioText, String speech, String aff, String role, String speechTimeFunc) {
        this.mId = pmId;
        this.fullName = fullName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.bDate = bDate;
        this.bioText = bioText;
        this.speeches.append("\n" + speech);
        this.affiliations = new HashSet<>();
        this.affiliations.add(aff);
        this.roles = new HashSet<>();
        this.roles.add(role);
        this.speechTimeFunctions = new HashSet<>();
        this.speechTimeFunctions.add(speechTimeFunc);
        this.period = period;
    }

    public String getmId() {
        return mId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public DateTime getbDate() {
        return bDate;
    }

    public String getBioText() {
        return bioText;
    }

    public ArrayList<Function> getFunctions() {
        return functions;
    }

    public ArrayList<Membership> getMemberships() {
        return memberships;
    }

    public String getPeriod() {
        return period;
    }

    public StringBuilder getSpeeches() {
        return speeches;
    }

    public Integer getSpeechNum() {
        return speechNum;
    }

    public HashSet<String> getAffiliations() {
        return affiliations;
    }

    public HashSet<String> getRoles() {
        return roles;
    }

    public HashSet<String> getSpeechTimeFunctions() {
        return speechTimeFunctions;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public void setSpeeches(StringBuilder speeches) {
        this.speeches = speeches;
    }

    public void setSpeechNum(Integer speechNum) {
        this.speechNum = speechNum;
    }

    public void setAffiliations(HashSet<String> affiliations) {
        this.affiliations = affiliations;
    }

    public void setRoles(HashSet<String> roles) {
        this.roles = roles;
    }

    public void setSpeechTimeFunctions(HashSet<String> speechTimeFunctions) {
        this.speechTimeFunctions = speechTimeFunctions;
    }

}
