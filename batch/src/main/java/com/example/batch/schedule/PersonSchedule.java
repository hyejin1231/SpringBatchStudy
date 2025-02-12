package com.example.batch.schedule;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PersonSchedule
{
	private final JobLauncher jobLauncher;
	private final Job importPersonJob;

	@Scheduled(cron = "0 */3 15-16 * * ?")
	public void startJob()
	{
		JobParameters jobParameters = new JobParametersBuilder()
				.addLong("time", System.currentTimeMillis())
				.toJobParameters();

		try
		{
			jobLauncher.run(importPersonJob, jobParameters);
		}
		catch (JobExecutionAlreadyRunningException
				| JobRestartException
				| JobInstanceAlreadyCompleteException
				| JobParametersInvalidException
				e)
		{
			log.error("startJob run error :{}", e.getMessage(), e);
		}
	}
}
