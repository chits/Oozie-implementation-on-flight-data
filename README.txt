README

This file explains the folder structure used for the execution of Oozie workflow.

Flight --> Source Folder

This contains : apps --> bigdataFinal 

The bigdataFinal folder contains the required Job.properties file and the workflow.xml file along with that a lib folder that contains our jar file to be executed as a part of the Oozie workflow.

Also it contains flightdata -->

This folder contains all the required input data files for our job execution.

We need to move this folder to HDFS and then submit the job.properties file for the successful workflow execution.