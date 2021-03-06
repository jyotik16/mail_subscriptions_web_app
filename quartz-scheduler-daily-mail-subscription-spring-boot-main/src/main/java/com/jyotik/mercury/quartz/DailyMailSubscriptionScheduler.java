package com.jyotik.mercury.quartz;

import org.quartz.ObjectAlreadyExistsException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Component;

import com.jyotik.mercury.exception.EmailAlreadyRegisteredException;
import com.jyotik.mercury.exception.GenericServerException;
import com.jyotik.mercury.quartz.job.detail.DailySubscriptionMailSenderJobDetail;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@AllArgsConstructor
@Slf4j
public class DailyMailSubscriptionScheduler {

	private final Scheduler scheduler;
	private final DailySubscriptionMailSenderJobDetail dailySubscriptionMailSenderJobDetail;

	public void start() throws SchedulerException {
		this.scheduler.start();
		this.scheduler.addJob(dailySubscriptionMailSenderJobDetail.getJobDetail(), false);
	}

	public void addTriggerInDailyMailSubscriptionService(Trigger trigger) {
		try {
			this.scheduler.scheduleJob(trigger);
			log.info("Successfully scheduled trigger with identity: {}", trigger.getKey());
		} catch (ObjectAlreadyExistsException exception) {
			log.error("Daily mail sender Trigger Already Added!");
			throw new EmailAlreadyRegisteredException();
		} catch (SchedulerException e) {
			log.error("Unable to add trigger {}", e);
			throw new GenericServerException();
		}
	}

	public void removeTrigger(final String email) {
		try {
			this.scheduler.unscheduleJob(new TriggerKey(email));
		} catch (SchedulerException e) {
			log.error("Unable to unschedule email from daily mail subscription service: {}", e);
			throw new GenericServerException();
		}
	}

	public Boolean triggerWithEmailScheduled(final String emailId) {
		try {
			return this.scheduler.checkExists(new TriggerKey(emailId));
		} catch (SchedulerException e) {
			log.error("Unable to check for trigger existence: {}", e);
			throw new GenericServerException();
		}
	}

}