package com.bigdata.finalproject;

import java.io.File;
import java.io.FileInputStream;
import java.text.MessageFormat;
import java.util.Properties;

import org.apache.hadoop.fs.Path;
import org.apache.oozie.client.OozieClient;
import org.apache.oozie.client.WorkflowAction;
import org.apache.oozie.client.WorkflowJob;
import org.apache.oozie.local.LocalOozie;

public class DriverOozie {
	
	 public static void main(String[] args)
	  {
	    System.exit(execute(args));
	  }

	  static int execute(String[] args) {
	    if (args.length != 2) {
	      System.out.println();
	      System.out.println("Expected parameters: <WF_APP_HDFS_URI> <WF_PROPERTIES>");
	      return -1;
	    }
	    String appUri = args[0];
	    String propertiesFile = args[1];
	    if (propertiesFile != null) {
	      File file = new File(propertiesFile);
	      if (!file.exists()) {
	        System.out.println();
	        System.out.println("Specified Properties file does not exist: " + propertiesFile);
	        return -1;
	      }
	      if (!file.isFile()) {
	        System.out.println();
	        System.out.println("Specified Properties file is not a file: " + propertiesFile);
	        return -1;
	      }
	    }

	    try
	    {
	      LocalOozie.start();
	      
	      OozieClient wc = LocalOozie.getClient();

	      Properties conf = wc.createConfiguration();
	      conf.setProperty("oozie.wf.application.path", new Path(appUri, "workflow.xml").toString());

	      if (propertiesFile != null) {
	        conf.load(new FileInputStream(propertiesFile));
	      }

	      String jobId = wc.run(conf);
	      Thread.sleep(1000L);
	      System.out.println("Workflow job submitted");

	      while (wc.getJobInfo(jobId).getStatus() == WorkflowJob.Status.RUNNING) {
	        System.out.println("Workflow job running ...");
	        printWorkflowInfo(wc.getJobInfo(jobId));
	        Thread.sleep(10000L);
	      }

	      System.out.println("Workflow job completed ...");
	      printWorkflowInfo(wc.getJobInfo(jobId));

	      return wc.getJobInfo(jobId).getStatus() == WorkflowJob.Status.SUCCEEDED ? 0 : -1;
	    }
	    catch (Exception ex)
	    {
	      
	      System.out.println();
	      System.out.println(ex.getMessage()+" "+ex.getCause()+" "+ex.getStackTrace());
	      return -1;
	    }
	    finally
	    {
	      LocalOozie.stop();
	    }
	  }

	  private static void printWorkflowInfo(WorkflowJob wf) {
	    System.out.println("Application Path   : " + wf.getAppPath());
	    System.out.println("Application Name   : " + wf.getAppName());
	    System.out.println("Application Status : " + wf.getStatus());
	    System.out.println("Application Actions:");
	    for (WorkflowAction action : wf.getActions()) {
	      System.out.println(MessageFormat.format("   Name: {0} Type: {1} Status: {2}", new Object[] { action.getName(), action.getType(), action.getStatus() }));
	    }

	    System.out.println();
	  }

}
