/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.expose.entities.government;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author Mostafa Dehghani
 */
public class Cabinet {
    public String period;
    public String name;
    public Date startDate;
    public Date endDate;
    public String spectrum;
    public ArrayList<String> coalitionPartiesID;
    public ArrayList<String> agreePartiesID;

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Cabinet.class.getName());
   
    public Cabinet(String period) throws ParseException, Exception {
        this.period = period;
        Date sD = null;
        Date eD = null;
        ArrayList coalition = null;
        String name = null;
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        if(this.period.equals("20062010")){
            try {
                //
               sD = formatter.parse("22-02-2007");
               eD= formatter.parse("14-10-2010");
               coalition = new ArrayList(Arrays.asList("nl.p.cda", "nl.p.pvda", "nl.p.cu"));
               name = "BalkenendeIV";
            } catch (ParseException ex) {
                log.error(ex);
                throw ex;
            }
        }
        if(this.period.equals("20102012")){
            try {
                //
                sD= formatter.parse("14-10-2010");
                eD= formatter.parse("05-11-2012");
                coalition = new ArrayList(Arrays.asList("nl.p.vvd", "nl.p.cda"));
                name = "rutte-I";
            } catch (ParseException ex) {
                log.error(ex);
                throw ex;
            }
        }
        if(this.period.equals("20122014")){
            try {
                //
                sD= formatter.parse("05-11-2012");
                eD= new Date();
                coalition = new ArrayList(Arrays.asList("nl.p.vvd", "nl.p.pvda"));
                name  = "rutte-IّI";
            } catch (ParseException ex) {
                log.error(ex);
                throw ex;
            }
        }
        else{
            Exception ex = new Exception();
            log.error("No cabinet found for this period");
            throw ex;
        }
        this.startDate = sD;
        this.endDate = eD;
        this.name = name;
        this.coalitionPartiesID = coalition;
    }
}
