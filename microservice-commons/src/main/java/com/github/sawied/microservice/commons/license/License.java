package com.github.sawied.microservice.commons.license;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class License {

	private static final Logger LOG = LoggerFactory.getLogger(License.class);
	private static final int MAGIC = 0x4e5ece21;

	final private Map<String, Feature> features = new HashMap<String, Feature>();

	final private static String LICENSE_ID = "licenseid";

	final private static String EXPIRTION_DATE = "expiryDate";

	final private static String SIGNATURE_KEY = "licenseSignature";// signature key

	final private static String DIGEST_KEY = "signatureDigest";// digest algorithm

	final private static byte[] PKEY = new byte[] { (byte) 0x30, (byte) 0x82, (byte) 0x01, (byte) 0x1d, (byte) 0x30,
			(byte) 0x0d, (byte) 0x06, (byte) 0x09, (byte) 0x2a, (byte) 0x86, (byte) 0x48, (byte) 0x86, (byte) 0xf7,
			(byte) 0x0d, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x05, (byte) 0x00, (byte) 0x03, (byte) 0x82,
			(byte) 0x01, (byte) 0x0a, (byte) 0x00, (byte) 0x30, (byte) 0x82, (byte) 0x01, (byte) 0x05, (byte) 0x02,
			(byte) 0x81, (byte) 0xfd, (byte) 0x03, (byte) 0x95, (byte) 0xad, (byte) 0xa5, (byte) 0xe4, (byte) 0x87,
			(byte) 0x67, (byte) 0xb6, (byte) 0x46, (byte) 0x48, (byte) 0xc2, (byte) 0x01, (byte) 0x4d, (byte) 0x81,
			(byte) 0xee, (byte) 0xab, (byte) 0xb7, (byte) 0x31, (byte) 0x94, (byte) 0xe7, (byte) 0x98, (byte) 0x3e,
			(byte) 0xb7, (byte) 0x63, (byte) 0x68, (byte) 0x1b, (byte) 0xd3, (byte) 0x24, (byte) 0x0b, (byte) 0x15,
			(byte) 0x04, (byte) 0x05, (byte) 0x1a, (byte) 0x01, (byte) 0x0d, (byte) 0x0c, (byte) 0x80, (byte) 0x79,
			(byte) 0x56, (byte) 0xcb, (byte) 0xc4, (byte) 0x21, (byte) 0xae, (byte) 0xd1, (byte) 0x1b, (byte) 0x12,
			(byte) 0x5c, (byte) 0xb2, (byte) 0x2c, (byte) 0x4b, (byte) 0x66, (byte) 0x03, (byte) 0xf6, (byte) 0x77,
			(byte) 0x9c, (byte) 0x4e, (byte) 0x35, (byte) 0x82, (byte) 0xa7, (byte) 0x8e, (byte) 0x0e, (byte) 0xea,
			(byte) 0x2f, (byte) 0x01, (byte) 0x7a, (byte) 0x97, (byte) 0xc5, (byte) 0x91, (byte) 0xa0, (byte) 0x12,
			(byte) 0x5c, (byte) 0x72, (byte) 0xa0, (byte) 0x55, (byte) 0x13, (byte) 0xc8, (byte) 0x6c, (byte) 0xbc,
			(byte) 0x0b, (byte) 0x49, (byte) 0x9f, (byte) 0x82, (byte) 0x5f, (byte) 0x47, (byte) 0x00, (byte) 0x1a,
			(byte) 0x84, (byte) 0x86, (byte) 0x37, (byte) 0x55, (byte) 0xf8, (byte) 0x2f, (byte) 0xb5, (byte) 0xea,
			(byte) 0xf3, (byte) 0x8b, (byte) 0x69, (byte) 0x38, (byte) 0xce, (byte) 0x4e, (byte) 0x12, (byte) 0xeb,
			(byte) 0x98, (byte) 0x1a, (byte) 0x4d, (byte) 0xf9, (byte) 0x86, (byte) 0xfc, (byte) 0x06, (byte) 0x11,
			(byte) 0x34, (byte) 0x66, (byte) 0x18, (byte) 0x50, (byte) 0xa5, (byte) 0x6a, (byte) 0x9d, (byte) 0xf2,
			(byte) 0x53, (byte) 0xea, (byte) 0x6d, (byte) 0x0e, (byte) 0x38, (byte) 0x30, (byte) 0xae, (byte) 0x22,
			(byte) 0xb0, (byte) 0xe3, (byte) 0xdb, (byte) 0xd4, (byte) 0x2a, (byte) 0x78, (byte) 0xa3, (byte) 0xf9,
			(byte) 0x95, (byte) 0x1f, (byte) 0xc2, (byte) 0x79, (byte) 0xce, (byte) 0xb3, (byte) 0x86, (byte) 0x86,
			(byte) 0x14, (byte) 0x0b, (byte) 0x3b, (byte) 0xc6, (byte) 0xc4, (byte) 0xb1, (byte) 0xc4, (byte) 0xb4,
			(byte) 0xcc, (byte) 0xb6, (byte) 0x9e, (byte) 0x68, (byte) 0x11, (byte) 0xfc, (byte) 0x45, (byte) 0x0f,
			(byte) 0x9f, (byte) 0x3c, (byte) 0x3e, (byte) 0xb8, (byte) 0xda, (byte) 0x18, (byte) 0xfb, (byte) 0xf8,
			(byte) 0xa0, (byte) 0x3a, (byte) 0xdf, (byte) 0x81, (byte) 0x50, (byte) 0x77, (byte) 0x3b, (byte) 0x1e,
			(byte) 0xf6, (byte) 0x63, (byte) 0x08, (byte) 0xf4, (byte) 0x24, (byte) 0x89, (byte) 0x4b, (byte) 0x96,
			(byte) 0x75, (byte) 0xb6, (byte) 0x94, (byte) 0xc2, (byte) 0xed, (byte) 0x5f, (byte) 0x5c, (byte) 0xb0,
			(byte) 0xf7, (byte) 0x82, (byte) 0x67, (byte) 0x6b, (byte) 0x80, (byte) 0x3b, (byte) 0xda, (byte) 0x11,
			(byte) 0x74, (byte) 0x62, (byte) 0xf1, (byte) 0x8e, (byte) 0x5a, (byte) 0xa8, (byte) 0x57, (byte) 0x1a,
			(byte) 0x4b, (byte) 0xaa, (byte) 0x1d, (byte) 0x16, (byte) 0x82, (byte) 0xac, (byte) 0x60, (byte) 0xe4,
			(byte) 0xf8, (byte) 0x82, (byte) 0xe0, (byte) 0xa4, (byte) 0x30, (byte) 0x00, (byte) 0xc1, (byte) 0x14,
			(byte) 0x0c, (byte) 0x8b, (byte) 0x59, (byte) 0x7b, (byte) 0x85, (byte) 0x68, (byte) 0xa7, (byte) 0xc9,
			(byte) 0x53, (byte) 0xcb, (byte) 0xe8, (byte) 0xf6, (byte) 0x35, (byte) 0x56, (byte) 0x86, (byte) 0x4c,
			(byte) 0x45, (byte) 0x33, (byte) 0x1d, (byte) 0x08, (byte) 0xe7, (byte) 0x65, (byte) 0xfe, (byte) 0x8e,
			(byte) 0x63, (byte) 0x92, (byte) 0x69, (byte) 0xe9, (byte) 0x55, (byte) 0xd7, (byte) 0xb1, (byte) 0x02,
			(byte) 0x03, (byte) 0x01, (byte) 0x00, (byte) 0x01 };

	public License() {

	}

	public UUID setLicenseId() {
		UUID randomUUID = UUID.randomUUID();
		setLicenseId(randomUUID);
		return randomUUID;
	}

	private Feature setLicenseId(UUID randomUUID) {
		return features.put(LICENSE_ID, Feature.Create.uuidFeature(LICENSE_ID, randomUUID));

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

		add(Feature.Create.stringFeature(DIGEST_KEY, digest));
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
			if (magic != MAGIC) {
				throw new IllegalArgumentException("the license is incorrect.  ");
			}

			while (byteBuffer.hasRemaining()) {
				int featureLen = byteBuffer.getInt();
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

	@SuppressWarnings("unchecked")
	public byte[] serialized() {
		return serialized(Collections.EMPTY_SET);
	}

	public boolean isOK() {
		
		Feature feature = features.get(EXPIRTION_DATE);
		Date expirtion = feature.getDate();
		
		if(!new Date().before(expirtion)) {
			return false;
		}
		
		byte[] pubKeyByte = PKEY;
		try {
			KeyFactory kf = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pubKeyByte);
			PublicKey pubKey = kf.generatePublic(keySpec);
			return verify(pubKey);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			LOG.error("parse public key occur error.");
		}
		
		
		
		return false;

	}

	public boolean verify(PublicKey pubKey) {
		try {
			MessageDigest md = MessageDigest.getInstance(get(DIGEST_KEY).getString());
			byte[] unSigned = unSigned();
			byte[] digestValue = md.digest(unSigned);

			Cipher cipher = Cipher.getInstance(pubKey.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, pubKey);
			byte[] signature = cipher.doFinal(getSignature());
			return Arrays.equals(digestValue, signature);

		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException e) {
			LOG.error("verify encounter errors {}", e.getMessage());
		}
		return false;
	}

	private byte[] getSignature() {
		return get(SIGNATURE_KEY).getBinary();
	}

	public Feature get(String name) {
		return features.get(name);
	}

}
