package com.example.demo;

import com.example.demo.dao.DetailRecordRepository;
import com.example.demo.dao.FileOutDetailRecord;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DetailRecordItemWriter implements ItemWriter<FileOutDetailRecord> {

    @Autowired
    private DetailRecordRepository detailRecordRepository;

    @Override
    public void write(List<? extends FileOutDetailRecord> items) throws Exception {
        detailRecordRepository.saveAll(items);
    }
}
