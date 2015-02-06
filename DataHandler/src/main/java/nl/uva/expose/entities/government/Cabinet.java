/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.expose.entities.government;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import static nl.uva.expose.settings.Config.configFile;

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
        String cabInfoLine = configFile.getProperty(this.period);
        String[] cabInfo = cabInfoLine.split(",");
        name = cabInfo[0];
        sD = formatter.parse(cabInfo[1]);
        eD = cabInfo[2].equals("now")?new Date():formatter.parse(cabInfo[2]);
        coalition = new ArrayList(Arrays.asList(Arrays.copyOfRange(cabInfo, 3, cabInfo.length)));
        this.startDate = sD;
        this.endDate = eD;
        this.name = name;
        this.coalitionPartiesID = coalition;
    }

    public String getStatus(String Affiliations) throws IOException {
        String status = "Oposition";
        for (String coa : this.coalitionPartiesID) {
            if (Affiliations.contains(coa)) {
                status = "Coalition";
                break;
            }
        }
        if (Affiliations.equals("gov")) {
            status = "1";
        }
        if (Affiliations.equals("parl")) {
            status = "0";
        }
        return status;
    }
}
