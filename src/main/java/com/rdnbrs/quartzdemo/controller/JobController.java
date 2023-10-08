package com.rdnbrs.quartzdemo.controller;

import com.rdnbrs.quartzdemo.dto.MessageDto;
import com.rdnbrs.quartzdemo.entity.JobInfo;
import com.rdnbrs.quartzdemo.service.SchedulerJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class JobController {

    private final SchedulerJobService scheduleJobService;

    @PostMapping(value = "/saveOrUpdate")
    public Object saveOrUpdate(@RequestBody JobInfo job) {
        log.info("params, job = {}", job);
        MessageDto message = MessageDto.failure();
        try {
            scheduleJobService.saveOrupdate(job);
            message = MessageDto.success();
        } catch (Exception e) {
            message.setMsg(e.getMessage());
            log.error("updateCron ex:", e);
        }
        return message;
    }

    @PostMapping("/metaData")
    public Object metaData() throws SchedulerException {
        SchedulerMetaData metaData = scheduleJobService.getMetaData();
        return metaData;
    }

    @PostMapping("/getAllJobs")
    public Object getAllJobs() throws SchedulerException {
        List<JobInfo> jobList = scheduleJobService.getAllJobList();
        return jobList;
    }

    @PostMapping(value = "/runJob")
    public Object runJob(@RequestBody JobInfo job) {
        log.info("params, job = {}", job);
        MessageDto message = MessageDto.failure();
        try {
            scheduleJobService.startJobNow(job);
            message = MessageDto.success();
        } catch (Exception e) {
            message.setMsg(e.getMessage());
            log.error("runJob ex:", e);
        }
        return message;
    }

    @PostMapping(value = "/pauseJob")
    public Object pauseJob(@RequestBody JobInfo job) {
        log.info("params, job = {}", job);
        MessageDto message = MessageDto.failure();
        try {
            scheduleJobService.pauseJob(job);
            message = MessageDto.success();
        } catch (Exception e) {
            message.setMsg(e.getMessage());
            log.error("pauseJob ex:", e);
        }
        return message;
    }

    @PostMapping(value = "/resumeJob")
    public Object resumeJob(@RequestBody JobInfo job) {
        log.info("params, job = {}", job);
        MessageDto message = MessageDto.failure();
        try {
            scheduleJobService.resumeJob(job);
            message = MessageDto.success();
        } catch (Exception e) {
            message.setMsg(e.getMessage());
            log.error("resumeJob ex:", e);
        }
        return message;
    }

    @PostMapping(value = "/deleteJob")
    public Object deleteJob(@RequestBody JobInfo job) {
        log.info("params, job = {}", job);
        MessageDto message = MessageDto.failure();
        try {
            scheduleJobService.deleteJob(job);
            message = MessageDto.success();
        } catch (Exception e) {
            message.setMsg(e.getMessage());
            log.error("deleteJob ex:", e);
        }
        return message;
    }
}
