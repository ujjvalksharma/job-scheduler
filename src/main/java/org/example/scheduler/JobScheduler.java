package org.example.scheduler;

import org.example.job.IJob;
import org.example.job.JobWrapper;

import java.time.Instant;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;

public class JobScheduler implements IJobScheduler {

  int totalWorkers;
  AtomicInteger totalAvailableWorkers;
  Thread jobDispatcherThread;
  AtomicBoolean isRunning;
  PriorityBlockingQueue<JobWrapper> pendingJobs;
  Set<IJob> setOfJobs;

  // Comparator to order jobs by scheduled start time.
  class JobWrapperComparator implements Comparator<JobWrapper> {
    @Override
    public int compare(JobWrapper j1, JobWrapper j2) {
      return Long.compare(j1.getStartTimeInEpochMilliSecond(), j2.getStartTimeInEpochMilliSecond());
    }
  }

  public JobScheduler(int totalWorkers) {
    this.totalWorkers = totalWorkers;
    this.isRunning = new AtomicBoolean(true);
    this.totalAvailableWorkers = new AtomicInteger(totalWorkers);
    this.pendingJobs = new PriorityBlockingQueue<>(totalWorkers, new JobWrapperComparator());
    this.setOfJobs = new HashSet<>();
    startDispatcher();
  }

  private void startDispatcher() {
    this.jobDispatcherThread = new Thread(() -> {
      while(isRunning.get()) {
        // Check the shared available workers.
        if (totalAvailableWorkers.get() == 0) {
          try {
            sleep(50);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
          continue;
        }

        long currentTimeInEpoch = Instant.now().toEpochMilli();
        // Poll from the original pendingJobs queue.
        JobWrapper jobWrapper = pendingJobs.peek();
        if (jobWrapper == null || jobWrapper.getStartTimeInEpochMilliSecond() > currentTimeInEpoch) {
          // No jobs ready yet.
          try {
            sleep(50);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
          continue;
        }
        System.out.println("pendingJobs: " + pendingJobs + " totalAvailableWorkers: " + totalAvailableWorkers);
        // Poll the job as it is ready to run.
        jobWrapper = pendingJobs.poll();
        // Decrement the available worker count once.
        totalAvailableWorkers.decrementAndGet();

        JobWrapper finalJobWrapper = jobWrapper;
        new Thread(() -> {
          try {
            // Optionally simulate some delay in execution.
            sleep(100);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
          // Execute the job.
          finalJobWrapper.getJob().execute();

          // Reschedule recurring job if applicable.
          if(setOfJobs.contains(finalJobWrapper.getJob()) && finalJobWrapper.getRecurringIntervalMilliSecond() > 0) {
            // Compute new start time relative to current time.
            long newStartTime = Instant.now().toEpochMilli() + finalJobWrapper.getRecurringIntervalMilliSecond() + 50000;
            pendingJobs.add(new JobWrapper(finalJobWrapper.getJob(), newStartTime, finalJobWrapper.getRecurringIntervalMilliSecond()));
          }
          // Mark this worker as available.
          totalAvailableWorkers.incrementAndGet();
        }).start();
      }
    });
    this.jobDispatcherThread.setDaemon(true);
    this.jobDispatcherThread.start();
  }

  @Override
  public void addJob(IJob job, long startTimeInEpochMilliSecond, long recurringIntervalMilliSecond) {
    JobWrapper jobWrapper = new JobWrapper(job, startTimeInEpochMilliSecond, recurringIntervalMilliSecond);
    pendingJobs.add(jobWrapper);
    setOfJobs.add(job);
  }

  @Override
  public void removeJob(IJob job) {
    setOfJobs.remove(job);
  }

  @Override
  public void shutDown() {
    this.isRunning.set(false);
    this.jobDispatcherThread.interrupt();
  }
}
