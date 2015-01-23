package nl.uva.expose.classification;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import nl.uva.expose.data.Data;
import nl.uva.expose.entities.member.Member;
import nl.uva.expose.entities.speech.Speech;
import static nl.uva.expose.settings.Config.configFile;
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
    private IndexReader ireader;
    private Data data;

    public MakeTextFile(String period) throws IOException, ParserConfigurationException, SAXException, ParseException, XPathExpressionException {
        this.ireader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("INDEXES_PATH") + period + "/m")));
        this.data = new Data(period);
        for (Map.Entry<String, Speech> e : data.speeches.entrySet()) {
            try {
                Speech s = e.getValue();
                Member m = this.data.members.get(s.getSpeakerId());
//                StringBuilder sb = m.getSpeeches();
//                m.setSpeeches(sb.append(s.getSpeechText()).append("\n"));
                HashSet<String> affiliations = m.getAffiliations();
                affiliations.add(s.getSpeakerAffiliation());
                m.setAffiliations(affiliations);
                this.data.members.put(m.getmId(), m);
            } catch (Exception ex) {
                System.err.println(ex);
                continue;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        String period = "20122014";
        MakeTextFile mtf = new MakeTextFile(period);
        String dir = "/Users/Mosi/Desktop/SIGIR_SHORT/" + period + "-mem";
        File dirF = new File(dir);
        if (dirF.exists()) {
            FileUtils.deleteDirectory(dirF);
        }
        FileUtils.forceMkdir(new File(dir));
        for (int i = 0; i < mtf.ireader.numDocs(); i++) {
            Document hitDoc = mtf.ireader.document(i);
            String id = hitDoc.get("ID");
            String text = hitDoc.get("TEXT");
            String aff = "";
            String status = "Oposition";
            for (String s : mtf.data.members.get(id).getAffiliations()) {
                aff += s + " ";
            }
            for (String coa : mtf.data.cabinet.coalitionPartiesID) {
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
}
