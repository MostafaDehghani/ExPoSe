/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.expose.entities.member;

import nl.uva.expose.entities.DateTime;

/**
 *
 * @author Mostafa Dehghani
 */
public class Membership {

    public String name;
    public DateTime from;
    public DateTime to;
    public String extraText;
    public String body; //government or commons or senate
    public String LegislativeSession;
    public String PartyRef;

}
