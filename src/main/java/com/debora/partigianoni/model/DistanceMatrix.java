package com.debora.partigianoni.model;

import com.debora.partigianoni.controller.CSVHandler;
import com.opencsv.CSVReader;

import java.io.IOException;
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
public class DistanceMatrix {
    private static final String NEWLINE = System.getProperty("line.separator");

    private int V;
    private int E;
    private DirectedEdge[][] adj;
    private int posFirstMover;

    /**
     * Initializes an empty edge-weighted digraph with {@code V} vertices and 0 edges.
     * @param V the number of vertices
     * @throws IllegalArgumentException if {@code V < 0}
     */
    public DistanceMatrix(int V) {
        if (V < 0) throw new IllegalArgumentException("number of vertices must be nonnegative");
        this.V = V;
        this.E = 0;
        this.adj = new DirectedEdge[V][V];
    }

    public void setPosFirstMover(int posFirstMover) {
        this.posFirstMover = posFirstMover;
    }

    public int getPosFirstMover(){
        return this.posFirstMover;
    }

    public DirectedEdge[][] getAdj() {
        return adj;
    }

    /**
     *
     * @param file
     * @param V     Numero di vertici totale (compresi i mover)
     */
    public DistanceMatrix(String file, int V)
    {
     //   this.V = 0;
        this.E = 0;
        this.V = V;
        this.adj = new DirectedEdge[V][V];

        CSVHandler csvHandler = new CSVHandler();
        CSVReader distanceReader = csvHandler.readCSV(false, file);
        String[] lineDistance;

        int k=0;
        try {
            lineDistance = distanceReader.readNext();
            int j;
            while ( (lineDistance = distanceReader.readNext()) != null)
            {
             //   System.out.println("kkkkkkkkk  "+lineDistance[0]);
                if(lineDistance[0].equals("M1"))
                {
                    setPosFirstMover(k);
              //      System.out.println("TROVATOOOOOOOOOOOOO!!!!!!!!!!!!!!!\n"+"----->"+k);
                }
                for(j=1; j<lineDistance.length; j++){
                 //   System.out.println("("+k+", "+(j-1)+") --> "+lineDistance[j]);
                    //    if(k != (j-1))
                    addEdge(new DirectedEdge(k, (j-1), Double.parseDouble(lineDistance[j])));
                }
                k++;
            }
        }catch (IOException e) {
            e.printStackTrace();
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
     //   System.out.println("V... "+v+" W... "+w);
  //      System.out.println("adj[v][w]: "+adj[v][w]);
        if (adj[v][w] == null) {
        //    System.out.println("E' null!!!");
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
     /*   for(int i=0; i<v; i++) {
            if(this.adj[v][i] != null) {
                System.out.println("%%%");
                System.out.println(this.adj[v][i]);//.from()+",,,"+this.adj[v-1][i].to()+",,,,,,,"+this.adj[v-1][i].weight());
            }
        }*/
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

    public DirectedEdge[][] adj(){
        return adj;
    }

    /**
     * Unit tests the {@code AdjMatrixEdgeWeightedDigraph} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
       // int V = Integer.parseInt(args[0]);
       // int E = Integer.parseInt(args[1]);
        DistanceMatrix G = new DistanceMatrix("distanceMatrix_ist2.csv", 275);
        int k,z;
     /*   for(k=0; k<275; k++)
            for (z=0; z<(275-36); z++)
                if(G.adj[k][z] != null)
                    System.out.println("...."+G.adj[k][z].weight());

        int y=0;
        Iterator iterator = G.adj(2).iterator();
        while(iterator.hasNext()) {
            DirectedEdge obj = (DirectedEdge)iterator.next();
            System.out.println("+++ "+y+": " + obj);
            y++;
        }*/
     //   System.out.println(G);
     //   System.out.println(G.getPosFirstMover());
    }

}