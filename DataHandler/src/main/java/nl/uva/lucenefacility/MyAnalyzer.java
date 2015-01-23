package nl.uva.lucenefacility;

import java.io.FileNotFoundException;
import java.io.Reader;
import java.util.ArrayList;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.AnalyzerWrapper;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterFilter;
import org.apache.lucene.analysis.nl.DutchAnalyzer;
import org.apache.lucene.analysis.nl.DutchStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.CharTokenizer;
import org.apache.lucene.util.Version;

/**
 *
 * @author Mostafa Dehghani
 */
public class MyAnalyzer {

    private String eol = System.getProperty("line.separator");

    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(MyAnalyzer.class.getName());
    private CharArraySet stopList = null;
    private Boolean steming;
    private Boolean stopwordRemooving;

    public MyAnalyzer(Boolean steming, ArrayList<String> stopCollection) { //In case of stopword removing
        stopList = new CharArraySet(Version.LUCENE_CURRENT, stopCollection, true);
        this.stopwordRemooving = true;
        this.steming = steming;
    }

    public MyAnalyzer(Boolean steming) { //In case of no stopword removing
        this.stopwordRemooving = false;
        this.steming = steming;
    }
        //////

    //
    public Analyzer ArbitraryCharacterAsDelimiterAnalyzer(final Character delimiter) {
        return new AnalyzerWrapper() {
            @Override
            protected Analyzer getWrappedAnalyzer(String string) {
                return new WhitespaceAnalyzer(Version.LUCENE_CURRENT);
            }

            protected Analyzer.TokenStreamComponents creatComponents(
                    String fieldName, final Reader reader) {
                Tokenizer tokenizer = new CharTokenizer(Version.LUCENE_CURRENT, reader) {

                    @Override
                    protected boolean isTokenChar(final int character) {
                        return delimiter != character;
                    }
                };
                TokenStream filter = new LowerCaseFilter(Version.LUCENE_CURRENT, tokenizer);
                return new TokenStreamComponents(tokenizer, filter);
            }

        };
    }

    /////////////
    public Analyzer MyNgramAnalyzer() {

        return new AnalyzerWrapper() {

            int wordDelimiterConfig = WordDelimiterFilter.GENERATE_WORD_PARTS;

            @Override
            protected Analyzer getWrappedAnalyzer(String string) {
                return new StandardAnalyzer(Version.LUCENE_CURRENT);
            }

            @Override
            protected Analyzer.TokenStreamComponents wrapComponents(
                    String fieldName, Analyzer.TokenStreamComponents tsc) {
                TokenStream tokenStream = new WordDelimiterFilter(Version.LUCENE_CURRENT, new StandardFilter(
                        Version.LUCENE_CURRENT, tsc.getTokenStream()), wordDelimiterConfig, null);

                tokenStream = new LowerCaseFilter(Version.LUCENE_CURRENT,
                        tokenStream);

                return new StandardAnalyzer.TokenStreamComponents(
                        tsc.getTokenizer(), tokenStream);
            }
        };
    }

    ///////////
    public Analyzer MyEnglishAnalizer() {
        if (steming && stopwordRemooving) {
            return new AnalyzerWrapper() {
                @Override
                protected Analyzer getWrappedAnalyzer(String string) {
                    return new StandardAnalyzer(Version.LUCENE_CURRENT);
                }

                @Override
                protected Analyzer.TokenStreamComponents wrapComponents(String fieldName, Analyzer.TokenStreamComponents tsc) {
                    TokenStream tokenStream = new StandardFilter(Version.LUCENE_CURRENT, tsc.getTokenStream());
                    tokenStream = new LowerCaseFilter(Version.LUCENE_CURRENT, tokenStream);
                    tokenStream = new PorterStemFilter(tokenStream);
                    tokenStream = new StopFilter(Version.LUCENE_CURRENT, tokenStream, stopList);
                    return new StandardAnalyzer.TokenStreamComponents(tsc.getTokenizer(), tokenStream);
                }
            };
        } else if (!steming && stopwordRemooving) {
            return new AnalyzerWrapper() {
                @Override
                protected Analyzer getWrappedAnalyzer(String string) {
                    return new StandardAnalyzer(Version.LUCENE_CURRENT);
                }

                @Override
                protected Analyzer.TokenStreamComponents wrapComponents(String fieldName, Analyzer.TokenStreamComponents tsc) {

                    TokenStream tokenStream = new StandardFilter(Version.LUCENE_CURRENT, tsc.getTokenStream());
                    tokenStream = new LowerCaseFilter(Version.LUCENE_CURRENT, tokenStream);
                    tokenStream = new StopFilter(Version.LUCENE_CURRENT, tokenStream, stopList);
                    return new StandardAnalyzer.TokenStreamComponents(tsc.getTokenizer(), tokenStream);
                }
            };
        } else if (steming && !stopwordRemooving) {
            return new AnalyzerWrapper() {
                @Override
                protected Analyzer getWrappedAnalyzer(String string) {
                    return new StandardAnalyzer(Version.LUCENE_CURRENT);
                }

                @Override
                protected Analyzer.TokenStreamComponents wrapComponents(String fieldName, Analyzer.TokenStreamComponents tsc) {

                    TokenStream tokenStream = new StandardFilter(Version.LUCENE_CURRENT, tsc.getTokenStream());
                    tokenStream = new LowerCaseFilter(Version.LUCENE_CURRENT, tokenStream);
                    tokenStream = new PorterStemFilter(tokenStream);
                    return new StandardAnalyzer.TokenStreamComponents(tsc.getTokenizer(), tokenStream);
                }
            };
        }
        return new StandardAnalyzer(Version.LUCENE_CURRENT);
    }

    public Analyzer MyDutchAnalizer() {
        if (steming && stopwordRemooving) {
            return new AnalyzerWrapper() {
                @Override
                protected Analyzer getWrappedAnalyzer(String string) {
                    return new DutchAnalyzer(Version.LUCENE_CURRENT);
                }

                @Override
                protected Analyzer.TokenStreamComponents wrapComponents(String fieldName, Analyzer.TokenStreamComponents tsc) {
                    TokenStream tokenStream = new StandardFilter(Version.LUCENE_CURRENT, tsc.getTokenStream());
                    tokenStream = new LowerCaseFilter(Version.LUCENE_CURRENT, tokenStream);
                    tokenStream = new DutchStemFilter(tokenStream);
                    tokenStream = new StopFilter(Version.LUCENE_CURRENT, tokenStream, stopList);
                    return new DutchAnalyzer.TokenStreamComponents(tsc.getTokenizer(), tokenStream);
                }
            };
        } else if (!steming && stopwordRemooving) {
            return new AnalyzerWrapper() {
                @Override
                protected Analyzer getWrappedAnalyzer(String string) {
                    return new DutchAnalyzer(Version.LUCENE_CURRENT);
                }

                @Override
                protected Analyzer.TokenStreamComponents wrapComponents(String fieldName, Analyzer.TokenStreamComponents tsc) {

                    TokenStream tokenStream = new StandardFilter(Version.LUCENE_CURRENT, tsc.getTokenStream());
                    tokenStream = new LowerCaseFilter(Version.LUCENE_CURRENT, tokenStream);
                    tokenStream = new StopFilter(Version.LUCENE_CURRENT, tokenStream, stopList);
                    return new DutchAnalyzer.TokenStreamComponents(tsc.getTokenizer(), tokenStream);
                }
            };
        } else if (steming && !stopwordRemooving) {
            return new AnalyzerWrapper() {
                @Override
                protected Analyzer getWrappedAnalyzer(String string) {
                    return new DutchAnalyzer(Version.LUCENE_CURRENT);
                }

                @Override
                protected Analyzer.TokenStreamComponents wrapComponents(String fieldName, Analyzer.TokenStreamComponents tsc) {

                    TokenStream tokenStream = new StandardFilter(Version.LUCENE_CURRENT, tsc.getTokenStream());
                    tokenStream = new LowerCaseFilter(Version.LUCENE_CURRENT, tokenStream);
                    tokenStream = new DutchStemFilter(tokenStream);
                    return new DutchAnalyzer.TokenStreamComponents(tsc.getTokenizer(), tokenStream);
                }
            };
        }
        return new DutchAnalyzer(Version.LUCENE_CURRENT);
    }

    public Analyzer getAnalyzer(String Language) throws FileNotFoundException {
        Analyzer analyzer = new SimpleAnalyzer(Version.LUCENE_CURRENT);
        if (Language.equalsIgnoreCase("EN")) {
            analyzer = MyEnglishAnalizer();
        } else if (Language.equalsIgnoreCase("NL")) {
            analyzer = MyDutchAnalizer();
        }
        return analyzer;

    }

}
