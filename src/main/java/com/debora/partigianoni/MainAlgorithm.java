package com.debora.partigianoni;

import com.debora.partigianoni.controller.DeliveryTimeController;
import com.debora.partigianoni.model.AdjMatrix;
import com.debora.partigianoni.model.DeliveryTime;
import com.debora.partigianoni.model.DirectedEdge;
import com.debora.partigianoni.model.DistanceMatrix;
import org.jcp.xml.dsig.internal.dom.DOMBase64Transform;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.*;

public class MainAlgorithm {
    private double[] t_ist;     //tempo istantaneo del mover
    private int[] pos_ist;   //posizione istantanea del mover
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

        this.kSmallestDelT = DeliveryTimeController.selectKthIndex(deliveryTime.getTime(), M);

        for(i=0; i<t_ist.length; i++)
            t_ist[i] = 0;

        this.deliveryTime = new DeliveryTime(deliveryFile);
 //       System.out.println("%%%%%%%%%%%%%%%%%");
//        System.out.println(adjMatrix.getAdj());
    }

    public Minimum edgeWithMinWeight(DirectedEdge[] arr, List<Integer> kSmallest)
    {
        Minimum min = new Minimum();
        int index = 0;
        DirectedEdge minEdge = arr[kSmallest.get(0)];
        for (int i = 1; i < kSmallest.size(); i++) {
            if (arr[kSmallest.get(i)].getWeight() < minEdge.getWeight()) {
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

    // getting the miniumum value
    public static int getMinPositiveValueIndex(double[] array) {
        int minIndex = array.length;
        for (int j=0; j<array.length; j++) {
            if (array[j] >= 0) {
//                System.out.println("TROVATO: "+j);
                minIndex = j;
                break;
            }
        }

        if(minIndex != array.length)
        {
            System.out.println("minIndex: "+minIndex);
            for (int i = 0; i < array.length; i++)
            {
                if(array[i] >= 0)
                {
                    if (array[i] < array[minIndex]) {
                        minIndex = i;
                   //     System.out.println("New min index: "+minIndex);
                    }
                }
            }
        }
      //  System.out.println("minIndex returned: "+minIndex);
        return minIndex;
    }

    public static int getMaxNegativeValueIndex(double[] array) {
        int maxIndex = array.length;
        for (int j=0; j<array.length; j++) {
            if (array[j] < 0) {
                maxIndex = j;
                break;
            }
        }
        if(maxIndex != array.length)
        {
            for (int i = 0; i < array.length; i++)
            {
                if (array[i] < 0)
                {
                    if (array[i] > array[maxIndex]) {
                        maxIndex = i;
                    }
                }
            }
        }
        return maxIndex;
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
        tempKSmall = DeliveryTimeController.selectKthIndex(deliveryTime.getTime(), M);//this.kSmallestDelT;

      /*  System.out.println("%%%%%%%%%%%%%%%%%");
        System.out.println(this.adjMatrix);
        System.out.println("%%%%%%%%%%%%%%%%%");*/
        for(i=posFirstMover; i<posFirstMover+M; i++)
        {
            foundNext = false;
            this.deliveryTime = new DeliveryTime(deliveryFile);         //todo Trovare modo per evitare questa cosa che rallenta.
            System.out.println("++++++++++"+tempKSmall.size());
            System.out.println("--------------"+i+"--------------");
            while(!foundNext)
            {
                tempDist = distanceMatrix.getAdj()[i];
                minimum = this.edgeWithMinWeight(tempDist, tempKSmall);     //prendo il j con peso minimo tra quelli dei delivery time ordinati

                if(distanceMatrix.getAdj()[i][minimum.getMin().to()].getWeight() > (deliveryTime.getTime().get(minimum.getMin().to())+12))
                {
                 /*   if(tempKSmall.size() > 0)
                        tempKSmall.remove(minimum.getIndexInSmallest());
                    else{
                        System.out.println("-------------ERRORE-------------");
                        System.out.println("Prendere qualche altro tempo di delivery per la prima fase perché per un certo mover non va bene nessuno!");
                        System.out.println("-------------ERRORE-------------");
                    }*/
                 //todo in tal caso per rimuovere servirebbe un tempKSmall per ogni mover. Ma dovrebbero essere sincronizzati, nel senso che se cancello un ordine perché già visitato deve essere cancellato anche negli altri.
                    //todo da migliorare questa parte, che con i dati che abbiamo non dovrebbe verificarsi.
                    continue;
                }
                else
                {
                    System.out.println("Min weight for "+minimum.getMin().to()+". Expected time: "+deliveryTime.getTime().get(minimum.getMin().to()));
                    double sum = deliveryTime.getTime().get(minimum.getMin().to()) +12;
                    System.out.println(distanceMatrix.getAdj()[i][minimum.getMin().to()].getWeight()+" > "+sum+" ??? NO!!!");
                    if(distanceMatrix.getAdj()[i][minimum.getMin().to()].getWeight() <= (deliveryTime.getTime().get(minimum.getMin().to())-3))
                    {
                    //    double time = deliveryTime.getTime().get(minimum.getMin().to())-3;
                    //    System.out.println("Tempo successivo:::::: "+time+"....attuale: "+t_ist[i-posFirstMover]);
                        t_ist[i-posFirstMover] += (deliveryTime.getTime().get(minimum.getMin().to())-3);
                    }
                    else if(distanceMatrix.getAdj()[i][minimum.getMin().to()].getWeight() <= (deliveryTime.getTime().get(minimum.getMin().to())+3))
                    {
                        t_ist[i-posFirstMover] += distanceMatrix.getAdj()[i][minimum.getMin().to()].getWeight();
                    }
                    else if(distanceMatrix.getAdj()[i][minimum.getMin().to()].getWeight() <= (deliveryTime.getTime().get(minimum.getMin().to())+6))
                    {
                        t_ist[i-posFirstMover] += distanceMatrix.getAdj()[i][minimum.getMin().to()].getWeight();
                        z1[minimum.getMin().to()] = 1;
                    }
                    else if(distanceMatrix.getAdj()[i][minimum.getMin().to()].getWeight() <= (deliveryTime.getTime().get(minimum.getMin().to())+9))
                    {
                        t_ist[i-posFirstMover] += distanceMatrix.getAdj()[i][minimum.getMin().to()].getWeight();
                        z2[minimum.getMin().to()] = 1;
                    }
                    else if(distanceMatrix.getAdj()[i][minimum.getMin().to()].getWeight() <= (deliveryTime.getTime().get(minimum.getMin().to())+12))
                    {
                        t_ist[i-posFirstMover] += distanceMatrix.getAdj()[i][minimum.getMin().to()].getWeight();
                        z3[minimum.getMin().to()] = 1;
                    }
                    foundNext = true;
                    counterDelivery++;
                    tempKSmall.remove(minimum.getIndexInSmallest());
                    adjMatrix.getAdj()[i][minimum.getMin().to()] = 1;
                    X[minimum.getMin().to()] = t_ist[i-posFirstMover];
                    pos_ist[i-posFirstMover] = minimum.getMin().to();

                    for(int k=0; k<(V-M); k++)
                        distanceMatrix.getAdj()[k][minimum.getMin().to()].setWeight(Double.NaN);
                }
                System.out.println("====="+counterDelivery);
            //    foundNext = true;
            }

        }

        System.out.println("*********************");
        System.out.println(Arrays.toString(t_ist));
   /*     System.out.println(Arrays.toString(w));
        System.out.println(Arrays.toString(z1));
        System.out.println(Arrays.toString(z2));
        System.out.println(Arrays.toString(z3));*/

        System.out.println("---------------------");
        System.out.println(Arrays.toString(X));
     //   System.out.println(this.kSmallestDelT);
//        System.out.println(this.distanceMatrix);
     /*   for(int z=0; z< V; z++)
            for(int t=0; t< V-M; t++)
            {
                if(this.adjMatrix.getAdj()[z][t] == 1)
                    System.out.println("TROVATOOOOOOO ---> "+z+", "+t);
                    System.out.println(this.adjMatrix.getAdj()[z][t]);
            }*/

     //   System.out.println(this.deliveryTime.getTime());

     //   System.out.println("---------------------");
      // System.out.println(this.minWeight(temp));

    }



    public void nextSteps()
    {
        int i, j, from;
        double[] tempArr;
        int minOfPositive;
        int maxOfNegative;
        System.out.println(counterDelivery+"---"+counterMovers);
        while(counterDelivery != (V-M) && counterMovers != M)
        {
            for (i=0; i<M; i++)
            {
                System.out.println("M"+i+"-> "+moverEnd[i]+". Conclusi: "+counterMovers);
             //   System.out.println(moverEnd[i]);
                if(moverEnd[i])
                {
                    System.out.println("M"+i+" è TRUE. Continue...");
                    continue;
                }
                else{
                    from = pos_ist[i];
                    System.out.println("M"+i+" è FALSE. Può spostarsi altrove.");
                    tempArr = new double[V-M];
                    System.out.println("M"+i+" si trova in "+from+" al tempo "+t_ist[i]);
                    //System.out.println(t_ist[i]);
                    for(j=0; j<(V-M); j++)
                    {
                        if(j != i)
                        {
                //            System.out.println("Andare in "+j+" ha costo "+distanceMatrix.getAdj()[from][j].getWeight()+" e il tempo di consegna ideale è "+deliveryTime.getTime().get(j)+".");
                            tempArr[j] = t_ist[i]+distanceMatrix.getAdj()[from][j].getWeight() - deliveryTime.getTime().get(j)+3;
                    //        System.out.println(tempArr[j]);
                        }
                        else tempArr[j] = Double.NaN;
                    }
                    System.out.println("^^^"+Arrays.toString(tempArr));
                    minOfPositive = getMinPositiveValueIndex(tempArr);
                    System.out.println("Minimo positivo calcolato: "+minOfPositive+". Lunghezza: "+tempArr.length);
                    if(minOfPositive == tempArr.length || tempArr[minOfPositive] > 15)
                    {
                        System.out.println("Non ottengo nulla con minOfPositive.");// tempArr["+minOfPositive+"] = "+tempArr[minOfPositive]);
                        maxOfNegative = getMaxNegativeValueIndex(tempArr);
                        if(maxOfNegative == tempArr.length)
                        {
                            System.out.println("Non c'è alcun Negative!");
                            //moverEnd a true, il mover non può più fare nulla.
                            moverEnd[i] = true;
                            counterMovers++;
                            System.out.println("M"+i+" HA FINITO. Mover che hanno terminato: "+counterMovers);
                        }
                        else{
                            System.out.println("--------NEW NEG--------");
                            System.out.println("M"+i+": "+from+"--->"+maxOfNegative);
                            System.out.println("Massimo tra i negativi trovato! tempArr["+maxOfNegative+"] = "+tempArr[maxOfNegative]);
                            t_ist[i] += distanceMatrix.getAdj()[from][maxOfNegative].getWeight()-(tempArr[maxOfNegative]);//deliveryTime.getTime().get(maxOfNegative)+tempArr[maxOfNegative];
                            System.out.println("Arriva al tempo "+t_ist[i]+" e quello atteso è "+deliveryTime.getTime().get(maxOfNegative));
                            adjMatrix.getAdj()[from][maxOfNegative] = 1;
                            X[maxOfNegative] =t_ist[i];
                            pos_ist[i] = maxOfNegative;
                            for(int k=0; k<(V-M); k++)
                                distanceMatrix.getAdj()[k][maxOfNegative].setWeight(Double.NaN);
                            counterDelivery++;
                            System.out.println("COUNTER DELIVERY: "+counterDelivery);
                        }
                    }
                    else{
                        //Ci sono valori positivi < 15 in tempArr.
                        //if (tempArr[minOfPositive] >= 0 && tempArr[minOfPositive] <= 6) {

                        System.out.println("--------NEW POS--------");
                        System.out.println("M"+i+": "+from+"--->"+minOfPositive);
                        if (tempArr[minOfPositive] > 6 && tempArr[minOfPositive] <= 9) {       //TODO Basterebbe anche solo arr[minOfPositive] <= 9. Provare!
                            System.out.println("Costo 1");
                            z1[minOfPositive] = 1;
                        } else if (tempArr[minOfPositive] > 9 && tempArr[minOfPositive] <= 12) {
                            z2[minOfPositive] = 1;
                            System.out.println("Costo 2");
                        } else if (tempArr[minOfPositive] > 12 && tempArr[minOfPositive] <= 15) {
                            z3[minOfPositive] = 1;
                            System.out.println("Costo 3");
                        }
                        t_ist[i] += distanceMatrix.getAdj()[from][minOfPositive].getWeight();
                        System.out.println("Arriva al tempo "+t_ist[i]+" e quello atteso è "+deliveryTime.getTime().get(minOfPositive));
                        adjMatrix.getAdj()[from][minOfPositive] = 1;
                        X[minOfPositive] = t_ist[i];
                        pos_ist[i] = minOfPositive;
                        for (int k = 0; k < (V - M); k++)
                            distanceMatrix.getAdj()[k][minOfPositive].setWeight(Double.NaN);
                        counterDelivery++;
                        System.out.println("COUNTER DELIVERY: "+counterDelivery);
                    }
                }
            }
        }
        //System.out.println(this.distanceMatrix);
 //       System.out.println(Arrays.toString(X));
        System.out.println(this.counterDelivery+"....."+this.counterMovers);
    }

    public static void main(String args[])
    {
        MainAlgorithm algorithm = new MainAlgorithm(36, 275, "deliveryTime_ist2.csv", "distanceMatrix_ist2.csv");
        long startTime = System.nanoTime();
        algorithm.firstDelivery("deliveryTime_ist2.csv");
        algorithm.nextSteps();
        long endTime = System.nanoTime();

        long duration = (endTime - startTime);
        System.out.println(duration);
    }
}


