package com.github.sawied.azure.speech;

public class SpeechItem {
	
	private String text;
	
	private double offset;
	
	private double duration;
	
	private Integer size;

	/**
	 * 
	 * @param text
	 * @param offset
	 * @param duration
	 */
	public SpeechItem(String text, double offset, double duration) {
		super();
		this.text = text;
		this.offset = offset;
		this.duration = duration;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public double getOffset() {
		return offset;
	}

	public void setOffset(double offset) {
		this.offset = offset;
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	@Override
	public String toString() {
		return "SpeechItem [text=" + text + ", offset=" + offset + ", duration=" + duration + "]\n";
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}
	
	
	public static int getWordsize(String str) {
		if(str.isEmpty()) {
			return 0;
		}
		return str.trim().split(" ").length;
	}
	
	public static String getLastEnglishWords(String text) {
		if(!text.isEmpty()) {
			String removal= text.trim();
			int index=removal.lastIndexOf(32);
			if(index!=-1) {				
				removal=removal.substring(index+1);
			}
			return removal;
		}
		return null;
	}
	
	

	
	
	
	
	

}
