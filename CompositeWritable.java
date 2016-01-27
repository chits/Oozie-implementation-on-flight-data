package com.bigdata.finalproject;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class CompositeWritable implements Writable{
	int count = 0;
	int timeIn = 0;
	int timeOut = 0;
	public CompositeWritable() {}

    public CompositeWritable(int val,int val1, int val2) {
       count = val;	
       timeIn = val1;
       timeOut = val2;
    }
	@Override
	public void readFields(DataInput in) throws IOException {
		// TODO Auto-generated method stub
		count = in.readInt();
		timeIn = in.readInt();
		timeOut = in.readInt();
		
	}

	@Override
	public void write(DataOutput out) throws IOException {
		// TODO Auto-generated method stub
		out.writeInt(count);
		out.writeInt(timeIn);
		out.writeInt(timeOut);
	}
	public void merge(CompositeWritable other) {
		this.count += other.count;
        this.timeIn += other.timeIn;
        this.timeOut += other.timeOut;
    }
	@Override
    public String toString() {
        return this.count + "\t" + this.timeIn + "\t" + this.timeOut;
    }

}
