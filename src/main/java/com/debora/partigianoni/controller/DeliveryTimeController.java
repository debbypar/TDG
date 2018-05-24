package com.debora.partigianoni.controller;

import com.debora.partigianoni.model.DeliveryTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class DeliveryTimeController {

    private int[] kSmallest;

    public int[] getkSmallest() {
        return kSmallest;
    }

    public void setkSmallest(int[] kSmallest) {
        this.kSmallest = kSmallest;
    }

    public static int minIndex (ArrayList<Double> list) {
        return list.indexOf(Collections.min(list));
    }

    public static <T extends Comparable<T>> int findMinIndex(final List<T> xs) {
        int minIndex;
        if (xs.isEmpty()) {
            minIndex = -1;
        } else {
            final ListIterator<T> itr = xs.listIterator();
            T min = itr.next(); // first element as the current minimum
            minIndex = itr.previousIndex();
            while (itr.hasNext()) {
                final T curr = itr.next();
                if (curr.compareTo(min) < 0) {
                    min = curr;
                    minIndex = itr.previousIndex();
                }
            }
        }
        return minIndex;
    }

    public static void kLargest(ArrayList<Double> array, int k){

        int minIndex = 0, i;                            //Find Min

        for (i = k; i < array.size(); i++){
            minIndex = 0;
            for (int j = 0; j < k; j++){
                if(array.get(j)< array.get(minIndex)){
                    minIndex = j;
                    array.set(minIndex, array.get(j));
                }
            }
            if (array.get(minIndex) < array.get(i)){         //Swap item if min < array[i]

                double temp = array.get(minIndex);
                array.set(minIndex, array.get(i));
                array.set(i, temp);
            }
        }
        for (int q = 0; q < k; q++){                            //Print output
            System.out.print(array.get(q) + " ");
        }
    }

    public static void kSmallest(ArrayList<Double> array, int k){

        int maxIndex = 0, i;                            //Find Max

        for (i = k; i < array.size(); i++){
            maxIndex = 0;
            for (int j = 0; j < k; j++){
                if(array.get(j)> array.get(maxIndex)){
                    maxIndex = j;
                    array.set(maxIndex, array.get(j));
                //    System.out.println("+++++"+i);

                }
            }
            if (array.get(maxIndex) > array.get(i)){         //Swap item if max > array[i]

                double temp = array.get(maxIndex);
                array.set(maxIndex, array.get(i));
                array.set(i, temp);
             //   System.out.println("***"+i);
            }
        }
        for (int q = 0; q < k; q++){                            //Print output
            System.out.print(array.get(q) + " ");
        }
    }

    public static List<Integer> selectKthIndex(ArrayList<Double> arr, int k) {
        if (arr == null || arr.size() <= k)
            throw new IllegalArgumentException();

        List<Integer> splitArr = new ArrayList<Integer>();

        int from = 0, to = arr.size() - 1;

     //   System.out.println("To: "+to);
        // ***ADDED: create and fill indices array
        ArrayList<Integer> indices = new ArrayList<Integer>(arr.size());
        for (int i = 0; i < arr.size(); i++)
            indices.add(i, i);

        // if from == to we reached the kth element
        while (from < to) {
            int r = from, w = to;
            Double mid = arr.get((r + w) / 2);

            // stop if the reader and writer meets
            while (r < w) {

                if (arr.get(r) >= mid) { // put the large values at the end
                //    System.out.println("Dentro if!");
                    Double tmp = arr.get(w);
                 //   System.out.println(arr.get(w));
                    arr.set(w, arr.get(r));
                    arr.set(r, tmp);
                    // *** ADDED: here's the only place where arr is changed
                    // change indices array in the same way
                //    System.out.println(indices);
                    int tmp2 = indices.get(w);
                 //   System.out.println("Dopo di tmp2");
                    indices.set(w, indices.get(r));
                    indices.set(r, tmp2);
                    w--;
                 //   System.out.println("Fine if!!!");
                } else { // the value is smaller than the pivot, skip
                    r++;
                }
            }

            // if we stepped up (r++) we need to step one down
            if (arr.get(r) > mid)
                r--;

            // the r pointer is on the end of the first k elements
            if (k <= r) {
                to = r;
            } else {
                from = r + 1;
            }
        }

        // *** CHANGED: return indices[k] instead of arr[k]
        //System.out.println("-------------");
        //System.out.println(indices);
        splitArr = indices.subList(0,k);
        return splitArr;
    }

    public static void main(String args[])
    {
        DeliveryTime delT = new DeliveryTime("deliveryTime_ist2.csv");
        delT.printDelTimes();
    /*    System.out.println(findMinIndex(delT.getTime()));
        System.out.println(minIndex(delT.getTime()));*/

   /*     System.out.println("??????");
        kLargest(delT.getTime(), 10);*/
     /*   System.out.println("??????");
        kSmallest(delT.getTime(), 10);*/
        DeliveryTime delT2 = new DeliveryTime("deliveryTime_ist2.csv");
        delT2.printDelTimes();
     /*   System.out.println(findMinIndex(delT2.getTime()));
        System.out.println(minIndex(delT2.getTime()));*/

     //   System.out.println("£££££££££££££");
        System.out.println("%%%"+selectKthIndex(delT2.getTime(), 36));
     /*   System.out.println("%%%"+selectKthIndex(delT2.getTime(), 1));
        System.out.println("%%%"+selectKthIndex(delT2.getTime(), 2));
        System.out.println("%%%"+selectKthIndex(delT2.getTime(), 3));*/
    }
}
