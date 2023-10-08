<h1>Service samples</h1>
<p>runjob trigger has some problems. These problems will be fixed later</p>
<h4>create job</h4>
<p>http://localhost:8080/api/saveOrUpdate</p>
{
    "jobName": "TestJob1",
    "jobGroup": "mail",
    "jobStatus": "NORMAL",
    "jobClass": "com.rdnbrs.quartzdemo.job.TestJob1",
    "cronExpression": "0 0/2 * * * ?",
    "desc": "TestJob1 desc",
    "interfaceName": "TestJob1",
    "repeatTime": 10,
    "cronJob": true
}
<h4>run created job</h4>
<p>http://localhost:8080/api/runJob</p>
{
    "jobName": "TestJob1"
}

<h4>update job</h4>
<p>http://localhost:8080/api/saveOrUpdate</p>
{
    "jobName": "TestJob1",
    "jobGroup": "mail",
    "jobStatus": "NORMAL",
    "jobClass": "com.rdnbrs.quartzdemo.job.TestJob1",
    "cronExpression": "0 0/2 * * * ?",
    "desc": "TestJob1 desc",
    "interfaceName": "TestJob1",
    "repeatTime": 10,
    "cronJob": true
}
