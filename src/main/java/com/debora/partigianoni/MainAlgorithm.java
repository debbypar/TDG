package com.debora.partigianoni;

import com.debora.partigianoni.controller.DeliveryTimeController;
import com.debora.partigianoni.model.AdjMatrix;
import com.debora.partigianoni.model.DeliveryTime;
import com.debora.partigianoni.model.DirectedEdge;
import com.debora.partigianoni.model.DistanceMatrix;

import java.util.*;

public class MainAlgorithm {
    private double[] t_ist;     //tempo istantaneo del mover
    private double[] pos_ist;   //posizione istantanea del mover
    private List<Integer> kSmallestDelT;
    private int M;  //numero di mover
    private int V;  //Numero di vertici (compreso il numero di movers)
    private DistanceMatrix distanceMatrix;
    private DeliveryTime deliveryTime;
    private double[] X;    //istanti di completamento degli ordini
    private int[] z1;
    private int[] z2;
    private int[] z3;
    private int[] w;
    private AdjMatrix adjMatrix;
    private int counterMovers;
    private int counterDelivery;
    private boolean[] moverEnd;



    public MainAlgorithm(int movers, int vertices, String deliveryFile, String distanceFile){
        this.M = movers;
        this.V = vertices;
        this.X = new double[V-M];
        this.deliveryTime = new DeliveryTime(deliveryFile);
        this.distanceMatrix = new DistanceMatrix(distanceFile, vertices);
        this.adjMatrix = new AdjMatrix(V);
        this.z1 = new int[V-M];
        this.z2 = new int[V-M];
        this.z3 = new int[V-M];
        this.w = new int[V-M];
        this.t_ist = new double[M];
        this.pos_ist = new double[M];
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

        for (int i=0; i<M; i++)
        {
            this.moverEnd[i] = false;
        }


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

    public void firstDelivery(String deliveryFile)
    {
        int i, j;
        int posFirstMover = this.distanceMatrix.getPosFirstMover();
//        System.out.println(this.distanceMatrix);
        DirectedEdge[] tempDist;
        Minimum minimum = null ;
        List<Integer> tempKSmall;
        boolean foundNext;

    //    System.out.println("++++++++++++");
    //    System.out.println(deliveryTime.getTime());

        for(i=posFirstMover; i<posFirstMover+M; i++)
        {
            foundNext = false;
            tempKSmall = DeliveryTimeController.selectKthIndex(deliveryTime.getTime(), M);//this.kSmallestDelT;
            this.deliveryTime = new DeliveryTime(deliveryFile);         //todo Trovare modo per evitare questa cosa che rallenta.
           // System.out.println(this.kSmallestDelT.size());
            System.out.println("--------------"+i+"--------------");
            while(!foundNext)
            {
                tempDist = distanceMatrix.getAdj()[i];
                minimum = this.edgeWithMinWeight(tempDist, tempKSmall);     //prendo il j con peso minimo tra quelli dei delivery time ordinati

                if(distanceMatrix.getAdj()[i][minimum.getMin().to()].weight() > (deliveryTime.getTime().get(minimum.getMin().to())+12))
                {
                    if(tempKSmall.size() > 0)
                        tempKSmall.remove(minimum.getIndexInSmallest());
                    else{
                        System.out.println("-------------ERRORE-------------");
                        System.out.println("Prendere qualche altro tempo di delivery per la prima fase perché per un certo mover non va bene nessuno!");
                        System.out.println("-------------ERRORE-------------");
                    }
                    continue;
                }
                else
                {
                    System.out.println("Min weight for "+minimum.getMin().to()+". Expected time: "+deliveryTime.getTime().get(minimum.getMin().to()));
                    double sum = deliveryTime.getTime().get(minimum.getMin().to()) +12;
                    System.out.println(distanceMatrix.getAdj()[i][minimum.getMin().to()].weight()+" > "+sum+" ??? NO!!!");
                    if(distanceMatrix.getAdj()[i][minimum.getMin().to()].weight() <= (deliveryTime.getTime().get(minimum.getMin().to())-3))
                    {
                        double time = deliveryTime.getTime().get(minimum.getMin().to())-3;
                        System.out.println("Tempo successivo:::::: "+time+"....attuale: "+t_ist[i-posFirstMover]);
                        t_ist[i-posFirstMover] += (deliveryTime.getTime().get(minimum.getMin().to())-3);
                        foundNext = true;
                        counterDelivery++;
                    }
                    else if(distanceMatrix.getAdj()[i][minimum.getMin().to()].weight() <= (deliveryTime.getTime().get(minimum.getMin().to())+3))
                    {
                        t_ist[i-posFirstMover] += distanceMatrix.getAdj()[i][minimum.getMin().to()].weight();
                        foundNext = true;
                        counterDelivery++;
                    }
                    else if(distanceMatrix.getAdj()[i][minimum.getMin().to()].weight() <= (deliveryTime.getTime().get(minimum.getMin().to())+6))
                    {
                        t_ist[i-posFirstMover] += distanceMatrix.getAdj()[i][minimum.getMin().to()].weight();
                        z1[minimum.getMin().to()] = 1;
                        foundNext = true;
                        counterDelivery++;
                    }
                    else if(distanceMatrix.getAdj()[i][minimum.getMin().to()].weight() <= (deliveryTime.getTime().get(minimum.getMin().to())+9))
                    {
                        t_ist[i-posFirstMover] += distanceMatrix.getAdj()[i][minimum.getMin().to()].weight();
                        z2[minimum.getMin().to()] = 1;
                        foundNext = true;
                        counterDelivery++;
                    }
                    else if(distanceMatrix.getAdj()[i][minimum.getMin().to()].weight() <= (deliveryTime.getTime().get(minimum.getMin().to())+12))
                    {
                        t_ist[i-posFirstMover] += distanceMatrix.getAdj()[i][minimum.getMin().to()].weight();
                        z3[minimum.getMin().to()] = 1;
                        foundNext = true;
                        counterDelivery++;
                    }
                }
            //    foundNext = true;
            }

        }

   /*     System.out.println("*********************");
        System.out.println(Arrays.toString(t_ist));
        System.out.println(Arrays.toString(w));
        System.out.println(Arrays.toString(z1));
        System.out.println(Arrays.toString(z2));
        System.out.println(Arrays.toString(z3));*/

     //   System.out.println("---------------------");
     //   System.out.println(this.kSmallestDelT);
     //   System.out.println(this.distanceMatrix);
     //   System.out.println(this.deliveryTime.getTime());

     //   System.out.println("---------------------");
      // System.out.println(this.minWeight(temp));

    }

    public static void main(String args[])
    {
        MainAlgorithm algorithm = new MainAlgorithm(36, 275, "deliveryTime_ist2.csv", "distanceMatrix_ist2.csv");
        long startTime = System.nanoTime();
        algorithm.firstDelivery("deliveryTime_ist2.csv");
        long endTime = System.nanoTime();

        long duration = (endTime - startTime);
        System.out.println(duration);
    }
}


