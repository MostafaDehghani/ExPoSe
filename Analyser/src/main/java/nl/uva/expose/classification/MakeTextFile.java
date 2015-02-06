package nl.uva.expose.classification;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashSet;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import nl.uva.expose.entities.government.Cabinet;
import static nl.uva.expose.settings.Config.configFile;
import nl.uva.lucenefacility.IndexInfo;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.SimpleFSDirectory;
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
public class MakeTextFile {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(MakeTextFile.class.getName());
    private IndexReader mireader;
    private IndexInfo miInfo;
    private Cabinet cabinet;

    public MakeTextFile(String period) throws IOException, ParserConfigurationException, SAXException, ParseException, XPathExpressionException, Exception {
        this.mireader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("INDEXES_PATH") + period + "/m")));
        this.miInfo = new IndexInfo(mireader);
        this.cabinet = new Cabinet(period);
    }

    public static void main0() throws Exception {
        String period = "20122014";
        MakeTextFile mtf = new MakeTextFile(period);
        String dir = "/Users/Mosi/Desktop/SIGIR_SHORT/" + period + "-mem";
        File dirF = new File(dir);
        if (dirF.exists()) {
            FileUtils.deleteDirectory(dirF);
        }
        FileUtils.forceMkdir(new File(dir));
        for (int i = 0; i < mtf.mireader.numDocs(); i++) {
            Document hitDoc = mtf.mireader.document(i);
            String id = hitDoc.get("ID");
            String text = hitDoc.get("TEXT");
            String aff = "";
            String status = "Oposition";
            for (String s : mtf.miInfo.getDocAllTerm(i, "AFF")) {
                aff += s + " ";
            }
            for (String coa : mtf.cabinet.coalitionPartiesID) {
                if (aff.contains(coa)) {
                    status = "Coalition";
                    break;
                }
            }
            File dirFF = new File(dir + "/" + status);
            if (!dirFF.exists()) {
                FileUtils.forceMkdir(new File(dir + "/" + status));
            }
            FileWriter fileWritter = new FileWriter(dir + "/" + status + "/" + id);
            BufferedWriter bw = new BufferedWriter(fileWritter);
            bw.write(text);
            bw.close();
            System.out.println("doc " + id + "is generated...");
        }
    }
    
    public static void main1() throws Exception {
        String period = "20062010";
        MakeTextFile mtf = new MakeTextFile(period);
        String dir = "/Users/Mosi/Desktop/SIGIR_SHORT/" + period + "-mem";
        File dirF = new File(dir);
        if (dirF.exists()) {
            FileUtils.deleteDirectory(dirF);
        }
        FileUtils.forceMkdir(new File(dir));
        for (int i = 0; i < mtf.mireader.numDocs(); i++) {
            Document hitDoc = mtf.mireader.document(i);
            String id = hitDoc.get("ID");
            String text = hitDoc.get("TEXT");
            String party= mtf.getMemParty(i);
            File dirFF = new File(dir + "/" + party);
            if (!dirFF.exists()) {
                FileUtils.forceMkdir(new File(dir + "/" + party));
            }
            FileWriter fileWritter = new FileWriter(dir + "/" + party + "/" + id);
            BufferedWriter bw = new BufferedWriter(fileWritter);
            bw.write(text);
            bw.close();
            System.out.println("doc " + id + "is generated...");
        }
    }
    
    public String getMemParty(Integer memIndexId) throws IOException {
        String aff = "";
        HashSet<String> affiliations = this.miInfo.getDocAllTerm(memIndexId, "AFF");
        //
        if(affiliations.contains("nl.p.lidbontes"))
            return "nl.p.lidbontes";
        if(affiliations.contains("nl.p.groepkortenoevenhernandez"))
            return "nl.p.groepkortenoevenhernandez";
        if(affiliations.contains("nl.p.lidbrinkman"))
            return "nl.p.lidbrinkman";
       if(affiliations.contains("nl.p.lidverdonk"))
            return "nl.p.lidverdonk";
       //
        for (String s : affiliations) {
            aff = s;
//            break;
        }
        if (affiliations.size() > 1) {
            System.err.println("More than one affiliation: " + affiliations.toString() + " --> " + aff);
        }
        return aff.trim();
    }
    
    public static void main(String[] args) throws Exception {
        main1();
    }
}
