/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.expose.entities.government;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author Mostafa Dehghani
 */
public class Cabinet {
    public String name;
    public Date startDate;
    public Date endDate;
    public String spectrum;
    public ArrayList<String> coalitionPartiesID;
    public ArrayList<String> agreePartiesID;

    public Cabinet(String name,Date startDate, Date endDate, ArrayList<String> coalitionPartiesID) {
        this.name=name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.coalitionPartiesID = coalitionPartiesID;
    }
}
