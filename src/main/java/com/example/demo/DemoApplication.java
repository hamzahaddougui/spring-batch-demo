package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@SpringBootApplication
public class DemoApplication {
    String file="file.csv";

    public DemoApplication()  {
    }

    public void generateFile() throws IOException {
        PrintWriter writer = new PrintWriter("file.csv", "UTF-8");
        String line="DETAIL12968734105648LKjuhg5" ;
        // process a file starting from 10 records until in some cases 1000000 records
        for(int count=0; count<1000000;count++)
        {
            writer.println(line);
        }
        writer.close();
        }
    
    public static void main(String[] args) throws IOException {

        SpringApplication.run(DemoApplication.class, args);
    }

}
