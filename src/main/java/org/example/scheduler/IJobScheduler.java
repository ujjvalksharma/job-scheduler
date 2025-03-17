package org.example.scheduler;

import org.example.job.IJob;

public interface IJobScheduler {

  void addJob(IJob job, long startTimeInEpochMilliSecond, long recurringIntervalMilliSecond);

  void removeJob(IJob job);

  void shutDown();
}
