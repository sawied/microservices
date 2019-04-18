package com.github.sawied.microservice.commons.license;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class License {
	private static final int MAGIC = 0x4e5ece21;

	final private Map<String, Feature> features = new HashMap<String, Feature>();

	final private static String LICENSE_ID = "licenseid";

	final private static String EXPIRTION_DATE = "expiryDate";

	final private static String SIGNATURE_KEY = "licenseSignature";

	final private static String DIGEST_KEY = "signatureDigest";

	private License() {

	}

	public Feature setExpiry(Date expiry) {
		return features.put(EXPIRTION_DATE, Feature.Create.dateFeature(EXPIRTION_DATE, expiry));
	}

	/**
	 * signature
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public void sign(PrivateKey privateKey, String digest) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		MessageDigest instance = MessageDigest.getInstance(digest);
		byte[] unSigned = unSigned();
		byte[] digestValue = instance.digest(unSigned);

		Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
		cipher.init(Cipher.ENCRYPT_MODE, privateKey);
		byte[] signature = cipher.doFinal(digestValue);
		add(signature);

	}

	private void add(byte[] signature) {
		this.features.put(SIGNATURE_KEY, Feature.Create.binaryFeature(SIGNATURE_KEY, signature));
	}

	private byte[] unSigned() {
		return serialized(Collections.singleton(SIGNATURE_KEY));
	}

	private byte[] serialized(Set<String> excluded) {
		Feature[] includedFeatures = featuresSorted(excluded);
		int length = includedFeatures.length;
		byte[][] featuresSerialized = new byte[length][];
		int i = 0;
		int size = 0;
		for (final Feature feature : includedFeatures) {
			featuresSerialized[i] = feature.serialized();
			size += featuresSerialized[i].length;
			i++;
		}
		ByteBuffer buffer = ByteBuffer.allocate(size + Integer.BYTES * (length + 1));
		buffer.putInt(MAGIC);
		for (byte[] featureSerialized : featuresSerialized) {
			buffer.putInt(featureSerialized.length);
			buffer.put(featureSerialized);
		}

		return buffer.array();
	}

	private Feature[] featuresSorted(Set<String> excluded) {
		return this.features.values().stream().filter(f -> !excluded.contains(f.name()))
				.sorted(Comparator.comparing(Feature::name)).toArray(Feature[]::new);
	}

	public static class Create {
		
		
		public static License from(final byte[] bytes) {
			if (bytes.length < Integer.BYTES) {
				throw new IllegalArgumentException("the license is too short");
			}

			License license = new License();
			ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
			int magic = byteBuffer.getInt();
			if(magic!=MAGIC) {
				throw new IllegalArgumentException("the license is incorrect.  ");
			}
			
			while(byteBuffer.hasRemaining()) {
				int featureLen=byteBuffer.getInt();
				byte[] featureSerialized = new byte[featureLen];
				byteBuffer.get(featureSerialized);
				Feature feature = Feature.Create.from(featureSerialized);
				license.add(feature);
			}
			
			return license;
		}
		
		
	}

	public void add(Feature feature) {
		this.features.put(feature.name(), feature);
	}

}
