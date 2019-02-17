/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Kiner Shah
 */


import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Topological;
import java.util.HashMap;
import java.util.HashSet;

public class WordNet {
    private final HashMap<String, HashSet<Integer>> synsetIdMap;
    private final HashMap<Integer, String> originalIdSynsetMap;
    private int synsetNodesSize;
    private Digraph DG;
    private final SAP sapInstance;
    
    private void readSynsets(String synsets) {
        In in = new In(synsets);
        String line;
        while (true) {
            line = in.readLine();
            if (line == null) break;
            String[] splittedLine = line.trim().split(",");
//            synsetNodes.put(Integer.parseInt(splittedLine[0]), splittedLine[1]);
            String[] synsetWords = splittedLine[1].split("\\s");
            int id = Integer.parseInt(splittedLine[0]);
            originalIdSynsetMap.put(id, splittedLine[1]);
            for (String s : synsetWords) {
                if (synsetIdMap.containsKey(s)) {
                    HashSet<Integer> hs = synsetIdMap.get(s);
                    hs.add(id);
                    synsetIdMap.remove(s);
                    synsetIdMap.put(s, hs);
                }
                else {
                    HashSet<Integer> hs = new HashSet<Integer>();
                    hs.add(id);
                    synsetIdMap.put(s, hs);
                }
            }
        }
        synsetNodesSize = originalIdSynsetMap.size();
    } 
    private void readHypernyms(String hypernyms) {
        In in = new In(hypernyms);
        String line;
        DG = new Digraph(synsetNodesSize);
        while (true) {
            line = in.readLine();
            if (line == null) break;
            String[] splittedLine = line.trim().split(",");
            int len = splittedLine.length;
            int v = -1, u;
            for (int i = 0; i < len; i++) {
                if (i == 0) {
                    v = Integer.parseInt(splittedLine[i]);
                }
                else {
                    u = Integer.parseInt(splittedLine[i]);
                    DG.addEdge(v, u);
                }    
            }
        }
    }
    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null)
            throw new java.lang.IllegalArgumentException();
        if (hypernyms == null)
            throw new java.lang.IllegalArgumentException();
        
        synsetIdMap = new HashMap<String, HashSet<Integer>>();
        originalIdSynsetMap = new HashMap<Integer, String>();
        synsetNodesSize = 0;
        readSynsets(synsets);
        readHypernyms(hypernyms);
//        StdOut.println(synsetNodesSize + " " + DG.E());
        // check if DG is a rooted DAG, if not throw IllegalArgumentException
        // a graph has topological order if it is a DAG
        Topological topo = new Topological(DG);
        if (!topo.hasOrder()) {
            throw new java.lang.IllegalArgumentException();
        }
        // a rooted graph has only one node with zero outdegree
        int rootCnt = 0;
        for (int i = 0; i < DG.V(); i++) {
            if (DG.outdegree(i) == 0)
                rootCnt++;
        }
        if (rootCnt != 1) {
            throw new java.lang.IllegalArgumentException();
        }
        sapInstance = new SAP(DG);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return synsetIdMap.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null)
            throw new java.lang.IllegalArgumentException();
//        long start = System.currentTimeMillis();
        boolean isThere = synsetIdMap.keySet().contains(word);
//        boolean isThere = synsetIdMap.containsKey(word);
//        long end = System.currentTimeMillis();
//        StdOut.println((end - start) + " ms");
        return isThere;
    }
    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null || !isNoun(nounA))
            throw new java.lang.IllegalArgumentException();
        if (nounB == null || !isNoun(nounB))
            throw new java.lang.IllegalArgumentException();
        
        HashSet<Integer> id1 = synsetIdMap.get(nounA);
        HashSet<Integer> id2 = synsetIdMap.get(nounB);
        
        return sapInstance.length(id1, id2);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || !isNoun(nounA))
            throw new java.lang.IllegalArgumentException();
        if (nounB == null || !isNoun(nounB))
            throw new java.lang.IllegalArgumentException();
        
        HashSet<Integer> id1 = synsetIdMap.get(nounA);
        HashSet<Integer> id2 = synsetIdMap.get(nounB);
        
        int ancestorId = sapInstance.ancestor(id1, id2);
        if (ancestorId != -1) return originalIdSynsetMap.get(ancestorId);
        return null;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wn = new WordNet("synsets.txt", "hypernyms.txt");
        StdOut.println(wn.sap("worm", "bird") + " " + wn.distance("worm", "bird"));
        StdOut.println(wn.sap("municipality", "region") + " " + wn.distance("municipality", "region"));
        StdOut.println(wn.sap("individual", "physical_entity") + " " + wn.distance("individual", "physical_entity"));
        StdOut.println(wn.sap("edible_fruit", "physical_entity") + " " + wn.distance("edible_fruit", "physical_entity"));
        StdOut.println(wn.sap("white_marlin", "mileage") + " " + wn.distance("white_marlin", "mileage"));
        StdOut.println(wn.sap("Black_Plague", "black_marlin") + " " + wn.distance("Black_Plague", "black_marlin"));
        StdOut.println(wn.sap("American_water_spaniel", "histology") + " " + wn.distance("American_water_spaniel", "histology"));
        StdOut.println(wn.sap("Brown_Swiss", "barrel_roll") + " " + wn.distance("Brown_Swiss", "barrel_roll"));
        StdOut.println(wn.sap("Ambrose", "entity") + " " + wn.distance("Ambrose", "entity"));
        StdOut.println(wn.synsetIdMap.size());
    }
}
