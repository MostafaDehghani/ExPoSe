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
import nl.uva.expose.data.Data;
import nl.uva.expose.entities.member.Member;
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
public class Analysis {

    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Analysis.class.getName());
    private IndexReader mireader;
    private IndexInfo miInfo;
    private IndexReader sireader;
    private IndexInfo sInfo;
    private String period;
    private Data data;
    private Integer allDebNum;
    private HashSet<String> allNodes = new HashSet<>();

    public Analysis(String period) throws Exception {
        try {
            this.period = period;
            mireader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("INDEXES_PATH") + period + "/m")));
            miInfo = new IndexInfo(mireader);
            sireader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("INDEXES_PATH") + period + "/s")));
            sInfo = new IndexInfo(sireader);
            data = new Data(this.period);
            allDebNum = calcDebNum();
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
            Member m = data.members.get(line);

            String gender = m.getGender(); //.equals("male")?"-1":"1";
            line += "," + gender;
            Integer age = this.getAgeInYear(m);
            line += "," + age;
            String status = this.getStatus(m);
            line += "," + status;
            String affiliation = this.getAffiliation(m);
            line += "," + affiliation;
            Double nSpeechNum = this.getSpeechNum(m);
            line += "," + nSpeechNum;
            Double nDebateNum = this.getDebateNum(m);
            line += "," + nDebateNum;
            Double activityRation = this.getActivityRatio(m);
            line += "," + activityRation;
            Double nAllSpeechlength = this.getAllSpeechlength(m);
            line += "," + nAllSpeechlength;
            Double nVocabSize = this.getVocabSize(m);
            line += "," + nVocabSize;
            Double Novelty = this.getNovelty(m);
            line += "," + Novelty;
            Double SpeechAvgLength = this.getSpeechAvgLength(m);
            line += "," + SpeechAvgLength;
            bw.write(line + "\n");
        }
        bw.close();
    }

    private Integer getAgeInYear(Member m) throws java.text.ParseException {
        if (m.getbDate().date == null) {
            return 0;
        }
        Date last = this.data.cabinets.startDate;
        Date first = m.getbDate().date;
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        Integer diff = b.get(YEAR) - a.get(YEAR);
        if (a.get(MONTH) > b.get(MONTH)
                || (a.get(MONTH) == b.get(MONTH) && a.get(DATE) > b.get(DATE))) {
            diff--;
        }
        return diff;
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }

    private String getStatus(Member m) {
        String aff = "";
        String status = "Oposition";
        for (String s : m.getAffiliations()) {
            aff += s + " ";
        }
        for (String coa : this.data.cabinets.coalitionPartiesID) {
            if (aff.contains(coa)) {
                status = "Coalition";
                break;
            }
        }
        if (aff.contains("gov")) {
            status = "1";
        }
        if (aff.equals("parl")) {
            status = "0";
        }
        Integer affNum = m.getAffiliations().size();
        if (affNum != 1) {
            System.err.println(affNum + " --- " + m.getmId() + ": " + status);
        }
        return status;
    }

    private String getAffiliation(Member m) {
        String aff = "";
        for (String s : m.getAffiliations()) {
            aff += s + " ";
        }
        Integer affNum = m.getAffiliations().size();
        if (affNum != 1) {
            System.err.println(affNum + " --- " + m.getmId() + ": " + aff);
        }
//        aff = aff.replaceAll("gov", "");
        return aff.trim();
    }

    public Double getAllSpeechlength(Member m) throws IOException {
        int indexDocId = this.miInfo.getIndexId(m.getmId());
        Long l = this.miInfo.getDocumentLength(indexDocId, "TEXT");
        Long L = this.sInfo.getNumOfAllTerms("TEXT");
        Double normL = l.doubleValue() / L.doubleValue();
        return normL;
    }

    public Double getVocabSize(Member m) throws IOException {
        int indexDocId = this.miInfo.getIndexId(m.getmId());
        Long v = this.miInfo.getNumberofUniqTermsInDocument(indexDocId, "TEXT");
        Long V = this.sInfo.getNumOfAllUniqueTerms_PerField("TEXT");
        Double normV = v.doubleValue() / V.doubleValue();
        return normV;
    }

    public Double getNovelty(Member m) throws IOException {
        Double n;
        int indexDocId = this.miInfo.getIndexId(m.getmId());
        Long v = this.miInfo.getNumberofUniqTermsInDocument(indexDocId, "TEXT");
        Long l = this.miInfo.getDocumentLength(indexDocId, "TEXT");
        n = v.doubleValue() / l.doubleValue();
        return n;
    }

    public Double getSpeechAvgLength(Member m) throws IOException {
        Double lengthAvg = 0D;
        int count = 0;
        TermsEnum te = MultiFields.getTerms(this.sireader, "SPEAKERID").iterator(null);
        BytesRef id = new BytesRef(m.getmId());
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

    public Double getSpeechNum(Member m) throws IOException {
        Long speechNum = 0L;
        TermsEnum te = MultiFields.getTerms(this.sireader, "SPEAKERID").iterator(null);
        BytesRef id = new BytesRef(m.getmId());
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

    public Double getDebateNum(Member m) throws IOException {

        HashSet<String> debIds = new HashSet<>();
        TermsEnum te = MultiFields.getTerms(this.sireader, "SPEAKERID").iterator(null);
        BytesRef id = new BytesRef(m.getmId());
        te.seekExact(id);
        DocsEnum docsEnum = te.docs(null, null);
        int docIdEnum;
        while ((docIdEnum = docsEnum.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {
            debIds.add(this.sireader.document(docIdEnum).get("DEBATEID"));
        }
        Double normalizedDN = debIds.size() / allDebNum.doubleValue();
        return normalizedDN;
    }

    public Double getActivityRatio(Member m) throws IOException {

        HashSet<String> debIds = new HashSet<>();
        TermsEnum te = MultiFields.getTerms(this.sireader, "SPEAKERID").iterator(null);
        BytesRef id = new BytesRef(m.getmId());
        te.seekExact(id);
        DocsEnum docsEnum = te.docs(null, null);
        int docIdEnum;
        while ((docIdEnum = docsEnum.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {
            debIds.add(this.sireader.document(docIdEnum).get("DEBATEID"));
        }
        Long speechNum = 0L;
        TermsEnum te2 = MultiFields.getTerms(this.sireader, "SPEAKERID").iterator(null);
        BytesRef id2 = new BytesRef(m.getmId());
        te2.seekExact(id2);
        DocsEnum docsEnum2 = te2.docs(null, null);
        int docIdEnum2;
        while ((docIdEnum2 = docsEnum2.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {
            speechNum++;
        }

        Double ar = debIds.size() / speechNum.doubleValue();
        return ar;
    }

    public static void main(String[] args) throws IOException, ParseException, ParserConfigurationException, SAXException, java.text.ParseException, XPathExpressionException, Exception {
        Analysis anal = new Analysis("20062010");
        anal.dataEnreachment();
    }
}
