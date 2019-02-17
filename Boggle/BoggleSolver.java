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
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;
import java.util.HashSet;

class TrieSTForEnglish {
    private static final int R = 26;    // only 26 alphabet characters of English [A-Z]
    private Node root = new Node();
    private static class Node {
        private Integer value;
        private Node[] next = new Node[R];
    }
    public void put(String key, Integer val) {
        root = put(root, key, val, 0);
    }
    private Node put(Node node, String key, Integer val, int d) {
        if (node == null) node = new Node();
        if (d == key.length()) {
            node.value = val;
            return node;
        }
        char c = key.charAt(d);
        node.next[c - 'A'] = put(node.next[c - 'A'], key, val, d + 1);
        return node;
    }
    public Integer get(String key) {
        Node node = getIterative(root, key);
        if (node == null) return null;
        return node.value;
    }
    private Node getIterative(Node node, String key) {
        int d = 0, len = key.length();
        Node temp = node;
        while (d < len) {
            if (temp == null) return null;
            int index = key.charAt(d) - 'A';
//            StdOut.println(key.charAt(d));
            if (temp.next[index] == null) return null;
            temp = temp.next[index];
            d++;
        }
        return temp;
    }
    public boolean hasKeyWithPrefix(String prefix) {
        Node node = getIterative(root, prefix);
        return hasKeyWithPrefixNode(node);
    }
    public Object getPrefixNode(String prefix, Object o, int suffixLen) {
        Node node, prev = (Node) o;
        if (prev == null) node = getIterative(root, prefix);
        else node = prev.next[prefix.charAt(prefix.length() - suffixLen) - 'A'];
        if (suffixLen == 2 && prev != null && node != null) return node.next[20]; // 'U' - 'A' = 20
        return (node != null ? node : null);
    }
    public boolean hasKeyWithPrefixNode(Node node) {
        Stack<Node> st = new Stack<Node>();
        st.push(node);
        while (!st.isEmpty()) {
            Node n = st.pop();
            if (n == null) continue;
            if (n.value != null) return true;
            for (char c = 'A'; c <= 'Z'; c++) {
                st.push(n.next[c - 'A']);
            }
        }
        return false;
    }
    public boolean isNodeInDict(Object o) {
        Node node = (Node) o;
        return node.value != null;
    }
}

class Pair {
    public final int i, j;
    public boolean visited;
    public String c;
//    public final HashSet<Pair> adj;
    public final Pair[] adj;
    public Pair(int i, int j, String c) {
        this.i = i;
        this.j = j;
        this.c = c;
//        adj = new HashSet<Pair>();
        adj = new Pair[8];  // for 8 adjacent neighbors
    }
    public void setString(String c) { this.c = c; }
//    public void add(int x, int y, String c) {
//        adj.add(new Pair(x, y, c));
//    }
//    public void add(Pair p) { adj.add(p); }
}

public class BoggleSolver {
    // Use 26-way trie as dictionary
    private final TrieSTForEnglish trieDict;
    private Pair[][] boardGraph;
    // Helper for calculating word score
    private int wordScoreHelper(String w) {
        int wordLen = w.length();
        if (wordLen >= 0 && wordLen <= 2) return 0;
        else if (wordLen >= 3 && wordLen <= 4) return 1;
        else if (wordLen == 5) return 2;
        else if (wordLen == 6) return 3;
        else if (wordLen == 7) return 5;
        return 11;
    }
    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        trieDict = new TrieSTForEnglish();
        for (String s : dictionary) {
            trieDict.put(s, wordScoreHelper(s));
        }
        boardGraph = new Pair[50][50];
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < 50; j++) {
                boardGraph[i][j] = new Pair(i, j, "A");
            }
        }
    }
    // Helper function for getting letter at (i, j) from board
    // Special handing for "QU" is there: if letter at (i, j) is 'Q', function returns "QU"
    private String getLetter(BoggleBoard board, int i, int j) {
        char c = board.getLetter(i, j);
        if (c == 'Q') return "QU";
        else return "" + c;
    }
    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        int m = board.rows();
        int n = board.cols();
//        boardGraph = new Pair[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
//                boardGraph[i][j] = new Pair(i, j, getLetter(board, i, j));
                boardGraph[i][j].setString(getLetter(board, i, j));
                /* if (i - 1 >= 0 && j - 1 >= 0)
                    boardGraph[i][j].add(new Pair(i - 1, j - 1, getLetter(board, i - 1, j - 1)));
                if (i - 1 >= 0)
                    boardGraph[i][j].add(new Pair(i - 1, j, getLetter(board, i - 1, j)));
                if (i - 1 >= 0 && j + 1 < n)
                    boardGraph[i][j].add(new Pair(i - 1, j + 1, getLetter(board, i - 1, j + 1)));
                if (j - 1 >= 0)
                    boardGraph[i][j].add(new Pair(i, j - 1, getLetter(board, i, j - 1)));
                if (j + 1 < n)
                    boardGraph[i][j].add(new Pair(i, j + 1, getLetter(board, i, j + 1)));
                if (i + 1 < m && j - 1 >= 0)
                    boardGraph[i][j].add(new Pair(i + 1, j - 1, getLetter(board, i + 1, j - 1)));
                if (i + 1 < m)
                    boardGraph[i][j].add(new Pair(i + 1, j, getLetter(board, i + 1, j)));
                if (i + 1 < m && j + 1 < n)
                    boardGraph[i][j].add(new Pair(i + 1, j + 1, getLetter(board, i + 1, j + 1))); */
                
                if (i - 1 >= 0 && j - 1 >= 0)
                    boardGraph[i][j].adj[0] = new Pair(i - 1, j - 1, getLetter(board, i - 1, j - 1));
                if (i - 1 >= 0)
                    boardGraph[i][j].adj[1] = new Pair(i - 1, j, getLetter(board, i - 1, j));
                if (i - 1 >= 0 && j + 1 < n)
                    boardGraph[i][j].adj[2] = new Pair(i - 1, j + 1, getLetter(board, i - 1, j + 1));
                if (j - 1 >= 0)
                    boardGraph[i][j].adj[3] = new Pair(i, j - 1, getLetter(board, i, j - 1));
                if (j + 1 < n)
                    boardGraph[i][j].adj[4] = new Pair(i, j + 1, getLetter(board, i, j + 1));
                if (i + 1 < m && j - 1 >= 0)
                    boardGraph[i][j].adj[5] = new Pair(i + 1, j - 1, getLetter(board, i + 1, j - 1));
                if (i + 1 < m)
                    boardGraph[i][j].adj[6] = new Pair(i + 1, j, getLetter(board, i + 1, j));
                if (i + 1 < m && j + 1 < n)
                    boardGraph[i][j].adj[7] = new Pair(i + 1, j + 1, getLetter(board, i + 1, j + 1));
            }
        }
        HashSet<String> allValidWords = new HashSet<String>();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                dfs(/*boardGraph, */i, j, boardGraph[i][j].c, allValidWords, null, boardGraph[i][j].c.length());
            }
        }
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                boardGraph[i][j].adj[0] = null;
                boardGraph[i][j].adj[1] = null;
                boardGraph[i][j].adj[2] = null;
                boardGraph[i][j].adj[3] = null;
                boardGraph[i][j].adj[4] = null;
                boardGraph[i][j].adj[5] = null;
                boardGraph[i][j].adj[6] = null;
                boardGraph[i][j].adj[7] = null;
            }
        }
        return allValidWords;
    }
    // Run dfs for (i, j)
    private void dfs(/*Pair[][] boardGraph, */int i, int j, String s, HashSet<String> q, Object prevPrefixNode, int sufLen) {
        boardGraph[i][j].visited = true;
        int len = s.length();
        Object existingNode = trieDict.getPrefixNode(s, prevPrefixNode, sufLen);
        if (len > 2) {
            if (existingNode == null) {
                boardGraph[i][j].visited = false;
                return;    
            }
            else if (trieDict.isNodeInDict(existingNode)) {
                q.add(s);
            }
        }
        for (Pair p : boardGraph[i][j].adj) {
//            if (!boardGraph[p.i][p.j].visited)
            if (p != null && !boardGraph[p.i][p.j].visited)
                dfs(/*boardGraph, */p.i, p.j, s + p.c, q, existingNode, p.c.length());
        }
        boardGraph[i][j].visited = false;
    }
    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        Integer score = trieDict.get(word);
        if (score == null) return 0;
        return score;
    }
    
    // Test client
    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}
