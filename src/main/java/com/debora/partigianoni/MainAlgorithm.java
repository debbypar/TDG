package com.debora.partigianoni;

import com.debora.partigianoni.controller.CSVHandler;
import com.debora.partigianoni.controller.DeliveryTimeController;
import com.debora.partigianoni.model.*;
/*import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;
import org.jcp.xml.dsig.internal.dom.DOMBase64Transform;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;*/

import java.io.IOException;
import java.lang.reflect.Array;
import java.security.AlgorithmConstraints;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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

    public MainAlgorithm(int movers, int vertices, String deliveryFile, String distanceFile, String folderIndex){
        this.M = movers;
        this.V = vertices;
        this.X = new double[V-M];
        this.deliveryTime = new DeliveryTime(deliveryFile);
        this.distanceMatrix = new DistanceMatrix(folderIndex, distanceFile, vertices);
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
    }

    public int getMinBetweenExtremesIndex(double[] array, double min, double max)
    {
        int minIndex = array.length;
        for (int j=0; j<array.length; j++) {
            if (array[j] >= min && array[j] <= max && !Double.isNaN(array[j])) {
//                System.out.println("TROVATO: "+j);
                minIndex = j;
                break;
            }
        }
        if(minIndex != array.length)
        {
            //    System.out.println("minIndex: "+minIndex);
            for (int i = 0; i < array.length; i++)
            {
                if(array[i] >= min && array[i] <= max && !Double.isNaN(array[i]))
                {
                    if (array[i] < array[minIndex]) {
                        minIndex = i;
                        //     System.out.println("New min index: "+minIndex);
                    }
                }
            }
        }

        return minIndex;
    }

    public int getMaxBetweenExtremesIndexProva(double[] array, double min, double max)
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
    public int getMaxBetweenExtremesIndexProva2(double[] array, double min, double max)
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
                    if (array[i] > array[maxIndex] && deliveryTime.getTime().get(i) < deliveryTime.getTime().get(i)) {
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

    public int getMinDelTimeForNegativeOrders(double[] array) {
        int minIndex = array.length;
        for (int j=0; j<array.length; j++) {
            if (array[j] < 0) {
                minIndex = j;
                break;
            }
        }
        if(minIndex != array.length)
        {
            for (int i = 0; i < array.length; i++)
            {
                if (array[i] < 0)
                {
                    if (this.deliveryTime.getTime().get(i) < this.deliveryTime.getTime().get(minIndex)) {
                        minIndex = i;
                    }
                }
            }
        }
        return minIndex;
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
        System.out.println(tempKSmall);
        for(j=0; j<M; j++)
        {
            System.out.println("M"+j+", 26 --> "+distanceMatrix.getAdj()[V-M+j][26]);
        }
        for(i=0; i<M; i++)
        {
            System.out.println("-------------"+tempKSmall.get(i)+"-------------");
            foundNext = false;
            this.deliveryTime = new DeliveryTime(deliveryFile);
            while(!foundNext) {
                tempDist = this.getColumnFromStartRow(distanceMatrix.getAdj(), tempKSmall.get(i), (V-M));
                System.out.println(Arrays.toString(tempDist));
                minIndex = this.getMinIndex(tempDist);
                System.out.println("MinIndex: "+minIndex+". Value: "+tempDist[minIndex]);
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
                        System.out.println("Z1: "+Arrays.toString(z1));
                    }
                    else if(distanceMatrix.getAdj()[V-M+minIndex][tempKSmall.get(i)].getWeight() <= (deliveryTime.getTime().get(tempKSmall.get(i)+9)))
                    {
                        t_ist[minIndex] += distanceMatrix.getAdj()[V-M+minIndex][tempKSmall.get(i)].getWeight();
                        z2[minIndex] = 1;
                        System.out.println("Z2: "+Arrays.toString(z2));
                    }
                    else if(distanceMatrix.getAdj()[i][minIndex].getWeight() <= (deliveryTime.getTime().get(minIndex)+12))
                    {
                        t_ist[minIndex] += distanceMatrix.getAdj()[V-M+minIndex][tempKSmall.get(i)].getWeight();
                        z3[minIndex] = 1;
                        System.out.println("Z3: "+Arrays.toString(z3));
                    }
                    foundNext = true;
                    counterDelivery++;
                    System.out.println("^^^^^^^^^^^^^^^POS IN ADJ - first: ["+(V-M+minIndex)+"]["+tempKSmall.get(i)+"]^^^^^^^^^^^^^^^");
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

    public int[] shuffleArray(int[] arr)
    {
        // If running on Java 6 or older, use `new Random()` on RHS here
        Random rnd = ThreadLocalRandom.current();
        for (int i = arr.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);
            // Simple swap
            int a = arr[index];
            arr[index] = arr[i];
            arr[i] = a;
        }
        return arr;
    }

    public int[] moveArrElementsOneStep(int[] arr)
    {
        int i;
        int start = arr[0];
        for(i=0; i<arr.length-1; i++)
            arr[i] = arr[i+1];
        arr[arr.length-1] = start;
        return  arr;
    }

    /*public int[] getRandomArrayOfMovers(int[] movers1)
    {
        int i;
        int[] movers = new int[M];
        for(i=0; i<M; i++)
            movers[i] = i;

    }*/

    public ArrayList<Integer> getValueInArrayIndex(double[] arr, double value)
    {
        int i;
        ArrayList<Integer> arrList = new ArrayList<>();
        for(i=0; i<arr.length; i++)
        {
            if(arr[i] == value)
                arrList.add(i);
        }
        return arrList;
    }

    public int getOrderWithMinDeliveryTime(ArrayList<Integer> arrList){
        int i, min;
        min = arrList.get(0);
        for(i=1; i<arrList.size(); i++)
        {
            if(deliveryTime.getTime().get(arrList.get(i)) < deliveryTime.getTime().get(min))
                min = arrList.get(i);
        }
        return min;
    }

    public void firstDelivery3(int[] arr){

        int i, j;
        double[] tempArr;
        int index1, index;
        this.delivered = new ArrayList<>();
        for(i=0; i<M; i++)
        {
            tempArr = new double[V-M];

            for(j=0; j<(V-M); j++)
            {
                if(j != arr[i])
                {
                    tempArr[j] = t_ist[arr[i]]+distanceMatrix.getAdj()[V-M+arr[i]][j].getWeight() - deliveryTime.getTime().get(j)+3;
                }
                else tempArr[j] = Double.NaN;
            }

            //A VOLTE è MEGLIO CON MIN, ALTRE CON MAX. SCEGLIEREEEEEEE
            index = getMinBetweenExtremesIndex(tempArr, 0, 6);
            if(index != tempArr.length)
            {
                t_ist[arr[i]] += distanceMatrix.getAdj()[V-M+arr[i]][index].getWeight();
                adjMatrix.getAdj()[V-M+arr[i]][index] = 1;
                X[index] = t_ist[arr[i]];
                pos_ist[arr[i]] = index;
                for(int k=0; k<(V); k++)
                    distanceMatrix.getAdj()[k][index].setWeight(Double.NaN);
                for(int k=0; k<(V-M); k++)
                    distanceMatrix.getAdj()[V-M+arr[i]][k].setWeight(Double.NaN);
                counterDelivery++;
                this.delivered.add(index);
            }
            else{
                index = getMaxNegativeValueIndex(tempArr);
              //  System.out.println(tempArr[index]);
             //   index = getOrderWithMinDeliveryTime(getValueInArrayIndex(tempArr, tempArr[index1]));
                //index = getMinDelTimeForNegativeOrders(tempArr);
                if(index != tempArr.length){

                    t_ist[arr[i]] += distanceMatrix.getAdj()[V-M+arr[i]][index].getWeight()-(tempArr[index]);            //modificato da from
                    adjMatrix.getAdj()[V-M+arr[i]][index] = 1;                                                              //modificato da from
                    X[index] =t_ist[arr[i]];
                    pos_ist[arr[i]] = index;
                    for(int k=0; k<(V); k++)
                        distanceMatrix.getAdj()[k][index].setWeight(Double.NaN);
                    for(int k=0; k<(V-M); k++)
                        distanceMatrix.getAdj()[V-M+arr[i]][k].setWeight(Double.NaN);
                    counterDelivery++;
                    this.delivered.add(index);
                }
                else{
                    System.out.println("Cercare tra 6 e 15. 000000000000000000000000000000000000000");
                }
            }
            int k;
            for(k=0; k<(V-M); k++)
                distanceMatrix.getAdj()[k][index].setWeight(Double.NaN);
            for(k=0; k<(V-M); k++)
                distanceMatrix.getAdj()[V-M+arr[i]][k].setWeight(Double.NaN);
        }
    }

    public void firstDelivery2(int[] arr){

        int i, j;
        double[] tempArr, otherValues;
        int indexPos, indexNeg;
        this.delivered = new ArrayList<>();

        boolean[] firstDelMade = new boolean[M];

        for(i=0; i<firstDelMade.length; i++)
            firstDelMade[i] = false;

        /*for(i=0; i<M; i++)
        {
            System.out.println(distanceMatrix.getAdj()[V-M+i][18]);
            System.out.println(distanceMatrix.getAdj()[V-M+i][45]);
            System.out.println(distanceMatrix.getAdj()[V-M+i][62]);
        }*/

    /*    int[] movers = new int[M];
        for(i=0; i<M; i++)
            movers[i] = i;*/

        for(i=0; i<M; i++)
        {
//            System.out.println("--------M"+i+"--------");
//            System.out.println("Tempo istantaneo: "+t_ist[i]);
            //System.out.println("-----M"+arr[i]+"-----");
            tempArr = new double[V-M];

            for(j=0; j<(V-M); j++)
            {
                if(j != arr[i])
                {
                    tempArr[j] = t_ist[arr[i]]+distanceMatrix.getAdj()[V-M+arr[i]][j].getWeight() - deliveryTime.getTime().get(j)+3;
                }
                else tempArr[j] = Double.NaN;
            }
//            System.out.println("***");
//            System.out.println(Arrays.toString(tempArr));

            /*System.out.println(Arrays.toString(tempArr));
            System.out.println("Value per 18: "+tempArr[18]);
            System.out.println("Value per 45: "+tempArr[45]);
            System.out.println("Value per 62: "+tempArr[62]);*/
            while(t_ist[arr[i]] == 0.0)
            {
//                System.out.println("Mover "+i+" da assegnare.");
                //A VOLTE è MEGLIO CON MIN, ALTRE CON MAX. SCEGLIEREEEEEEE
                indexPos = getMinBetweenExtremesIndex(tempArr, 0, 6);
                //indexNeg = getMaxNegativeValueIndex(tempArr);
                //indexNeg = getMinDelTimeForNegativeOrders(tempArr);
                indexNeg = getMaxNegativeValueIndex(tempArr);

                if(indexPos != tempArr.length)// && tempArr[index]==0.0)
                {
               //     System.out.println("Min tra i positivi per ordine "+indexPos+". Value: "+tempArr[indexPos]);
//                System.out.println("SI tra 0 e 6. Index: "+index+"..."+tempArr[index]);
                    //modifico tutto per [M1, index]
//                System.out.println("Peso da mover "+i+" a "+index+": "+distanceMatrix.getAdj()[V-M+i][index].getWeight());

                    if(tempArr[indexPos] != 0.0)
                    {
                    //    System.out.println("Min tra i positivi NON pari a 0.");
                        //vedo se qualche altro movers ci può andare
                        otherValues = new double[M];
                        for(int z=0; z<otherValues.length; z++)
                            otherValues[z] = t_ist[z]+distanceMatrix.getAdj()[V-M+z][indexPos].getWeight()-deliveryTime.getTime().get(indexPos)+3;

                    //    System.out.println("Gli altri mover hanno i seguenti valori per andare all'ordine "+indexPos+":");
                    //    System.out.println(Arrays.toString(otherValues));
                        int foundZero = getMinBetweenExtremesIndex(otherValues, 0, 0);
                        if(foundZero != otherValues.length)
                        {
                    //        System.out.println("Ho trovato value a 0 per l'ordine "+indexPos);
                    //        System.out.println("Mover: "+foundZero+". Value: "+otherValues[foundZero]);
                    //        System.out.println("Tempo istantaneo prima: "+t_ist[foundZero]);
                            t_ist[foundZero] += distanceMatrix.getAdj()[V-M+foundZero][indexPos].getWeight();
                    //        System.out.println("Tempo istantaneo dopo: "+t_ist[foundZero]);
                            adjMatrix.getAdj()[V-M+foundZero][indexPos] = 1;
                            X[indexPos] = t_ist[foundZero];
                            pos_ist[foundZero] = indexPos;
                            for(int k=0; k<V; k++)
                                distanceMatrix.getAdj()[k][indexPos].setWeight(Double.NaN);
                            for(int k=0; k<(V-M); k++)
                                distanceMatrix.getAdj()[V-M+foundZero][k].setWeight(Double.NaN);
                            counterDelivery++;
                            this.delivered.add(indexPos);
                            firstDelMade[foundZero] = true;
                            tempArr[indexPos] = Double.NaN;
                        }
                        else{
                        //    System.out.println("NON ho trovato value a 0 per ordine "+indexPos);
                        //    System.out.println("Prendo massimo tra i negativi.");
                        //    System.out.println("I: "+arr[i]);
                            int maxNegative = getMaxNegativeValueIndex(otherValues);
                            if(maxNegative != otherValues.length && otherValues[maxNegative] > -3.0)
                            {
                        //        System.out.println("Massimo tra i negativi trovato: "+otherValues[maxNegative]+" per mover "+maxNegative);
                                //t_ist[i] += distanceMatrix.getAdj()[pos_ist[i]][maxNegative].getWeight()-(tempArr[maxNegative]);            //modificato da from
                                t_ist[maxNegative] += deliveryTime.getTime().get(indexPos)-3;
                                adjMatrix.getAdj()[V-M+maxNegative][indexPos] = 1;
                                X[indexPos] = t_ist[maxNegative];
                                pos_ist[maxNegative] = indexPos;
                                for(int k=0; k<V; k++)
                                    distanceMatrix.getAdj()[k][indexPos].setWeight(Double.NaN);
                                for(int k=0; k<(V-M); k++)
                                    distanceMatrix.getAdj()[V-M+maxNegative][k].setWeight(Double.NaN);
                                counterDelivery++;
                                this.delivered.add(indexPos);
                                firstDelMade[maxNegative] = true;
                                tempArr[indexPos] = Double.NaN;
                            }else{
                        /*        System.out.println("I: "+arr[i]);
                                System.out.println("Nessun negativo.");
                                System.out.println("C'è qualcosa di minore di "+tempArr[indexPos]+"???");*/
                                int minOfPrec = getMinBetweenExtremesIndex(otherValues, 0, tempArr[indexPos]);
                                if(minOfPrec != otherValues.length)
                                {
                        //            System.out.println("SI, c'è per il mover "+minOfPrec);
                        //            System.out.println("Tempo istantaneo prima: "+t_ist[minOfPrec]+". Costo per "+indexPos+": "+distanceMatrix.getAdj()[V-M+minOfPrec][indexPos].getWeight());
                                    t_ist[minOfPrec] += distanceMatrix.getAdj()[V-M+minOfPrec][indexPos].getWeight();
                        //            System.out.println("Tempo istantaneo dopo: "+t_ist[minOfPrec]+". Tempo atteso: "+deliveryTime.getTime().get(indexPos));
                                    adjMatrix.getAdj()[V-M+minOfPrec][indexPos] = 1;
                                    X[indexPos] = t_ist[minOfPrec];
                                    pos_ist[minOfPrec] = indexPos;
                                    for(int k=0; k<(V); k++)
                                        distanceMatrix.getAdj()[k][indexPos].setWeight(Double.NaN);
                                    for(int k=0; k<(V-M); k++)
                                        distanceMatrix.getAdj()[V-M+minOfPrec][k].setWeight(Double.NaN);
                                    counterDelivery++;
                                    this.delivered.add(indexPos);
                                    firstDelMade[minOfPrec] = true;
                                    tempArr[indexPos] = Double.NaN;
                        //            System.out.println("Il mover "+minOfPrec+" ha preso l'ordine "+indexPos);
                                }
                                else System.out.println("E CHE CAZZO!!!!!");
                            }
                        }
                    }
                    else{
                    /*    System.out.println("Min tra i positivi pari a 0.");
                        System.out.println("Mover "+arr[i]+" si prende l'ordine "+indexPos);
                        System.out.println("M["+(V-M+arr[i])+"]["+indexPos+"] -> "+distanceMatrix.getAdj()[V-M+arr[i]][indexPos].getWeight());
                        System.out.println("Tempo istantaneo prima: "+t_ist[arr[i]]);*/
                        t_ist[arr[i]] += distanceMatrix.getAdj()[V-M+arr[i]][indexPos].getWeight();
                    //    System.out.println("Tempo istantaneo dopo: "+t_ist[i]+". Tempo atteso: "+deliveryTime.getTime().get(indexPos));
                        adjMatrix.getAdj()[V-M+arr[i]][indexPos] = 1;
                        X[indexPos] = t_ist[arr[i]];
                        pos_ist[arr[i]] = indexPos;
                        for(int k=0; k<(V); k++)
                            distanceMatrix.getAdj()[k][indexPos].setWeight(Double.NaN);
                        for(int k=0; k<(V-M); k++)
                            distanceMatrix.getAdj()[V-M+arr[i]][k].setWeight(Double.NaN);
                        counterDelivery++;
                        this.delivered.add(indexPos);
                        firstDelMade[i] = true;
//                System.out.println("Nuovo tempo: "+t_ist[i]+". Tempo atteso: "+deliveryTime.getTime().get(index));
                    }
                }
                else{
                //    index = getMaxNegativeValueIndex(tempArr);
                 //   System.out.println("Cerco tra i negativi.");
                    if(indexNeg != tempArr.length){
//                    System.out.println("SI Negativi. Index: "+index+"... "+tempArr[index]);
                //        System.out.println("Mover "+arr[i]+" si prende l'ordine "+indexNeg+" con value "+tempArr[indexNeg]);
                //        System.out.println("Tempo istantaneo prima: "+t_ist[arr[i]]);
                        t_ist[arr[i]] += distanceMatrix.getAdj()[V-M+arr[i]][indexNeg].getWeight()-(tempArr[indexNeg]);            //modificato da from
                //        System.out.println("Tempo istantaneo dopo: "+t_ist[arr[i]]);
                        adjMatrix.getAdj()[V-M+arr[i]][indexNeg] = 1;                                                              //modificato da from
                        X[indexNeg] =t_ist[arr[i]];
                        pos_ist[arr[i]] = indexNeg;
                        for(int k=0; k<(V); k++)
                            distanceMatrix.getAdj()[k][indexNeg].setWeight(Double.NaN);
                        for(int k=0; k<(V-M); k++)
                            distanceMatrix.getAdj()[V-M+arr[i]][k].setWeight(Double.NaN);
                        counterDelivery++;
                        this.delivered.add(indexNeg);
                        firstDelMade[arr[i]] = true;
//                    System.out.println("Nuovo tempo: "+t_ist[i]);
                    }
                    else{
                        System.out.println("Cercare tra 6 e 15. 000000000000000000000000000000000000000");
                    }
                }
//                System.out.println("iiiiiii: "+i);
            }
        }
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
/*        System.out.println(counterDelivery+"---"+counterMovers);
        System.out.println("z1: "+sumInArray(z1));
        System.out.println("z2: "+sumInArray(z2));
        System.out.println("z3: "+sumInArray(z3));*/
        while(counterDelivery != (V-M) && counterMovers != M)
        {
/*            System.out.println("ISTANTANEO MOVERS");
            System.out.println(Arrays.toString(t_ist));
            System.out.println(Arrays.toString(pos_ist));
            System.out.println("Consegnati: "+counterDelivery);
            System.out.println("Movers terminati: "+counterMovers);*/
            this.getDelivered().sort(Comparator.naturalOrder());
//            System.out.println("Ordini consegnati: "+this.getDelivered());
            for (i=0; i<M; i++)
            {
//                System.out.println("\n M"+i+"-> "+moverEnd[i]+". Conclusi: "+counterMovers);
                if(moverEnd[i])
                {
                    continue;
                }
                else{
//                    from = pos_ist[i];
                    tempArr = new double[V-M];


                    found = false;


//                    System.out.println("M"+i+" si trova in "+pos_ist[i]+" al tempo "+t_ist[i]);
                    for(j=0; j<(V-M); j++)
                    {
                        if(j != pos_ist[i])
                        {
                            tempArr[j] = t_ist[i]+distanceMatrix.getAdj()[pos_ist[i]][j].getWeight() - deliveryTime.getTime().get(j)+3;
                        }
                        else tempArr[j] = Double.NaN;
                    }

                    int dim = tempArr.length;
                    int index = getMaxBetweenExtremesIndex(tempArr, 7, 15);
                    if(index != tempArr.length)
                        contatoreTemp++;
//                    System.out.println("òòòòòòòòòòò "+Arrays.toString(tempArr));

                    while(index != tempArr.length && !found)
                    {
//                        System.out.println("MOVER "+i+"..... STA IN POSIZIONE "+pos_ist[i]+" al tempo "+t_ist[i]);
//                        System.out.println("$$$$$$ Index a cui voglio mandare qualcuno:  "+index+". Value: "+tempArr[index]);
                        tempEdges = new DirectedEdge[M];
                        arrValues = new double[M];
                        for(j=0; j<M; j++)
                            tempEdges[j] = distanceMatrix.getAdj()[pos_ist[j]][index];
                        for(j=0; j< tempEdges.length; j++)
                            arrValues[j] = t_ist[j]+distanceMatrix.getAdj()[pos_ist[j]][index].getWeight()-deliveryTime.getTime().get(index)+3;

/*                        System.out.println("Gli altri mover hanno i seguenti values per andare in "+index);
                        System.out.println(Arrays.toString(arrValues));
                        System.out.println(Arrays.toString(pos_ist));
                        System.out.println(Arrays.toString(t_ist));*/
                     //   max = getMaxBetweenExtremesIndex(arrValues, 0,6);
                        //todo sostituire variabile max con min perché ora è il minimo.
                        max = getMinBetweenExtremesIndex(arrValues, 0,6);
//                        System.out.println("Tra 0 e 6? "+max);

                        if(max != arrValues.length)
                        {
                            int prova = getMaxNegativeValueIndex(arrValues);
//                                System.out.println("============================ "+prova);//+" --> "+arrValues[prova]);

/*                            System.out.println("Posso andare in "+index+" da "+pos_ist[max]+"?");
                            System.out.println("SI! Value da "+pos_ist[max]+" a "+index+": "+arrValues[max]);
                            System.out.println("Mover "+max+" si trova in "+pos_ist[max]+" al tempo "+t_ist[max]);*/
                            t_ist[max] += distanceMatrix.getAdj()[pos_ist[max]][index].getWeight();
//                            System.out.println("Mover "+max+" arriverà al tempo "+t_ist[max]+" in "+index+". Il tempo atteso è "+deliveryTime.getTime().get(index));
//                            System.out.println("^^^^^^^^^^^^^^^POS IN ADJ: ["+pos_ist[max]+"]["+index+"]^^^^^^^^^^^^^^^");
                            adjMatrix.getAdj()[pos_ist[max]][index] = 1;
                            X[index] = t_ist[max];
                            pos_ist[max] = index;
                            for (int k = 0; k < (V - M); k++)
                                distanceMatrix.getAdj()[k][index].setWeight(Double.NaN);
                            counterDelivery++;
                            tempArr[index] = Double.NaN;
                            this.delivered.add(index);
                            this.delivered.sort(Comparator.naturalOrder());

//                            System.out.println("Counter Delivery: "+counterDelivery+". Consegnati: ");
//                            System.out.println(this.delivered);
                        }
                        else{
                           int negativeValue = getMaxNegativeValueIndex(arrValues);
                            if(negativeValue != arrValues.length) {
//                                System.out.println("CI SONO NEGATIVIIIIIIII --- PER IL MOVER: " + negativeValue);
                            }
                        //    else{
                                int temp = getMinBetweenExtremesIndex(arrValues, 7, tempArr[index]);
  //                          System.out.println("C'è qualcosa tra 7 e "+tempArr[index]+"?");
                                if(temp != arrValues.length && temp != i)
                                {
    /*                                System.out.println("SI. Posso andare in "+index+" con value < "+tempArr[index]+" pari a "+arrValues[temp]+"...temp: "+temp);
                                    System.out.println("Posso andare in "+index+" da "+pos_ist[temp]+"?");
                                    System.out.println("SI! Value per "+pos_ist[temp]+": "+arrValues[temp]);*/
                                    t_ist[temp] += distanceMatrix.getAdj()[pos_ist[temp]][index].getWeight();
//                                    System.out.println("Si arriverà in "+index+" al tempo "+t_ist[temp]+". Il tempo atteso è "+deliveryTime.getTime().get(index));
//                                    System.out.println("^^^^^^^^^^^^^^^POS IN ADJ: ["+pos_ist[temp]+"]["+index+"]^^^^^^^^^^^^^^^");
                                    adjMatrix.getAdj()[pos_ist[temp]][index] = 1;
                                    X[index] = t_ist[temp];
                                    pos_ist[temp] = index;
                                    for (int k = 0; k < (V - M); k++)
                                        distanceMatrix.getAdj()[k][index].setWeight(Double.NaN);
                                    counterDelivery++;
                                    tempArr[index] = Double.NaN;
                                    this.delivered.add(index);
                                    this.delivered.sort(Comparator.naturalOrder());
//                                    System.out.println("*******"+arrValues[temp]);
                                    if (arrValues[temp] > 6 && arrValues[temp] <= 9){
//                                        System.out.println("Z1: "+Arrays.toString(z1));
                                        z1[index] = 1;
                                    }
                                    else if (arrValues[temp] > 9 && arrValues[temp] <= 12) {
//                                        System.out.println("Z2: " + Arrays.toString(z2));
                                        z2[index] = 1;
                                    }
                                    else if (arrValues[temp] > 12 && arrValues[temp] <= 15){
//                                        System.out.println("Z3: "+Arrays.toString(z3));
                                        z3[index] = 1;
                                    }
                                }
                                else {
//                                    System.out.println("NO. Tempo del mover "+i+": "+t_ist[i]+"...pos: "+pos_ist[i]);
                                    t_ist[i] += distanceMatrix.getAdj()[pos_ist[i]][index].getWeight();
//                                    System.out.println("Arriva al tempo " + t_ist[i] + " e quello atteso è " + deliveryTime.getTime().get(index));
//                                    System.out.println("^^^^^^^^^^^^^^^POS IN ADJ: ["+pos_ist[i]+"]["+index+"]^^^^^^^^^^^^^^^");
                                    adjMatrix.getAdj()[pos_ist[i]][index] = 1;
                                    X[index] = t_ist[i];
                                    pos_ist[i] = index;
                                    for (int k = 0; k < (V - M); k++)
                                        distanceMatrix.getAdj()[k][index].setWeight(Double.NaN);
                                    counterDelivery++;

//                                    System.out.println("Consegne fatte: "+counterDelivery);
//                                    System.out.println("------"+tempArr[index]);
                                    if (tempArr[index] > 6 && tempArr[index] <= 9)
                                    {
//                                        System.out.println("Z1: "+Arrays.toString(z1));
                                        z1[index] = 1;
                                    }
                                    else if (tempArr[index] > 9 && tempArr[index] <= 12)
                                    {
//                                        System.out.println("Z2: "+Arrays.toString(z2));
                                        z2[index] = 1;
                                    }
                                    else if (tempArr[index] > 12 && tempArr[index] <= 15)
                                    {
//                                        System.out.println("Z3: "+Arrays.toString(z3));
                                        z3[index] = 1;
                                    }

                                    this.delivered.add(index);
                                    this.delivered.sort(Comparator.naturalOrder());


//                                    System.out.println("Counter Delivery: " + counterDelivery + ". Consegnati: ");
//                                    System.out.println(this.delivered);

                                    found = true;
                               // }
                            }
                        }
                        index = getMaxBetweenExtremesIndex(tempArr, 7, 15);
                    }
                    if(!found)
                    {
//                        System.out.println("Non ci sono più value compresi tra 7 e 15");
//                        System.out.println("^^^"+Arrays.toString(tempArr));

                        minForPositive = getMaxBetweenExtremesIndex(tempArr, 0, 6);

/*                        if(minForPositive != tempArr.length)
                            System.out.println("Massimo tra 0 e 6 per ordine "+minForPositive+". Valore: "+tempArr[minForPositive]);
*/                      if(minForPositive != tempArr.length)
                        {
//                            System.out.println("M"+i+" fa lo spostamento "+pos_ist[i]+"--->"+minForPositive+" con value "+tempArr[minForPositive]);
                            if (tempArr[minForPositive] > 6 && tempArr[minForPositive] <= 9) {       //TODO Basterebbe anche solo arr[minOfPositive] <= 9. Provare!
//                                System.out.println("Z1: "+Arrays.toString(z1));
                                z1[minForPositive] = 1;
                            } else if (tempArr[minForPositive] > 9 && tempArr[minForPositive] <= 12) {
//                                z2[minForPositive] = 1;
                                System.out.println("Z2: "+Arrays.toString(z2));
                            } else if (tempArr[minForPositive] > 12 && tempArr[minForPositive] <= 15) {
//                                z3[minForPositive] = 1;
                                System.out.println("Z3: "+Arrays.toString(z3));
                            }
                            t_ist[i] += distanceMatrix.getAdj()[pos_ist[i]][minForPositive].getWeight();    //modificato da from
                            DirectedEdge[] temp =  distanceMatrix.getAdj()[pos_ist[i]];                     //modificato da from
//                            System.out.println(Arrays.toString(temp));
//                            System.out.println("Arriva al tempo "+t_ist[i]+" e quello atteso è "+deliveryTime.getTime().get(minForPositive));
//                            System.out.println("^^^^^^^^^^^^^^^POS IN ADJ: ["+pos_ist[i]+"]["+minForPositive+"]^^^^^^^^^^^^^^^");
                            adjMatrix.getAdj()[pos_ist[i]][minForPositive] = 1;         //modificato da from
                            X[minForPositive] = t_ist[i];
                            pos_ist[i] = minForPositive;
                            for (int k = 0; k < (V - M); k++)
                                distanceMatrix.getAdj()[k][minForPositive].setWeight(Double.NaN);
                            counterDelivery++;

                            //TODO CANCELLARE ALLA FINE. SOLO PER TEST E IMPIEGA TEMPO.
                            this.delivered.add(minForPositive);
                            this.delivered.sort(Comparator.naturalOrder());


//                            System.out.println("Counter Delivery: "+counterDelivery+". Consegnati: ");
//                            System.out.println(this.delivered);
                        }
                        else{
//                            System.out.println("Non c'è nulla tra 0 e 6 per il mover "+i);// tempArr["+minOfPositive+"] = "+tempArr[minOfPositive]);
                            maxOfNegative = getMaxNegativeValueIndex(tempArr);
                            if(maxOfNegative == tempArr.length)
                            {
//                                System.out.println("Non c'è alcun Negative per il mover "+i);
                                //moverEnd a true, il mover non può più fare nulla.
                                moverEnd[i] = true;
                                counterMovers++;
//                                System.out.println("M"+i+" HA FINITO. Mover che hanno terminato: "+counterMovers);
                            }
                            else{
/*                                System.out.println("M"+i+" fa lo spostamento "+pos_ist[i]+"--->"+maxOfNegative+" con value "+tempArr[maxOfNegative]);
                                System.out.println("Massimo tra i negativi trovato! tempArr["+maxOfNegative+"] = "+tempArr[maxOfNegative]);
                                System.out.println("Il mover "+i+" si trovava al tempo "+t_ist[i]);*/
                                t_ist[i] += distanceMatrix.getAdj()[pos_ist[i]][maxOfNegative].getWeight()-(tempArr[maxOfNegative]);            //modificato da from
//                                System.out.println("Arriva al tempo "+t_ist[i]+" e quello atteso è "+deliveryTime.getTime().get(maxOfNegative));
//                                System.out.println("^^^^^^^^^^^^^^^POS IN ADJ: ["+pos_ist[i]+"]["+maxOfNegative+"]^^^^^^^^^^^^^^^");
                                adjMatrix.getAdj()[pos_ist[i]][maxOfNegative] = 1;                                                              //modificato da from
                                X[maxOfNegative] =t_ist[i];
                                pos_ist[i] = maxOfNegative;
                                for(int k=0; k<(V-M); k++)
                                    distanceMatrix.getAdj()[k][maxOfNegative].setWeight(Double.NaN);
                                counterDelivery++;
//                                System.out.println("Counter Delivery: "+counterDelivery);
                            }
                        }
                    }
                }
            }
        }
//        System.out.println(this.counterDelivery+"....."+this.counterMovers);
//        System.out.println(Arrays.toString(this.X));
        int removed = 0;
        for(i=0; i<X.length; i++)
        {
            if(X[i] == 0.0)
            {
                w[i] = 1;
//                System.out.println(i);
                removed++;
            }
        }


/*        System.out.println(Arrays.toString(z1));
        System.out.println(Arrays.toString(z2));
        System.out.println(Arrays.toString(z3));
            System.out.println("z1: "+sumInArray(z1));
            System.out.println("z2: "+sumInArray(z2));
            System.out.println("z3: "+sumInArray(z3));*/
//            int fObiettivo = sumInArray(z1)+2*sumInArray(z2)+3*sumInArray(z3)+10*removed;
/*            System.out.println("w: "+removed);
            System.out.println("Funzione Obiettivo: "+fObiettivo);
        System.out.println(getDelivered());*/
 }


    public void firstDeliveryProva(String deliveryFile, int[] arr)
    {
        int i, j;
        int posFirstMover = this.distanceMatrix.getPosFirstMover();
//        System.out.println(this.distanceMatrix);
        DirectedEdge[] tempDist;
        Minimum minimum = null ;
        List<Integer> tempKSmall;
        boolean foundNext;
        this.delivered = new ArrayList<>();

        //    System.out.println("++++++++++++");
        //    System.out.println(deliveryTime.getTime());
        tempKSmall = DeliveryTimeController.selectKthIndex(deliveryTime.getTime(), M);//this.kSmallestDelT;

      /*  System.out.println("%%%%%%%%%%%%%%%%%");
        System.out.println(this.adjMatrix);
        System.out.println("%%%%%%%%%%%%%%%%%");*/
        for(i=0; i<M; i++)
        {
            foundNext = false;
            this.deliveryTime = new DeliveryTime(deliveryFile);         //todo Trovare modo per evitare questa cosa che rallenta.
         //   System.out.println("++++++++++"+tempKSmall.size());
         //   System.out.println("--------------"+i+"--------------");
            while(!foundNext)
            {
                tempDist = distanceMatrix.getAdj()[V-M+arr[i]];
                //todo Da cambiare la funzione successiva.
                minimum = this.edgeWithMinWeight(tempDist, tempKSmall);     //prendo il j con peso minimo tra quelli dei delivery time ordinati

                if(distanceMatrix.getAdj()[V-M+arr[i]][minimum.getMin().to()].getWeight() > (deliveryTime.getTime().get(minimum.getMin().to())+12))
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
                 //   System.out.println("Min weight for "+minimum.getMin().to()+". Expected time: "+deliveryTime.getTime().get(minimum.getMin().to()));
                    double sum = deliveryTime.getTime().get(minimum.getMin().to()) +12;
                 //   System.out.println(distanceMatrix.getAdj()[V-M+arr[i]][minimum.getMin().to()].getWeight()+" > "+sum+" ??? NO!!!");
                    if(distanceMatrix.getAdj()[V-M+arr[i]][minimum.getMin().to()].getWeight() <= (deliveryTime.getTime().get(minimum.getMin().to())-3))
                    {
                        //    double time = deliveryTime.getTime().get(minimum.getMin().to())-3;
                        //    System.out.println("Tempo successivo:::::: "+time+"....attuale: "+t_ist[i-posFirstMover]);
                        t_ist[arr[i]] += (deliveryTime.getTime().get(minimum.getMin().to())-3);
                    }
                    else if(distanceMatrix.getAdj()[V-M+arr[i]][minimum.getMin().to()].getWeight() <= (deliveryTime.getTime().get(minimum.getMin().to())+3))
                    {
                        t_ist[arr[i]] += distanceMatrix.getAdj()[V-M+arr[i]][minimum.getMin().to()].getWeight();
                    }
                    else if(distanceMatrix.getAdj()[V-M+arr[i]][minimum.getMin().to()].getWeight() <= (deliveryTime.getTime().get(minimum.getMin().to())+6))
                    {
                        t_ist[arr[i]] += distanceMatrix.getAdj()[V-M+arr[i]][minimum.getMin().to()].getWeight();
                        z1[minimum.getMin().to()] = 1;
                    }
                    else if(distanceMatrix.getAdj()[V-M+arr[i]][minimum.getMin().to()].getWeight() <= (deliveryTime.getTime().get(minimum.getMin().to())+9))
                    {
                        t_ist[arr[i]] += distanceMatrix.getAdj()[V-M+arr[i]][minimum.getMin().to()].getWeight();
                        z2[minimum.getMin().to()] = 1;
                    }
                    else if(distanceMatrix.getAdj()[V-M+arr[i]][minimum.getMin().to()].getWeight() <= (deliveryTime.getTime().get(minimum.getMin().to())+12))
                    {
                        t_ist[arr[i]] += distanceMatrix.getAdj()[V-M+arr[i]][minimum.getMin().to()].getWeight();
                        z3[minimum.getMin().to()] = 1;
                    }
                    foundNext = true;
                    counterDelivery++;
                    tempKSmall.remove(minimum.getIndexInSmallest());
                    adjMatrix.getAdj()[V-M+arr[i]][minimum.getMin().to()] = 1;
                    X[minimum.getMin().to()] = t_ist[arr[i]];
                    pos_ist[arr[i]] = minimum.getMin().to();

                    this.delivered.add(minimum.getMin().to());

                    for(int k=0; k<(V-M); k++)
                        distanceMatrix.getAdj()[k][minimum.getMin().to()].setWeight(Double.NaN);
                }
//                System.out.println("====="+counterDelivery);
                //    foundNext = true;
            }
        }

/*        System.out.println("*********************");
        System.out.println(Arrays.toString(t_ist));
        System.out.println("---------------------");
        System.out.println(Arrays.toString(X));*/
    }

    public Integer getIndexOfValue(Integer[] arr, int value)
    {
        int i, result=0;
        for(i=0; i<arr.length; i++)
            if(arr[i] == value)
                result = i;

        return result;

    }


    public void checkAdjMatrix(AdjMatrix adjM)
    {
        int i, j, sum;

       // boolean isOne = true;

        //check righe
        for(i=0; i<V-M; i++)
        {
           /* if(isOne == false)
                break;
            else {*/
                sum = 0;
                for(j=0; j<V; j++)
                {
                    sum += adjM.getAdj()[j][i];
                }
                if(sum != 1)
                {
       //             isOne = false;
                    System.out.println("!!!!!!!!!!"+i+"-----"+sum);
                }
     //       }
        }
       // return isOne;
    }

    public static int getMinObjectiveIndex(Result[] results){
        int minIndex = 0;
        int i;
        for(i=0; i<results.length; i++)
        {
            if(results[i].getObjective() < results[minIndex].getObjective())
                minIndex = i;
        }
        return minIndex;
    }

    public static void testAllDatasets(String folderNumber, int[] vertices, int[] movers)
    {
        Result[] results;
        int objective, sumZ1, sumZ2, sumZ3, sumW;
        int[] arr;
        int i, j, k;
        long totStartTime, totEndTime, totTime;
        //(movers.length+2)
        for(i=2; i<(movers.length+2); i++)
        {
            MainAlgorithm algorithm;
            results = new Result[movers[i-2]];
            arr = new int[movers[i-2]];
            int minObjIndex;
            for(j=0; j< movers[i-2]; j++)
                arr[j] = j;
            totStartTime = System.nanoTime();
            for(k=0; k<arr.length; k++)
            {
                algorithm = new MainAlgorithm(movers[i-2], vertices[i-2], "deliveryTime_ist".concat(Integer.toString(i)).concat(".csv"), "distanceMatrix_ist".concat(Integer.toString(i)).concat(".csv"), folderNumber);
                Result res = new Result();
                long startTime = System.nanoTime();
                algorithm.firstDelivery3(arr);
                algorithm.nextSteps();
                long endTime = System.nanoTime();

                long duration = (endTime - startTime);
                double timeExecution = (double) duration/1000000000;
                sumZ1 = algorithm.sumInArray(algorithm.z1);
                sumZ2 = algorithm.sumInArray(algorithm.z2);
                sumZ3 = algorithm.sumInArray(algorithm.z3);
                sumW = algorithm.sumInArray(algorithm.w);
                objective = sumZ1+2*sumZ2+3*sumZ3+10*sumW;
                res.setAlgInfo(algorithm);
                res.setTimeExecution(timeExecution);
                res.setObjective(objective);
                algorithm.moveArrElementsOneStep(arr);
                results[k] = res;
                algorithm.moveArrElementsOneStep(arr);
            }

            minObjIndex = MainAlgorithm.getMinObjectiveIndex(results);
            totEndTime = System.nanoTime();

            totTime = (totEndTime - totStartTime);

        //    System.out.println(duration);
            CSVHandler csvHandler = new CSVHandler();
            csvHandler.writeOnFile("output".concat(folderNumber).concat("/ist").concat(Integer.toString(i)).concat("/"), results[minObjIndex].getAlgInfo().X, results[minObjIndex].getAlgInfo().z1, results[minObjIndex].getAlgInfo().z2, results[minObjIndex].getAlgInfo().z3, results[minObjIndex].getAlgInfo().w, results[minObjIndex].getAlgInfo().adjMatrix.getAdj(), totTime);
        //    System.out.println(algorithm.deliveryTime.getTime());
        //    System.out.println(Arrays.toString(algorithm.X));
        //    System.out.println("-----ist"+i+"-----");
        //    results[minObjIndex].getAlgInfo().checkAdjMatrix(results[minObjIndex].getAlgInfo().adjMatrix);
        }
    }

    public static void main(String args[])
    {
     //   String istNumber = "ist4";
        String folder = "2";
        int[] V = {275, 275, 266, 260, 256, 247, 247, 248, 243, 257, 237, 233, 231, 241, 227,235, 230, 217, 223, 221, 216, 219, 216, 216, 213, 217, 205, 208, 207, 204, 204, 199, 198, 201};
        int[] M = {36, 38, 45, 41, 39, 34, 35, 36, 34, 50, 35, 33, 34, 44, 30, 41, 39, 29, 36, 34, 30, 35, 33, 34, 31, 37, 26, 30, 31, 31, 32, 27, 29, 38};
        //int[] V = {235, 275, 266, 260, 256, 247, 247, 248, 243, 257, 237, 233, 231, 241, 227,275, 230, 217, 223, 221, 216, 219, 216, 216, 213, 217, 205, 208, 207, 204, 204, 199, 198, 201};
        //int[] M = {41, 38, 45, 41, 39, 34, 35, 36, 34, 50, 35, 33, 34, 44, 30,36, 39, 29, 36, 34, 30, 35, 33, 34, 31, 37, 26, 30, 31, 31, 32, 27, 29, 38};


        MainAlgorithm.testAllDatasets(folder, V, M);

      //  int fObiettivo = algorithm.sumInArray(algorithm.z1)+2*algorithm.sumInArray(algorithm.z2)+3*algorithm.sumInArray(algorithm.z3)+10*algorithm.sumInArray(algorithm.w);
    }
}


