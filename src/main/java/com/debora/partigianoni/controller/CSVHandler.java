package com.debora.partigianoni.controller;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.io.File;

public class CSVHandler {

    public CSVReader readCSV(boolean isDelivery, String filename)
    {
        String pathRel;
        if(isDelivery)
            pathRel = "/src/csv/deliveryTime/";
        else pathRel = "/src/csv/distanceMatrix/";
        pathRel = pathRel.concat(filename);
        String filePath = new File("").getAbsolutePath();
        String pathAbs = filePath.concat(pathRel);
        CSVReader reader = null;
        try{
            reader = new CSVReader(new FileReader(pathAbs));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return reader;
    }


    //funzione createGraph()

    public static void main(String[] args)
    {
        CSVHandler csvHandler = new CSVHandler();
        CSVReader deliveryReader = csvHandler.readCSV(true, "deliveryTime_ist2.csv");
        CSVReader distanceReader = csvHandler.readCSV(false, "distanceMatrix_ist2.csv");
        String[] lineDelivery;
        String[] lineDistance;
        System.out.println("DELIVERY:\n");
        try{
            lineDelivery = deliveryReader.readNext();
            lineDistance = distanceReader.readNext();
            System.out.println(lineDelivery.length);

            int j=0;
            int k=0;
            while ((lineDelivery = deliveryReader.readNext()) != null) {
                j++;
                System.out.println("Del "+j+":"+lineDelivery[1]); //[id= " + line[0] + ", code= " + line[1] + " , name=" + line[2] + "]");
            }
            System.out.println("DISTANCE:\n");
            System.out.println(lineDistance.length);

            while ((lineDistance = distanceReader.readNext()) != null) {
                k++;
                System.out.println("Dist "+k+":"+lineDistance[1]); //[id= " + line[0] + ", code= " + line[1] + " , name=" + line[2] + "]");
            }
            System.out.println(distanceReader.getLinesRead());
        }catch (IOException e) {
            e.printStackTrace();
        }


        //Inserimento file da command line
/*        Scanner scanner = new Scanner(System.in);
        System.out.println("Insert delivery file name: ");
        String csvDelivery = scanner.next();
        System.out.println("Insert distanceMatrix file name: ");
        String csvDistance = scanner.next();

   //     String csvFile = "C:/Users/Debby/IdeaProjects/tdg2/src/main/java/com/debora/partigianoni/read/deliveryTime_ist2.csv";
        String pathDeliveryRel  = "/src/csv/deliveryTime/";//deliveryTime_ist2.csv";
        String pathDistanceRel  = "/src/csv/distanceMatrix/";//deliveryTime_ist2.csv";
        pathDeliveryRel = pathDeliveryRel.concat(csvDelivery);
        pathDistanceRel = pathDistanceRel.concat(csvDistance);
*/

/*        String pathDeliveryRel = "/src/csv/deliveryTime/deliveryTime_ist2.csv";
        String pathDistanceRel = "/src/csv/distanceMatrix/distanceMatrix_ist2.csv";
        String filePath = new File("").getAbsolutePath();
        //System.out.println ("++++"+filePath);
        //System.out.println ("++++"+csvFile);
        String pathDeliveryAbs = filePath.concat(pathDeliveryRel);
        String pathDistanceAbs = filePath.concat(pathDistanceRel);


        CSVReader readerDelivery = null;
        CSVReader readerDistance = null;
    try {
            System.out.println("HHHH");
            System.out.println(filePath);
        //    reader = new CSVReader(new InputStreamReader(CSVReaderExample.class.getResourceAsStream(csvFile)));
            readerDelivery = new CSVReader(new FileReader(pathDeliveryAbs));
            readerDistance = new CSVReader(new FileReader(pathDistanceAbs));

            System.out.println("pppppp");

            String[] lineDelivery;
            String[] lineDistance;
            System.out.println("DELIVERY:\n");
            lineDelivery = readerDelivery.readNext();
            lineDistance = readerDistance.readNext();
            System.out.println(lineDelivery.length);

            while ((lineDelivery = readerDelivery.readNext()) != null) {
                System.out.println("Del "+lineDelivery[1]); //[id= " + line[0] + ", code= " + line[1] + " , name=" + line[2] + "]");
            }
            System.out.println("DISTANCE:\n");
            System.out.println(lineDistance.length);
            while ((lineDistance = readerDistance.readNext()) != null) {
                    System.out.println("Dist "+lineDistance[239]); //[id= " + line[0] + ", code= " + line[1] + " , name=" + line[2] + "]");
                }
            } catch (IOException e) {
            e.printStackTrace();
        }*/

        System.out.println("Ciaoooooo");
    }
}
