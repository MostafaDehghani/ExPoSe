/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.expose.Indexer;

import java.io.File;
import java.io.IOException;
import static nl.uva.expose.settings.Config.configFile;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Mostafa Dehghani
 */
public class IndexesCleaner {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(IndexesCleaner.class.getName());

    public IndexesCleaner(String period) {
        try {
            String path = configFile.getProperty("INDEXES_PATH") + period;
            File Index = new File(path);
            if (Index.exists()) {
                FileUtils.deleteDirectory(Index);
                log.info("Deletting the existing index directories on: " + path);
            }
            FileUtils.forceMkdir(new File(path + "/m"));
            FileUtils.forceMkdir(new File(path + "/p"));
            FileUtils.forceMkdir(new File(path + "/s"));
            FileUtils.forceMkdir(new File(path + "/d"));
            log.info("Making Index directory on: " + path);
            log.info("\n\n -----------------------CLeaning is Finished--------------------------\n");
        } catch (IOException ex) {
            log.error(ex);
        }
    }

    public IndexesCleaner(String period, String indexType) {
        try {
            String path = configFile.getProperty("INDEXES_PATH") + period + "/" + indexType;
            File Index = new File(path);
            if (Index.exists()) {
                FileUtils.deleteDirectory(Index);
                log.info("Deletting the existing index directory on: " + path);
            }
            FileUtils.forceMkdir(new File(path));
            log.info("Making Index directory on: " + path);
            log.info("\n\n -----------------------CLeaning is Finished--------------------------\n");
        } catch (IOException ex) {
            log.error(ex);
        }
    }

    public static void main(String[] args) {
        new IndexesCleaner("20122014");
    }
}
