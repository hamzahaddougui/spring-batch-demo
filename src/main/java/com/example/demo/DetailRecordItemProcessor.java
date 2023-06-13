package com.example.demo;

import com.example.demo.dao.FileInDetailRecord;
import com.example.demo.dao.FileOutDetailRecord;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DetailRecordItemProcessor implements ItemProcessor<FileInDetailRecord, FileOutDetailRecord> {

    // fetch input file name from configuration file
    @Value("${inputFileName}")
    public String originalFileName;
    @Override
    public FileOutDetailRecord process(FileInDetailRecord item) throws Exception {

        FileOutDetailRecord fileOutDetailRecord = new FileOutDetailRecord();

        // set fileOutDetailRecord object properties
        return fileOutDetailRecord;
    }

    public String getFileName()
    {
      // as an example we have input file named: "users/folder/file.csv" => result will be newOriginalFileName= "file.csv"
      String newOriginalFileName = originalFileName.substring(originalFileName.length()-6,originalFileName.length());
      return newOriginalFileName;
    }
}
