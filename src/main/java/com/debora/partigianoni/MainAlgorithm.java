package com.debora.partigianoni;

import com.debora.partigianoni.controller.DeliveryTimeController;
import com.debora.partigianoni.model.AdjMatrix;
import com.debora.partigianoni.model.DeliveryTime;
import com.debora.partigianoni.model.DirectedEdge;
import com.debora.partigianoni.model.DistanceMatrix;
/*import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;
import org.jcp.xml.dsig.internal.dom.DOMBase64Transform;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;*/

import java.lang.reflect.Array;
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
    private List<Integer> delivered;

    public List<Integer> getDelivered() {
        return delivered;
    }

    public void setDelivered(List<Integer> delivered) {
        this.delivered = delivered;
    }

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

    public int getMaxBetweenExtremesIndex(double[] array, double min, double max)
    {
        int maxIndex = array.length;
        for (int j=0; j<array.length; j++) {
            if (array[j] >= min && array[j] <= max && !Double.isNaN(array[j])) {
//                System.out.println("TROVATO: "+j);
                maxIndex = j;
                break;
            }
        }
        if(maxIndex != array.length)
        {
        //    System.out.println("minIndex: "+minIndex);
            for (int i = 0; i < array.length; i++)
            {
                if(array[i] >= min && array[i] <= max && !Double.isNaN(array[i]))
                {
                    if (array[i] > array[maxIndex]) {
                        maxIndex = i;
                        //     System.out.println("New min index: "+minIndex);
                    }
                }
            }
        }

        return maxIndex;
    }public int getMaxBetweenExtremesIndexProva(double[] array, double min, double max)
    {
        int maxIndex = array.length;
        for (int j=0; j<array.length; j++) {
            if (array[j] >= min && array[j] <= max && !Double.isNaN(array[j])) {
//                System.out.println("TROVATO: "+j);
                maxIndex = j;
                break;
            }
        }
        if(maxIndex != array.length)
        {
        //    System.out.println("minIndex: "+minIndex);
            for (int i = 0; i < array.length; i++)
            {
                if(array[i] >= min && array[i] <= max && !Double.isNaN(array[i]))
                {
                    if (array[i] > array[maxIndex] && pos_ist[i] < pos_ist[maxIndex]) {
                        maxIndex = i;
                        //     System.out.println("New min index: "+minIndex);
                    }
                }
            }
        }

        return maxIndex;
    }


    // getting the miniumum value
    public int getMinPositiveValueIndex(double[] array) {
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


    public DirectedEdge[] getColumnFromStartRow(DirectedEdge[][] array, int index, int startRow){
        DirectedEdge[] column = new DirectedEdge[array[0].length-startRow]; // Here I assume a rectangular 2D array!
        for(int i=0; i<column.length; i++){
            column[i] = array[startRow+i][index];
        }
        return column;
    }

    public int getMinIndex(DirectedEdge[] arr)
    {
        int i;
        int minIndex = arr.length;
        for(i=0; i<arr.length; i++)
        {
            if(!Double.isNaN(arr[i].getWeight()))
            {
                minIndex = i;
                break;
            }
        }
        if(minIndex != arr.length)
        {
            for(i=0; i<arr.length; i++)
            {
                if(!Double.isNaN(arr[i].getWeight()))
                {
                    if(arr[i].getWeight() < arr[minIndex].getWeight())
                        minIndex = i;
                }
            }
        }
        return minIndex;
    }

    public void firstDelivery(String deliveryFile)
    {
        int i, j;
        DirectedEdge[] tempDist;
        int minIndex;
        List<Integer> tempKSmall;
        boolean foundNext;
        tempKSmall = DeliveryTimeController.selectKthIndex(deliveryTime.getTime(), M);//this.kSmallestDelT;
        //tempKSmall.sort(Comparator.naturalOrder());
        this.setDelivered(tempKSmall);
     //   System.out.println(tempKSmall);
        for(i=0; i<tempKSmall.size(); i++)
        {
        //    System.out.println("-------------"+tempKSmall.get(i)+"-------------");
            foundNext = false;
            this.deliveryTime = new DeliveryTime(deliveryFile);
            while(!foundNext) {
                tempDist = this.getColumnFromStartRow(distanceMatrix.getAdj(), tempKSmall.get(i), (V-M));
                minIndex = this.getMinIndex(tempDist);
            //    System.out.println(Arrays.toString(tempDist));
                if(distanceMatrix.getAdj()[V-M+minIndex][tempKSmall.get(i)].getWeight() > (deliveryTime.getTime().get(tempKSmall.get(i))+12))
                {
                    System.out.println("????????????????????????????????????");
                    //todo in tal caso per rimuovere servirebbe un tempKSmall per ogni mover. Ma dovrebbero essere sincronizzati, nel senso che se cancello un ordine perché già visitato deve essere cancellato anche negli altri.
                    //todo da migliorare questa parte, che con i dati che abbiamo non dovrebbe verificarsi.
                    continue;
                }
                else
                {
                 //   System.out.println("Min weight for "+minIndex+". Expected time: "+deliveryTime.getTime().get(tempKSmall.get(i)));
                    double sum = deliveryTime.getTime().get(tempKSmall.get(i)) +12;
                //    System.out.println(distanceMatrix.getAdj()[V-M+minIndex][tempKSmall.get(i)].getWeight()+" > "+sum+" ??? NO!!!");
                    if(distanceMatrix.getAdj()[V-M+minIndex][tempKSmall.get(i)].getWeight() <= (deliveryTime.getTime().get(tempKSmall.get(i))-3))
                    {
                        t_ist[minIndex] += (deliveryTime.getTime().get(tempKSmall.get(i))-3);
                    }
                    else if(distanceMatrix.getAdj()[V-M+minIndex][tempKSmall.get(i)].getWeight() <= (deliveryTime.getTime().get(tempKSmall.get(i))+3))
                    {
                        t_ist[minIndex] += distanceMatrix.getAdj()[V-M+minIndex][tempKSmall.get(i)].getWeight();
                    }
                    else if(distanceMatrix.getAdj()[V-M+minIndex][tempKSmall.get(i)].getWeight() <= (deliveryTime.getTime().get(tempKSmall.get(i))+6))
                    {
                        t_ist[minIndex] += distanceMatrix.getAdj()[V-M+minIndex][tempKSmall.get(i)].getWeight();
                        z1[minIndex] = 1;
                    }
                    else if(distanceMatrix.getAdj()[V-M+minIndex][tempKSmall.get(i)].getWeight() <= (deliveryTime.getTime().get(tempKSmall.get(i)+9)))
                    {
                        t_ist[minIndex] += distanceMatrix.getAdj()[V-M+minIndex][tempKSmall.get(i)].getWeight();
                        z2[minIndex] = 1;
                    }
                    else if(distanceMatrix.getAdj()[i][minIndex].getWeight() <= (deliveryTime.getTime().get(minIndex)+12))
                    {
                        t_ist[minIndex] += distanceMatrix.getAdj()[V-M+minIndex][tempKSmall.get(i)].getWeight();
                        z3[minIndex] = 1;
                    }
                    foundNext = true;
                    counterDelivery++;
                    adjMatrix.getAdj()[V-M+minIndex][tempKSmall.get(i)] = 1;
                    X[tempKSmall.get(i)] = t_ist[minIndex];
                    pos_ist[minIndex] = tempKSmall.get(i);

                    int k;
                    for(k=0; k<(V-M); k++)
                        distanceMatrix.getAdj()[k][tempKSmall.get(i)].setWeight(Double.NaN);
                    for(k=0; k<(V-M); k++)
                        distanceMatrix.getAdj()[V-M+minIndex][k].setWeight(Double.NaN);
/*                    System.out.println((V-M+minIndex)+" ---> "+tempKSmall.get(i));
                    System.out.println(Arrays.toString(X));
                    System.out.println(Arrays.toString(t_ist));*/
                }
            }
        }
    }

    public int sumInArray(int[] array)
    {
        int sum=0;
        int i;
        for(i=0; i<array.length; i++)
            sum +=array[i];
        return sum;
    }

    public int countPositiveNumbers(int[] arr)
    {
        int i;
        int sum = 0;
        for(i=0; i<arr.length; i++)
        {
            if(arr[i] >= 0)
                sum++;
        }
        return sum;
    }

    public int getMaxValueIndex(double[] arr)
    {
        int i, j;
        int max = arr.length;
        for(i=0; i<arr.length; i++)
        {
            if(!Double.isNaN(arr[i]))
            {
                max = i;
                break;
            }
        }
        if(max != arr.length)
        {
            for(j=0; j<arr.length; j++)
            {
                if(!Double.isNaN(arr[j]))
                {
                    if(arr[j] > arr[max])
                        max = j;
                }
            }

        }
        return max;
    }


    public void nextSteps()
    {
        int i, j, from;
        double[] tempArr;
        int minForPositive;
        int maxOfNegative;
        DirectedEdge[] tempEdges;
        double[] arrValues;
        int max;
        boolean found;
        int contatoreTemp = 0;
        System.out.println(counterDelivery+"---"+counterMovers);
        while(counterDelivery != (V-M) && counterMovers != M)
        {
            System.out.println("ISTANTANEO MOVERS");
            System.out.println(Arrays.toString(t_ist));
            System.out.println(Arrays.toString(pos_ist));
            System.out.println("Consegnati: "+counterDelivery);
            System.out.println("Movers terminati: "+counterMovers);
            this.getDelivered().sort(Comparator.naturalOrder());
            System.out.println(this.getDelivered());
            for (i=0; i<M; i++)
            {
                System.out.println("\n M"+i+"-> "+moverEnd[i]+". Conclusi: "+counterMovers);
             //   System.out.println(moverEnd[i]);
                if(moverEnd[i])
                {
                    //System.out.println("M"+i+" è TRUE. Continue...");
                    continue;
                }
                else{
                    from = pos_ist[i];
                 //   System.out.println("M"+i+" è FALSE. Può spostarsi altrove.");
                    tempArr = new double[V-M];


                    found = false;


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

                    int dim = tempArr.length;
                    int index = getMaxBetweenExtremesIndex(tempArr, 7, 15);
                    if(index != tempArr.length)
                        contatoreTemp++;
                    System.out.println(Arrays.toString(tempArr));
                    System.out.println("&&&&&&&&&&&&&&&&&&&&&&  "+index+". Trovati: "+contatoreTemp);

                    while(index != tempArr.length && !found)
                    {
                        System.out.println("$$$$$$ Index a cui voglio mandre qualcuno:  "+index+". Value: "+tempArr[index]);
                        tempEdges = new DirectedEdge[M];
                        arrValues = new double[M];
                        for(j=0; j<M; j++)
                            tempEdges[j] = distanceMatrix.getAdj()[pos_ist[j]][index];
                        for(j=0; j< tempEdges.length; j++)
                            arrValues[j] = t_ist[j]+distanceMatrix.getAdj()[pos_ist[j]][index].getWeight()-deliveryTime.getTime().get(index)+3;

                        System.out.println("Gli altri mover hanno i seguenti values: ");
                        System.out.println(Arrays.toString(arrValues));
                        System.out.println(Arrays.toString(pos_ist));
                        max = getMaxBetweenExtremesIndexProva(arrValues, 0,6);
                        System.out.println("Tra 0 e 6? "+max);

                        if(max != arrValues.length)
                        {
                            System.out.println("Posso andare in "+index+" da "+pos_ist[max]+"?");
                            System.out.println("SI! Value per "+pos_ist[max]+": "+arrValues[max]);
                            t_ist[max] += distanceMatrix.getAdj()[pos_ist[max]][index].getWeight();
                          //  DirectedEdge[] temp =  distanceMatrix.getAdj()[from];
                          //  System.out.println(Arrays.toString(temp));
                            System.out.println("Si arriverà in "+max+" al tempo "+t_ist[max]+". Il tempo atteso è "+deliveryTime.getTime().get(index));
                            adjMatrix.getAdj()[pos_ist[max]][index] = 1;
                            X[index] = t_ist[max];
                            pos_ist[max] = index;
                            for (int k = 0; k < (V - M); k++)
                                distanceMatrix.getAdj()[k][index].setWeight(Double.NaN);
                            counterDelivery++;
                            tempArr[index] = Double.NaN;
                            this.delivered.add(index);
                            this.delivered.sort(Comparator.naturalOrder());


                            System.out.println("Counter Delivery: "+counterDelivery+". Consegnati: ");
                            System.out.println(this.delivered);

                        }
                        else{
                            t_ist[i] += distanceMatrix.getAdj()[from][index].getWeight();
                         //   DirectedEdge[] temp =  distanceMatrix.getAdj()[from];
                         //   System.out.println(Arrays.toString(temp));
                            System.out.println("Arriva al tempo "+t_ist[i]+" e quello atteso è "+deliveryTime.getTime().get(index));
                            System.out.println(counterDelivery);
                            adjMatrix.getAdj()[from][index] = 1;
                            X[index] = t_ist[i];
                            pos_ist[i] = index;
                            for (int k = 0; k < (V - M); k++)
                                distanceMatrix.getAdj()[k][index].setWeight(Double.NaN);
                            counterDelivery++;

                            //todo Vedere in che intervallo capita il valore per modificare le z.
                            if(tempArr[index] > 6 && tempArr[index] <=9)
                                z1[index] = 1;
                            else if(tempArr[index] > 9 && tempArr[index] <= 12)
                                z2[index] = 1;
                            else if(tempArr[index] > 12 && tempArr[index] <= 15)
                                z3[index] = 1;

                            this.delivered.add(index);
                            this.delivered.sort(Comparator.naturalOrder());


                            System.out.println("Counter Delivery: "+counterDelivery+". Consegnati: ");
                            System.out.println(this.delivered);


                            found = true;
                        }
                        index = getMaxBetweenExtremesIndex(tempArr, 7, 15);
                    }
                    if(!found)
                    {
                        System.out.println("^^^"+Arrays.toString(tempArr));
                   //     System.out.println("Max value is for index "+this.getMaxValueIndex(tempArr)+". Value: "+tempArr[this.getMaxValueIndex(tempArr)]);
                        //    minOfPositive = getMinPositiveValueIndex(tempArr);

                        //parte iniziale per if sul foglio.
                        minForPositive = getMaxBetweenExtremesIndex(tempArr, 0, 6);

                        if(minForPositive != tempArr.length)
                            System.out.println("Massimo tra 0 e 6 per ordine "+minForPositive+". Valore: "+tempArr[minForPositive]);
                        if(minForPositive != tempArr.length)
                        {
                            System.out.println("--------NEW POS--------");
                            System.out.println("M"+i+": "+from+"--->"+minForPositive);
                            if (tempArr[minForPositive] > 6 && tempArr[minForPositive] <= 9) {       //TODO Basterebbe anche solo arr[minOfPositive] <= 9. Provare!
                                System.out.println("Costo 1");
                                z1[minForPositive] = 1;
                            } else if (tempArr[minForPositive] > 9 && tempArr[minForPositive] <= 12) {
                                z2[minForPositive] = 1;
                                System.out.println("Costo 2");
                            } else if (tempArr[minForPositive] > 12 && tempArr[minForPositive] <= 15) {
                                z3[minForPositive] = 1;
                                System.out.println("Costo 3");
                            }
                            t_ist[i] += distanceMatrix.getAdj()[from][minForPositive].getWeight();
                            DirectedEdge[] temp =  distanceMatrix.getAdj()[from];
                            System.out.println(Arrays.toString(temp));
                            System.out.println("Arriva al tempo "+t_ist[i]+" e quello atteso è "+deliveryTime.getTime().get(minForPositive));
                            adjMatrix.getAdj()[from][minForPositive] = 1;
                            X[minForPositive] = t_ist[i];
                            pos_ist[i] = minForPositive;
                            for (int k = 0; k < (V - M); k++)
                                distanceMatrix.getAdj()[k][minForPositive].setWeight(Double.NaN);
                            counterDelivery++;

                            //TODO CANCELLARE ALLA FINE. SOLO PER TEST E IMPIEGA TEMPO.
                            this.delivered.add(minForPositive);
                            this.delivered.sort(Comparator.naturalOrder());


                            System.out.println("Counter Delivery: "+counterDelivery+". Consegnati: ");
                            System.out.println(this.delivered);
                        }
                        else{
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
                                System.out.println("Counter Delivery: "+counterDelivery);
                            }
                        }
                    }
                }
            }
        }
        //System.out.println(this.distanceMatrix);
 //       System.out.println(Arrays.toString(X));
        System.out.println(this.counterDelivery+"....."+this.counterMovers);
        System.out.println(Arrays.toString(this.X));
        for(i=0; i<X.length; i++)
        {
            if(X[i] == 0.0)
                System.out.println(i);
        }


            System.out.println("z1: "+sumInArray(z1));
            System.out.println("z2: "+sumInArray(z2));
            System.out.println("z3: "+sumInArray(z3));
            for(i=0; i<(V-M); i++)
            {
                if (z3[i] == 1)
                    System.out.println(i);
            }
        //    System.out.println("&&&&&&&&&&&&&&&&&&&&&&&"+contatoreTemp);
    }

    public void firstDeliveryProva(String deliveryFile)
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
                //todo Da cambiare la funzione successiva.
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


    public static void main(String args[])
    {
        MainAlgorithm algorithm = new MainAlgorithm(36, 275, "deliveryTime_ist2.csv", "distanceMatrix_ist2.csv");
      //  System.out.println(algorithm.distanceMatrix);
        long startTime = System.nanoTime();
        algorithm.firstDelivery("deliveryTime_ist2.csv");
        algorithm.nextSteps();
        long endTime = System.nanoTime();

        long duration = (endTime - startTime);
        System.out.println(duration);
     //   System.out.println(algorithm.distanceMatrix);
    }
}


