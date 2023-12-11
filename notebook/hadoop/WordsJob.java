/**
 * Example of a simple MapReduce job: it reads 
 * file containing Words and publications, and 
 * produce each Word with her publication count.
 */

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

/**
 * The follozing class implements the Job submission, based on 
 * the Mapper (WordsMapper) and the Reducer (CountReducer)
 */
public class WordsJob {

  public static void main(String[] args) throws Exception {

	/*
	 * Load the Haddop configuration. IMPORTANT: the 
	 * $HADOOP_HOME/conf directory must be in the CLASSPATH
	 */
	Configuration conf = new Configuration();

	/* We expect two arguments */

	if (args.length != 2) {
	  System.err.println("Usage: WordsJob <in> <out>");
	  System.exit(2);
	}

	/* Allright, define and submit the job */
	// Job job = new Job(conf, "Words count");
	Job job=Job.getInstance(conf);

	/* Define the Mapper and the Reducer */
	job.setMapperClass(Words.WordsMapper.class);
	job.setReducerClass(Words.CountReducer.class);

	/* Define the output type */
	job.setOutputKeyClass(Text.class);
	job.setOutputValueClass(IntWritable.class);

	job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

	/* Set the input and the output */
	//FileInputFormat.addInputPath(job, new Path(args[0]));
	FileInputFormat.setInputPaths(job,new Path(args[0]));
	//	FileOutputFormat.setOutputPath(job, new Path(args[1]));
	FileOutputFormat.setOutputPath(job, new Path(args[1]));

	job.setJarByClass(WordsJob.class);
	job.submit();

	/* Do it! */
	//	System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}