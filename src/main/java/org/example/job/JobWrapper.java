package org.example.job;

public class JobWrapper {
  IJob job;
  long startTimeInEpochMilliSecond;
  long recurringIntervalMilliSecond;

  public JobWrapper(IJob job, long startTimeInEpoch, long recurringIntervalMilliSecond) {
    this.job = job;
    this.startTimeInEpochMilliSecond = startTimeInEpochMilliSecond;
    this.recurringIntervalMilliSecond = recurringIntervalMilliSecond;
  }

  public IJob getJob() {
    return job;
  }

  public void setJob(IJob job) {
    this.job = job;
  }

  public long getStartTimeInEpochMilliSecond() {
    return startTimeInEpochMilliSecond;
  }

  public void setStartTimeInEpochMilliSecond(int startTimeInEpochMilliSecond) {
    this.startTimeInEpochMilliSecond = startTimeInEpochMilliSecond;
  }

  public long getRecurringIntervalMilliSecond() {
    return recurringIntervalMilliSecond;
  }

  public void setRecurringIntervalMilliSecond(int recurringIntervalMilliSecond) {
    this.recurringIntervalMilliSecond = recurringIntervalMilliSecond;
  }
}
