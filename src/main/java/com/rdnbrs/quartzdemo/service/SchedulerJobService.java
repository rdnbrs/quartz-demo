package com.rdnbrs.quartzdemo.service;

import com.rdnbrs.quartzdemo.entity.JobInfo;
import com.rdnbrs.quartzdemo.job.TestJob1;
import com.rdnbrs.quartzdemo.repository.JobInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Slf4j
@Transactional
@Service
public class SchedulerJobService {

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    private JobInfoRepository repository;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private JobScheduleCreatorService scheduleCreator;

    public SchedulerMetaData getMetaData() throws SchedulerException {
        SchedulerMetaData metaData = scheduler.getMetaData();
        return metaData;
    }

    public List<JobInfo> getAllJobList() {
        return repository.findAll();
    }

    public boolean deleteJob(JobInfo jobInfo) {
        try {
            JobInfo getJobInfo = repository.findByJobName(jobInfo.getJobName());
            repository.delete(getJobInfo);
            log.info(">>>>> jobName = [" + jobInfo.getJobName() + "]" + " deleted.");
            return schedulerFactoryBean.getScheduler().deleteJob(new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
        } catch (SchedulerException e) {
            log.error("Failed to delete job - {}", jobInfo.getJobName(), e);
            return false;
        }
    }

    public boolean pauseJob(JobInfo jobInfo) {
        try {
            JobInfo getJobInfo = repository.findByJobName(jobInfo.getJobName());
            getJobInfo.setJobStatus("PAUSED");
            repository.save(getJobInfo);
            schedulerFactoryBean.getScheduler().pauseJob(new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
            log.info(">>>>> jobName = [" + jobInfo.getJobName() + "]" + " paused.");
            return true;
        } catch (SchedulerException e) {
            log.error("Failed to pause job - {}", jobInfo.getJobName(), e);
            return false;
        }
    }

    public boolean resumeJob(JobInfo jobInfo) {
        try {
            JobInfo getJobInfo = repository.findByJobName(jobInfo.getJobName());
            getJobInfo.setJobStatus("RESUMED");
            repository.save(getJobInfo);
            schedulerFactoryBean.getScheduler().resumeJob(new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
            log.info(">>>>> jobName = [" + jobInfo.getJobName() + "]" + " resumed.");
            return true;
        } catch (SchedulerException e) {
            log.error("Failed to resume job - {}", jobInfo.getJobName(), e);
            return false;
        }
    }

    public boolean startJobNow(JobInfo jobInfo) {
        try {
            JobInfo getJobInfo = repository.findByJobName(jobInfo.getJobName());
            getJobInfo.setJobStatus("SCHEDULED & STARTED");
            repository.save(getJobInfo);
            schedulerFactoryBean.getScheduler().triggerJob(new JobKey(jobInfo.getJobName(), jobInfo.getJobGroup()));
            log.info(">>>>> jobName = [" + jobInfo.getJobName() + "]" + " scheduled and started now.");
            return true;
        } catch (SchedulerException e) {
            log.error("Failed to start new job - {}", jobInfo.getJobName(), e);
            return false;
        }
    }

    @SuppressWarnings("deprecation")
    public void saveOrupdate(JobInfo scheduleJob) throws Exception {
        if (scheduleJob.getCronExpression().length() > 0) {
            scheduleJob.setJobClass(TestJob1.class.getName());
            scheduleJob.setCronJob(true);
        }
        if (StringUtils.isEmpty(scheduleJob.getJobId())) {
            log.info("Job Info: {}", scheduleJob);
            scheduleNewJob(scheduleJob);
        } else {
            updateScheduleJob(scheduleJob);
        }
        scheduleJob.setDesc("i am job number " + scheduleJob.getJobId());
        scheduleJob.setInterfaceName("interface_" + scheduleJob.getJobId());
        log.info(">>>>> jobName = [" + scheduleJob.getJobName() + "]" + " created.");
    }

    @SuppressWarnings("unchecked")
    private void scheduleNewJob(JobInfo jobInfo) {
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();

            JobDetail jobDetail = JobBuilder
                    .newJob((Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()))
                    .withIdentity(jobInfo.getJobName(), jobInfo.getJobGroup()).build();
            if (!scheduler.checkExists(jobDetail.getKey())) {

                jobDetail = scheduleCreator.createJob(
                        (Class<? extends QuartzJobBean>) Class.forName(jobInfo.getJobClass()), false, context,
                        jobInfo.getJobName(), jobInfo.getJobGroup());

                Trigger trigger;
                if (jobInfo.getCronJob()) {
                    trigger = scheduleCreator.createCronTrigger(jobInfo.getJobName(), new Date(),
                            jobInfo.getCronExpression(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
                } else {
                    trigger = scheduleCreator.createSimpleTrigger(jobInfo.getJobName(), new Date(),
                            jobInfo.getRepeatTime(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
                }
                scheduler.scheduleJob(jobDetail, trigger);
                jobInfo.setJobStatus("SCHEDULED");
                repository.save(jobInfo);
                log.info(">>>>> jobName = [" + jobInfo.getJobName() + "]" + " scheduled.");
            } else {
                log.error("scheduleNewJobRequest.jobAlreadyExist");
            }
        } catch (ClassNotFoundException e) {
            log.error("Class Not Found - {}", jobInfo.getJobClass(), e);
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void updateScheduleJob(JobInfo jobInfo) {
        Trigger newTrigger;
        if (jobInfo.getCronJob()) {
            newTrigger = scheduleCreator.createCronTrigger(jobInfo.getJobName(), new Date(),
                    jobInfo.getCronExpression(), SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        } else {
            newTrigger = scheduleCreator.createSimpleTrigger(jobInfo.getJobName(), new Date(), jobInfo.getRepeatTime(),
                    SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        }
        try {
            schedulerFactoryBean.getScheduler().rescheduleJob(TriggerKey.triggerKey(jobInfo.getJobName()), newTrigger);
            jobInfo.setJobStatus("EDITED & SCHEDULED");
            repository.save(jobInfo);
            log.info(">>>>> jobName = [" + jobInfo.getJobName() + "]" + " updated and scheduled.");
        } catch (SchedulerException e) {
            log.error(e.getMessage(), e);
        }
    }
}
