package com.github.storm.starter;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.apache.storm.utils.Utils;

public class WordCountTopology {

	
	private static final String TOPOLOGY_WORD_COUNT = "TOPOLOGY_WORD_COUNT";
	private static final String REPORT_BOLT_ID = "report_bolt_id";
	private static final String COUNT_BOLT_ID = "count_bolt_id";
	private static final String SPLIT_BOLT_ID = "split_bolt_Id";
	private static final String SENTENCE_SPOUT_ID = "sentence_Spout_ID";

	public static void main(String[] args) throws Exception {
		
		SentenceSpout sentenceSpout = new SentenceSpout();
		SplitSenceBolt splitSenceBolt = new SplitSenceBolt();
		WordCountBolt wordCountBolt = new WordCountBolt();
		ReportBolt reportBolt = new ReportBolt();
		
		TopologyBuilder topologyBuilder = new TopologyBuilder();
		topologyBuilder.setSpout(SENTENCE_SPOUT_ID, sentenceSpout);
		
		
		topologyBuilder.setBolt(SPLIT_BOLT_ID, splitSenceBolt).shuffleGrouping(SENTENCE_SPOUT_ID);
		
		topologyBuilder.setBolt(COUNT_BOLT_ID, wordCountBolt).fieldsGrouping(SPLIT_BOLT_ID, new Fields("word"));
		
		topologyBuilder.setBolt(REPORT_BOLT_ID, reportBolt).globalGrouping(COUNT_BOLT_ID);
		
		
		Config config = new Config();
		
		
		if (args != null && args.length > 0) {
			config.setNumWorkers(3);

		      StormSubmitter.submitTopologyWithProgressBar(args[0], config, topologyBuilder.createTopology());
		    }
		    else {
		    config.setMaxTaskParallelism(3);

		      LocalCluster cluster = new LocalCluster();
		      cluster.submitTopology(TOPOLOGY_WORD_COUNT, config, topologyBuilder.createTopology());

		      Thread.sleep(10000);

		      cluster.shutdown();
		    }
		
		
	}
	
}
