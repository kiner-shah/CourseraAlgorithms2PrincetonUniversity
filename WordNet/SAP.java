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
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Queue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
class AncestorNode {
    final int anc_node;
    final int len;
    public AncestorNode(int node, int length) {
        anc_node = node;
        len = length;
    }
}
class QueryNode {
    final Iterable<Integer> node1;
    final Iterable<Integer> node2;
    public QueryNode(Iterable<Integer> a, Iterable<Integer> b) {
        node1 = a;
        node2 = b;
    }
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null) return false;
        if (other.getClass() != this.getClass()) return false;
        
        QueryNode y = (QueryNode) other;
        return y.node1.equals(this.node1) && y.node2.equals(this.node2);
    }
    @Override
    public int hashCode() {
    	int hash1 = 1;
    	for (Integer x : node1) {
            hash1 = 31 * hash1 + (x == null ? 0 : x ^ (x >>> 16));
    	}
    	int hash2 = 1;
    	for (Integer x : node2) {
            hash2 = 31 * hash2 + (x == null ? 0 : x ^ (x >>> 16));
    	}
    	return hash1 ^ hash2;
    }
}
public class SAP {
    private static int INF = Integer.MAX_VALUE;
    private final int digraphSize;
    private final Iterable<Integer>[] adj;
    private final HashMap<QueryNode, AncestorNode> cache;
    private boolean[] marked1;
    private boolean[] marked2;
    private int[] dist1;
    private int[] dist2;
      
    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null)
            throw new java.lang.IllegalArgumentException();
        digraphSize = G.V();
        adj = (Iterable<Integer>[]) new Iterable[digraphSize];
        marked1 = new boolean[digraphSize];
        marked2 = new boolean[digraphSize];
        dist1 = new int[digraphSize];
        dist2 = new int[digraphSize];
        for (int i = 0; i < digraphSize; i++) {
            dist1[i] = Integer.MAX_VALUE;
            dist2[i] = Integer.MAX_VALUE;
            
            adj[i] = G.adj(i);
        }
        cache = new HashMap();
    }
    private void reset(Iterable<Integer> it1, Iterable<Integer> it2) {
        for (int x : it1) {
            dist1[x] = INF;
            marked1[x] = false;
        }
        for (int x : it2) {
            dist2[x] = INF;
            marked2[x] = false;
        }
    }
    private int runBFS(int v, int w, boolean lengthOrAncestor) {
        Queue<Integer> q1 = new Queue<Integer>();
        Queue<Integer> q2 = new Queue<Integer>();
        HashSet<Integer> hs1 = new HashSet();
        HashSet<Integer> hs2 = new HashSet();
        
        dist1[v] = 0; hs1.add(v);
        dist2[w] = 0; hs2.add(w);

        q1.enqueue(v);
        q2.enqueue(w);

        int length = INF, ancestor = -1;
        while (true) {
            int v1, v2;
            if (!q1.isEmpty()) {
                v1 = q1.dequeue();
                marked1[v1] = true;
                if (marked2[v1]) {
                    if (length > dist1[v1] + dist2[v1]) {
                        length = dist1[v1] + dist2[v1];
                        ancestor = v1;
                    }
                }
                
                for (int u : adj[v1]) {
                    if (!marked1[u]) {
                        dist1[u] = Math.min(dist1[u], dist1[v1] + 1);
                        if (dist1[u] > length) marked1[u] = true;
                        else q1.enqueue(u);
                        hs1.add(u);
                    }
                }
            }
            if (!q2.isEmpty()) {
                v2 = q2.dequeue();
                marked2[v2] = true;
                if (marked1[v2]) {
                    if (length > dist1[v2] + dist2[v2]) {
                        length = dist1[v2] + dist2[v2];
                        ancestor = v2;
                    }
                }
                
                for (int u : adj[v2]) {
                    if (!marked2[u]) {
                        dist2[u] = Math.min(dist2[u], dist2[v2] + 1);
                        if (dist2[u] > length) marked2[u] = true;
                        else q2.enqueue(u);
                        hs2.add(u);
                    }
                }
            }
            if (q1.isEmpty() && q2.isEmpty()) break;
        }
        if (length == INF) length = -1;
        ArrayList<Integer> lst1 = new ArrayList(); lst1.add(v);
        ArrayList<Integer> lst2 = new ArrayList(); lst2.add(w);
        cache.put(new QueryNode(lst1, lst2), new AncestorNode(ancestor, length));
        
        reset(hs1, hs2);
        
        if (lengthOrAncestor) return length;
        else return ancestor;
    }
    private int runBFS(Iterable<Integer> v, Iterable<Integer> w, boolean lengthOrAncestor) {
        Queue<Integer> q1 = new Queue<>();
        Queue<Integer> q2 = new Queue<>();
        
        HashSet<Integer> hs1 = new HashSet();
        HashSet<Integer> hs2 = new HashSet();
        
        for (Integer x : v) {
            if (x == null || x < 0 || x >= digraphSize)
                throw new java.lang.IllegalArgumentException();
            q1.enqueue(x);
            dist1[x] = 0;
            hs1.add(x);
        }
        for (Integer x : w) {
            if (x == null || x < 0 || x >= digraphSize)
                throw new java.lang.IllegalArgumentException();
            q2.enqueue(x);
            dist2[x] = 0;
            hs2.add(x);
        }
        int length = INF, ancestor = -1;
        while (true) {
            int v1, v2;
            if (!q1.isEmpty()) {
                v1 = q1.dequeue();
                marked1[v1] = true;
                if (marked2[v1]) {
                    if (length > dist1[v1] + dist2[v1]) {
                        length = dist1[v1] + dist2[v1];
                        ancestor = v1;
                    }
                }
                
                for (int u : adj[v1]) {
                    if (!marked1[u]) {
                        dist1[u] = Math.min(dist1[u], dist1[v1] + 1);
                        if (dist1[u] > length) marked1[u] = true;
                        else q1.enqueue(u);
                        hs1.add(u);
                    }
                }
            }
            if (!q2.isEmpty()) {
                v2 = q2.dequeue();
                marked2[v2] = true;
                if (marked1[v2]) {
                    if (length > dist1[v2] + dist2[v2]) {
                        length = dist1[v2] + dist2[v2];
                        ancestor = v2;
                    }
                }
                
                for (int u : adj[v2]) {
                    if (!marked2[u]) {
                        dist2[u] = Math.min(dist2[u], dist2[v2] + 1);
                        if (dist2[u] > length) marked2[u] = true;
                        else q2.enqueue(u);
                        hs2.add(u);
                    }
                }
            }
            if (q1.isEmpty() && q2.isEmpty()) break;
        }
        if (length == INF) length = -1;
        cache.put(new QueryNode(v, w), new AncestorNode(ancestor, length));
        
        reset(hs1, hs2);
        
        if (lengthOrAncestor) return length;
        else return (length == -1 ? -1 : ancestor);
    }
    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (v < 0 || v >= digraphSize || w < 0 || w >= digraphSize)
            throw new java.lang.IllegalArgumentException();
        
        ArrayList<Integer> lst1 = new ArrayList(); lst1.add(v);
        ArrayList<Integer> lst2 = new ArrayList(); lst2.add(w);
        QueryNode q = new QueryNode(lst1, lst2);
        if (cache.containsKey(q))
            return cache.get(q).len;
                
        return runBFS(v, w, true);
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        if (v < 0 || v >= digraphSize || w < 0 || w >= digraphSize)
            throw new java.lang.IllegalArgumentException();
            
        ArrayList<Integer> lst1 = new ArrayList(); lst1.add(v);
        ArrayList<Integer> lst2 = new ArrayList(); lst2.add(w);
        QueryNode q = new QueryNode(lst1, lst2);
        if (cache.containsKey(q))
            return cache.get(q).anc_node;
                
        return runBFS(v, w, false);
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null)
            throw new java.lang.IllegalArgumentException();
        if (v instanceof Collection) {
            if (((Collection<?>) v).isEmpty())
                throw new java.lang.IllegalArgumentException();
        }
        if (w instanceof Collection) {
            if (((Collection<?>) w).isEmpty())
                throw new java.lang.IllegalArgumentException();
        }
        QueryNode q = new QueryNode(v, w);
        if (cache.containsKey(q))
            return cache.get(q).len;
        
        return runBFS(v, w, true);
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null)
            throw new java.lang.IllegalArgumentException();
        if (v instanceof Collection) {
            if (((Collection<?>) v).isEmpty())
                throw new java.lang.IllegalArgumentException();
        }
        if (w instanceof Collection) {
            if (((Collection<?>) w).isEmpty())
                throw new java.lang.IllegalArgumentException();
        }
        QueryNode q = new QueryNode(v, w);
        if (cache.containsKey(q))
            return cache.get(q).anc_node;
        
        return runBFS(v, w, false);
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
//        while (!StdIn.isEmpty()) {
//            int v = StdIn.readInt();
//            int w = StdIn.readInt();
//            int length   = sap.length(v, w);
//            int ancestor = sap.ancestor(v, w);
//            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
//        }
        java.util.ArrayList<Integer> l1 = new java.util.ArrayList<Integer>();
        //6 11 12 13 19
        l1.add(6); l1.add(11); l1.add(12); l1.add(13); l1.add(19);
        java.util.ArrayList<Integer> l2 = new java.util.ArrayList<Integer>();
        // 3 11 12 13 19
        l2.add(3); l2.add(4); l2.add(9); l2.add(17); l2.add(19);
        int length_set = sap.length(l1, l2);
        int ancestor_set = sap.ancestor(l1, l2);
        StdOut.printf("length = %d, ancestor = %d\n", length_set, ancestor_set);
        int length_set1 = sap.length(l1, l2);
        int ancestor_set1 = sap.ancestor(l1, l2);
        StdOut.printf("length = %d, ancestor = %d\n", length_set1, ancestor_set1);
    }
}
