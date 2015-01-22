/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.expose.clustering;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Scanner;

/**
 *
 * @author Mostafa Dehghani
 */
public class ComplementGraph {

    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ComplementGraph.class.getName());

    public void complement(String period) throws IOException {

        FileWriter fileWritter = new FileWriter("/Users/Mosi/Desktop/SIGIR_SHORT/CdebGraph" + period + ".csv");
        BufferedWriter bw = new BufferedWriter(fileWritter);
        Scanner sc = new Scanner(new File("/Users/Mosi/Desktop/SIGIR_SHORT/debGraph" + period + ".csv"));
        bw.write(sc.nextLine() + "\n");
        HashSet<String> nodes = new HashSet<String>();
        HashMap<HashSet<String>, Double> edges = new HashMap<>();
        HashMap<HashSet<String>, Double> cedges = new HashMap<>();

        while (sc.hasNext()) {
            String[] parts = sc.nextLine().split(",");
            nodes.add(parts[0]);
            nodes.add(parts[1]);
            HashSet<String> ns = new HashSet(Arrays.asList(new String[]{parts[0], parts[1]}));
            edges.put(ns, Double.parseDouble(parts[3]));
        }
        for (String s : nodes) {
            for (String t : nodes) {
                if (s.equals(t)) {
                    continue;
                }
                HashSet<String> ns = new HashSet(Arrays.asList(new String[]{s, t}));
                Double W = edges.get(ns);
                Double newW = W == null ? 1 : 1 - W;
                cedges.put(ns, newW);
            }
        }
        for (Entry<HashSet<String>, Double> e : cedges.entrySet()) {
            String Line = "";
            for (String n : e.getKey()) {
                Line += n + ",";
            }
            Line += "Undirected," + e.getValue() + "\n";
            bw.write(Line);
        }
        bw.close();
    }

    public static void main(String[] args) throws IOException {
        ComplementGraph cg = new ComplementGraph();
        cg.complement("20062010");
    }

}
