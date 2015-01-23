/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.expose.data;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import nl.uva.expose.entities.DateTime;
import nl.uva.expose.entities.debate.Debate;
import nl.uva.expose.entities.government.Cabinet;
import nl.uva.expose.entities.parties.Party;
import nl.uva.expose.entities.member.Member;
import nl.uva.expose.entities.speech.Speech;
import org.xml.sax.SAXException;

/**
 *
 * @author Mostafa Dehghani
 */
public class Data {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Data.class.getName());
    
    public String period;
    public HashMap<String,Member> members;
    public HashMap<String,Party> parties;
    public HashMap<String,Speech> speeches;
    public HashMap<String,Debate> debates;
    public Cabinet cabinet;


    public Data(String period) throws ParserConfigurationException, SAXException, IOException, ParseException, XPathExpressionException{
        try {
            this.debates = new HashMap<>();
            this.members = new HashMap<>();
            this.parties = new HashMap<>();
            this.speeches = new HashMap<>();
            this.period = period;
            new DataLoader(this, period);
        } catch (ParserConfigurationException|SAXException|IOException
                |XPathExpressionException|ParseException ex) {
            log.error(ex);
            throw ex;
        }
    }
    
        
    public static void main(String[] args) throws Exception {
        Data data = new Data("20062010");
        for(Debate d: data.debates.values()){
            System.out.println( d.getdId() + "," + d.getDate().toString());
        }
//        System.out.println(data.debates.size());
//        System.out.println(data.speeches.size());
    }
}
