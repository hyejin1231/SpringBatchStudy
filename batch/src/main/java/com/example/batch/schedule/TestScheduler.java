package com.example.batch.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
//@Component
public class TestScheduler
{

	@Scheduled(fixedRate = 5000) // 5초 마다 작업 실행
	public void fixedRateTask()
	{
		log.info("Fixed Rate Task Executed !!");
	}

	@Scheduled(fixedDelay = 5000) // 이전 작업 종료 후 5초 뒤 실행
	public void fixedDelayTask()
	{
		log.info("Fixed Delay Task Executed !! ");
	}

	@Scheduled(cron = "0 0 12 * * ?") // 매일 정오 12시에 실행
	public void cronTask()
	{
		log.info("Cron Task Executed !! ");
	}
}
