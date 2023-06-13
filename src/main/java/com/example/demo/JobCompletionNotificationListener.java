package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dao.DetailRecordRepository;
import com.example.demo.dao.FileOutDetailRecord;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RestController
@CrossOrigin(origins="*")
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;
    
    @Autowired
    private DetailRecordRepository detailRecordRepository;


    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    @Override
    public void afterJob(JobExecution jobExecution)
    {
        if(jobExecution.getStatus() == BatchStatus.COMPLETED)
            log.info("job completed");
    }


    @GetMapping("/startJob")
    public BatchStatus load() throws Exception{
        Map<String, JobParameter> map = new HashMap<String, JobParameter>();
        map.put("time", new JobParameter(System.currentTimeMillis()));
        JobParameters jobParameters = new JobParameters(map);
        JobExecution jobExecution = jobLauncher.run(job, jobParameters);
        while(jobExecution.isRunning())
        {
            System.out.println("....");
        }


        return jobExecution.getStatus();

    }
    
    @GetMapping("/output")
    public List<FileOutDetailRecord> getDetailRecords()
    {
    	return detailRecordRepository.findAll();
    }
}
