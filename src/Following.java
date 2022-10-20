import java.io.IOException;
import java.util.*;
        
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

class MyMapWritable extends MapWritable {
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        Set<Writable> keySet = this.keySet();

        for (Object key : keySet) {
            result.append("{" + key.toString() + " = " + this.get(key) + "}");
        }
        return result.toString();
    }
}

public class Following {
      
 public static class Map extends Mapper<LongWritable, Text, IntWritable, Text> {
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        StringTokenizer tokenizer = new StringTokenizer(line);
        String token1 = tokenizer.nextToken();
	String token2 = tokenizer.nextToken();
        context.write(new IntWritable(Integer.parseInt(token1)), new Text(token2));
    }
 } 
        
 public static class Reduce extends Reducer<IntWritable, Text, IntWritable, Text> {
    private MapWritable arry = new MapWritable();
    public void reduce(IntWritable key, Iterable<Text> values, Context context) 
      throws IOException, InterruptedException {
        String following = "";
        for (Text val : values) {
	    if (following == ""){
	        following = following + val;
	    }
 	    else {
	        following = following + "," + val;
	    }
	    
            //context.write(key, new MapWritable());
	    //arry.put(new IntWritable(0), new Text(val));
        }
        context.write(key, new Text(following));
    }
 }
        
 public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
        
        Job job = new Job(conf, "Following");
    
    job.setOutputKeyClass(IntWritable.class);
    job.setOutputValueClass(Text.class);
        
    job.setMapperClass(Map.class);
    job.setReducerClass(Reduce.class);
    job.setJarByClass(WordCount.class);
        
    job.setInputFormatClass(TextInputFormat.class);
    job.setOutputFormatClass(TextOutputFormat.class);
        
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
        
    job.waitForCompletion(true);
 }
        
}
