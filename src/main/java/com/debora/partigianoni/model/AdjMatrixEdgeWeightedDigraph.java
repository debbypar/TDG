package com.debora.partigianoni.model;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *  The {@code AdjMatrixEdgeWeightedDigraph} class represents a edge-weighted
 *  digraph of vertices named 0 through <em>V</em> - 1, where each
 *  directed edge is of type {@link DirectedEdge} and has a real-valued weight.
 *  It supports the following two primary operations: add a directed edge
 *  to the digraph and iterate over all of edges incident from a given vertex.
 *  It also provides
 *  methods for returning the number of vertices <em>V</em> and the number
 *  of edges <em>E</em>. Parallel edges are disallowed; self-loops are permitted.
 *  <p>
 *  This implementation uses an adjacency-matrix representation.
 *  All operations take constant time (in the worst case) except
 *  iterating over the edges incident from a given vertex, which takes
 *  time proportional to <em>V</em>.
 *  <p>
 *  For additional documentation,
 *  see <a href="https://algs4.cs.princeton.edu/44sp">Section 4.4</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class AdjMatrixEdgeWeightedDigraph {
    private static final String NEWLINE = System.getProperty("line.separator");

    private final int V;
    private int E;
    private DirectedEdge[][] adj;

    /**
     * Initializes an empty edge-weighted digraph with {@code V} vertices and 0 edges.
     * @param V the number of vertices
     * @throws IllegalArgumentException if {@code V < 0}
     */
    public AdjMatrixEdgeWeightedDigraph(int V) {
        if (V < 0) throw new IllegalArgumentException("number of vertices must be nonnegative");
        this.V = V;
        this.E = 0;
        this.adj = new DirectedEdge[V][V];
    }

    /**
     * Initializes a random edge-weighted digraph with {@code V} vertices and <em>E</em> edges.
     * @param V the number of vertices
     * @param E the number of edges
     * @throws IllegalArgumentException if {@code V < 0}
     * @throws IllegalArgumentException if {@code E < 0}
     */
    public AdjMatrixEdgeWeightedDigraph(int V, int E) {
        this(V);
        if (E < 0) throw new IllegalArgumentException("number of edges must be nonnegative");
        if (E > V*V) throw new IllegalArgumentException("too many edges");

        // can be inefficient
        while (this.E != E) {
            int v = StdRandom.uniform(V);
            int w = StdRandom.uniform(V);
            double weight = Math.round(100 * StdRandom.uniform()) / 100.0;
            System.out.println("*********** "+weight);
            addEdge(new DirectedEdge(v, w, weight));
        }
    }

    /**
     * Returns the number of vertices in the edge-weighted digraph.
     * @return the number of vertices in the edge-weighted digraph
     */
    public int V() {
        return V;
    }

    /**
     * Returns the number of edges in the edge-weighted digraph.
     * @return the number of edges in the edge-weighted digraph
     */
    public int E() {
        return E;
    }

    public DirectedEdge[][] adj(){
        return adj;
    }


    /**
     * Adds the directed edge {@code e} to the edge-weighted digraph (if there
     * is not already an edge with the same endpoints).
     * @param e the edge
     */
    public void addEdge(DirectedEdge e) {
        int v = e.from();
        int w = e.to();
        validateVertex(v);
        validateVertex(w);
        if (adj[v][w] == null) {
            E++;
            adj[v][w] = e;
        }
    }

    /**
     * Returns the directed edges incident from vertex {@code v}.
     * @param v the vertex
     * @return the directed edges incident from vertex {@code v} as an Iterable
     * @throws IllegalArgumentException unless {@code 0 <= v < V}
     */
    public Iterable<DirectedEdge> adj(int v) {
        validateVertex(v);
        for(int i=0; i<v; i++) {
            if(this.adj[v][i] != null) {
                System.out.println("%%%");
                System.out.println(this.adj[v][i]);//.from()+",,,"+this.adj[v-1][i].to()+",,,,,,,"+this.adj[v-1][i].weight());
                System.out.println("%%%");
            }
        }
        return new AdjIterator(v);
    }

    // support iteration over graph vertices
    private class AdjIterator implements Iterator<DirectedEdge>, Iterable<DirectedEdge> {
        private int v;
        private int w = 0;

        public AdjIterator(int v) {
            this.v = v;
        }

        public Iterator<DirectedEdge> iterator() {
            return this;
        }

        public boolean hasNext() {
            while (w < V) {
                if (adj[v][w] != null) return true;
                w++;
            }
            return false;
        }

        public DirectedEdge next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
        //    System.out.println("llllllllllll    "+adj[18][w]);
            return adj[v][w++];
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Returns a string representation of the edge-weighted digraph. This method takes
     * time proportional to <em>V</em><sup>2</sup>.
     * @return the number of vertices <em>V</em>, followed by the number of edges <em>E</em>,
     *   followed by the <em>V</em> adjacency lists of edges
     */
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(V + " " + E + NEWLINE);
        for (int v = 0; v < V; v++) {
            s.append(v + ": ");
            for (DirectedEdge e : adj(v)) {
                s.append(e + "  ");
            }
            s.append(NEWLINE);
        }
        return s.toString();
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V-1));
    }


    /**
     * Unit tests the {@code AdjMatrixEdgeWeightedDigraph} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        int V = Integer.parseInt(args[0]);
        int E = Integer.parseInt(args[1]);
        AdjMatrixEdgeWeightedDigraph G = new AdjMatrixEdgeWeightedDigraph(V, E);
        StdOut.println(G);
        int k,z;
        for(k=0; k<G.adj().length; k++)
            for (z=0; z<G.adj().length; z++)
                if(G.adj[k][z] != null && k==3)
                    System.out.println("...."+G.adj[k][z].getWeight());

        int y=0;
        Iterator iterator = G.adj(2).iterator();
        while(iterator.hasNext()) {
            DirectedEdge obj = (DirectedEdge)iterator.next();
            System.out.println("+++ "+y+": " + obj);
            y++;
        }
    }

}