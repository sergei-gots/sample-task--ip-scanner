package org.example;

import java.io.FileWriter;

import static java.lang.System.out;

public class ReportCreator {
    private FileWriter fileWriter;

    public void init (String filename) throws Exception {
        fileWriter = new FileWriter(filename);
    }

    public void check() {
        if (fileWriter == null) {
            throw new IllegalStateException("First, initialize the FileWriter instance using the FileWriter.init(String filename) method.");
        }
    }

    public void write(String s) throws Exception {
        check();

        fileWriter.write(s);
    }

    public void close() {
        check();
        try {
            fileWriter.close();
        }
        catch (Exception e) {
            e.printStackTrace(out);
        }
    }
}
