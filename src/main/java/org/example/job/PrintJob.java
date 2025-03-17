package org.example.job;

public class PrintJob implements IJob {
String text;
  public PrintJob(String text) {
    this.text = text;
  }

  @Override
  public void execute() {
    System.out.println("executing:- " + text);
  }
}
