package com.debora.partigianoni;

import com.debora.partigianoni.controller.DeliveryTimeController;
import com.debora.partigianoni.model.AdjMatrix;
import com.debora.partigianoni.model.DeliveryTime;
import com.debora.partigianoni.model.DistanceMatrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainAlgorithm {
    private double[] t_ist;
    private List<Integer> kSmallestDelT;
    private int M;  //numero di mover
    private int V;  //Numero di vertici (compreso il numero di movers)
    private DistanceMatrix distanceMatrix;
    private DeliveryTime deliveryTime;
    private double[] X;    //istanti di completamento degli ordini
    private boolean[] z1;
    private boolean[] z2;
    private boolean[] z3;
    private boolean[] w;
    private AdjMatrix adjMatrix;




    public MainAlgorithm(int movers, int vertices, String deliveryFile, String distanceFile){
        this.M = movers;
        this.V = vertices;
        this.X = new double[V-M];
        this.deliveryTime = new DeliveryTime(deliveryFile);
        this.distanceMatrix = new DistanceMatrix(distanceFile, vertices);
        this.adjMatrix = new AdjMatrix(V);
        this.z1 = new boolean[V-M];
        this.z2 = new boolean[V-M];
        this.z3 = new boolean[V-M];
        this.w = new boolean[V-M];
        this.t_ist = new double[M];

        this.kSmallestDelT = DeliveryTimeController.selectKthIndex(deliveryTime.getTime(), M);

        for(int i=0; i<t_ist.length; i++)
            t_ist[i] = 0;

    }

    public void firstDelivery()
    {
        int i;
        for(i=0; i<M; i++)
        {

        }
       System.out.println(this.kSmallestDelT);
       System.out.println(this.distanceMatrix);
       System.out.println(this.distanceMatrix.getPosFirstMover());

    }

    public static void main(String args[])
    {
        MainAlgorithm algorithm = new MainAlgorithm(36, 275, "deliveryTime_ist2.csv", "distanceMatrix_ist2.csv");
        algorithm.firstDelivery();
    }
}


