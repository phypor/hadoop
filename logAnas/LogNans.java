import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * 
 * @author bojiehuang@163.com
 * 
 */
public class LogNans {

	public static class LogMapper extends
			Mapper<Object, Text, Text, IntWritable> {

		private final static IntWritable flow = new IntWritable();
		private Text ip = new Text();

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			String val = value.toString();
			String[] arr = val.split(" ");
			String strIp = arr[0];
			String strFlow = arr[arr.length - 1];
			Pattern pattern = Pattern.compile("[0-9]*");
			Matcher isNum = pattern.matcher(strFlow);
			if (!isNum.matches()) {
				return;
			}
			ip.set(strIp);
			flow.set(Integer.parseInt(strFlow));
			context.write(ip, flow);
		}
	}

	public static class LogReducer extends
			Reducer<Text, IntWritable, Text, IntWritable> {
		private IntWritable result = new IntWritable();

		public void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			result.set(sum);
			context.write(key, result);
		}
	}

	// set input path
	final static String input = "hdfs://*.*.*.*:9000/user/hadoop/input/log.txt";
	// set ouput path
	final static String output = "hdfs://*.*.*.*:9000/user/hadoop/output/";

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "logNans");
		job.setJarByClass(LogNans.class);

		job.setMapperClass(LogMapper.class);

		job.setCombinerClass(LogReducer.class);

		job.setReducerClass(LogReducer.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		FileInputFormat.addInputPath(job, new Path(input));
		FileOutputFormat.setOutputPath(job, new Path(output));

		FileSystem fileSystem = FileSystem.get(conf);
		Path path = new Path(output);
		if (fileSystem.exists(path)) {
			fileSystem.delete(path, true);
		}
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}