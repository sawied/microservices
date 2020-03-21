package com.github.sawied.azure.sts;

import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import com.microsoft.cognitiveservices.speech.conversation.ConversationTranscriber;
import com.microsoft.cognitiveservices.speech.conversation.Participant;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;

public class MyConversationTranscriber {


	/**
	 * {"Status":"OK","Signature":{"Version":0,"Tag":"Mf99bE34J3m+tMwUskjtT/rlPTAI+9funuqswfg8dL4=","Data":"A1+k+KAFVzX/ozZiV6Wd1oQYVwgGdsVJl7C/f4vpdy0IXf0BihB+l9fTU4KUm4mxsw5A53wbg8B2wsVc08ZtBOdH2lrMt8PyiUNy1WCfCmd0/e+Q9NcXTlMote1QTXp/X4S1rPOzWFVNuf30GxGg8w7ww+IKbNdEp5Ln/Ddud5+wsDl30YqYJ28GTlV3omHr5QSqYHNgU4hcs0cbf4HPEpF3qJaCX7ecGQ3E5Z7W+Rf1J2MhADMNS/Bzy0tC9Zi90HvkYGel6P9hzcqUdGP6HMDBKSYVSL25WoW7D5/GU22RagmqSJ/UwSNc0f/daS4Dxu/nBGMrtQW07+TR5Fl3l1UIsMb3FrE29vRkgI+wFHMcY0Vk312CJbAOAst1tRnQqYT5kCJ7zjlp57/okSjIAqCwulCNaVbaeetnU/OTDd7UUapec30z93aJP/SlbSFt6Vl2RpczWQlHIhyn9SRW5JZ/5kTU0eNzouD4fPzpnofkzh01ydYxaoRvMB9joLdY1Vw1MVKu2d2zhpLsNl+hCXj6eVsRWyaaAweRusGooREeuClX83uMGHlq1ZbgL0Qn5HQgkQJmQQ40zZpK5piOvh1BVRUICbyuwpdWwmQWKFwO2hUuqmPImwAZqCs3UULj4bQf/Bpg3FK7xRlecrmBHcYD9YtQVWPhTRtHcoSt76h9VQ4LhtjRJOzenpYy7CBG"},"Transcription":"yes who's calling sure please. yes i understand. OK. stable source of family income. twenty two thirty. five million. about thirty percent. steady appreciation of assets. OK i will wait for news by."}
	 * { "Version": <Numeric value>, "Tag": "string", "Data": "string" }
	 * @param args
	 */
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		SpeechConfig config = SpeechConfig.fromSubscription("675f1a8e19064675b1b6112e799d5299", "eastasia");
		AudioConfig audioConfig = AudioConfig.fromWavFileInput("E:/githome/conversation/16bit8channels.wav");

		Semaphore stopRecognitionSemaphore = new Semaphore(0);

		ConversationTranscriber transcriber=new ConversationTranscriber(config,audioConfig);
		transcriber.sessionStarted.addEventListener((s,e) -> {
			System.out.println("Session started event.");
		});

		/*transcriber.recognizing.addEventListener((s,e)->{
			System.out.println("RECOGNIZING: Text={"+e.getResult().getText()+"}");
		});*/

		transcriber.sessionStopped.addEventListener((s,e) -> {
			System.out.println("Session stopped event.");
			stopRecognitionSemaphore.release();
		});

		transcriber.canceled.addEventListener((s,e)->{
			System.out.println("CANCELED: Reason=" + e.getReason());
			System.out.println("CANCELED: Code=" + e.getErrorCode());
			System.out.println("CANCELED: Detail=" + e.getErrorDetails());
			stopRecognitionSemaphore.release();
		});

		transcriber.recognized.addEventListener((s,e)->{
			if (e.getResult().getReason() == ResultReason.RecognizedSpeech)
			{
				System.out.println("RECOGNIZED: Text={"+e.getResult().getText()+"}, UserID={"+e.getResult().getUserId()+"}");
			}
			else if (e.getResult().getReason() == ResultReason.NoMatch)
			{
				System.out.println("NOMATCH: Speech could not be recognized.");
			}
		});

		transcriber.setConversationId("AConversationFromTeams101");

		// {"Status":"OK","Signature":{"Version":0,"Tag":"7LQep7sfb3eMjgfGwf6Cjt7cibpa/bpSAR+irUloIII=","Data":"26HD7rp3LdeMM78xQ+uLZJSpF6tSiC3pRJVIv8mhlINkFo72ofZjxiydYIPuH96sRwZZxwj3FukUPD3KQzVe3HJuO4Y1XbH/azMkfdLWFUcxazv/Pp8Y9m6JvBnhOjLPp2RGx6dsiTelEjUgts/mvgx3wYqwN/IF5/dDbzhk6E5CTyxH/fQEw102GnYhNRuMbIw3BxKhQhDgxaHxybdLASE9/Cp2L3qWazxU4FqndKdhJI5GJdWF2uWk6BID6f3EFw19h7Z64PiS42fNnnckkhbRbAJ2Z8Qx4eEeIXRM833IETSScwl0nIRVDIGnYUYVjD0TblGZnVM47VwbhiExqM+OOL9eo8/K26KGANhp1CLCUNZq6mexIa5fLj9gPSH8W/TIuiHQ9lU++Zz59j0iNIBR4CimN7cKyHW2WCWQGC8+anWmCOGKIUsNzFpLD2B4SQKKmlJbzpCftXFqVZj5uvVc8LxAdvDqWOOe8yBIjE95KZ+a0zzA/bj4BH49zfBNF0XtRIXu/NyBa8mtRjIi4nagSbUMzc/J4lzcnXAF6sMB3PXZXldnreaScVmtdeuEJKF0ckFp4iYvchUv8ua2GVFn45HORNIgfCoCbgljpDLpjz+MiBih69w85BxOAalKZjEKEt4VIb/AHhyHun4v3kRFrNs7+g6nk1Z2HtR52LTKAoV08i4nmPCHzVFE7o7E"},"Transcription":"hello it is a good day for me to teach you the sound of my voice you have learned what i look like now you can hear what i sound like the sound of my voice will help the transcription service to recognize my unique voice in the future. training will provide a better experience with greater accuracy when talking or dictating thank you and goodbye."}
		// {"Status":"OK","Signature":{"Version":0,"Tag":"+wEWbd4NHgAkwM2oE5z9hxQ10ahLYlAnYWFjpkElmDI=","Data":"8AMqYxrrW6gnfYJH7X42EF9029xleQVR0jmjLkk8iauSe7jyZeUjer2VC/sHC0lTt5ejbK2nfdyrseSR/DQv3cPhbEA0huK9OXl6CzEBRVjD/GtDre2FatAoxbjqLsKfvloCv+GstuRJS+G4GoVoLvAMr6C8WVkPAi0TVtaGTbvIhRqzQhpJ320PIRue1GO977tUlq2JWuz2LjYnfyCXjWjvPSLtU4zoPxMcqqEkK7WRUI4sz5qmnEMKU1PVkYf+O2kaCj3YB+woQTxyn1QcwDKSG3n8RCJWqV4WriFnKKoALIpqFDhfhZ4PMGxsT/t4ydH7oqeH5MyaYgftEurRP5zZd6/6jrKYnaI6z/hCGvdinCTRD9peCKfG4CZfyYrYBxuu+tYPuYYy0KTIgycOjJcyfiiA3gwSzolks0oQOzBkVhQU1CRodl+M6w5D/Ei3zOVDbEvHLns8qOz6mrvMT5pGpYh8KYoQ0ZjiTGX9SeWDVzO4WoErztX9Pg50Ajpvw+vdRK08nZsESAicB9pTJiJmv6L1fCsNX3oDScTonZaFLqpJj2jHdVh69QQdn05I6yYb6xTf9R7FLMXXq3gsA15L9b/JnhQRaT4V3Lfj4YJvoO/ZX5ThkpsBCcEXbEXYXi6liPyC8tPq0F2SWH6p/GDJlcos46qbBGyXYQibIaSwh4NlCoQB2E04mQLjMwlb"},"Transcription":"hello it's a good day for me to teach you the sound of my voice. you have learned what i'd look like now you can hear what i sound like. the sound of my voice will help the transcription service to recognize my unique voice in the future. training will provide a better experience with greater accuracy when talking or dictating thank you and goodbye. ."}
		String signatureA = "{\"Version\":0,\"Tag\":\"7LQep7sfb3eMjgfGwf6Cjt7cibpa/bpSAR+irUloIII=\",\"Data\":\"26HD7rp3LdeMM78xQ+uLZJSpF6tSiC3pRJVIv8mhlINkFo72ofZjxiydYIPuH96sRwZZxwj3FukUPD3KQzVe3HJuO4Y1XbH/azMkfdLWFUcxazv/Pp8Y9m6JvBnhOjLPp2RGx6dsiTelEjUgts/mvgx3wYqwN/IF5/dDbzhk6E5CTyxH/fQEw102GnYhNRuMbIw3BxKhQhDgxaHxybdLASE9/Cp2L3qWazxU4FqndKdhJI5GJdWF2uWk6BID6f3EFw19h7Z64PiS42fNnnckkhbRbAJ2Z8Qx4eEeIXRM833IETSScwl0nIRVDIGnYUYVjD0TblGZnVM47VwbhiExqM+OOL9eo8/K26KGANhp1CLCUNZq6mexIa5fLj9gPSH8W/TIuiHQ9lU++Zz59j0iNIBR4CimN7cKyHW2WCWQGC8+anWmCOGKIUsNzFpLD2B4SQKKmlJbzpCftXFqVZj5uvVc8LxAdvDqWOOe8yBIjE95KZ+a0zzA/bj4BH49zfBNF0XtRIXu/NyBa8mtRjIi4nagSbUMzc/J4lzcnXAF6sMB3PXZXldnreaScVmtdeuEJKF0ckFp4iYvchUv8ua2GVFn45HORNIgfCoCbgljpDLpjz+MiBih69w85BxOAalKZjEKEt4VIb/AHhyHun4v3kRFrNs7+g6nk1Z2HtR52LTKAoV08i4nmPCHzVFE7o7E\"}";
		String signatureB = "{\"Version\":0,\"Tag\":\"+wEWbd4NHgAkwM2oE5z9hxQ10ahLYlAnYWFjpkElmDI=\",\"Data\":\"8AMqYxrrW6gnfYJH7X42EF9029xleQVR0jmjLkk8iauSe7jyZeUjer2VC/sHC0lTt5ejbK2nfdyrseSR/DQv3cPhbEA0huK9OXl6CzEBRVjD/GtDre2FatAoxbjqLsKfvloCv+GstuRJS+G4GoVoLvAMr6C8WVkPAi0TVtaGTbvIhRqzQhpJ320PIRue1GO977tUlq2JWuz2LjYnfyCXjWjvPSLtU4zoPxMcqqEkK7WRUI4sz5qmnEMKU1PVkYf+O2kaCj3YB+woQTxyn1QcwDKSG3n8RCJWqV4WriFnKKoALIpqFDhfhZ4PMGxsT/t4ydH7oqeH5MyaYgftEurRP5zZd6/6jrKYnaI6z/hCGvdinCTRD9peCKfG4CZfyYrYBxuu+tYPuYYy0KTIgycOjJcyfiiA3gwSzolks0oQOzBkVhQU1CRodl+M6w5D/Ei3zOVDbEvHLns8qOz6mrvMT5pGpYh8KYoQ0ZjiTGX9SeWDVzO4WoErztX9Pg50Ajpvw+vdRK08nZsESAicB9pTJiJmv6L1fCsNX3oDScTonZaFLqpJj2jHdVh69QQdn05I6yYb6xTf9R7FLMXXq3gsA15L9b/JnhQRaT4V3Lfj4YJvoO/ZX5ThkpsBCcEXbEXYXi6liPyC8tPq0F2SWH6p/GDJlcos46qbBGyXYQibIaSwh4NlCoQB2E04mQLjMwlb\"}";
		Participant speakerA  = Participant.from("Katie", "en-us", signatureA);
		Participant speakerB  = Participant.from("Steve", "en-us", signatureB);
		transcriber.addParticipant(speakerA);
		transcriber.addParticipant(speakerB);

		transcriber.startTranscribingAsync().get();

		stopRecognitionSemaphore.acquire();

		transcriber.stopTranscribingAsync().get();
		transcriber.endConversationAsync().get();
		
		
	}

}
