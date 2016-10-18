package mapreduce;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.jobcontrol.ControlledJob;
import org.apache.hadoop.mapreduce.lib.jobcontrol.JobControl;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import mapreduce.LemmaIndexMapred.LemmaIndexMapper;
import mapreduce.util.IndexableFileInputFormat;
import mapreduce.util.StringIntegerList;
import mapreduce.util.StringIntegerList.StringInteger;
import mapreduce.util.WikipediaPageInputFormat;

/**
 * This class is used for Section C.2 of assignment 1. You are supposed to run
 * the code taking the lemma index filename as input, and output being the
 * inverted index.
 */
public class InvertedIndexMapred {
	public static class InvertedIndexMapper extends Mapper<Text, Text, Text, StringInteger> {

		@Override
		public void map(Text articleTitle, Text indices, Context context) throws IOException,
				InterruptedException {
			// TODO: You should implement inverted index mapper here
			
			StringIntegerList indincesList=new StringIntegerList();
			indincesList.readFromString(indices.toString());			
			Map<String,Integer> indicesMap=indincesList.getMap();
			Text Key=new Text();
			StringInteger Value=new StringInteger();	
			for (String key : indicesMap.keySet()) {  
				Key.set(key);
				Value=new StringInteger(articleTitle.toString(),indicesMap.get(key).intValue());
				context.write(Key,Value);			  
			} 
		}
	}

	public static class InvertedIndexReducer extends Reducer<Text, StringInteger, Text, StringIntegerList> {
		@Override
		public void reduce(Text lemma, Iterable<StringInteger> articlesAndFreqs, Context context)
				throws IOException, InterruptedException {
			// TODO: You should implement inverted index reducer here
			
			Text Key=lemma;
			StringIntegerList  Value;
			HashMap<String,Integer> stringIntegerMap=new HashMap<String,Integer>();
			for(StringInteger stringInteger:articlesAndFreqs){
				stringIntegerMap.put(stringInteger.getString(),stringInteger.getValue());
			}		
			Value=new StringIntegerList(stringIntegerMap);
			context.write(Key,Value);			
		}
	}

	public static void main(String[] args) throws Exception {
		// TODO: you should implement the Job Configuration and Job call
		// here
		Configuration conf=new Configuration();
		GenericOptionsParser gop=new GenericOptionsParser(conf, args);
		String[] otherArgs=gop.getRemainingArgs();
		Job job1=Job.getInstance(conf, "Lamma Index");
//		Job job2=Job.getInstance(conf, "Inverted Index");
//		job2.setJarByClass(InvertedIndexMapred.class);
		
		job1.setJarByClass(InvertedIndexMapred.class);
		job1.setNumReduceTasks(0);
		
		job1.setMapperClass(LemmaIndexMapper.class);
		
		job1.setInputFormatClass(WikipediaPageInputFormat.class);
		job1.setOutputKeyClass(Text.class);
		job1.setOutputValueClass(StringIntegerList.class);
		FileInputFormat.addInputPath(job1, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job1, new Path(otherArgs[1]));
		
		System.exit((job1.waitForCompletion(true))?0:1);
//		
//		ControlledJob ctrljob1=new  ControlledJob(conf);   
//	    ctrljob1.setJob(job1); 
			
//		job2.setMapperClass(InvertedIndexMapper.class);
//		job2.setReducerClass(InvertedIndexReducer.class);
//		job2.setInputFormatClass(TextInputFormat.class);
//		job2.setOutputKeyClass(Text.class);
//		job2.setOutputValueClass(StringIntegerList.class);

//		FileInputFormat.addInputPath(job2, new Path(args[1]));
//		FileOutputFormat.setOutputPath(job2, new Path(args[2]));
		
//		FileInputFormat.addInputPath(job2, new Path(args[0]));
//		FileOutputFormat.setOutputPath(job2, new Path(args[1]));
//		
//		System.exit((job2.waitForCompletion(true))?0:1);
		
//        ControlledJob ctrljob2=new ControlledJob(conf);   
//        ctrljob2.setJob(job2); 
//        
//        ctrljob2.addDependingJob(ctrljob1);
//        JobControl jobCtrl=new JobControl("myctrl");
//        
//        jobCtrl.addJob(ctrljob1);   
//        jobCtrl.addJob(ctrljob2);    
//        Thread  t=new Thread(jobCtrl);   
//        t.start();  
//		System.exit((job1.waitForCompletion(true) && job2.waitForCompletion(true))?0:1);
	
	}
}
