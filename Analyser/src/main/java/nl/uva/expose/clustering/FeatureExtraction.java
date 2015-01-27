/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.expose.clustering;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Scanner;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import nl.uva.expose.entities.government.Cabinet;
import static nl.uva.expose.settings.Config.configFile;
import nl.uva.lucenefacility.IndexInfo;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.BytesRef;
import org.xml.sax.SAXException;

/**
 *
 * @author mosi
 */
public class FeatureExtraction {

    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(FeatureExtraction.class.getName());
    private IndexReader mireader;
    private IndexInfo miInfo;
    private IndexReader sireader;
    private IndexInfo sInfo;
    private String period;
    private Integer allDebNum;
    private HashSet<String> allNodes = new HashSet<>();
    private Cabinet cabinet;

    public FeatureExtraction(String period) throws Exception {
        try {
            this.period = period;
            mireader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("INDEXES_PATH") + period + "/m")));
            miInfo = new IndexInfo(mireader);
            sireader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("INDEXES_PATH") + period + "/s")));
            sInfo = new IndexInfo(sireader);
            allDebNum = calcDebNum();
            cabinet = new Cabinet(period);
            Scanner sc = new Scanner(new FileInputStream(new File("/Users/Mosi/Desktop/SIGIR_SHORT/debGraph" + this.period + ".csv")));
            sc.nextLine();
            while (sc.hasNext()) {
                String[] parts = sc.nextLine().split(",");
                this.allNodes.add(parts[0]);
                this.allNodes.add(parts[1]);
            }
            sc = new Scanner(new FileInputStream(new File("/Users/Mosi/Desktop/SIGIR_SHORT/simGraph" + this.period + ".csv")));
            sc.nextLine();
            while (sc.hasNext()) {
                String[] parts = sc.nextLine().split(",");
                this.allNodes.add(parts[0]);
                this.allNodes.add(parts[1]);
            }
        } catch (Exception ex) {
            log.error(ex);
            throw ex;
        }
    }

    private Integer calcDebNum() throws IOException {
        HashSet<String> debIds = new HashSet<>();
        for (int i = 0; i < this.sireader.numDocs(); i++) {
            debIds.add(this.sireader.document(i).get("DEBATEID"));
        }
        return debIds.size();
    }

    public void dataEnreachment() throws IOException, ParserConfigurationException, SAXException, java.text.ParseException, XPathExpressionException {
        FileWriter fileWritter = new FileWriter("/Users/Mosi/Desktop/SIGIR_SHORT/MembersInfo" + this.period + ".csv");
        BufferedWriter bw = new BufferedWriter(fileWritter);
        bw.write("Id,Gender,Age,Status,Affiliation,SpeechNum,DebateNum,ActivitiRatio,AllSpeechLength,VocabSize,Novelty,SpeechAvgLength\n");
        for (String line : this.allNodes) {
            System.out.println("Member id: " + line);

            Integer memIndexId = this.miInfo.getIndexId(line);

            String gender = this.getGender(memIndexId); //.equals("male")?"-1":"1";
            line += "," + gender;
            Integer age = this.getAgeInYear(memIndexId);
            line += "," + age;
            String status = this.getStatus(memIndexId);
            line += "," + status;
            String affiliation = this.getAffiliation(memIndexId);
            line += "," + affiliation;
            Double nSpeechNum = this.getSpeechNum(memIndexId);
            line += "," + nSpeechNum;
            Double nDebateNum = this.getDebateNum(memIndexId);
            line += "," + nDebateNum;
            Double activityRation = this.getActivityRatio(memIndexId);
            line += "," + activityRation;
            Double nAllSpeechlength = this.getAllSpeechlength(memIndexId);
            line += "," + nAllSpeechlength;
            Double nVocabSize = this.getVocabSize(memIndexId);
            line += "," + nVocabSize;
            Double Novelty = this.getNovelty(memIndexId);
            line += "," + Novelty;
            Double SpeechAvgLength = this.getSpeechAvgLength(memIndexId);
            line += "," + SpeechAvgLength;
            bw.write(line + "\n");
        }
        bw.close();
    }

    private Integer getAgeInYear(Integer memIndexId) throws java.text.ParseException, IOException {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date first = formatter.parse(this.mireader.document(memIndexId).get("BDATE"));
        Date last = this.cabinet.startDate;
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        Integer diff = b.get(YEAR) - a.get(YEAR);
        if (a.get(MONTH) > b.get(MONTH)
                || (a.get(MONTH) == b.get(MONTH) && a.get(DATE) > b.get(DATE))) {
            diff--;
        }
        return diff;
    }

    private String getGender(Integer memIndexId) throws java.text.ParseException, IOException {
        try {
            return this.mireader.document(memIndexId).get("GENDER");
        } catch (IOException ex) {
            log.error(ex);
            throw ex;
        }
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }

    private String getStatus(Integer memIndexId) throws IOException {
        String aff = "";
        HashSet<String> affiliations = this.miInfo.getDocAllTerm(memIndexId, "AFF");
        for (String s : affiliations) {
            aff += s + " ";
        }
        String status = this.cabinet.getStatus(aff);
        Integer affNum = affiliations.size();
        if (affNum != 1) {
            System.err.println(affNum + " --- " + memIndexId + ": " + status);
        }
        return status;
    }

    private String getAffiliation(Integer memIndexId) throws IOException {
        String aff = "";
        HashSet<String> affiliations = this.miInfo.getDocAllTerm(memIndexId, "AFF");
        for (String s : affiliations) {
            aff += s + " ";
        }
        Integer affNum = affiliations.size();
        if (affNum != 1) {
            System.err.println(affNum + " --- " + memIndexId + ": " + aff);
        }
//        aff = aff.replaceAll("gov", "");
        return aff.trim();
    }

    public Double getAllSpeechlength(Integer memIndexId) throws IOException {
        Long l = this.miInfo.getDocumentLength(memIndexId, "TEXT");
        Long L = this.sInfo.getNumOfAllTerms("TEXT");
        Double normL = l.doubleValue() / L.doubleValue();
        return normL;
    }

    public Double getVocabSize(Integer memIndexId) throws IOException {
        Long v = this.miInfo.getNumberofUniqTermsInDocument(memIndexId, "TEXT");
        Long V = this.sInfo.getNumOfAllUniqueTerms_PerField("TEXT");
        Double normV = v.doubleValue() / V.doubleValue();
        return normV;
    }

    public Double getNovelty(Integer memIndexId) throws IOException {
        Double n;
        Long v = this.miInfo.getNumberofUniqTermsInDocument(memIndexId, "TEXT");
        Long l = this.miInfo.getDocumentLength(memIndexId, "TEXT");
        n = v.doubleValue() / l.doubleValue();
        return n;
    }

    public Double getSpeechAvgLength(Integer memIndexId) throws IOException {
        Double lengthAvg = 0D;
        int count = 0;
        TermsEnum te = MultiFields.getTerms(this.sireader, "SPEAKERID").iterator(null);
        BytesRef id = new BytesRef(this.mireader.document(memIndexId).get("ID"));
        te.seekExact(id);
        DocsEnum docsEnum = te.docs(null, null);
        int docIdEnum;
        while ((docIdEnum = docsEnum.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {
            count++;
            lengthAvg += sInfo.getDocumentLength(docIdEnum, "TEXT");
            //Document doc = sireader.document(docIdEnum);
            //docs.add(doc.get("ID"));
        }
        return lengthAvg / count;
    }

    public Double getSpeechNum(Integer memIndexId) throws IOException {
        Long speechNum = 0L;
        TermsEnum te = MultiFields.getTerms(this.sireader, "SPEAKERID").iterator(null);
        BytesRef id = new BytesRef(this.mireader.document(memIndexId).get("ID"));
        te.seekExact(id);
        DocsEnum docsEnum = te.docs(null, null);
        int docIdEnum;
        while ((docIdEnum = docsEnum.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {
            speechNum++;
        }
        int allSpeechNum = this.sireader.numDocs();
        Double normalizedSN = speechNum.doubleValue() / allSpeechNum;
        return normalizedSN;
    }

    public Double getDebateNum(Integer memIndexId) throws IOException {

        HashSet<String> debIds = new HashSet<>();
        TermsEnum te = MultiFields.getTerms(this.sireader, "SPEAKERID").iterator(null);
        BytesRef id = new BytesRef(this.mireader.document(memIndexId).get("ID"));
        te.seekExact(id);
        DocsEnum docsEnum = te.docs(null, null);
        int docIdEnum;
        while ((docIdEnum = docsEnum.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {
            debIds.add(this.sireader.document(docIdEnum).get("DEBATEID"));
        }
        Double normalizedDN = debIds.size() / allDebNum.doubleValue();
        return normalizedDN;
    }

    public Double getActivityRatio(Integer memIndexId) throws IOException {
        Long speechNum = 0L;
        HashSet<String> debIds = new HashSet<>();
        TermsEnum te = MultiFields.getTerms(this.sireader, "SPEAKERID").iterator(null);
        BytesRef id = new BytesRef(this.mireader.document(memIndexId).get("ID"));
        te.seekExact(id);
        DocsEnum docsEnum = te.docs(null, null);
        int docIdEnum;
        while ((docIdEnum = docsEnum.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {
            speechNum++;
            debIds.add(this.sireader.document(docIdEnum).get("DEBATEID"));
        }
        Double ar = debIds.size() / speechNum.doubleValue();
        return ar;
    }

    public static void main(String[] args) throws IOException, ParseException, ParserConfigurationException, SAXException, java.text.ParseException, XPathExpressionException, Exception {
        FeatureExtraction anal = new FeatureExtraction("20102012");
        anal.dataEnreachment();
    }
}
