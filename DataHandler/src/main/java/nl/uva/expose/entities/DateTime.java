/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.expose.entities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 *
 * @author Mostafa Dehghani
 */
public class DateTime {
    
    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(DateTime.class.getName());
    
    public Date date;
    public Integer granularity;

    public DateTime(String dateStr, String granularity) throws ParseException {
        
        if(dateStr==null && granularity==null){
            this.date = null;
            this.granularity = null;
            return;
        }
        DateFormat formatter;
        this.granularity = Integer.parseInt(granularity);
//        if(this.granularity != 8){
//            log.info("Granularity is not perfect for: " + dateStr);
//        }
        formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if(dateStr.equalsIgnoreCase("present"))
                this.date = new Date();
            else
                this.date = formatter.parse(dateStr);
        } catch (ParseException ex) {
            log.error(ex);
            throw ex;
        }
    } 
     public DateTime() throws ParseException {
            this.date = null;
            this.granularity = null;
            return;
     }

    @Override
    public String toString() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        if(this.date == null)
            return "yyyy-MM-dd";
        return df.format(this.date);
    }
    
     
     
     
}
