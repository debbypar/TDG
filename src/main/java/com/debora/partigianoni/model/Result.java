package com.debora.partigianoni.model;

import com.debora.partigianoni.MainAlgorithm;

public class Result {
    private MainAlgorithm algInfo;
    private int objective;
    private double timeExecution;
    private double[] X;    //istanti di completamento degli ordini
    private int[] z1;
    private int[] z2;
    private int[] z3;
    private int[] w;
    private AdjMatrix adjMatrix;

    public MainAlgorithm getAlgInfo() {
        return algInfo;
    }

    public void setAlgInfo(MainAlgorithm algInfo) {
        this.algInfo = algInfo;
    }

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
}
