package com.github.storm.starter;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

public class SplitSenceBolt extends BaseRichBolt{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7060746135308649230L;
	private OutputCollector collector;

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector =collector;
	}

	@Override
	public void execute(Tuple input) {
		String sentence=input.getStringByField("sentence");
		String[] words=sentence.split(" ");
		for(String word: words) {
			this.collector.emit(new Values(word));
		}
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("word"));
	}

}
