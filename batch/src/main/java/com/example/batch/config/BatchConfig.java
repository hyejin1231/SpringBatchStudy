package com.example.batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.example.batch.entity.Person;
import com.example.batch.repository.PersonRepository;

import lombok.RequiredArgsConstructor;

/**
 * Job, Step 설정
 */
@Configuration
//@EnableBatchProcessing // Spring Batch 5.x 필수 x
@RequiredArgsConstructor
public class BatchConfig
{

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager; // 트랜잭션 관리 담당
	private final PersonRepository personRepository;

	/**
	 * csv 파일 데이터 읽어오는 Reader
	 * @return
	 */
	@Bean
	public FlatFileItemReader<Person> reader()
	{
		return new FlatFileItemReaderBuilder<Person>()
				.name("personItemReader")
				.resource(new ClassPathResource("people.csv"))
				.delimited()// CSV 파일의 구분자를 기반으로 데이터 읽도록 설정
				.names("name", "age", "phoneNo") // CSV 파일의 각 열 이름 지정
				.linesToSkip(1) // 첫번째 줄 헤더 무시
				.fieldSetMapper(new BeanWrapperFieldSetMapper<>(){
					{
						setTargetType(Person.class); // CSV 파일에서 읽은 데이터를 Person 객체로 매핑
					}
				})
//				.fieldSetMapper(fieldSet -> new Person(fieldSet.readString("name"), fieldSet.readInt("age"), fieldSet.readString("phoneNo")))
				.build();
	}

	/**
	 * Person phoneNo 데이터 변환 processor
	 * @return
	 */
	@Bean
	public ItemProcessor<Person, Person> processor()
	{
		return person -> {
			person.setPhoneNo(person.getPhoneNo().replaceAll("-", ""));
			return person;
		};
	}

	/**
	 * 변환한 데이터 DB에 저장
	 * @return
	 */
	@Bean
	public ItemWriter<Person> writer()
	{
		return personRepository::saveAll;
	}

	/**
	 * Step 설정 : Reader -> Processor -> Writer
	 * @return
	 */
	@Bean
	public Step step()
	{
		return new StepBuilder("step1", jobRepository)
				.<Person, Person>chunk(10, transactionManager) // 한 번에 10개의 데이터 읽고, 처리한 뒤 커밋
				.reader(reader())
				.processor(processor())
				.writer(writer())
				.build();
	}

	/**
	 * Job 설정 : Step 실행 설정
	 * @return
	 */
	@Bean
	public Job importPersonJob()
	{
		return new JobBuilder("importPersonJob", jobRepository)
				.start(step())
				.build();
	}
}
