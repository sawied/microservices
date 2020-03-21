package com.github.sawied.microservice.trade.csv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.UUID;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.ByteOrderMark;

public class CSVOUT {

	public static void main(String[] args) throws IOException {
		CSVFormat csvFormat = CSVFormat.EXCEL.withHeader("application name");
		File tempFile = File.createTempFile(UUID.randomUUID().toString(), ".csv");
		FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
		fileOutputStream.write(ByteOrderMark.UTF_8.getBytes());
		OutputStreamWriter osw = new OutputStreamWriter(fileOutputStream,"UTF-8");
		CSVPrinter csvPrinter = new CSVPrinter(osw, csvFormat);
		csvPrinter.printRecords("剪","1","excel");
		csvPrinter.close();
		osw.close();
		fileOutputStream.close();
	}
}
