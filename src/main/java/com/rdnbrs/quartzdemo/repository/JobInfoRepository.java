package com.rdnbrs.quartzdemo.repository;


import com.rdnbrs.quartzdemo.entity.JobInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobInfoRepository extends JpaRepository<JobInfo, Long> {

    JobInfo findByJobName(String jobName);

}
