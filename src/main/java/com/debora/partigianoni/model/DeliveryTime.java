package com.debora.partigianoni.model;

import com.debora.partigianoni.controller.CSVHandler;
import com.opencsv.CSVReader;

import java.io.IOException;
import java.util.ArrayList;

public class DeliveryTime {
   // private final double[] time;
    private final ArrayList<Double> time;

    public DeliveryTime(String file)
    {
        CSVHandler csvHandler = new CSVHandler();
        CSVReader deliveryReader = csvHandler.readCSV(true, file);
        String[] lineDelivery;
      //  double[] time = null;
        ArrayList<Double> time = new ArrayList<Double>();
        int i=0;
        try {
            lineDelivery = deliveryReader.readNext(); //salto la prima riga del file excel con i nomi delle colonne.
            while ((lineDelivery = deliveryReader.readNext()) != null) {
            //    System.out.println("Del "+i+":"+lineDelivery[1]);
            //    System.out.println(Double.parseDouble(lineDelivery[1]));
                time.add(Double.parseDouble(lineDelivery[1]));
            //    System.out.println("Del "+i+":"+lineDelivery[1]);
                i++;
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("LUNGHEZZA: "+time.size());
        this.time = time;
    }

    public ArrayList<Double> getTime() {
        return time;
    }

    public void printDelTimes()
    {
        for(int i=0; i< time.size(); i++)
            System.out.println(i+": "+time.get(i));
    }

    public static void main(String args[]){
        DeliveryTime delT = new DeliveryTime("deliveryTime_ist2.csv");
        delT.printDelTimes();
    }
}
