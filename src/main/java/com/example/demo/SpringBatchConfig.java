package com.example.demo;

import com.example.demo.dao.FileOutDetailRecord;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Date;
import com.example.demo.dao.FileInDetailRecord;
import com.example.demo.dao.HeaderRecord;
import com.example.demo.dao.TrailerRecord;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.item.file.transform.FixedLengthTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import javax.persistence.Transient;
import java.util.HashMap;
import java.util.Map;







@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {

@Autowired
private JobBuilderFactory jobBuilderFactory;

@Autowired
private StepBuilderFactory stepBuilderFactory;

@Autowired
private ItemReader<FileInDetailRecord> itemReader;

@Autowired
private ItemWriter<FileOutDetailRecord> itemWriter;

    @Bean
    public TaskExecutor taskExecutor(){
        return new SimpleAsyncTaskExecutor("spring_batch");
    }

    /**
     * set batch process: ++ steps
     * => step: 1 reader (read incoming data) , ++ processor , 1 writer
     * => processor n => processor m => processor k => processor l ======> final business logic operations done ready for writer
     * => writer: persist data on store
     */
    @Bean
    public Job balanceFileJob(JobCompletionNotificationListener jobCompletionNotificationListener)
    {
        Step step = stepBuilderFactory.get("step-load-data")
                .<FileInDetailRecord,FileOutDetailRecord>chunk(100)
                .reader(itemReader)
                .processor(detailItemProcessor())
                .writer(itemWriter)
                .taskExecutor(taskExecutor())
                .throttleLimit(100)
                .build();


        return jobBuilderFactory.get("file-in-loader-job")
                .listener(jobCompletionNotificationListener)
                .start(step).build();
    }


    @Bean
    public FlatFileItemReader<FileInDetailRecord> flatFileItemReader(@Value("${inputFileName}") File filePath) throws IOException {
        FlatFileItemReader<FileInDetailRecord> fileItemReader = new FlatFileItemReader<FileInDetailRecord>();
        fileItemReader.setName("item-reader");
        fileItemReader.setLinesToSkip(1);

        //File file = filePath.getFile();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
        String line ;
        File fileModified = new File("format.csv") ;

        PrintWriter writer = new PrintWriter(fileModified, "UTF-8");

        //FileOutputStream out = new FileOutputStream("C:\\Users\\lp-09hCbnWA3T5h6\\Documents\\file.txt");
         while ((line = bufferedReader.readLine()) != null) {
            if (!line.startsWith("TR",0)){
                //out.write(Integer.parseInt(line.toString()));
                if (!line.isEmpty()) {
                    writer.write(line);
                    writer.write("\n");
                }

            }


        }
        writer.close();

        fileItemReader.setResource(new FileUrlResource(String.valueOf(fileModified)));
        fileItemReader.setLineMapper(new SpringBatchConfig().lineMapper());
        return fileItemReader;
    }

    @Bean
    public PatternMatchingCompositeLineMapper lineMapper() throws IOException {
        PatternMatchingCompositeLineMapper lineMapper = new PatternMatchingCompositeLineMapper();

        // property1 <=> first_key
        // property2 <=> second_key
        // property3 <=> third_key
        FixedLengthTokenizer detailTokenizer = new FixedLengthTokenizer();
        detailTokenizer.setNames("first_key", "second_key", "third_key");
        detailTokenizer.setColumns(new Range(1,2),
                new Range(3,21),
                new Range(22,27)
        );


        Map<String, LineTokenizer> tokenizers = new HashMap<>();
        tokenizers.put("DETAIL*", detailTokenizer);
        lineMapper.setTokenizers(tokenizers);

        // detail fieldset mapper
        BeanWrapperFieldSetMapper<FileInDetailRecord> FileInfieldSetMapper = new BeanWrapperFieldSetMapper<FileInDetailRecord>();
        FileInfieldSetMapper.setTargetType(FileInDetailRecord.class);

        Map<String, FieldSetMapper> mappers = new HashMap<>();

        mappers.put("DETAIL*", FileInfieldSetMapper);
        
        lineMapper.setFieldSetMappers(mappers);

        return lineMapper;
    }




    @Bean
    public DetailRecordItemProcessor detailItemProcessor()
    {
        return new DetailRecordItemProcessor();
    }


}
