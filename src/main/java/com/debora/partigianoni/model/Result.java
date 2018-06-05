package com.debora.partigianoni.model;

import com.debora.partigianoni.MainAlgorithm;

public class Result {
    private MainAlgorithm algInfo;
    private int objective;
    private double timeExecution;

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
}
