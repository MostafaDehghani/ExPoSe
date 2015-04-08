/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.expose.Indexer;

import java.io.IOException;
import java.util.Map;
import nl.uva.expose.entities.member.Member;
import nl.uva.expose.entities.speech.Speech;
import nl.uva.lucenefacility.MyAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.util.Version;

/**
 *
 * @author Mostafa Dehghani
 */
public class MemberIndexer extends Indexer {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(MemberIndexer.class.getName());

    public MemberIndexer(String period) throws Exception {
        super(period, "m");
    }

    @Override
    protected void docIndexer() throws Exception {
        try {
//            for (Map.Entry<String, Speech> e : data.speeches.entrySet()) {
//                Speech s = e.getValue();
//                try {
//                    Member m = data.members.get(s.getSpeakerId());
//                    StringBuilder sb = m.getSpeeches();
//                    m.setSpeeches(sb.append(s.getSpeechText()).append("\n"));
//                    data.members.put(m.getmId(), m);
//                } catch (NullPointerException ex) {
////                    log.error(ex);
//                    log.error("No information for seakerID: \"" + s.getSpeakerId() + "\" in speach:" + s.getSpeechId());
//                }
//            }
            for (Map.Entry<String, Member> e : data.members.entrySet()) {
//                String fileName = "memSpeeches/"+e.getKey()+".txt";
//                BufferedWriter bw = new BufferedWriter(new FileWriter(new File(fileName)));
//                bw.write(e.getValue().getSpeeches().toString());
//                bw.close();
                this.IndexDoc(e.getValue());
            }
        } catch (Exception ex) {
            log.error(ex);
            throw ex;
        }
    }

    @Override
    protected void analyzerMapInitializer(Map<String, Analyzer> analyzerMap) {
        analyzerMap.put("ID", new KeywordAnalyzer());//StandardAnalyzer(Version.LUCENE_CURRENT));
        analyzerMap.put("BDATE", new StandardAnalyzer(Version.LUCENE_CURRENT));
        analyzerMap.put("GENDER", new KeywordAnalyzer());//Version.LUCENE_CURRENT));
        analyzerMap.put("FULLNAME", new KeywordAnalyzer());// new StandardAnalyzer(Version.LUCENE_CURRENT));
        analyzerMap.put("ROLES", new MyAnalyzer(false).ArbitraryCharacterAsDelimiterAnalyzer(','));
        analyzerMap.put("AFF", new WhitespaceAnalyzer());
        analyzerMap.put("FUNC", new MyAnalyzer(false).ArbitraryCharacterAsDelimiterAnalyzer(','));
    }

    @Override
    protected void IndexDoc(Object obj) throws Exception {
        Member m = (Member) obj;
        Document doc = new Document();
        if (m.getSpeeches().toString().split("\\s+").length <= minDocLength) //Filtering small documents
        {
            return;
        }
        doc.add(new Field("ID", m.getmId(), Field.Store.YES, Field.Index.ANALYZED_NO_NORMS, Field.TermVector.YES));
        doc.add(new Field("BIO", m.getBioText(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS));
        doc.add(new Field("TEXT", m.getSpeeches().toString(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS));
        doc.add(new Field("GENDER", m.getGender(), Field.Store.YES, Field.Index.ANALYZED_NO_NORMS, Field.TermVector.YES));
        doc.add(new Field("BDATE", m.getbDate().toString(), Field.Store.YES, Field.Index.ANALYZED_NO_NORMS, Field.TermVector.YES));
        doc.add(new Field("FULLNAME", m.getFullName(), Field.Store.YES, Field.Index.ANALYZED_NO_NORMS, Field.TermVector.YES));

        String roles = "";
        for (String s : m.getRoles()) {
            roles += s.trim() + " ";
        }
        doc.add(new Field("ROLES", roles.trim(), Field.Store.YES, Field.Index.ANALYZED_NO_NORMS, Field.TermVector.YES));

        String affs = "";
        for (String s : m.getAffiliations()) {
            affs += s.trim() + " ";
        }
        doc.add(new Field("AFF", affs.trim(), Field.Store.YES, Field.Index.ANALYZED_NO_NORMS, Field.TermVector.YES));

        String funcs = "";
        for (String s : m.getSpeechTimeFunctions()) {
            funcs += s.trim() + " ";
        }
        doc.add(new Field("FUNC", funcs.trim(), Field.Store.YES, Field.Index.ANALYZED_NO_NORMS, Field.TermVector.YES));

        try {
            writer.addDocument(doc);
        } catch (IOException ex) {
            log.error(ex);
        }
        log.info("Document " + m.getmId() + " has been indexed successfully...");
    }

    public static void main(String[] args) throws Exception {
        MemberIndexer mi = new MemberIndexer("20122014");
    }
}
