package com.debora.partigianoni;

import com.debora.partigianoni.controller.DeliveryTimeController;
import com.debora.partigianoni.model.AdjMatrix;
import com.debora.partigianoni.model.DeliveryTime;
import com.debora.partigianoni.model.DirectedEdge;
import com.debora.partigianoni.model.DistanceMatrix;

import java.util.ArrayList;
import java.util.Iterator;
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

        this.deliveryTime = new DeliveryTime(deliveryFile);

    }

    public Minimum edgeWithMinWeight(DirectedEdge[] arr, List<Integer> kSmallest)
    {
        Minimum min = new Minimum();
        int index = 0;
        DirectedEdge minEdge = arr[kSmallest.get(0)];
        for (int i = 1; i < kSmallest.size(); i++) {
            if (arr[kSmallest.get(i)].weight() < minEdge.weight()) {
                minEdge = arr[kSmallest.get(i)];
                index = i;
            }
        }
        min.setMin(minEdge);
        min.setIndexInSmallest(index);
        return min;
    }

    public class Minimum{
        DirectedEdge min;
        int indexInSmallest;

        public Minimum(){
            //this.min = new DirectedEdge();
            this.indexInSmallest = 0;
        }

        public DirectedEdge getMin() {
            return min;
        }

        public void setMin(DirectedEdge min) {
            this.min = min;
        }

        public int getIndexInSmallest() {
            return indexInSmallest;
        }

        public void setIndexInSmallest(int indexInSmallest) {
            this.indexInSmallest = indexInSmallest;
        }
    }

    public void firstDelivery()
    {
        int i, j;
        int posFirstMover = this.distanceMatrix.getPosFirstMover();
        System.out.println(this.distanceMatrix);
        DirectedEdge[] tempDist;
        Minimum minimum = null ;
        List<Integer> tempKSmall;
        boolean foundNext;

        for(i=posFirstMover; i<posFirstMover+M; i++)
        {
            foundNext = false;
            tempKSmall = DeliveryTimeController.selectKthIndex(deliveryTime.getTime(), M);//this.kSmallestDelT;

            System.out.println(this.kSmallestDelT.size());
            System.out.println("--------------"+i+"--------------");
            while(!foundNext)
            {
                tempDist = distanceMatrix.getAdj()[i];
                int size = tempKSmall.size();
                for(j = 0; j< size; j++)
                {
                //    System.out.println("("+i+", "+tempKSmall.get(j)+"): "+tempDist[tempKSmall.get(j)].weight());
                    minimum = this.edgeWithMinWeight(tempDist, tempKSmall);
                }
                if(distanceMatrix.getAdj()[i][minimum.getMin().to()].weight() > (deliveryTime.getTime().get(minimum.getMin().to())+12))
                {
//                    System.out.println("PRIMA: "+tempKSmall.size());
                    if(tempKSmall.size() > 0)
                        tempKSmall.remove(minimum.getIndexInSmallest());
                    else{
                        System.out.println("-------------ERRORE-------------");
                        System.out.println("Prendere qualche altro tempo di delivery per la prima fase perché per un certo mover non va bene nessuno!");
                        System.out.println("-------------ERRORE-------------");
                    }
//                    System.out.println("DOPO: "+tempKSmall.size());
                    continue;
                }
                else
                {
                    System.out.println("£££  "+minimum.getMin().to());
                    System.out.println(deliveryTime.getTime().get(minimum.getMin().to()));
                    double sum = deliveryTime.getTime().get(minimum.getMin().to()) +12;
                    System.out.println(distanceMatrix.getAdj()[i][minimum.getMin().to()].weight()+" > "+sum+" ??? NO!!!");
                }
                foundNext = true;
            }

        }

        System.out.println(this.kSmallestDelT);
     //   System.out.println("---------------------");
     //   System.out.println(this.distanceMatrix);
        System.out.println(this.deliveryTime.getTime());

     //   System.out.println("---------------------");
      // System.out.println(this.minWeight(temp));

    }

    public static void main(String args[])
    {
        MainAlgorithm algorithm = new MainAlgorithm(36, 275, "deliveryTime_ist2.csv", "distanceMatrix_ist2.csv");
        algorithm.firstDelivery();
    }
}


