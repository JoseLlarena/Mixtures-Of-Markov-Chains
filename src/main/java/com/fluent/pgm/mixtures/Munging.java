package com.fluent.pgm.mixtures;

import au.com.bytecode.opencsv.CSVReader;
import com.fluent.collections.FList;
import com.fluent.util.WriteLines;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.fluent.collections.Lists.parse;
import static com.fluent.util.ReadLines.Read_Lines;
import static com.fluent.util.WriteLines.Write_Lines;
import static java.lang.Math.random;
import static java.lang.System.out;

public class Munging
{
    public static void main(String[] args) throws IOException
    {
        String data = "C:/Users/Jose/project-workspace/Sequensir/src/test/resources/tweets.txt";
        FList<String> lines = Read_Lines.from(Paths.get(data), line -> parse(line, "\\s+").apply(word ->

                word.matches("[A-Z']+[\\?\\.\\,\\!]*") ? "U" :
                        word.matches("[a-z']+[\\?\\.\\,\\!]*") ? "L" :
                                word.matches("[A-Z'][a-z']+[\\?\\.\\,\\!]*") ? "C" :
                                        word.matches("@mention\\:?") ? "M" :
                                                word.matches("\\{link\\}") ? "X" :
                                                        word

        ).toString("%s ").trim(), line -> random() > .9);

        out.println(lines.size());


        Write_Lines.to(Paths.get("C:/Users/Jose/project-workspace/Sequensir/src/test/resources/tweets" +
                "-processed" +
                "" +
                ".txt"), lines);

    }

    private static void clean_tweets() throws IOException
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

                if (split.length == 1)
                    lines.add(nextLine[1]);
                else if (split.length > 1 && split[0].isEmpty())
                    lines.add(split[1]);
                else if (split.length > 1 && !split[0].isEmpty())
                    lines.add(split[0]);
                else
                    out.println(nextLine[1]);
            }

        }

        WriteLines.write("C:\\Users\\Jose\\project-workspace\\Sequensir\\src\\main" +
                "\\resources\\tweets.txt", lines);
    }
}
