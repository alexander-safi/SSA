package Simulation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class CSVWriter
{
    private  File file;
    private String fileName;
    private String directoryPath;
    public CSVWriter(String fileName)
    {
        directoryPath = "data/";
        this.fileName = fileName;

        file = new File(directoryPath+ fileName);
    }
    public  void write(int[] values)
    {
        try {
            FileWriter writer = new FileWriter(file);

            for(int s : values)
            {
                writer.write(s + "\n");
            }

            writer.close();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public  void write(ArrayList<Integer> values)
    {
        try {
            FileWriter writer = new FileWriter(file);

            for(int s : values)
            {
                writer.write(s + "\n");
            }

            writer.close();
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public  void write(String[] wordArray)
    {
        try {
            FileWriter writer = new FileWriter(file);

            for(String s : wordArray)
            {
                writer.write(s + "\n");
            }

            writer.close();
        } catch (IOException e) {

            e.printStackTrace();
        }


    }




}
