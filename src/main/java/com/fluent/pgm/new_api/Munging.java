package com.fluent.pgm.new_api;

import au.com.bytecode.opencsv.CSVReader;
import com.fluent.util.WriteLines;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Munging
{
    public static void main(String[] args) throws IOException
    {
        CSVReader reader = new CSVReader(new FileReader("C:\\Users\\Jose\\project-workspace\\Sequensir\\src\\main" +
                "\\resources\\train.csv"));

        String[] nextLine;

        List<String> lines = new ArrayList<>();
        int line_count = 0;
        while ((nextLine = reader.readNext()) != null)
        {
            if (line_count++ > 0)
            {
                String[] split = nextLine[1].split("\"");


                if(split.length == 1)
                    lines.add(nextLine[1]);
                else if(split.length > 1 && split[0].isEmpty())
                    lines.add(split[1]);
                else if(split.length > 1 && !split[0].isEmpty())
                    lines.add(split[0]);
                else
                    System.out.println(nextLine[1]);
            }

        }

        WriteLines.write("C:\\Users\\Jose\\project-workspace\\Sequensir\\src\\main" +
                "\\resources\\tweets.txt", lines);
    }
}
