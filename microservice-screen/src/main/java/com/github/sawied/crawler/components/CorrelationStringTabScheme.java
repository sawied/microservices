package com.github.sawied.crawler.components;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.storm.spout.Scheme;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import com.digitalpebble.stormcrawler.Metadata;
import com.digitalpebble.stormcrawler.persistence.Status;

/**
 * correlation id for tracking 
 * @author sawied
 *
 */
public class CorrelationStringTabScheme implements Scheme {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3283475982339899132L;
	
	private Status withStatus = null;

	public CorrelationStringTabScheme() {
	        withStatus = null;
	    }

	public CorrelationStringTabScheme(Status status) {
	        withStatus = status;
	    }

	@Override
	public List<Object> deserialize(ByteBuffer bytes) {
		String input = new String(bytes.array(), StandardCharsets.UTF_8);

        String[] tokens = input.split("\t");
        if (tokens.length < 1)
            return new Values();
        
        String url = tokens[0];
        
        String collelationId = "";
        if(tokens.length>1) {
        	 collelationId=tokens[1];
        }
        
        Metadata metadata = null;

        for (int i = 2; i < tokens.length; i++) {
            String token = tokens[i];
            // split into key & value
            int firstequals = token.indexOf("=");
            String value = null;
            String key = token;
            if (firstequals != -1) {
                key = token.substring(0, firstequals);
                value = token.substring(firstequals + 1);
            }
            if (metadata == null)
                metadata = new Metadata();
            metadata.addValue(key, value);
        }

        if (metadata == null)
            metadata = new Metadata();

        if (withStatus != null)
            return new Values(url, collelationId,metadata, withStatus);

        return new Values(url, collelationId,metadata);
	}

	@Override
	public Fields getOutputFields() {
		if (withStatus != null)
            return new Fields("url","correlationId", "metadata", "status");
        return new Fields("url", "correlationId","metadata");
	}

}
