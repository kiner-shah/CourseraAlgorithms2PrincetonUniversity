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
import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.StdOut;

public class MoveToFront {
    private static final int R = 256;
    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        int[] index = new int[R + 1];
        char[] charAtIndex = new char[R + 1];
        for (int i = 0; i < R + 1; i++) { 
            index[i] = i; 
            charAtIndex[i] = (char) i;
        }
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            BinaryStdOut.write((char)index[c]);
            for (int i = index[c] - 1; i >= 0; i--) {
                char temp = charAtIndex[i];
                int tempIndex = ++index[temp];
                charAtIndex[tempIndex] = temp;
            }
            charAtIndex[0] = c;
            index[c] = 0;
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        int[] index = new int[R + 1];
        char[] charAtIndex = new char[R + 1];
        for (int i = 0; i < R + 1; i++) { 
            index[i] = i; 
            charAtIndex[i] = (char) i;
        }
        while (!BinaryStdIn.isEmpty()) {
            int c = BinaryStdIn.readChar();
            char t = charAtIndex[c];
            BinaryStdOut.write(t);
            for (int i = c - 1; i >= 0; i--) {
                char temp = charAtIndex[i];
                int tempIndex = ++index[temp];
                charAtIndex[tempIndex] = temp;
            }
            charAtIndex[0] = t;
            index[t] = 0;
        }
        BinaryStdOut.close();     
    }

    // if args[0] is '-', apply move-to-front encoding
    // if args[0] is '+', apply move-to-front decoding
    public static void main(String[] args) {
        String operation = args[0];
        switch (operation) {
            case "+":       // decode
                decode();
                break;
            case "-":       // encode
                encode();
                break;
            default:        // invalid command-line argument
                StdOut.println("Invalid command line argument: " + operation);
                break;
        }        
    }
}
