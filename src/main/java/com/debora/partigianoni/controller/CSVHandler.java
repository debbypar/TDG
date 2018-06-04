package com.debora.partigianoni.controller;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CSVHandler {

    public CSVReader readCSV(boolean isDelivery, String filename, String folderIndex)
    {
        String pathRel;
        if(isDelivery)
            pathRel = "/src/csv/deliveryTime/";
        else pathRel = "/src/csv/distanceMatrix".concat(folderIndex).concat("/");
        pathRel = pathRel.concat(filename);
        String filePath = new File("").getAbsolutePath();
        System.out.println("---"+filePath);
        String pathAbs = filePath.concat(pathRel);
        System.out.println("***"+pathAbs);
        CSVReader reader = null;
        try{
            reader = new CSVReader(new FileReader(pathAbs));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return reader;
    }

    /*public void writeCSV(int[] test, String filename) throws IOException{
        //StringWriter writer = new StringWriter();
        String pathRel = "/src/csv/results/";
        pathRel = pathRel.concat(filename);
        String filePath = new File("").getAbsolutePath();
        String pathAbs = filePath.concat(pathRel);
        CSVWriter writer = new CSVWriter(new FileWriter(pathAbs));

        String prova = Arrays.toString(test);
        for(int i=0; i< test.length; i++)
        {
            //writer.writeNext(Arrays.toString(test));
        }
        System.out.println("CSV File written successfully line by line");
        writer.close();

    }*/


    /*public void writeOnFile(int[] test, String fileName)
    {
        String pathRel = "/src/csv/results/";
        pathRel = pathRel.concat(fileName);
        String filePath = new File("").getAbsolutePath();
        String pathAbs = filePath.concat(pathRel);
        try {
            FileOutputStream file = new FileOutputStream(pathAbs);
            for (int i = 0; i < test.length; i++)
                file.write(test[i]);
            file.close();
        } catch (IOException e) {
            System.out.println("Error - " + e.toString());
        }
    }*/

    public void writeOnFile(String folderPathInResults, double[] X, int[] z1, int[] z2, int[] z3, int[] w, Integer[][] Y)
    {
        String pathRel = "/src/csv/results/";
        pathRel = pathRel.concat(folderPathInResults);
        String filePath = new File("").getAbsolutePath();
        String pathAbs = filePath.concat(pathRel);
        new File(pathAbs).mkdirs();

        String fileX = pathAbs.concat("X.csv");
        String fileY = pathAbs.concat("Y.csv");
        String fileZ1 = pathAbs.concat("Z1.csv");
        String fileZ2 = pathAbs.concat("Z2.csv");
        String fileZ3 = pathAbs.concat("Z3.csv");
        String fileW = pathAbs.concat("W.csv");
        String fileSummary = pathAbs.concat("Summary.csv");

        int objective, sumZ1, sumZ2, sumZ3, sumW;

        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < Y.length; i++)//for each row
        {
            for(int j = 0; j < Y.length; j++)//for each column
            {
                builder.append(Y[i][j]+"");//append to the output string
                if(j < Y.length - 1)//if this is not the last row element
                    builder.append(",");//then add comma (if you don't like commas you can use spaces)
            }
            builder.append("\n");//append new line at the end of the row
        }

        try {
            Files.write(Paths.get(fileX), Arrays.toString(X).getBytes());
            Files.write(Paths.get(fileZ1), Arrays.toString(z1).getBytes());
            Files.write(Paths.get(fileZ2), Arrays.toString(z2).getBytes());
            Files.write(Paths.get(fileZ3), Arrays.toString(z3).getBytes());
            Files.write(Paths.get(fileW), Arrays.toString(w).getBytes());

            //Summary
            FileWriter writerSum = new FileWriter(fileSummary);
            sumZ1 = sumInArray(z1);
            sumZ2 = sumInArray(z2);
            sumZ3 = sumInArray(z3);
            sumW = sumInArray(w);
            objective = sumZ1+2*sumZ2+3*sumZ3+10*sumW;
            writerSum.append("Objective: "+Integer.toString(objective)+"\n");
            writerSum.append("Z1: "+Integer.toString(sumZ1)+"\n");
            writerSum.append("Z2: "+Integer.toString(sumZ2)+"\n");
            writerSum.append("Z3: "+Integer.toString(sumZ3)+"\n");
            writerSum.append("W: "+Integer.toString(sumW)+"\n");

            writerSum.flush();
            writerSum.close();

            //Matrice Y
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileY));
            writer.write(builder.toString());//save the string representation of the board
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
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

    public static void main(String[] args)
    {
        CSVHandler csvHandler = new CSVHandler();
        CSVReader deliveryReader = csvHandler.readCSV(true, "deliveryTime_ist2.csv", "");
        CSVReader distanceReader = csvHandler.readCSV(false, "distanceMatrix_ist2.csv", "1");
        String[] lineDelivery;
        String[] lineDistance;
     //   System.out.println("DELIVERY:\n");
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
       //     System.out.println("DISTANCE:\n");
       //     System.out.println(lineDistance.length);

            while ((lineDistance = distanceReader.readNext()) != null) {
                k++;
          //      System.out.println("Dist "+k+":"+lineDistance[1]); //[id= " + line[0] + ", code= " + line[1] + " , name=" + line[2] + "]");
            }
        //    System.out.println(distanceReader.getLinesRead());
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

     //   System.out.println("Ciaoooooo");
    }
}
