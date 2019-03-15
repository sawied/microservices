package com.github.sawied.azure.speech;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.microsoft.cognitiveservices.speech.CancellationDetails;
import com.microsoft.cognitiveservices.speech.CancellationReason;
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

		int exitCode = 1;
		SpeechConfig config = SpeechConfig.fromSubscription("193b67f02ae94f9996bda218286a2f3c", "southeastasia");
		config.setSpeechRecognitionLanguage("zh");
		AudioConfig audioInput = AudioConfig.fromWavFileInput("D:/callCenter/a1.wav");
		SpeechRecognizer recognizer = new SpeechRecognizer(config, audioInput);
		Future<SpeechRecognitionResult> recognizeOnceAsync = recognizer.recognizeOnceAsync();
		SpeechRecognitionResult result = recognizeOnceAsync.get();
		
		if (result.getReason() == ResultReason.RecognizedSpeech) {
            System.out.println("We recognized: " + result.getText());
            exitCode = 0;
        }
        else if (result.getReason() == ResultReason.NoMatch) {
            System.out.println("NOMATCH: Speech could not be recognized.");
        }
        else if (result.getReason() == ResultReason.Canceled) {
            CancellationDetails cancellation = CancellationDetails.fromResult(result);
            System.out.println("CANCELED: Reason=" + cancellation.getReason());

            if (cancellation.getReason() == CancellationReason.Error) {
                System.out.println("CANCELED: ErrorCode=" + cancellation.getErrorCode());
                System.out.println("CANCELED: ErrorDetails=" + cancellation.getErrorDetails());
                System.out.println("CANCELED: Did you update the subscription info?");
            }
        }
		
		recognizer.close();
		System.exit(exitCode);
	
	}

}
