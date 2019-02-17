/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Kiner Shah
 */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private final WordNet wnInstance;
    public Outcast(WordNet wordnet) {        // constructor takes a WordNet object
        wnInstance = wordnet;
    }
    public String outcast(String[] nouns) {  // given an array of WordNet nouns, return an outcast
        int maxOutcastDist = Integer.MIN_VALUE;
        String outcastNoun = "";
        for (String s : nouns) {
            int sum = 0;
            for (String p : nouns) {
                if (!s.equals(p)) {
                    sum += wnInstance.distance(s, p);
                }
            }
            if (sum > maxOutcastDist) {
                maxOutcastDist = sum;
                outcastNoun = s;
            }
        }
        return outcastNoun;
    }
    public static void main(String[] args) { // see test client below
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
