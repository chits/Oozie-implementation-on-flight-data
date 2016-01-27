package com.bigdata.finalproject;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class FindAverageTaxiTimeInAndOut {
	
	public static class AverageTaxiTimeMapper extends Mapper<LongWritable, Text, Text, CompositeWritable>{
		
		public void map(LongWritable key, Text value,Context context) throws IOException, InterruptedException{
			
			String [] record = value.toString().split(",");
			if(!(record[16].equals("NA")) && !(record[19].equals("NA")) && !(record[20].equals("NA")) && 
					!(record[16].equals("Origin")) && !(record[19].equals("TaxiIn")) && !(record[20].equals("TaxiOut"))){
				String origin = record[16].toString();
				int timeIn=Integer.parseInt(record[19].toString());
				int timeout=Integer.parseInt(record[20].toString());
				context.write(new Text(origin), new CompositeWritable(1,timeIn, timeout));
			}
			
		 }
	}
	public static class AverageTaxiTimeReducer extends Reducer<Text, CompositeWritable, Text, Text>{
		public void reduce(Text key, Iterable<CompositeWritable> values,Context context) throws IOException, InterruptedException {	
			CompositeWritable out=new CompositeWritable();
			for(CompositeWritable val : values){
				out.merge(val);
			}
			float averageIntime = out.timeIn/out.count;
			float averageOuttime= out.timeOut/out.count;
			context.write(key, new Text(out.count+"\t"+averageIntime+"\t"+averageOuttime));
			
		}
	}
	/*public static void main(String[] args) throws Exception {
	    Configuration conf = new Configuration();
	    Job job = Job.getInstance(conf, "Cancelled Flights");
	    job.setJarByClass(FindAverageTaxiTimeInAndOut.class);
	    job.setMapperClass(AverageTaxiTimeMapper.class);
	    //job.setCombinerClass(CardsReducer.class);
	    job.setReducerClass(AverageTaxiTimeReducer.class);
	    job.setMapOutputKeyClass(Text.class);
	    job.setMapOutputValueClass(CompositeWritable.class);
	    job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(Text.class);
	    FileInputFormat.addInputPath(job, new Path(args[0]));
	    FileOutputFormat.setOutputPath(job, new Path(args[1]));
	    System.exit(job.waitForCompletion(true) ? 0 : 1);
	  }*/

}
