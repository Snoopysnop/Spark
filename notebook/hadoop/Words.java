
/**
 * Import the necessary Java packages
 */

import java.io.IOException;
import java.util.Scanner;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * A Mapreduce example for Hadoop. It extracts some basic
 * information from a text file derived from the DBLP dataset.
 */
public class Words {

  /**
   * The Mapper class -- it takes a line from the input file and 
   * extracts the string after the first tab (= the Title)
   */
  public static class WordsMapper extends
	  Mapper<Object, Text, Text, IntWritable> {

	private final static IntWritable one = new IntWritable(1);
	private Text Word = new Text();

	public void map(Object key, Text value, Context context)
		throws IOException, InterruptedException {

	  /* Open a Java scanner object to parse the line */
	  Scanner line = new Scanner(value.toString());
	  line.useDelimiter("\t");
	  line.next();

	  /* extract all the words in the title */
	  String[] texts = line.next().split("\s+");
	  for(String text : texts){
	    Word.set(text);
	    context.write(Word, one);
	  }
	
	}
  }

  /**
   * The Reducer class -- receives pairs (Word, <list of counts>)
   * and sums up the counts of publications per Word
   */
  public static class CountReducer extends
	  Reducer<Text, IntWritable, Text, IntWritable> {
	private IntWritable result = new IntWritable();

	public void reduce(Text key, Iterable<IntWritable> values, 
			Context context)
		throws IOException, InterruptedException {
	  
	  /* Iterate on the list to compute the count */
	  int count = 0;
	  for (IntWritable val : values) {
		count += val.get();
	  }

	  result.set(count);
	  context.write(key, result);
	}
  }
}