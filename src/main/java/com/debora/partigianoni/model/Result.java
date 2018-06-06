package com.debora.partigianoni.model;

import com.debora.partigianoni.MainAlgorithm;

import java.util.ArrayList;
import java.util.List;

public class Result {
    //private MainAlgorithm algInfo;
    private int objective;
    private double timeExecution;
    private double[] X;    //istanti di completamento degli ordini
    private int[] z1;
    private int[] z2;
    private int[] z3;
    private int[] w;
    private AdjMatrix adjMatrix;
    private int counterMovers;
    private int counterDelivery;
    private boolean[] moverEnd;
    private List<Integer> delivered;
    private double[] t_ist;     //tempo istantaneo del mover
    private int[] pos_ist;   //posizione istantanea del mover
    public DirectedEdge[][] distMatrix;

    public DirectedEdge[][] getDistMatrix() {
        return distMatrix;
    }

    public void setDistMatrix(DirectedEdge[][] distMatrix) {
        this.distMatrix = distMatrix;
    }


/*public DistanceMatrix getDistMatrix() {
        return distMatrix;
    }

    public void setDistMatrix(DistanceMatrix distMatrix) {
        this.distMatrix = distMatrix;
    }*/


    public boolean[] getMoverEnd() {
        return moverEnd;
    }

    public void setMoverEnd(boolean[] moverEnd) {
        this.moverEnd = moverEnd;
    }

    public int getCounterMovers() {
        return counterMovers;
    }

    public void setCounterMovers(int counterMovers) {
        this.counterMovers = counterMovers;
    }

    public int getCounterDelivery() {
        return counterDelivery;
    }

    public void setCounterDelivery(int counterDelivery) {
        this.counterDelivery = counterDelivery;
    }

    public double[] getT_ist() {
        return t_ist;
    }

    public void setT_ist(double[] t_ist) {
        this.t_ist = t_ist;
    }

    public int[] getPos_ist() {
        return pos_ist;
    }

    public void setPos_ist(int[] pos_ist) {
        this.pos_ist = pos_ist;
    }

    public List<Integer> getDelivered() {
        return delivered;
    }

    public void setDelivered(List<Integer> delivered) {
        this.delivered = delivered;
    }

  /* public MainAlgorithm getAlgInfo() {
        return algInfo;
    }

    public void setAlgInfo(MainAlgorithm algInfo) {
        this.algInfo = algInfo;
    }*/

    public int getObjective() {
        return objective;
    }

    public void setObjective(int objective) {
        this.objective = objective;
    }

    public double getTimeExecution() {
        return timeExecution;
    }

    public void setTimeExecution(double timeExecution) {
        this.timeExecution = timeExecution;
    }

    public double[] getX() {
        return X;
    }

    public void setX(double[] x) {
        X = x;
    }

    public int[] getZ1() {
        return z1;
    }

    public void setZ1(int[] z1) {
        this.z1 = z1;
    }

    public int[] getZ2() {
        return z2;
    }

    public void setZ2(int[] z2) {
        this.z2 = z2;
    }

    public int[] getZ3() {
        return z3;
    }

    public void setZ3(int[] z3) {
        this.z3 = z3;
    }

    public int[] getW() {
        return w;
    }

    public void setW(int[] w) {
        this.w = w;
    }

    public AdjMatrix getAdjMatrix() {
        return adjMatrix;
    }

    public void setAdjMatrix(AdjMatrix adjMatrix) {
        this.adjMatrix = adjMatrix;
    }

    public Result(int V, int M)
    {
        this.X = new double[V-M];
        this.adjMatrix = new AdjMatrix(V);
        this.z1 = new int[V-M];
        this.z2 = new int[V-M];
        this.z3 = new int[V-M];
        this.w = new int[V-M];
        this.t_ist = new double[M];
        this.pos_ist = new int[M];
        this.counterDelivery = 0;
        this.counterMovers = 0;
        this.moverEnd = new boolean[M];

        int j;
        for(j=0; j<V-M; j++)
        {
            w[j] = 0;
            z1[j] = 0;
            z2[j] = 0;
            z3[j] = 0;
        }

        for(j=0; j<M; j++)
        {
            pos_ist[j] = 0;
        }
        int i;
        for (i=0; i<M; i++)
        {
            this.moverEnd[i] = false;
        }
        for(i=0; i<V; i++)
        {
            for (j=0; j<(V-M); j++)
            {
                this.adjMatrix.getAdj()[i][j] = 0;
                //      System.out.println("("+i+", "+j+")----->"+adjMatrix.getAdj()[i][j]);
            }
        }
        for(i=0; i<t_ist.length; i++)
            t_ist[i] = 0;

        this.delivered = new ArrayList<>();
        this.distMatrix = new DirectedEdge[V][V];
    }

    public void addCounterDelivery(){
        this.counterDelivery++;
    }
    public void addCounterMovers(){
        this.counterMovers++;
    }
}
