package nl.uva.expose.lm;

///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package nl.uva.expose.LM;
//
///**
// *
// * @author Mostafa Dehghani
// */
//
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
// 
//import org.apache.commons.csv.CSVFormat;
//import org.apache.commons.csv.CSVParser;
//import org.apache.commons.csv.CSVPrinter;
//import org.apache.commons.csv.CSVRecord;
// 
//public class csv {
//        private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(csv.class.getName());
//
//    public static void main(HashMap<String,LanguageModel> LMs, String fileName) throws FileNotFoundException, IOException {
//      //Delimiter used in CSV file
//    String NEW_LINE_SEPARATOR = "\n";
//    //CSV file header
//    Object [] FILE_HEADER = new Object[LMs.size()];
//    int i = 0;
//    for(String s:LMs.keySet()){
//        FILE_HEADER[i++]= s;
//    }
//     FileWriter fileWriter = null;
//        CSVPrinter csvFilePrinter = null;
//        //Create the CSVFormat object with "\n" as a record delimiter
//        CSVFormat csvFileFormat = CSVFormat.DEFAULT.withRecordSeparator(NEW_LINE_SEPARATOR);
//        try {
//            //initialize FileWriter object
//            fileWriter = new FileWriter(fileName);
//            //initialize CSVPrinter object
//            csvFilePrinter = new CSVPrinter(fileWriter, csvFileFormat);
//            //Create CSV file header
//            csvFilePrinter.printRecord(FILE_HEADER);
//            //Write a new student object list to the CSV file
//            for (Student student : students) {
//62
//                List studentDataRecord = new ArrayList();
//63
//                studentDataRecord.add(String.valueOf(student.getId()));
//64
//                studentDataRecord.add(student.getFirstName());
//65
//                studentDataRecord.add(student.getLastName());
//66
//                studentDataRecord.add(student.getGender());
//67
//                studentDataRecord.add(String.valueOf(student.getAge()));
//68
//                csvFilePrinter..printRecord(studentDataRecord);
//            }
//            System.out.println("CSV file was created successfully !!!");
//        } catch (Exception e) {
//            System.out.println("Error in CsvFileWriter !!!");
//            e.printStackTrace();
//        } finally {
//            try {
//                fileWriter.flush();
//                fileWriter.close();
//                csvFilePrinter.close();
//            } catch (IOException e) {
//                System.out.println("Error while flushing/closing fileWriter/csvPrinter !!!");
//                e.printStackTrace();
//            }
//        }
//
//    }
//    
// 
//}