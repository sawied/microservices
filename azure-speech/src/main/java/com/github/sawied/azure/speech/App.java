package com.github.sawied.azure.speech;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.microsoft.cognitiveservices.speech.CancellationDetails;
import com.microsoft.cognitiveservices.speech.CancellationReason;
import com.microsoft.cognitiveservices.speech.OutputFormat;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) throws InterruptedException, ExecutionException {

		Semaphore stopRecognitionSemaphore = new Semaphore(0);

		// Creates an instance of a speech config with specified
		// subscription key and service region. Replace with your own subscription key
		// and service region (e.g., "westus").
		SpeechConfig config = SpeechConfig.fromSubscription("193b67f02ae94f9996bda218286a2f3c", "southeastasia");
		// config.setSpeechRecognitionLanguage("zh-cn");
		config.setOutputFormat(OutputFormat.Detailed);
		// Create an audio stream from a wav file.
		// Replace with your own audio file name.

		AudioConfig audioInput = AudioConfig.fromWavFileInput("E:/githome//final/final0.wav");

		// sentence offset as map's key
		final Map<Double, List<SpeechItem>> speechMap = new HashMap<Double, List<SpeechItem>>();

		final ArrayList<SpeechItem> sentences = new ArrayList<SpeechItem>();

		// Creates a speech recognizer using audio stream input.
		SpeechRecognizer recognizer = new SpeechRecognizer(config, audioInput);
		{
			// Subscribes to events.
			recognizer.recognizing.addEventListener((s, e) -> {
				String text = e.getResult().getText();
				// default unit is 100 nsec
				double duration = e.getResult().getDuration().doubleValue() / (1000 * 10000);
				double offset = e.getResult().getOffset().doubleValue() / (1000 * 10000);

				List<SpeechItem> items = null;
				if (speechMap.containsKey(offset)) {
					items = speechMap.get(offset);
				} else {
					items = new ArrayList<SpeechItem>();
					speechMap.put(offset, items);
				}

				// get speech items

				int wordsize = SpeechItem.getWordsize(text);
				SpeechItem speechItem = new SpeechItem(App.getLastEnglishWords(text), offset, duration);
				if (items.size() < wordsize) {
					items.add(speechItem);
				} else {
					items.set(wordsize - 1, speechItem);
				}

				System.out.println(
						"Thread" + Thread.currentThread().getName() + "RECOGNIZING: Text=" + e.getResult().getText()
								+ " duration:" + ((e.getResult().getDuration().doubleValue()) / (1000 * 10000))
								+ " offset:" + e.getResult().getOffset().longValue() / (1000 * 10000));
			});

			recognizer.recognized.addEventListener((s, e) -> {
				if (e.getResult().getReason() == ResultReason.RecognizedSpeech) {

					String text = e.getResult().getText();
					// default unit is 100 nsec
					double duration = e.getResult().getDuration().doubleValue() / (1000 * 10000);
					double offset = e.getResult().getOffset().doubleValue() / (1000 * 10000);

					sentences.add(new SpeechItem(text, offset, duration));
					System.out.println(
							"Thread" + Thread.currentThread().getName() + "RECOGNIZED: Text=" + e.getResult().getText()
									+ " duration:" + ((e.getResult().getDuration().doubleValue()) / (1000 * 10000))
									+ " offset:" + e.getResult().getOffset().longValue() / (1000 * 10000));
				} else if (e.getResult().getReason() == ResultReason.NoMatch) {
					System.out.println("NOMATCH: Speech could not be recognized.");
				}
			});

			recognizer.canceled.addEventListener((s, e) -> {
				System.out.println("Thread" + Thread.currentThread().getName() + "CANCELED: Reason=" + e.getReason());

				if (e.getReason() == CancellationReason.Error) {
					System.out.println("CANCELED: ErrorCode=" + e.getErrorCode());
					System.out.println("CANCELED: ErrorDetails=" + e.getErrorDetails());
					System.out.println("CANCELED: Did you update the subscription info?");
				}

				stopRecognitionSemaphore.release();
			});

			recognizer.sessionStarted.addEventListener((s, e) -> {
				System.out.println("\nSession started event.");
			});

			recognizer.sessionStopped.addEventListener((s, e) -> {
				System.out.println("\nSession stopped event.");

				// Stops translation when session stop is detected.
				System.out.println("\nStop translation.");
				stopRecognitionSemaphore.release();
			});

			// Starts continuous recognition. Uses stopContinuousRecognitionAsync() to stop
			// recognition.
			recognizer.startContinuousRecognitionAsync().get();

			// Waits for completion.
			stopRecognitionSemaphore.acquire();

			// Stops recognition.
			recognizer.stopContinuousRecognitionAsync().get();
			System.out.println("Thread" + Thread.currentThread().getName());

			// starts recognition each sentence

			List<SpeechItem> dialog = sentenceMatch(sentences, speechMap);
			System.out.println(dialog);
			recognizer.close();
		}

	}

	private static List<SpeechItem> sentenceMatch(ArrayList<SpeechItem> sentences,
			Map<Double, List<SpeechItem>> speechMap) {

		List<SpeechItem> result = new ArrayList<SpeechItem>();

		for (int i = 0; i < sentences.size(); i++) {

			SpeechItem speechItem = sentences.get(i);
			String text = speechItem.getText();
			double offset = speechItem.getOffset();
			List<SpeechItem> list = speechMap.get(offset);

			List<SpeechItem> parseResult = sentenceParse(text, list);
			result.addAll(parseResult);
		}
		return result;
	}

	public static String getLastEnglishWords(String text) {
		if (!text.isEmpty()) {
			String removal = text.trim();
			int index = removal.lastIndexOf(32);
			if (index != -1) {
				removal = removal.substring(index + 1);
			}
			return removal;
		}
		return null;
	}

	public static List<SpeechItem> sentenceParse(String text, List<SpeechItem> origin) {

		Pattern compile = Pattern.compile("[\\.,\\?]");
		Matcher matcher = compile.matcher(text);
		int index = 0;
		List<String> sentences = new ArrayList<String>();
		while (matcher.find()) {
			int start = matcher.start();
			int end = matcher.end();
			sentences.add(text.substring(index, end).trim());
			index = start + 1;
		}

		int count = 0;
		Double offset = null;
		List<SpeechItem> speech = new ArrayList<SpeechItem>();
		for (int i = 0; i < sentences.size(); i++) {
			String str = sentences.get(i);
			String[] split = str.split(" ");
			int length = split.length;

			if (origin.size() <= count) {
				System.out.println("error");
			}
			SpeechItem start = origin.get(count);

			offset = start.getOffset();

			int ix = count + length - 1;

			if (ix >= origin.size()) {
				ix = origin.size() - 1;
			}
			SpeechItem end = origin.get(ix);


			speech.add(new SpeechItem(str, start.getDuration() + offset, end.getDuration() - start.getDuration()));
			count += length;

		}

		return speech;
	}

}
