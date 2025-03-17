package org.example;

import org.example.job.IJob;
import org.example.job.PrintJob;
import org.example.scheduler.IJobScheduler;
import org.example.scheduler.JobScheduler;
import java.time.Instant;

public class Driver {
  public static void main(String[] args) throws InterruptedException {
    IJobScheduler jobScheduler = new JobScheduler(3);
    long now = Instant.now().toEpochMilli();
    IJob job1 = new PrintJob("I am coco");
    IJob job2 = new PrintJob("I am Ujjval");
    IJob job3 = new PrintJob("I am Utkarsh");
    IJob job4 = new PrintJob("I am sweta");
    jobScheduler.addJob(job1, now + 100, now + 5000);
    jobScheduler.addJob(job2, now + 100, now + 10000);
    Thread.sleep(20000);
  }
}