package com.github.sawied.azure.speech;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import junit.framework.Assert;

public class EnglishWordsTest {

	@Test
	public void spaceCharTest() {
		String sentence ="Hell ,how can i do for you";
		Assert.assertEquals(22, sentence.lastIndexOf(32));
		Assert.assertEquals("you", App.getLastEnglishWords(sentence));
	}
	
	@Test
	public void sentenseParseTest() throws IOException {
		String text="Hello. How can I help you today? I'm very upset with the quality of your service. I'm sorry? What seems to be the problem. My Internet service did not work for 3 days. My record say a technician visit your area yesterday. Yes, he installed a new modem did it solve the problem. Yes, but I was overcharged for Internet service.";
		Pattern compile = Pattern.compile("[\\.,\\?]");
		Matcher matcher = compile.matcher(text);
		int index =0;
		List<String> sentences= new ArrayList<String>();
		while(matcher.find()) {
			int start = matcher.start();
			int end = matcher.end();
			sentences.add(text.substring(index, end).trim());
			index=start+1;
		}
		
		
		List<SpeechItem> origin =getSpeechWords(); 
		
		
		//append sentence time span
		
		//Text=hello how can i help you today duration:1.79 offset:0
		int count=0;
		Double offset = null;
		List<SpeechItem> speech=new ArrayList<SpeechItem>();
		for(int i=0;i<sentences.size();i++) {
			String str = sentences.get(i);
			String[] split = str.split(" ");
			int length = split.length;
			
			if(origin.size()<=count) {
				System.out.println("error");
			}
			SpeechItem start = origin.get(count);
			
			offset=start.getOffset();
			
			SpeechItem end = origin.get(count+length-1);
			
			speech.add(new SpeechItem(str,start.getDuration()+offset,end.getDuration()-start.getDuration()));
			count+=length;
			
			
		}
		
	
		
		System.out.println(speech);
		
		
	}
	
	
	
	@Test
	public void textSpeechParseTest() throws IOException {
		System.out.println(getSpeechWords());
	}

	private List<SpeechItem> getSpeechWords() throws IOException {
		InputStream stream = ClassLoader.getSystemResourceAsStream("com/github/sawied/azure/speech/speech.txt");
		InputStreamReader inputStreamReader = new InputStreamReader(stream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		String line =null;
		
		List<SpeechItem> items = new ArrayList<SpeechItem>();
		while((line = bufferedReader.readLine()) != null) {			
			Pattern pattern = Pattern.compile("[0-9]+\\.?[0-9]*");
			Matcher matcher = pattern.matcher(line);
			int i=0;
			Float[] number = new Float[2];
			int index =0;
			while(matcher.find()) {				
				int start = matcher.start();
				int end = matcher.end();
				number[i]=Float.parseFloat(line.substring(start, end));
				i++;
				if(index==0) {
					index=start;
				}
			}
			String substring = line.substring(0,index);
			int wordsize = SpeechItem.getWordsize(substring);
			
			SpeechItem speechItem = new SpeechItem(App.getLastEnglishWords(substring), number[1],number[0]);
			speechItem.setSize(wordsize);
			
			if(items.size()<wordsize) {				
				items.add(speechItem);
			}else {
				items.set(wordsize-1, speechItem);
			}
		
		}
		
		
		
		stream.close();
		
		return items;
	}
	
	
	
}
