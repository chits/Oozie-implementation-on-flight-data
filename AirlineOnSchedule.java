package com.bigdata.finalproject;

import java.io.*;
import java.util.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class AirlineOnSchedule {
	public static class AirlinePerformanceMap extends
			Mapper<Object, Text, Text, IntWritable> {
		private Text mapKey = new Text();
		private IntWritable mapValue = new IntWritable();

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			String line = value.toString();
			String[] record = line.split(",");
			String airline = record[8];
			String year = record[0];
			String arrDelayStr = record[14];
			String depDelayStr = record[15];
			int arrDelay, depDelay;
			int totalDelay = 0;
			if ((!airline.equals("NA")) && (!year.equals("NA"))
					&& (!arrDelayStr.equals("NA"))
					&& (!depDelayStr.equals("NA"))){
				
			if( (!airline.equals("UniqueCarrier"))&& (!year.equals("Year"))
							&& (!arrDelayStr.equals("ArrDelay"))&& (!depDelayStr.equals("DepDelay")))
					{

				try {
					arrDelay = Integer.parseInt(arrDelayStr);
					depDelay = Integer.parseInt(depDelayStr);
					totalDelay = arrDelay + depDelay;
					totalDelay = totalDelay > 15 ? totalDelay : 0;
				} catch (Exception e) {
					e.printStackTrace();
				}

				String mapKeyStr = merge(airline, year);
				mapKey.set(mapKeyStr);
				mapValue.set(totalDelay);
				context.write(mapKey, mapValue);
			}
			}
		}

		public String merge(String... values) {
			if (values == null) {
				throw new IllegalArgumentException("Invalid input");
			}
			if (values.length == 0) {
				return null;
			}
			StringBuilder outputString = new StringBuilder();
			for (int i = 0; i < values.length; i++) {
				outputString.append(values[i]);
				if (i != values.length - 1)
					outputString.append(":");
			}
			return outputString.toString();
		}
	}

	public static class AirlinePerformanceReduce extends
			Reducer<Text, IntWritable, Text, Text> {
		List<Pair> pairList = new ArrayList<>();
		private double prob;

		public void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			int totalCount = 0;
			int onTimeCount = 0;
			for (IntWritable val : values) {
				totalCount++;
				if (val.get() == 0) {
					onTimeCount++;
				}
			}
			prob = (double) onTimeCount / (double) totalCount;
			pairList.add(new Pair(prob, key.toString()));
		}

		protected void cleanup(Context context) throws IOException,
				InterruptedException {
			Collections.sort(pairList, new CustomComparator());
			Collections.reverse(pairList);
			for (Pair pair : pairList) {
				context.write(new Text(pair.key), new Text(pair.prob + ""));
			}
		}

		public class CustomComparator implements Comparator<Pair> {
			@Override
			public int compare(Pair o1, Pair o2) {
				if (o1.prob < o2.prob)
					return -1;
				if (o1.prob > o2.prob)
					return 1;
				return 0;
			}
		}

		class Pair {
			double prob;
			String key;

			Pair(double prob, String key) {
				this.prob = prob;
				this.key = key;
			}
		}
	}

	/*public static void main(String[] args) throws IOException,
			InterruptedException, ClassNotFoundException {
		Job job = Job.getInstance(new Configuration());
		job.setJarByClass(AirlineOnSchedule.class);
		job.setJobName("AirLinePerformance");

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.setMapperClass(AirlinePerformanceMap.class);
		job.setReducerClass(AirlinePerformanceReduce.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		System.exit(job.waitForCompletion(true) ? 0 : 1);

	}*/

}
