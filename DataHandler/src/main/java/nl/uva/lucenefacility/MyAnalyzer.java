package nl.uva.lucenefacility;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.AnalyzerWrapper;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.StemmerOverrideFilter;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterFilter;
import org.apache.lucene.analysis.nl.DutchAnalyzer;
import org.apache.lucene.analysis.nl.DutchStemFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.CharTokenizer;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;
import org.apache.lucene.util.fst.FST;
import org.tartarus.snowball.SnowballProgram;
import org.tartarus.snowball.ext.DutchStemmer;

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
        stopList = new CharArraySet(stopCollection, true);
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
                return new WhitespaceAnalyzer();
            }

            protected Analyzer.TokenStreamComponents creatComponents(
                    String fieldName, final Reader reader) {
                Tokenizer tokenizer = new CharTokenizer(reader) {

                    @Override
                    protected boolean isTokenChar(final int character) {
                        return delimiter != character;
                    }
                };
                TokenStream filter = new LowerCaseFilter(tokenizer);
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
                return new StandardAnalyzer();
            }

            @Override
            protected Analyzer.TokenStreamComponents wrapComponents(
                    String fieldName, Analyzer.TokenStreamComponents tsc) {
                TokenStream tokenStream = new WordDelimiterFilter(new StandardFilter(
                        tsc.getTokenStream()), wordDelimiterConfig, null);

                tokenStream = new LowerCaseFilter(tokenStream);

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
                    return new StandardAnalyzer();
                }

                @Override
                protected Analyzer.TokenStreamComponents wrapComponents(String fieldName, Analyzer.TokenStreamComponents tsc) {
                    TokenStream tokenStream = new StandardFilter(tsc.getTokenStream());
                    tokenStream = new LowerCaseFilter(tokenStream);
                    tokenStream = new PorterStemFilter(tokenStream);
                    tokenStream = new StopFilter(tokenStream, stopList);
                    return new StandardAnalyzer.TokenStreamComponents(tsc.getTokenizer(), tokenStream);
                }
            };
        } else if (!steming && stopwordRemooving) {
            return new AnalyzerWrapper() {
                @Override
                protected Analyzer getWrappedAnalyzer(String string) {
                    return new StandardAnalyzer();
                }

                @Override
                protected Analyzer.TokenStreamComponents wrapComponents(String fieldName, Analyzer.TokenStreamComponents tsc) {

                    TokenStream tokenStream = new StandardFilter(tsc.getTokenStream());
                    tokenStream = new LowerCaseFilter(tokenStream);
                    tokenStream = new StopFilter(tokenStream, stopList);

                     //Empty dictionary for stemming:
                    StemmerOverrideFilter.StemmerOverrideMap map = null;
                    StemmerOverrideFilter.Builder sofb = new StemmerOverrideFilter.Builder();
                    try {
                         map = sofb.build();
                    } catch (IOException ex) {
                        log.error(ex);
                    }
                    tokenStream = new StemmerOverrideFilter(tokenStream,map); 
   
                    return new StandardAnalyzer.TokenStreamComponents(tsc.getTokenizer(), tokenStream);
                }
            };
        } else if (steming && !stopwordRemooving) {
            return new AnalyzerWrapper() {
                @Override
                protected Analyzer getWrappedAnalyzer(String string) {
                    return new StandardAnalyzer();
                }

                @Override
                protected Analyzer.TokenStreamComponents wrapComponents(String fieldName, Analyzer.TokenStreamComponents tsc) {

                    TokenStream tokenStream = new StandardFilter(tsc.getTokenStream());
                    tokenStream = new LowerCaseFilter(tokenStream);
                    tokenStream = new PorterStemFilter(tokenStream);
                    //Empty list for stopwords:
                    tokenStream = new StopFilter(tokenStream, new CharArraySet(new ArrayList<String>(), true));
                    return new StandardAnalyzer.TokenStreamComponents(tsc.getTokenizer(), tokenStream);
                }
            };
        }
        //return new StandardAnalyzer();
         return new AnalyzerWrapper() {
                @Override
                protected Analyzer getWrappedAnalyzer(String string) {
                    return new StandardAnalyzer();
                }

                @Override
                protected Analyzer.TokenStreamComponents wrapComponents(String fieldName, Analyzer.TokenStreamComponents tsc) {

                    TokenStream tokenStream = new StandardFilter(tsc.getTokenStream());
                    tokenStream = new LowerCaseFilter(tokenStream);
                    //Empty list for stopwords:
                    tokenStream = new StopFilter(tokenStream, new CharArraySet(new ArrayList<String>(), true));
                    
                     //Empty dictionary for stemming:
                    StemmerOverrideFilter.StemmerOverrideMap map = null;
                    StemmerOverrideFilter.Builder sofb = new StemmerOverrideFilter.Builder();
                    try {
                         map = sofb.build();
                    } catch (IOException ex) {
                        log.error(ex);
                    }
                    tokenStream = new StemmerOverrideFilter(tokenStream,map); 
   
                    return new StandardAnalyzer.TokenStreamComponents(tsc.getTokenizer(), tokenStream);
                }
            };
    }

    public Analyzer MyDutchAnalizer() {
        if (steming && stopwordRemooving) {
            return new AnalyzerWrapper() {
                @Override
                protected Analyzer getWrappedAnalyzer(String string) {
                    return new DutchAnalyzer();
                }

                @Override
                protected Analyzer.TokenStreamComponents wrapComponents(String fieldName, Analyzer.TokenStreamComponents tsc) {
                    TokenStream tokenStream = new StandardFilter(tsc.getTokenStream());
                    tokenStream = new LowerCaseFilter(tokenStream);
//                    tokenStream = new DutchStemFilter(tokenStream);
                    tokenStream = new SnowballFilter(tokenStream, new DutchStemmer());
                    tokenStream = new StopFilter(tokenStream, stopList);
                    return new DutchAnalyzer.TokenStreamComponents(tsc.getTokenizer(), tokenStream);
                }
            };
        } else if (!steming && stopwordRemooving) {
            return new AnalyzerWrapper() {
                @Override
                protected Analyzer getWrappedAnalyzer(String string) {
                    return new DutchAnalyzer();
                }

                @Override
                protected Analyzer.TokenStreamComponents wrapComponents(String fieldName, Analyzer.TokenStreamComponents tsc) {

                    TokenStream tokenStream = new StandardFilter(tsc.getTokenStream());
                    tokenStream = new LowerCaseFilter(tokenStream);
                    tokenStream = new StopFilter(tokenStream, stopList);
                    
                     //Empty dictionary for stemming:
                    StemmerOverrideFilter.StemmerOverrideMap map = null;
                    StemmerOverrideFilter.Builder sofb = new StemmerOverrideFilter.Builder();
                    try {
                         map = sofb.build();
                    } catch (IOException ex) {
                        log.error(ex);
                    }
                    tokenStream = new StemmerOverrideFilter(tokenStream,map); 
   
                    return new DutchAnalyzer.TokenStreamComponents(tsc.getTokenizer(), tokenStream);
                }
            };
        } else if (steming && !stopwordRemooving) {
            return new AnalyzerWrapper() {
                @Override
                protected Analyzer getWrappedAnalyzer(String string) {
                    return new DutchAnalyzer();
                }

                @Override
                protected Analyzer.TokenStreamComponents wrapComponents(String fieldName, Analyzer.TokenStreamComponents tsc) {

                    TokenStream tokenStream = new StandardFilter(tsc.getTokenStream());
                    tokenStream = new LowerCaseFilter(tokenStream);
                    tokenStream = new SnowballFilter(tokenStream, new DutchStemmer());
                    //Empty list for stopwords:
                    tokenStream = new StopFilter(tokenStream, new CharArraySet(new ArrayList<String>(), true));
   
                    return new DutchAnalyzer.TokenStreamComponents(tsc.getTokenizer(), tokenStream);
                }
            };
        }
        //return new DutchAnalyzer();
        return new AnalyzerWrapper() {
                @Override
                protected Analyzer getWrappedAnalyzer(String string) {
                    return new DutchAnalyzer();
                }

                @Override
                protected Analyzer.TokenStreamComponents wrapComponents(String fieldName, Analyzer.TokenStreamComponents tsc) {
                    TokenStream tokenStream = new StandardFilter(tsc.getTokenStream());
                    tokenStream = new LowerCaseFilter(tokenStream);
                    //Empty list for stopwords:
                    tokenStream = new StopFilter(tokenStream, new CharArraySet(new ArrayList<String>(), true));
                    
                     //Empty dictionary for stemming:
                    StemmerOverrideFilter.StemmerOverrideMap map = null;
                    StemmerOverrideFilter.Builder sofb = new StemmerOverrideFilter.Builder();
                    try {
                         map = sofb.build();
                    } catch (IOException ex) {
                        log.error(ex);
                    }
                    tokenStream = new StemmerOverrideFilter(tokenStream,map); 
                    
                    return new DutchAnalyzer.TokenStreamComponents(tsc.getTokenizer(), tokenStream);
                }
            };
    }

    public Analyzer getAnalyzer(String Language) throws FileNotFoundException {
        Analyzer analyzer = new SimpleAnalyzer();
        if (Language.equalsIgnoreCase("EN")) {
            analyzer = MyEnglishAnalizer();
        } else if (Language.equalsIgnoreCase("NL")) {
            analyzer = MyDutchAnalizer();
        }
        return analyzer;

    }

}
