/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Kiner Shah
 */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.BinaryStdOut;
import java.util.ArrayList;

public class BurrowsWheeler {
    // apply Burrows-Wheeler transform, reading from standard input and writing to standard output
    public static void transform() {
        StringBuilder sb = new StringBuilder("");
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            sb.append(c);
        }
        String input = sb.toString();
        CircularSuffixArray csa = new CircularSuffixArray(input);
        int lenInput = csa.length();
        int targetIndex = -1;
        StringBuilder sb1 = new StringBuilder("");
        for (int i = 0; i < lenInput; i++) {
            int indexVal = csa.index(i);
            // index where original input string is there
            if (indexVal == 0) {
                targetIndex = i;
            }
            // get the last character of suffix starting at indexVal
            sb1.append(input.charAt((indexVal + lenInput - 1) % lenInput));
        }
        BinaryStdOut.write(targetIndex);
        BinaryStdOut.write(sb1.toString());
        BinaryStdOut.close();
    }
    private static void sort(char[] arr, int[] start, int[] end) {
        final int R = 256;  // radix
        int[] count = new int[R + 1];
        for (int i = 0; i < arr.length; i++) {
            count[arr[i]]++;
        }
        int j = 0;
        for (int i = 0; i < R; i++) {
            start[i] = -1;
            end[i] = -1;
            if (count[i] > 0) {
                start[i] = j;
                int endCnt = j;
                while (count[i] > 0) {
                    arr[j++] = (char) i;
                    count[i]--;
                    endCnt++;
                }
                end[i] = endCnt;
//                StdOut.println(((char) i) + " " + start[i] + " " + end[i]);
            }
        }
    }
    private static final int[] start = new int[256];
    private static final int[] end = new int[256];
    // apply Burrows-Wheeler inverse transform, reading from standard input and writing to standard output
    public static void inverseTransform() {
        StringBuilder sb = new StringBuilder("");
        int targetIndex = -1;
        if (!BinaryStdIn.isEmpty()) {
            targetIndex = BinaryStdIn.readInt();
        }
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            sb.append(c);
        }
        String inputStr = sb.toString();
        final int inputStrLen = inputStr.length();
//        char[] inputArr = inputStr.toCharArray();
        char[] sortedArr = inputStr.toCharArray();
        sort(sortedArr, start, end);
//        for (char x : sortedArr) StdOut.print(x); StdOut.println();
        int[] next = new int[inputStrLen];
        for (int i = 0; i < inputStrLen; i++) {
            char c = inputStr.charAt(i);
            if (start[c] != end[c]) {
                next[start[c]] = i;
                start[c]++;
            }
        }
//        for (int i = 0; i < inputStrLen; i++) StdOut.println(i + " " + next[i]);
        StringBuilder sb1 = new StringBuilder();
        int s = next[targetIndex];
        while (true) {
            sb1.append(inputStr.charAt(s));
            if (sb1.length() == inputStrLen) break;
            s = next[s];
        }
        String output = sb1.toString();
        BinaryStdOut.write(output);
        BinaryStdOut.close();
    }

    // if args[0] is '-', apply Burrows-Wheeler transform
    // if args[0] is '+', apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        String operation = args[0];
        switch (operation) {
            case "+":       // inverse transform
                inverseTransform();
                break;
            case "-":       // transform
                transform();
                break;
            default:        // invalid command-line argument
                StdOut.println("Invalid command line argument: " + operation);
                break;
        }
    }
}
