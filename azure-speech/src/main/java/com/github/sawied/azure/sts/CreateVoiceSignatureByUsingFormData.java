package com.github.sawied.azure.sts;

import java.io.File;
import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class CreateVoiceSignatureByUsingFormData {

	public static void main(String[] args) throws ClientProtocolException, IOException {
		String region = "eastasia";
        File sampelVoice=new File("E:/githome/conversation/enrollment_audio_steve.wav");
       CloseableHttpClient httpclient = HttpClients.createDefault();
       
       HttpPost post = new HttpPost("https://signature."+region+".cts.speech.microsoft.com/api/v1/Signature/GenerateVoiceSignatureFromByteArray");
       post.setHeader("Ocp-Apim-Subscription-Key", "675f1a8e19064675b1b6112e799d5299");
       FileEntity entity = new FileEntity(sampelVoice);
       post.setEntity(entity);
       CloseableHttpResponse response = httpclient.execute(post);
       System.out.println("response code : " + response.getStatusLine().getStatusCode());
       String responseEntity = EntityUtils.toString(response.getEntity());
       System.out.println(responseEntity);
       response.close();
       httpclient.close();
	}

}
