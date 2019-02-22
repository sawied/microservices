package com.github.sawied.crawler.components;

import static com.digitalpebble.stormcrawler.Constants.StatusStreamName;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.io.IOUtils;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.digitalpebble.stormcrawler.Metadata;
import com.digitalpebble.stormcrawler.indexing.AbstractIndexerBolt;
import com.digitalpebble.stormcrawler.persistence.Status;

/**
 * 
 * @author sawied
 *
 */
public class FileOutIndexer extends AbstractIndexerBolt {

	private static final long serialVersionUID = -1807685204508049628L;

	private final Logger LOG = LoggerFactory.getLogger(getClass());

	private OutputCollector _collector;

	private File outDirectory;

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
		super.prepare(conf, context, collector);
		this._collector = collector;
		if (conf.get("fileIndexer.out.directory") != null) {
			File directory = new File((String) conf.get("fileIndexer.out.directory"));
			if (directory.isDirectory() && directory.exists()) {
				outDirectory = directory;
			} else {
				LOG.error("the out directory is specified ,but it is invalied {}", directory.getAbsolutePath());
			}
		} else {
			String userHome = System.getProperty("user.home");
			outDirectory = new File(userHome);
			LOG.warn("Don't specify out directory ,use uer home as output path.{}", userHome);
		}

	}

	@Override
	public void execute(Tuple tuple) {

		String url = tuple.getStringByField("url");

		// Distinguish the value used for indexing
		// from the one used for the status
		String normalisedurl = valueForURL(tuple);

		Metadata metadata = (Metadata) tuple.getValueByField("metadata");

		// should this document be kept?
		boolean keep = filterDocument(metadata);
		if (!keep) {
			// treat it as successfully processed even if
			// we do not index it
			_collector.emit(StatusStreamName, tuple, new Values(url, metadata, Status.FETCHED));
			_collector.ack(tuple);
			return;
		}
		
		
		
		File output = new File(outDirectory,DigestUtils.md5Hex(normalisedurl.getBytes()));
		FileOutputStream fileOutputStream = null;

		try {
			fileOutputStream = new FileOutputStream(output);
			// display text of the document?
			if (fieldNameForText() != null) {
				String text = tuple.getStringByField("text");
				IOUtils.write(text, fileOutputStream);
			}
			String[] values = metadata.getValues("parse.image");
			if(values!=null) {
				for(int i=0;i<values.length;i++) {
					IOUtils.write("\n", fileOutputStream);
					IOUtils.write(values[i], fileOutputStream);
				}
			}
			
		}catch(IOException e) {
			LOG.error("write file fail. {}", output.getAbsoluteFile());
		}finally {
			IOUtils.closeQuietly(fileOutputStream);
		}

	

		_collector.emit(StatusStreamName, tuple, new Values(url, metadata, Status.FETCHED));
		_collector.ack(tuple);

	}

}
