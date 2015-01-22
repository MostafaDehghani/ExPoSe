package nl.uva.expose.xmldataparser;

import javax.xml.xpath.XPathExpression;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import nl.uva.expose.entities.DateTime;
import nl.uva.expose.entities.debate.Scene;
import nl.uva.expose.entities.speech.Speech;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * Note: 1.there is a field with the name of "pm:categories" 2. speeches in
 * stage-direction tag are ignored
 *
 * @author Mostafa Dehghani
 */
public class d_ProceedingDataParser {

    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(d_ProceedingDataParser.class.getName());
    private DocumentBuilderFactory factory;
    private DocumentBuilder builder;
    private Document doc;
    private XPathFactory xPathfactory;
    private XPath xpath;
    private XPathExpression expr;
    

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

    public String getDebateId() throws XPathExpressionException {
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

    public String getDebateTitile() throws XPathExpressionException {
        String title = null;
        try {
            XPathExpression expr
                    = this.xpath.compile("//dc:title/text()");

            title = (String) expr.evaluate(this.doc, XPathConstants.STRING);
        } catch (XPathExpressionException ex) {
            log.error("file path:" + this.getDebateId() + "\n" + ex);
            throw ex;
        }
        return title;
    }

    public String getSessionNumber() throws XPathExpressionException {
        String title = null;
        try {
            XPathExpression expr
                    = this.xpath.compile("//pm:session-number/text()");

            title = (String) expr.evaluate(this.doc, XPathConstants.STRING);
        } catch (XPathExpressionException ex) {
            log.error("file path:" + this.getDebateId() + "\n" + ex);
            throw ex;
        }
        return title;
    }

    public DateTime getDate() throws XPathExpressionException, ParseException {
        DateTime dDate = null;
        try {
            XPathExpression expr
                    = this.xpath.compile("//dc:date/text()");
            String dateStr = (String) expr.evaluate(this.doc, XPathConstants.STRING);
            dDate = new DateTime(dateStr, "8");
            Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);
        } catch (XPathExpressionException | ParseException ex) {
            log.error("file path:" + this.getDebateId() + "\n" + ex);
            throw ex;
        }
        return dDate;
    }

    public String getDebateTopic() throws XPathExpressionException {
        String topic = null;
        try {
            XPathExpression expr
                    = this.xpath.compile("//pm:topic");

            Node node = (Node) expr.evaluate(this.doc, XPathConstants.NODE);
            topic = node.getAttributes().getNamedItem("pm:title").getNodeValue();
        } catch (XPathExpressionException ex) {
            log.error(ex);
            throw ex;
        }
//        //just for check whther a proc can have more that one topic:
//        NodeList nodel = (NodeList) expr.evaluate(this.doc, XPathConstants.NODE);
//        if(nodel.getLength()>1){
//            log.error("More than one topic for one debate! --> Debate ID: " + this.getDebateId());
//        }
        return topic;
    }

    public HashMap<String, Scene> getScenes() throws XPathExpressionException {
        HashMap<String, Scene> scenes = new HashMap<>();
        try {
            XPathExpression expr
                    = this.xpath.compile("//pm:scene");
            NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Scene scene = new Scene();
                Node sNode = nodeList.item(i);
                if (sNode instanceof Element) {
                    Element sElement = (Element) sNode;
                    scene.sceneId = sElement.getAttribute("pm:id");
                    scene.sceneType = sElement.getAttribute("pm:type");
                    scene.speakerId = sElement.getAttribute("pm:member-ref");
                    scene.speakerRole = sElement.getAttribute("pm:role");
                    scene.speakerFunction = sElement.getAttribute("pm:function");
                    // TODO: scene speeches id...
                }
                scenes.put(scene.sceneId, scene);
            }
        } catch (XPathExpressionException ex) {
            log.error("file path:" + this.getDebateId() + "\n" + ex);
            throw ex;
        }
        return scenes;
    }
    
    public HashSet<String> getInvolvedMembersId() throws XPathExpressionException {
        HashSet<String> invMemId = new HashSet<>();
        try {
            XPathExpression expr
                    = this.xpath.compile("//dc:relation/pm:subject[@xsi:type='speaker']/pm:actor/pm:person");
            NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node sNode = nodeList.item(i);
                if (sNode instanceof Element) {
                    Element element = (Element) sNode;
                    invMemId.add(element.getAttribute("pm:member-ref"));
                }
            }
        } catch (XPathExpressionException ex) {
            log.error("file path:" + this.getDebateId() + "\n" + ex);
            throw ex;
        }
        return invMemId;
    }
    
    
    public StringBuilder getAllSpeeches() throws XPathExpressionException {
        StringBuilder allSpeeches = new StringBuilder();
        try {
            XPathExpression expr
                    = this.xpath.compile("//pm:speech/pm:p");
            NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element pEle = (Element) nodeList.item(i);
                allSpeeches.append(pEle.getTextContent());
                allSpeeches.append("\n");
            }
        } catch (XPathExpressionException ex) {
            log.error("file path:" + this.getDebateId() + "\n" + ex);
            throw ex;
        }
        return allSpeeches;
    }
    public String getChairmanId() throws XPathExpressionException {
        String chairmanId = null;
        try {
            XPathExpression expr
                    = this.xpath.compile("//dc:relation/pm:subject[@xsi:type='speaker' and @pm:role='chair']/pm:actor/pm:person");
            Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);
            if (node instanceof Element) {
                Element element = (Element) node;
                chairmanId = element.getAttribute("pm:member-ref");
            }
        } catch (XPathExpressionException ex) {
            log.error("file path:" + this.getDebateId() + "\n" + ex);
            throw ex;
        }
        return chairmanId;
    }

    public HashMap<String, Speech> getSpeeches() throws XPathExpressionException, ParseException {
        HashMap<String, Speech> speeches = new HashMap<>();
        try {
            XPathExpression expr
                    = this.xpath.compile("//pm:speech");
            NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Speech speech = new Speech();
                Node sNode = nodeList.item(i);
                if (sNode instanceof Element) {
                    Element sElement = (Element) sNode;
                    speech.setDebateId(this.getDebateId());
                    speech.setSpeecheId(sElement.getAttribute("pm:id"));
                    speech.setSpeakerId(sElement.getAttribute("pm:member-ref"));
                    speech.setSpeakerRole(sElement.getAttribute("pm:role"));
                    speech.setSpeakerFunction(sElement.getAttribute("pm:function"));
                    if (speech.getSpeakerRole().equals("mp")) {
                        speech.setSpeakerAffiliation(sElement.getAttribute("pm:party-ref"));
                    } else if (speech.getSpeakerRole().equals("government")) {
                        speech.setSpeakerAffiliation("gov");
                    } else if (speech.getSpeakerRole().equals("chair")) {
                        speech.setSpeakerAffiliation("parl");
                    }
                    Element pElements = (Element) sNode.getChildNodes();
                    NodeList pNodeList  = pElements.getElementsByTagName("p");
                    StringBuilder sb = new StringBuilder();
                    for (int j = 0; j < pNodeList.getLength(); j++) {
                        Element pEle = (Element) pNodeList.item(j);
                        sb.append(pEle.getTextContent());
                        sb.append("\n");
                    }
                    speech.setSpeechText(sb);
                    Node parNode = sNode.getParentNode();
                    if (sNode instanceof Element) {
                        Element parElement = (Element) parNode;
                        if(parNode.getNodeName().equals("stage-direction")){
                            speech.setSceneId(parElement.getAttribute("pm:id"));
                        }
                        else if(parNode.getNodeName().equals("scene")){
                            speech.setSceneId(parElement.getAttribute("pm:id"));
                        }
                    }
                }
                if(speech.getSpeakerAffiliation().equals("") || speech.getSpeakerAffiliation()==null)
                    continue;
                if(speech.getSpeakerAffiliation().equals("parl") || speech.getSpeakerAffiliation().equals("gov"))
                    continue;
                if(speech.getSpeakerId().equals("") || speech.getSpeakerId()==null)
                    continue;
                speeches.put(speech.getSpeechId(), speech);
            }
        } catch (XPathExpressionException ex) {
            log.error("file path:" + this.getDebateId() + "\n" + ex);
            throw ex;
        }
        return speeches;
    }
}
