package com.github.sawied.microservice.commons.license.crypto;

import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

/**
 * 
 * @author sawied
 *
 */
public class LicenseKeyPair {

	private final KeyPair pair;

	
	private LicenseKeyPair(KeyPair pair) {
		this.pair = pair;
	}



	public KeyPair getPair() {
		return pair;
	}


	public byte[] getPrivate() {
		keyNotNull(pair.getPrivate());
		Key key=pair.getPrivate();
		return getKeyBytes(key);
	}

	public byte[] getPublic() {
		keyNotNull(pair.getPublic());
		Key key=pair.getPublic();
		return getKeyBytes(key);
	}

	private void keyNotNull(Key key) {
		if (key == null) {
			throw new IllegalArgumentException("keyPair does not have the key.");
		}
	}

	private byte[] getKeyBytes(Key key) {
		final byte[] algorithm = key.getAlgorithm().getBytes(StandardCharsets.UTF_8);
		int length = algorithm.length + 1 + key.getEncoded().length;
		byte[] buffer = new byte[length];

		System.arraycopy(algorithm, 0, buffer, 0, algorithm.length);
		buffer[algorithm.length] = 0x00;
		System.arraycopy(key.getEncoded(), 0, buffer, algorithm.length + 1, key.getEncoded().length);
		return buffer;
	}
	
	
	public static class Create{
		
		
		
		public static LicenseKeyPair from(final PublicKey publicKey, final PrivateKey privateKey) {
			return new LicenseKeyPair(new KeyPair(publicKey, privateKey));
		}
		
		
		public static LicenseKeyPair from(final KeyPair keyPair) {
			return new LicenseKeyPair(keyPair);
		}
		
	
		
		
		public static LicenseKeyPair from(final String cipher,final int size) throws NoSuchAlgorithmException {
			String algorithm = algorithmPrefix(cipher);
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
			keyPairGenerator.initialize(size);
			return new LicenseKeyPair(keyPairGenerator.generateKeyPair());
		}
		
		
		
		public static LicenseKeyPair from(byte[] encoded,int type) throws NoSuchAlgorithmException, InvalidKeySpecException {
			if(type==Modifier.PRIVATE) {
				return from(null,getPrivateEncoded(encoded));
			}
			else {
				return from(getPublicEncoded(encoded),null);
			}
		}
		
		
		private static PublicKey getPublicEncoded(byte[] encoded) throws NoSuchAlgorithmException, InvalidKeySpecException {
			X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(getEncoded(encoded));
			KeyFactory factory = KeyFactory.getInstance(getAlgorithm(encoded));
			return factory.generatePublic(x509EncodedKeySpec);
		}


	


		private static PrivateKey getPrivateEncoded(byte[] encoded) throws NoSuchAlgorithmException, InvalidKeySpecException {
			X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(getEncoded(encoded));
			KeyFactory factory = KeyFactory.getInstance(getAlgorithm(encoded));
			return factory.generatePrivate(x509EncodedKeySpec);
		}


		private static String getAlgorithm(byte[] buffer) {
			for(int i=0;i<buffer.length;i++) {
				if(buffer[i]==0x00) {
					return new String(Arrays.copyOf(buffer, i),StandardCharsets.UTF_8);
				}
			}
			throw new IllegalArgumentException("key don't contain algorithm specification.");
		}
		
		
		private static byte[] getEncoded(byte[] buffer) {
			for(int i=0;i<buffer.length;i++) {
				if(buffer[i]==0x00) {
					return Arrays.copyOfRange(buffer, i+1, buffer.length);
				}
			}
			 throw new IllegalArgumentException("key don't contain encoded key.");
		}
		
		
		private static String algorithmPrefix(final String cipher) {
			if(cipher.contains("/")) {
				return cipher.substring(0, cipher.indexOf("cipher"));
			}else {
				return cipher;
			}
		}
		
		
		
		
	}

}
