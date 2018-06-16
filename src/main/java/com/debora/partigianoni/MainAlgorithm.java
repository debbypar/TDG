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
  /*  private double[] t_ist;     //tempo istantaneo del mover
    private int[] pos_ist;*/   //posizione istantanea del mover
    private List<Integer> kSmallestDelT;
    private int M;  //numero di mover
    private int V;  //Numero di vertici (compreso il numero di movers)
    private static DistanceMatrix distanceMatrix;
    private DeliveryTime deliveryTime;
/*    private double[] X;    //istanti di completamento degli ordini
    private int[] z1;
    private int[] z2;
    private int[] z3;
    private int[] w;
    private AdjMatrix adjMatrix;*/
/*    private int counterMovers;
    private int counterDelivery;
    private boolean[] moverEnd;
    private List<Integer> delivered;*/
    private Result[] results;

    public Result[] getResults() {
        return results;
    }

    public void setResults(Result[] results) {
        this.results = results;
    }

    public MainAlgorithm(int movers, int vertices, String deliveryFile, String distanceFile, String folderIndex){
        this.M = movers;
        this.V = vertices;
        this.deliveryTime = new DeliveryTime(deliveryFile);
        MainAlgorithm.distanceMatrix = new DistanceMatrix(folderIndex, distanceFile, vertices);

        this.results = new Result[2*M];
        for(int i=0; i<2*movers; i++)
        {
            this.results[i] = new Result(vertices, movers);
            this.results[i].setDistMatrix(new DistanceMatrix(folderIndex, distanceFile, vertices).getAdj());// = new Result(vertices, movers);
        }

        //this.kSmallestDelT = DeliveryTimeController.selectKthIndex(deliveryTime.getTime(), M);



        //this.deliveryTime = new DeliveryTime(deliveryFile);
 //       System.out.println("%%%%%%%%%%%%%%%%%");
//        System.out.println(adjMatrix.getAdj());
    }

    public void initializeDistMatrixResult(int V, int M, int iteration)
    {
        int i, j;
        //this.results[iteration] = new Result(V, M);
        for(i=0; i<V; i++) {
            for (j = 0; j < (V - M); j++) {
                this.results[iteration].getDistMatrix()[i][j] = MainAlgorithm.distanceMatrix.getAdj()[i][j];
            }
        }
                //this.results = new Result[M];
        /*for(int i=0; i<M; i++)
        {
            results[i] = new Result(V, M);
            this.results[i].setDistMatrix(this.getDistanceMatrix());
            System.out.println("+++"+Arrays.toString(this.results[i].getDistMatrix().getAdj()[i]));
        }
        System.out.println("---"+Arrays.toString(this.getDistanceMatrix().getAdj()[0]));*/
    }

    public DistanceMatrix getDistanceMatrix() {
        return distanceMatrix;
    }

    /*public void setDistanceMatrix(DistanceMatrix distanceMatrix) {
        this.distanceMatrix = distanceMatrix;
    }*/

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
/*
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
*/

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


    /*public void firstDelivery(String deliveryFile)
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
/*                }
            }
        }
    }*/

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

    public void firstDelivery3(int[] arr, int iter){

        int i, j;
        double[] tempArr;
        int index1, index;
        //this.delivered = new ArrayList<>();
        for(i=0; i<M; i++)
        {
            tempArr = new double[V-M];

            for(j=0; j<(V-M); j++)
            {
                if(j != arr[i])
                {
                //    System.out.println("..."+this.getResults()[iter]);
                    tempArr[j] = this.getResults()[iter].getT_ist()[arr[i]]+this.getResults()[iter].getDistMatrix()[V-M+arr[i]][j].getWeight() - deliveryTime.getTime().get(j)+3;
                }
                else tempArr[j] = Double.NaN;
            }

            //A VOLTE è MEGLIO CON MIN, ALTRE CON MAX. SCEGLIEREEEEEEE
            index = getMinBetweenExtremesIndex(tempArr, 0, 6);
            if(index != tempArr.length)
            {
                this.getResults()[iter].getT_ist()[arr[i]] += this.getResults()[iter].getDistMatrix()[V-M+arr[i]][index].getWeight();
                this.getResults()[iter].getAdjMatrix().getAdj()[V-M+arr[i]][index] = 1;
                this.getResults()[iter].getX()[index] = this.getResults()[iter].getT_ist()[arr[i]];
                this.getResults()[iter].getPos_ist()[arr[i]] = index;
                for(int k=0; k<(V); k++)
                    this.getResults()[iter].getDistMatrix()[k][index].setWeight(Double.NaN);
                for(int k=0; k<(V-M); k++)
                    this.getResults()[iter].getDistMatrix()[V-M+arr[i]][k].setWeight(Double.NaN);
                this.getResults()[iter].addCounterDelivery();
                this.getResults()[iter].getDelivered().add(index);
            }
            else{
                index = getMaxNegativeValueIndex(tempArr);
              //  System.out.println(tempArr[index]);
             //   index = getOrderWithMinDeliveryTime(getValueInArrayIndex(tempArr, tempArr[index1]));
                //index = getMinDelTimeForNegativeOrders(tempArr);
                if(index != tempArr.length){

                    this.getResults()[iter].getT_ist()[arr[i]] += this.getResults()[iter].getDistMatrix()[V-M+arr[i]][index].getWeight()-(tempArr[index]);            //modificato da from
                    this.getResults()[iter].getAdjMatrix().getAdj()[V-M+arr[i]][index] = 1;                                                              //modificato da from
                    this.getResults()[iter].getX()[index] =this.getResults()[iter].getT_ist()[arr[i]];
                    this.getResults()[iter].getPos_ist()[arr[i]] = index;
                    for(int k=0; k<(V); k++)
                        this.getResults()[iter].getDistMatrix()[k][index].setWeight(Double.NaN);
                    for(int k=0; k<(V-M); k++)
                        this.getResults()[iter].getDistMatrix()[V-M+arr[i]][k].setWeight(Double.NaN);
                    this.getResults()[iter].addCounterDelivery();
                    this.getResults()[iter].getDelivered().add(index);
                }
                else{
                    System.out.println("Cercare tra 6 e 15. 000000000000000000000000000000000000000");
                }
            }
            //System.out.println(iter+"+++"+Arrays.toString(this.distanceMatrix.getAdj()[0]));
            int k;
            for(k=0; k<(V-M); k++)
                this.getResults()[iter].getDistMatrix()[k][index].setWeight(Double.NaN);
            for(k=0; k<(V-M); k++)
                this.getResults()[iter].getDistMatrix()[V-M+arr[i]][k].setWeight(Double.NaN);
        }
    }

    public void nextSteps(int iter)
    {
        int i, j, from;
        double[] tempArr;
        int minForPositive;
        int maxOfNegative;
        DirectedEdge[] tempEdges;
        double[] arrValues;
        int min;
        boolean found;
        int contatoreTemp = 0;
        while(this.getResults()[iter].getCounterDelivery() != (V-M) && this.getResults()[iter].getCounterMovers() != M)
        {
            this.getResults()[iter].getDelivered().sort(Comparator.naturalOrder());
            for (i=0; i<M; i++)
            {
                if(this.getResults()[iter].getMoverEnd()[i])
                {
                    continue;
                }
                else{
                    tempArr = new double[V-M];


                    found = false;

                    for(j=0; j<(V-M); j++)
                    {
                        if(j != this.getResults()[iter].getPos_ist()[i])
                        {
                            tempArr[j] = this.getResults()[iter].getT_ist()[i]+this.getResults()[iter].getDistMatrix()[this.getResults()[iter].getPos_ist()[i]][j].getWeight() - deliveryTime.getTime().get(j)+3;
                        }
                        else tempArr[j] = Double.NaN;
                    }

//                    int dim = tempArr.length;
                    int index = getMaxBetweenExtremesIndex(tempArr, 7, 15);
                    if(index != tempArr.length)
                        contatoreTemp++;

                    while(index != tempArr.length && !found)
                    {
                        tempEdges = new DirectedEdge[M];
                        arrValues = new double[M];
                        for(j=0; j<M; j++)
                            tempEdges[j] = this.getResults()[iter].getDistMatrix()[this.getResults()[iter].getPos_ist()[j]][index];
                        for(j=0; j< tempEdges.length; j++)
                            arrValues[j] = this.getResults()[iter].getT_ist()[j]+this.getResults()[iter].getDistMatrix()[this.getResults()[iter].getPos_ist()[j]][index].getWeight()-deliveryTime.getTime().get(index)+3;

                        //todo sostituire variabile max con min perché ora è il minimo.
                        min = getMinBetweenExtremesIndex(arrValues, 0,6);
//                        System.out.println("Tra 0 e 6? "+max);

                        if(min != arrValues.length)
                        {

                            this.getResults()[iter].getT_ist()[min] += this.getResults()[iter].getDistMatrix()[this.getResults()[iter].getPos_ist()[min]][index].getWeight();
                            this.getResults()[iter].getAdjMatrix().getAdj()[this.getResults()[iter].getPos_ist()[min]][index] = 1;
                            this.getResults()[iter].getX()[index] = this.getResults()[iter].getT_ist()[min];
                            this.getResults()[iter].getPos_ist()[min] = index;
                            for (int k = 0; k < (V - M); k++)
                                this.getResults()[iter].getDistMatrix()[k][index].setWeight(Double.NaN);
                            this.getResults()[iter].addCounterDelivery();
                            tempArr[index] = Double.NaN;
                            this.getResults()[iter].getDelivered().add(index);
                            this.getResults()[iter].getDelivered().sort(Comparator.naturalOrder());
                        }
                        else{
/*                           int negativeValue = getMaxNegativeValueIndex(arrValues);
                            if(negativeValue != arrValues.length) {
                            }*/
                            int temp = getMinBetweenExtremesIndex(arrValues, 7, tempArr[index]);
                            if(temp != arrValues.length && temp != i)
                            {
                                this.getResults()[iter].getT_ist()[temp] += this.getResults()[iter].getDistMatrix()[this.getResults()[iter].getPos_ist()[temp]][index].getWeight();
                                this.getResults()[iter].getAdjMatrix().getAdj()[this.getResults()[iter].getPos_ist()[temp]][index] = 1;
                                this.getResults()[iter].getX()[index] = this.getResults()[iter].getT_ist()[temp];
                                this.getResults()[iter].getPos_ist()[temp] = index;
                                for (int k = 0; k < (V - M); k++)
                                    this.getResults()[iter].getDistMatrix()[k][index].setWeight(Double.NaN);
                                this.getResults()[iter].addCounterDelivery();
                                tempArr[index] = Double.NaN;
                                this.getResults()[iter].getDelivered().add(index);
                                this.getResults()[iter].getDelivered().sort(Comparator.naturalOrder());
                                if (arrValues[temp] > 6 && arrValues[temp] <= 9)
                                    this.getResults()[iter].getZ1()[index] = 1;
                                else if (arrValues[temp] > 9 && arrValues[temp] <= 12)
                                    this.getResults()[iter].getZ2()[index] = 1;
                                else if (arrValues[temp] > 12 && arrValues[temp] <= 15)
                                    this.getResults()[iter].getZ3()[index] = 1;
                            }
                            else {
                                this.getResults()[iter].getT_ist()[i] += this.getResults()[iter].getDistMatrix()[this.getResults()[iter].getPos_ist()[i]][index].getWeight();
                                this.getResults()[iter].getAdjMatrix().getAdj()[this.getResults()[iter].getPos_ist()[i]][index] = 1;
                                this.getResults()[iter].getX()[index] = this.getResults()[iter].getT_ist()[i];
                                this.getResults()[iter].getPos_ist()[i] = index;
                                for (int k = 0; k < (V - M); k++)
                                    this.getResults()[iter].getDistMatrix()[k][index].setWeight(Double.NaN);
                                this.getResults()[iter].addCounterDelivery();

                                if (tempArr[index] > 6 && tempArr[index] <= 9)
                                {
                                    this.getResults()[iter].getZ1()[index] = 1;
                                }
                                else if (tempArr[index] > 9 && tempArr[index] <= 12)
                                {
                                    this.getResults()[iter].getZ2()[index] = 1;
                                }
                                else if (tempArr[index] > 12 && tempArr[index] <= 15)
                                {
                                    this.getResults()[iter].getZ3()[index] = 1;
                                }

                                this.getResults()[iter].getDelivered().add(index);
                                this.getResults()[iter].getDelivered().sort(Comparator.naturalOrder());
                                found = true;
                            }
                        }
                        index = getMaxBetweenExtremesIndex(tempArr, 7, 15);
                    }
                    if(!found)
                    {
                        minForPositive = getMaxBetweenExtremesIndex(tempArr, 0, 6);

                      if(minForPositive != tempArr.length)
                        {
                            if (tempArr[minForPositive] > 6 && tempArr[minForPositive] <= 9) {       //TODO Basterebbe anche solo arr[minOfPositive] <= 9. Provare!
                                this.getResults()[iter].getZ1()[minForPositive] = 1;
                            } else if (tempArr[minForPositive] > 9 && tempArr[minForPositive] <= 12) {
                                this.getResults()[iter].getZ2()[minForPositive] = 1;
 //                               System.out.println("Z2: "+Arrays.toString(this.getResults()[iter].getZ2()));
                            } else if (tempArr[minForPositive] > 12 && tempArr[minForPositive] <= 15) {
                                this.getResults()[iter].getZ3()[minForPositive] = 1;
                                System.out.println("Z3: "+Arrays.toString(this.getResults()[iter].getZ3()));
                            }
                            this.getResults()[iter].getT_ist()[i] += this.getResults()[iter].getDistMatrix()[this.getResults()[iter].getPos_ist()[i]][minForPositive].getWeight();    //modificato da from
                            DirectedEdge[] temp =  this.getResults()[iter].getDistMatrix()[this.getResults()[iter].getPos_ist()[i]];                     //modificato da from
                            this.getResults()[iter].getAdjMatrix().getAdj()[this.getResults()[iter].getPos_ist()[i]][minForPositive] = 1;         //modificato da from
                            this.getResults()[iter].getX()[minForPositive] = this.getResults()[iter].getT_ist()[i];
                            this.getResults()[iter].getPos_ist()[i] = minForPositive;
                            for (int k = 0; k < (V - M); k++)
                                this.getResults()[iter].getDistMatrix()[k][minForPositive].setWeight(Double.NaN);
                            this.getResults()[iter].addCounterDelivery();

                            //TODO CANCELLARE ALLA FINE. SOLO PER TEST E IMPIEGA TEMPO.
                            this.getResults()[iter].getDelivered().add(minForPositive);
                            this.getResults()[iter].getDelivered().sort(Comparator.naturalOrder());

                        }
                        else{
                            maxOfNegative = getMaxNegativeValueIndex(tempArr);
                            if(maxOfNegative == tempArr.length)
                            {
                                this.getResults()[iter].getMoverEnd()[i] = true;
                                this.getResults()[iter].addCounterMovers();
                            }
                            else{
                                this.getResults()[iter].getT_ist()[i] += this.getResults()[iter].getDistMatrix()[this.getResults()[iter].getPos_ist()[i]][maxOfNegative].getWeight()-(tempArr[maxOfNegative]);            //modificato da from
                                this.getResults()[iter].getAdjMatrix().getAdj()[this.getResults()[iter].getPos_ist()[i]][maxOfNegative] = 1;                                                              //modificato da from
                                this.getResults()[iter].getX()[maxOfNegative] =this.getResults()[iter].getT_ist()[i];
                                this.getResults()[iter].getPos_ist()[i] = maxOfNegative;
                                for(int k=0; k<(V-M); k++)
                                    this.getResults()[iter].getDistMatrix()[k][maxOfNegative].setWeight(Double.NaN);
                                this.getResults()[iter].addCounterDelivery();
                            }
                        }
                    }
                }
            }
        }
        int removed = 0;
        for(i=V-M; i<this.getResults()[iter].getX().length; i++)
        {
            this.getResults()[iter].getX()[i] = -1;
        }
        for(i=0; i<this.getResults()[iter].getX().length; i++)
        {
            if(this.getResults()[iter].getX()[i] == 0.0)
            {
                this.getResults()[iter].getW()[i] = 1;
                removed++;
            }
        }


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

    public int getMinRemovedIndex(Result[] results){
        int minIndex = 0;
        int i;
        for(i=0; i<results.length; i++)
        {
            if(this.sumInArray(results[i].getW()) < this.sumInArray(results[minIndex].getW()))
                minIndex = i;
        }
        return minIndex;
    }
    /*public void spectralArr(int[] arr)
    {
        int i;
        int start = arr[0];
        for(i=0; i<arr.length-1; i++)
            arr[i] = arr[i+1];
        arr[arr.length-1] = start;
        //return  arr;

    }*/

    public boolean isEvenNumber(int num)
    {
        if ((num % 2) == 0) {
            return true;
        } else {
            return false;
        }
    }

    public int[] shuffledArr()
    {
        int i;
        int[] arr = new int[M];
        int tempStart = 0;
        int tempEnd = M-1;

        for(i=0; i<M; i++)
        {
            if(!this.isEvenNumber(i))
            {
                arr[i] = tempStart;
                tempStart++;
            }else{
                arr[i] = tempEnd;
                tempEnd--;
            }
        }
        return arr;
    }

    public ArrayList<Integer> getMinRemovedIndexArr(Result[] arr, int value)
    {
        int i;
        ArrayList<Integer> arrList = new ArrayList<>();
        for(i=0; i<arr.length; i++)
        {
            if(this.sumInArray(arr[i].getW()) == value)
                arrList.add(i);
        }
        return arrList;
    }

    public int getMinArrListIndex(Result[] arr, ArrayList<Integer> arrList){
        int min, i;
        min = arrList.get(0);
        for(i=1; i<arrList.size(); i++)
        {
            if(arr[arrList.get(i)].getObjective()  < arr[min].getObjective())
                min = arrList.get(i);
        }
        return min;
    }

    public static void testAllDatasets(String folderNumber, int[] vertices, int[] movers)
    {
//        Result[] results;
        int objective, sumZ1, sumZ2, sumZ3, sumW;
        int[] arr;
        int i, j, k, z;
        int minRemovalsIndex, minObjIndex;
        int minIndex;
        long totStartTime, totEndTime, totTime;
        MainAlgorithm algorithm;
        Result res;
        //(movers.length+2)
        for(i=2; i<(movers.length+2); i++)
        {
//            System.out.println("Dataset "+i);
    //        results = new Result[movers[i-2]];
            arr = new int[movers[i-2]];

            for(j=0; j< movers[i-2]; j++)
                arr[j] = j;
            algorithm = new MainAlgorithm(movers[i-2], vertices[i-2], "deliveryTime_ist".concat(Integer.toString(i)).concat(".csv"), "distanceMatrix_ist".concat(Integer.toString(i)).concat(".csv"), folderNumber);
            totStartTime = System.nanoTime();
            for(k=0; k<arr.length; k++)
            {
                //System.out.println(k+"..1"+Arrays.toString(arr));
                //System.out.println("-------"+k+"-------");

                long startTime = System.nanoTime();
                algorithm.firstDelivery3(arr, k);
                algorithm.nextSteps(k);
                long endTime = System.nanoTime();

                long duration = (endTime - startTime);
                double timeExecution = (double) duration/1000000000;
                sumZ1 = algorithm.sumInArray(algorithm.getResults()[k].getZ1());
                sumZ2 = algorithm.sumInArray(algorithm.getResults()[k].getZ2());
                sumZ3 = algorithm.sumInArray(algorithm.getResults()[k].getZ3());
                sumW = algorithm.sumInArray(algorithm.getResults()[k].getW());
                objective = sumZ1+2*sumZ2+3*sumZ3+10*sumW;
                algorithm.getResults()[k].setTimeExecution(timeExecution);
                algorithm.getResults()[k].setObjective(objective);
                algorithm.moveArrElementsOneStep(arr);
             //   algorithm.moveArrElementsOneStep(arr);
                //    algorithm.checkAdjMatrix(algorithm.getResults()[k].getAdjMatrix());
            }

            arr = algorithm.shuffledArr();
            for(k=0; k<arr.length; k++)
            {
                //System.out.println("-------"+k+"-------");
                //System.out.println(k+"..2"+Arrays.toString(arr));

                long startTime = System.nanoTime();
                algorithm.firstDelivery3(arr, arr.length+k);
                algorithm.nextSteps(arr.length+k);
                long endTime = System.nanoTime();

                long duration = (endTime - startTime);
                double timeExecution = (double) duration/1000000000;
                sumZ1 = algorithm.sumInArray(algorithm.getResults()[arr.length+k].getZ1());
                sumZ2 = algorithm.sumInArray(algorithm.getResults()[arr.length+k].getZ2());
                sumZ3 = algorithm.sumInArray(algorithm.getResults()[arr.length+k].getZ3());
                sumW = algorithm.sumInArray(algorithm.getResults()[arr.length+k].getW());
                objective = sumZ1+2*sumZ2+3*sumZ3+10*sumW;
                algorithm.getResults()[arr.length+k].setTimeExecution(timeExecution);
                algorithm.getResults()[arr.length+k].setObjective(objective);
                algorithm.moveArrElementsOneStep(arr);
                //   algorithm.moveArrElementsOneStep(arr);
                //    algorithm.checkAdjMatrix(algorithm.getResults()[k].getAdjMatrix());
            }


           // minObjIndex = MainAlgorithm.getMinObjectiveIndex(algorithm.results);
            //int temp = algorithm.getMinRemovedIndex(algorithm.results);
            //int sum = algorithm.sumInArray(algorithm.results[temp].getW());
          /*  System.out.println(sum);
            System.out.println(algorithm.getMinRemovedIndexArr(algorithm.results, sum));
            for(int t=0; t<algorithm.getMinRemovedIndexArr(algorithm.results, sum).size(); t++)
            {
                System.out.println(t+"-->"+algorithm.results[algorithm.getMinRemovedIndexArr(algorithm.results, sum).get(t)].getObjective());
            }*/

            minObjIndex = MainAlgorithm.getMinObjectiveIndex(algorithm.results);
            minRemovalsIndex = algorithm.getMinArrListIndex(algorithm.results,algorithm.getMinRemovedIndexArr(algorithm.results, algorithm.sumInArray(algorithm.results[algorithm.getMinRemovedIndex(algorithm.results)].getW())));
            int diffObj = algorithm.results[minObjIndex].getObjective() - algorithm.results[minRemovalsIndex].getObjective();
            int diffRem = algorithm.sumInArray(algorithm.results[minRemovalsIndex].getW()) - algorithm.sumInArray(algorithm.results[minObjIndex].getW());
            if(diffObj < 5*diffRem)
                minIndex = minObjIndex;
            else minIndex = minRemovalsIndex;

            totEndTime = System.nanoTime();

            totTime = (totEndTime - totStartTime);

            CSVHandler csvHandler = new CSVHandler();
            csvHandler.writeOnFile("output".concat(folderNumber).concat("/ist").concat(Integer.toString(i)).concat("/"), algorithm.results[minIndex].getX(), algorithm.results[minIndex].getZ1(), algorithm.results[minIndex].getZ2(), algorithm.results[minIndex].getZ3(), algorithm.results[minIndex].getW(), algorithm.results[minIndex].getAdjMatrix().getAdj(), totTime, movers[i-2]);
        }
    }

    public static void main(String args[])
    {
        String folder = "2";
        int[] V = {275, 275, 266, 260, 256, 247, 247, 248, 243, 257, 237, 233, 231, 241, 227,235, 230, 217, 223, 221, 216, 219, 216, 216, 213, 217, 205, 208, 207, 204, 204, 199, 198, 201};
        int[] M = {36, 38, 45, 41, 39, 34, 35, 36, 34, 50, 35, 33, 34, 44, 30, 41, 39, 29, 36, 34, 30, 35, 33, 34, 31, 37, 26, 30, 31, 31, 32, 27, 29, 38};


        MainAlgorithm.testAllDatasets(folder, V, M);

      //  int fObiettivo = algorithm.sumInArray(algorithm.z1)+2*algorithm.sumInArray(algorithm.z2)+3*algorithm.sumInArray(algorithm.z3)+10*algorithm.sumInArray(algorithm.w);
    }
}


