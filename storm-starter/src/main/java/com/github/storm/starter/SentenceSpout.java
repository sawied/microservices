package com.github.storm.starter;

import java.util.Map;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

public class SentenceSpout extends BaseRichSpout{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1095412742675092529L;

	private SpoutOutputCollector collector;
	
	private int index=0;
	
	private String [] sentences= {"he error message is telling you what's wrong.","The script is deprecated, and you should use the Powershell script instead."};

	@SuppressWarnings("rawtypes")
	@Override
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		this.collector=collector;
		
	}

	@Override
	public void nextTuple() {
		this.collector.emit(new Values(sentences[index]));
		index++;
		if(index>=sentences.length) {
			index=0;
		}
		Utils.sleep(1000);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("sentence"));
		
	}

}
