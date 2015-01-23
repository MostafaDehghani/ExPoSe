package nl.uva.expose.xmldataparser;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import nl.uva.expose.entities.DateTime;
import nl.uva.expose.entities.parties.Seat;
import nl.uva.expose.entities.member.Function;
import nl.uva.expose.entities.member.Membership;
import static nl.uva.expose.xmldataparser.m_MemberDataParser.log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Mostafa Dehghani
 */
public class p_PartyDataParser {

    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(p_PartyDataParser.class.getName());
    private DocumentBuilderFactory factory = null;
    private DocumentBuilder builder = null;
    private Document doc = null;
    private XPathFactory xPathfactory = null;
    private XPath xpath = null;
    private XPathExpression expr = null;

    public void init(String uri) throws ParserConfigurationException, SAXException, IOException {
        factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
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
        xpath.setNamespaceContext(new UniversalNamespaceCache(doc, true));
    }

    public String getPartyId() throws XPathExpressionException {
        String id = null;
        try {
            XPathExpression expr
                    = this.xpath.compile("//dc:identifier/text()");

            id = (String) expr.evaluate(this.doc, XPathConstants.STRING);
        } catch (XPathExpressionException ex) {
            log.error(ex);
            throw ex;
        }
        return id;
    }

    public String getFullName() throws XPathExpressionException {
        String fullName = null;
        try {
            XPathExpression expr
                    = this.xpath.compile("/root/pm:party/pm:name/text()");
            fullName = (String) expr.evaluate(this.doc, XPathConstants.STRING);
        } catch (XPathExpressionException ex) {
            log.error("file path:" + this.getPartyId() + "\n" + ex);
            throw ex;
        }
        return fullName;
    }

    public DateTime getFormationDate() throws XPathExpressionException, ParseException {
        DateTime fDate = null;
        try {
            XPathExpression expr
                    = this.xpath.compile("//pm:history/pm:formation");
            Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);
            try {
                String dateStr = node.getAttributes().getNamedItem("pm:date").getNodeValue();
                String dateGraStr = node.getAttributes().getNamedItem("pm:granularity").getNodeValue();
                fDate = new DateTime(dateStr, dateGraStr);
            } catch (NullPointerException ex) {
                log.error("No Formation Date info for party:" + this.getPartyId() + "\n" + ex);
                return new DateTime();
            }
        } catch (XPathExpressionException | ParseException ex) {
            log.error("file path:" + this.getPartyId() + "\n" + ex);
            throw ex;
        }
        return fDate;
    }

    public ArrayList<String> getAnccestorsID() throws XPathExpressionException {
        ArrayList<String> paId = new ArrayList<>();
        try {
            XPathExpression expr
                    = this.xpath.compile("//pm:history/pm:ancestors/pm:party-ref");
            NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodeList.getLength(); i++) {
                paId.add(nodeList.item(i).getAttributes().getNamedItem("pm:party-ref").getNodeValue());
            }
        } catch (XPathExpressionException ex) {
            log.error("file path:" + this.getPartyId() + "\n" + ex);
            throw ex;
        }
        return paId;
    }

    public ArrayList<Seat> getSeats() throws XPathExpressionException, ParseException {
        ArrayList<Seat> seats = new ArrayList<>();
        try {
            XPathExpression expr
                    = this.xpath.compile("//pm:seats/pm:session");
            NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node sNode = nodeList.item(i);
                Seat seat = new Seat();
                if (sNode instanceof Element) {
                    try {
                        seat.house = sNode.getAttributes().getNamedItem("pm:house").getNodeValue();
                        seat.seatsNum = Integer.parseInt(sNode.getAttributes().getNamedItem("pm:seats").getNodeValue());
                        Element fElement = (Element) sNode.getChildNodes();
                        try {
                            Element periodElement = (Element) fElement.getElementsByTagName("period").item(0);
                            seat.from = new DateTime(periodElement.getAttribute("pm:from"), periodElement.getAttribute("pm:from-granularity"));
                            seat.to = new DateTime(periodElement.getAttribute("pm:till"), periodElement.getAttribute("pm:till-granularity"));
                        } catch (NullPointerException ex) {
                            log.info("No period info for some session  for member:" + this.getPartyId() + "\n" + ex);
                        }
                    } catch (NullPointerException ex) {
                        log.info("No seats for:" + this.getPartyId() + "\n" + ex);
                    }
                }
                seats.add(seat);
            }
        } catch (XPathExpressionException | ParseException ex) {
            log.error("file path:" + this.getPartyId() + "\n" + ex);
            throw ex;
        }
        return seats;
    }

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, ParseException {
        p_PartyDataParser p = new p_PartyDataParser();
        p.init("/Users/Mosi/Desktop/ExPoSe/Data/Cleaned/Parliament/p/nl/nl.p.vvd.xml");
        System.out.println(p.getSeats().get(1).seatsNum);
    }
}
