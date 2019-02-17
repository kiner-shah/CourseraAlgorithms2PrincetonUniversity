/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Kiner Shah
 */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Quick3string;

class IndexSuffixFirstCharPair /* implements Comparable<IndexSuffixFirstCharPair> */ {
    int originalIndex;
    char firstCharInSuffix;
    /*
    @Override
    public int compareTo(IndexSuffixFirstCharPair that) {
        if (this.firstCharInSuffix < that.firstCharInSuffix) return -1;
        if (this.firstCharInSuffix > that.firstCharInSuffix) return 1;
        else if (this.firstCharInSuffix == that.firstCharInSuffix) {
            if (this.originalIndex > that.originalIndex) return -1;
            else if (this.originalIndex < that.originalIndex) return 1;           
        }
        return 0;
    }
    */
}
public class CircularSuffixArray {
    private final int lengthString;
    private static final int CUTOFF = 15;
    private IndexSuffixFirstCharPair[] pairArray;
    // modified 3-way radix quick sort
    private void sort(String s) {
        sort(s, 0, lengthString - 1, 0);
    }
    private void exch(int i, int j) {
        IndexSuffixFirstCharPair temp = pairArray[i];
        pairArray[i] = pairArray[j];
        pairArray[j] = temp;
    }
    private char charAt(String s, int ind) {
//        StdOut.println(ind + " " + s.length());
        if (ind >= s.length()) 
            return s.charAt(ind - s.length());
        return s.charAt(ind);
    }
    private void sort(String s, int lo, int hi, int d) {
        if (hi <= lo) return;
        if (d >= s.length()) return;
        int lt = lo, gt = hi;
        char v = charAt(s, pairArray[lo].originalIndex + d);
        int i = lo + 1;
        while (i <= gt) {
//            StdOut.println(pairArray[i].originalIndex + " " + d + " " + (pairArray[i].originalIndex + d));
            char t = charAt(s, pairArray[i].originalIndex + d);
            if (t < v) exch(lt++, i++);
            else if (t > v) exch(i, gt--);
            else i++;
        }
        sort(s, lo, lt - 1, d);
        if (v >= 0) sort(s, lt, gt, d + 1);
        sort(s, gt + 1, hi, d);
    }
    public CircularSuffixArray(String s) {   // circular suffix array of s
        if (s == null) {
            throw new java.lang.IllegalArgumentException();
        }
        lengthString = s.length();
        pairArray = new IndexSuffixFirstCharPair[lengthString];
        for (int i = 0; i < lengthString; i++) {
            pairArray[i] = new IndexSuffixFirstCharPair();
            pairArray[i].firstCharInSuffix = s.charAt(i);
            pairArray[i].originalIndex = i;
        }
        sort(s);
//        for (int i = 0; i < lengthString; i++) StdOut.println(i + " " + pairArray[i].originalIndex);
    }
    public int length() {                    // length of s
        return lengthString;
    }
    public int index(int i) {                // returns index of ith sorted suffix
        if (i < 0 || i >= lengthString) {
            throw new java.lang.IllegalArgumentException();
        }
        return pairArray[i].originalIndex;
    }
    public static void main(String[] args) { // unit testing (required)
        final String testString =  "couscous"; // "AAAAAAAAAA"; // "ABRACADABRA!"; 
        final int lenTestString = testString.length();
        
        // test for length
        CircularSuffixArray csa = new CircularSuffixArray(testString);
        StdOut.println(lenTestString + " " + csa.length());
        assert(lenTestString == csa.length());
        // test for null argument to constructor
//        CircularSuffixArray csa1 = new CircularSuffixArray(null);
        // test for index
        final int[] localIndexArray = {11, 10, 7, 0, 3, 5, 8, 1, 4, 6, 9, 2};   // from sample given in problem description
        final int lenLocalIndexArray = localIndexArray.length;
        assert(lenLocalIndexArray == lenTestString);
        for (int i = 0; i < lenTestString; i++) {
            StdOut.println(localIndexArray[i] + " " + csa.index(i));
            assert(localIndexArray[i] == csa.index(i));
        }
    }
}
