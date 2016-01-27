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

public class FindCancelledFlights {
	
	public static class CancelledFlightsMapper extends Mapper<LongWritable, Text, Text, IntWritable>{
		IntWritable one = new IntWritable(1);
		public void map(LongWritable key, Text value,Context context) throws IOException, InterruptedException{
			String line=value.toString();
			String cancellationCode=line.split(",")[22].toString();
			if(!(cancellationCode.toString().equals("NA")) && !(cancellationCode.toString().isEmpty())
					&& !(cancellationCode.toString().equals("CancellationCode"))){
				context.write(new Text(cancellationCode), one);
			}
		 }
	}
	public static class CancelledFlightsReducer extends Reducer<Text, IntWritable, Text, IntWritable>{
		public void reduce(Text key, Iterable<IntWritable> values,Context context) throws IOException, InterruptedException {	
			int count=0;
			for(IntWritable val : values){
				count += val.get();
			}
			context.write(key, new IntWritable(count));
		}
	}
	/*public static void main(String[] args) throws Exception {
	    Configuration conf = new Configuration();
	    Job job = Job.getInstance(conf, "Cancelled Flights");
	    job.setJarByClass(FindCancelledFlights.class);
	    job.setMapperClass(CancelledFlightsMapper.class);
	    //job.setCombinerClass(CardsReducer.class);
	    job.setReducerClass(CancelledFlightsReducer.class);
	    job.setMapOutputKeyClass(Text.class);
	    job.setMapOutputValueClass(IntWritable.class);
	    job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(IntWritable.class);
	    FileInputFormat.addInputPath(job, new Path(args[0]));
	    FileOutputFormat.setOutputPath(job, new Path(args[1]));
	    System.exit(job.waitForCompletion(true) ? 0 : 1);
	  }
*/
}
