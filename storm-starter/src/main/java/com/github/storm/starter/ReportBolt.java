package com.github.storm.starter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;

public class ReportBolt extends BaseRichBolt{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1439222584265524493L;
	private HashMap<String, Long> counts =null;

	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.counts = new HashMap<String,Long>();
	}

	@Override
	public void execute(Tuple input) {
		String word = input.getStringByField("word");
		Long count = input.getLongByField("count");
		this.counts.put(word, count);
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		
	}

	@Override
	public void cleanup() {
		System.out.println("--------print counts-------------");
		Iterator<String> it = this.counts.keySet().iterator();
		while(it.hasNext()) {
			String word = it.next();
			System.out.println(word+":"+this.counts.get(word));
		}
		System.out.println("--------END PRINT counts-------------");
	}
	
	
	

}
