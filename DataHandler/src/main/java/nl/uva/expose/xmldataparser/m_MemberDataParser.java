package nl.uva.expose.xmldataparser;


import javax.xml.xpath.XPathExpression;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import nl.uva.expose.entities.DateTime;
import nl.uva.expose.entities.member.Function;
import nl.uva.expose.entities.member.Membership;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
 
 
/**
 *
 * @author Mostafa Dehghani
 */
public class m_MemberDataParser {
    
    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(m_MemberDataParser.class.getName());
    private DocumentBuilderFactory factory = null;
    private DocumentBuilder builder = null;
    private Document doc = null;
    private XPathFactory xPathfactory = null;
    private XPath xpath = null;
    private XPathExpression expr = null;
    
    
    public void init(String uri) throws ParserConfigurationException, SAXException, IOException{
        factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        try {
            builder = factory.newDocumentBuilder();
        }catch (ParserConfigurationException ex) {
            log.error(ex);
            throw ex;
        }
        try {
            doc = builder.parse(uri);
        } catch (SAXException | IOException ex) {
            log.error(ex);
            throw ex;
        }
        xPathfactory = XPathFactory.newInstance();
        xpath = xPathfactory.newXPath();
        xpath.setNamespaceContext(new UniversalNamespaceCache(doc,true));
    }
    
    public String getMemberId() throws XPathExpressionException {
        String id = null;
        try {
            XPathExpression expr =
                    this.xpath.compile("//dc:identifier/text()");
            
            id = (String) expr.evaluate(this.doc, XPathConstants.STRING);
        } catch (XPathExpressionException ex) {
            log.error(ex);
            throw ex;
        }
        return id;
    }
    
    public String getGender() throws XPathExpressionException {
        String gender = null;
        try {
            XPathExpression expr =
                    this.xpath.compile("/root/pm:member/pm:personal/pm:gender/text()");
            
            gender = (String) expr.evaluate(this.doc, XPathConstants.STRING);
        } catch (XPathExpressionException ex) {
            log.error("file path:" + this.getMemberId() + "\n" + ex);
            throw ex;
        }
        return gender;
    }
     
    public String getFullName() throws XPathExpressionException {
        String fullName = null;
        try {
            XPathExpression expr =
                    this.xpath.compile("/root/pm:member/pm:name/pm:full/text()");
            fullName = (String) expr.evaluate(this.doc, XPathConstants.STRING);
        } catch (XPathExpressionException ex) {
            log.error("file path:" + this.getMemberId() + "\n" + ex);
            throw ex;
        }
        return fullName;
    }
    
    public String getFirstName() throws XPathExpressionException {
        String firstName = null;
        try {
            XPathExpression expr =
                    this.xpath.compile("/root/pm:member/pm:name/pm:first/text()");
            firstName = (String) expr.evaluate(this.doc, XPathConstants.STRING);
        } catch (XPathExpressionException ex) {
            log.error("file path:" + this.getMemberId() + "\n" + ex);
            throw ex;
        }
        return firstName;
    }
    
    public String getLastName() throws XPathExpressionException {
        String lastName = null;
        try {
            XPathExpression expr =
                    this.xpath.compile("/root/pm:member/pm:name/pm:last/text()");
            lastName = (String) expr.evaluate(this.doc, XPathConstants.STRING);
        } catch (XPathExpressionException ex) {
            log.error("file path:" + this.getMemberId() + "\n" + ex);
            throw ex;
        }
        return lastName;
    }
    
    public DateTime getBirthDate() throws XPathExpressionException, ParseException {
        DateTime bDate = null;
        try {
            XPathExpression expr =
                    this.xpath.compile("//pm:born");
            Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);
            try{
                String dateStr = node.getAttributes().getNamedItem("pm:date").getNodeValue();
                String dateGraStr = node.getAttributes().getNamedItem("pm:granularity").getNodeValue();
                bDate = new DateTime(dateStr, dateGraStr);
            } catch (NullPointerException ex) {
//                log.error("No Birth Date info for:" + this.getMemberId() + "\n" + ex);
                return new DateTime();
            }   
        } catch (XPathExpressionException|ParseException ex) {
            log.error("file path:" + this.getMemberId() + "\n" + ex);
            throw ex;
        } 
        return bDate;
    }
    
    public String getBio() throws XPathExpressionException {
        String bio = "";
        try {
           XPathExpression expr =
                    this.xpath.compile("//pm:biographies/pm:biography/text()");
            NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodeList.getLength(); i++)
                bio += nodeList.item(i).getNodeValue() + "\n\n";
        } catch (XPathExpressionException ex) {
            log.error("file path:" + this.getMemberId() + "\n" + ex);
            throw ex;
        }
        return bio;
    }
    
    public ArrayList<Function> getFunctions() throws XPathExpressionException, ParseException{
        ArrayList<Function> functions = new ArrayList<>();
        try {
           XPathExpression expr =
                    this.xpath.compile("//pm:curriculum/pm:function");
            NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodeList.getLength(); i++){
                 Node fNode = nodeList.item(i);
                 Function func = new Function();
                 if(fNode instanceof Element){
                    try{
                    Element fElement = (Element) fNode.getChildNodes();
                        func.name = fElement.getElementsByTagName("name").item(0).getTextContent();
                    try{
                        Element periodElement = (Element)fElement.getElementsByTagName("period").item(0);
                        func.from = new DateTime(periodElement.getAttribute("pm:from"),periodElement.getAttribute("pm:from-granularity"));
                        func.to = new DateTime(periodElement.getAttribute("pm:till"),periodElement.getAttribute("pm:till-granularity"));
                    } catch (NullPointerException ex) {
//                        log.info("No period info for func:" + func.name  + " for member:"+ this.getMemberId() + "\n" + ex);
                    }
                    } catch (NullPointerException ex) {
//                        log.info("No funcion for:" +  this.getMemberId() + "\n" + ex);
                    }
                 }
                 functions.add(func);
            }
        } catch (XPathExpressionException |ParseException ex) {
            log.error("file path:" + this.getMemberId() + "\n" + ex);
            throw ex;
        }
        return functions;
    }
    
    public ArrayList<Membership> getMembership() throws XPathExpressionException, ParseException{
        ArrayList<Membership> membership = new ArrayList<>();
        try {
           XPathExpression expr =
                    this.xpath.compile("//pm:memberships/pm:membership");
            NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodeList.getLength(); i++){
                 Membership memShip = new Membership();
                 Node fNode = nodeList.item(i);
                 if(fNode instanceof Element){
                    Element mElement = (Element) fNode;
                    memShip.body = mElement.getAttribute("pm:body");
                    Element msElement = (Element) fNode.getChildNodes();
                    if(memShip.body.equals("government")){
                        memShip.LegislativeSession=mElement.getAttribute("pm:legislative-session");
                        try{
                        memShip.name = msElement.getElementsByTagName("name").item(0).getTextContent();
                        }catch (NullPointerException ex) {
//                            log.info("No membership for:" +  this.getMemberId() + "\n" + ex);
                        }
                    }
                    else if(memShip.body.equals("commons") || memShip.body.equals("senate")){
                        memShip.PartyRef=mElement.getAttribute("pm:party-ref");
                    }
                    else
                        log.error("new type of body: " + memShip.body);
                    try{
                        Element periodElement = (Element)msElement.getElementsByTagName("period").item(0);
                        memShip.from = new DateTime(periodElement.getAttribute("pm:from"),periodElement.getAttribute("pm:from-granularity"));
                        memShip.to = new DateTime(periodElement.getAttribute("pm:till"),periodElement.getAttribute("pm:till-granularity"));
                 
                    } catch (NullPointerException ex) {
//                        log.info("No period info for membership for member:"+ this.getMemberId() + "\n" + ex);
                    }
                    
                  }
                 membership.add(memShip);
            }
        } catch (XPathExpressionException |ParseException ex) {
            log.error("file path:" + this.getMemberId() + "\n" + ex);
            throw ex;
        }
        return membership;
    }
    
   
    
    
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, ParseException {
        m_MemberDataParser m = new m_MemberDataParser();
        m.init("/Users/Mosi/Desktop/ExPoSe/Data/Cleaned/Parliament/m/nl/nl.m.xml");
        System.out.println(m.getMembership().get(0).body);
    }
    
    
}
