/******************************************************************************
 *  Compilation:  javac AdjMatrix.java
 *  Execution:    java AdjMatrixGraph V E
 *  Dependencies: StdOut.java
 *
 *  A graph, implemented using an adjacency matrix.
 *  Parallel edges are disallowed; self-loops are allowd.
 *
 ******************************************************************************/

package com.debora.partigianoni.model;

/******************************************************************************
 *  Compilation:  javac AdjMatrix.java
 *  Execution:    java AdjMatrix V E
 *  Dependencies: StdOut.java
 *
 *  A digraph, implemented using an adjacency matrix.
 *  Parallel edges are disallowed; self-loops are allowd.
 *
 ******************************************************************************/

import java.util.Iterator;
import java.util.NoSuchElementException;


public class AdjMatrix {
    private int V;
    private int E;
    private Integer[][] adj;

    public Integer[][] getAdj() {
        return adj;
    }

    public void setAdj(Integer[][] adj) {
        this.adj = adj;
    }

    // empty graph with V vertices
    public AdjMatrix(int V) {
        if (V < 0) throw new RuntimeException("Number of vertices must be nonnegative");
        this.V = V;
        this.E = 0;
        this.adj = new Integer[V][V];
    }

    // random graph with V vertices and E edges
    public AdjMatrix(int V, int E) {
        this(V);
        if (E < 0) throw new RuntimeException("Number of edges must be nonnegative");
        if (E > V*V) throw new RuntimeException("Too many edges");

        // can be inefficient
        while (this.E != E) {
            int v = StdRandom.uniform(V);
            int w = StdRandom.uniform(V);
            addEdge(v, w);
        }
    }

    // number of vertices and edges
    public int V() { return V; }
    public int E() { return E; }


    // add directed edge v->w
    public void addEdge(int v, int w) {
        if (adj[v][w]==0) E++;
        adj[v][w] = 1;
    }

    // return list of neighbors of v
    public Iterable<Integer> adj(int v) {
        return new AdjIterator(v);
    }

    // support iteration over graph vertices
    private class AdjIterator implements Iterator<Integer>, Iterable<Integer> {
        private int v;
        private int w = 0;

        AdjIterator(int v) {
            this.v = v;
        }

        public Iterator<Integer> iterator() {
            return this;
        }

        public boolean hasNext() {
            while (w < V) {
                if (adj[v][w] != null) return true;
                w++;
            }
            return false;
        }

        public Integer next() {
            if (hasNext()) return w++;
            else           throw new NoSuchElementException();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }


    // string representation of Graph - takes quadratic time
    public String toString() {
        String NEWLINE = System.getProperty("line.separator");
        StringBuilder s = new StringBuilder();
        s.append(V + " " + E + NEWLINE);
        for (int v = 0; v < V; v++) {
            s.append(v + ": ");
            for (int w : adj(v)) {
                s.append(w + " ");
            }
            s.append(NEWLINE);
        }
        return s.toString();
    }


    // test client
    public static void main(String[] args) {
        int V = Integer.parseInt(args[0]);
        int E = Integer.parseInt(args[1]);
        AdjMatrix G = new AdjMatrix(V);
        StdOut.println(G);
    }

}