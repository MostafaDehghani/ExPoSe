/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.expose.classification;

import weka.core.*;
import weka.core.converters.*;
import weka.filters.*;
import weka.filters.unsupervised.attribute.*;
import weka.core.converters.ArffLoader.ArffReader;

import java.io.*;
import java.util.Random;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.meta.FilteredClassifier;

/**
 *
 * @author Mostafa Dehghani
 */
public class WekaClassification {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(WekaClassification.class.getName());
    private Instances dataRaw;
    private Instances dataFiltered;
    private FilteredClassifier classifier;
    /*
     * convert the directory into a dataset
     */

    private void loadToArff(String dataDir, Instances dRaw) throws IOException {
        TextDirectoryLoader loader = new TextDirectoryLoader();
        loader.setDirectory(new File(dataDir));
        dRaw = loader.getDataSet();
        System.out.println("\n\nImported data:\n\n" + dRaw);
    }

    /*
     * apply the StringToWordVector
     */
    public void loadDataset(String fileName, Instances dRaw) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            ArffReader arff = new ArffReader(reader);
            dRaw = arff.getData();
            System.out.println("===== Loaded dataset: " + fileName + " =====");
            reader.close();
        } catch (IOException e) {
            System.out.println("Problem found when reading: " + fileName);
        }
    }

    private void getWordVector(Instances dRaw, Instances dFiltered) throws Exception {
        StringToWordVector filter = new StringToWordVector();
        filter.setAttributeIndices("first-last");
        filter.setIDFTransform(true);
        filter.setLowerCaseTokens(true);
        filter.setMinTermFreq(2);
        filter.setLowerCaseTokens(true);
        filter.setNormalizeDocLength(new SelectedTag(StringToWordVector.FILTER_NORMALIZE_ALL, StringToWordVector.TAGS_FILTER));
        filter.setOutputWordCounts(true);
//        filter.setTokenizer();
//        filter.setWordsToKeep();
        filter.setInputFormat(dRaw);
        dFiltered = Filter.useFilter(dRaw, filter);
    }

    private void classifierTrainer(Instances trainData) throws Exception {
        trainData.setClassIndex(0);
//        classifier.setFilter(filter);
        classifier.setClassifier(new NaiveBayes());
        classifier.buildClassifier(trainData);
        Evaluation eval = new Evaluation(trainData);
        eval.crossValidateModel(classifier, trainData, 5, new Random(1));
        System.out.println(eval.toSummaryString());
        System.out.println(eval.toClassDetailsString());
        System.out.println("===== Evaluating on filtered (training) dataset done =====");
        System.out.println("\n\nClassifier model:\n\n" + classifier);
    }

    public void saveModel(String fileName) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName));
            out.writeObject(classifier);
            out.close();
            System.out.println("===== Saved model: " + fileName + " =====");
        } catch (IOException e) {
            System.out.println("Problem found when writing: " + fileName);
        }
    }

    public void main(String[] args) throws Exception {

    }
}
