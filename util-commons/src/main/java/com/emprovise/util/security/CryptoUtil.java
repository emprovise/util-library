package com.emprovise.util.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility to encrypt and decrypt text. Includes command line encryption and decryption utility.
 *
 */
public class CryptoUtil {

	private String algorithm;
	private String cipherMode;
	private String padding;
	private String encoding;
	private String hashAlgorithm;

	private static final String CBC  = "CBC";
	private static final String HEX  = "HEX";
	private static final String DELIM = ";";
	private static final String UTF8 = "UTF8";
	private static final String JCEKS = "JCEKS";

	private static Logger log = LoggerFactory.getLogger(CryptoUtil.class);

	public CryptoUtil() {
		this.algorithm = "AES";
		this.cipherMode = "ECB";
		this.padding = "PKCS5Padding";
		this.encoding = "BASE64";
		this.hashAlgorithm = "HmacMD5";
	}

	/**
	 * Creates an instance of CryptoUtil with the algorithm, padding, cipherMode and encoding parameters.
	 * @param algorithm
	 * 		Encryption algorithm to be used. Currently only AES is supported.
	 * @param cipherMode
	 * 		Cipher mode to be user. Currently both CBC and ECB are supported.
	 * @param padding
	 * 		Padding mechanism for encryption. Currently only PKCS5 padding is supported.
	 * @param encoding
	 * 		Encoding to be used for ciphertext. Currently HEX and BASE64 formats are supported.
	 */
	public CryptoUtil(String algorithm, String cipherMode, String padding, String encoding, String hashAlgorithm) {

		if(!algorithm.equalsIgnoreCase("AES")) {
			throw new IllegalArgumentException("Invalid algorithm. Currently only AES algorithm is supported.");
		}
		else if(!cipherMode.equalsIgnoreCase("ECB") && !cipherMode.equalsIgnoreCase("CBC")) {
			throw new IllegalArgumentException("Invalid cipher mode. Currently ECB and CBC cipher modes are supported.");
		}
		else if(!padding.equalsIgnoreCase("PKCS5Padding")) {
			throw new IllegalArgumentException("Invalid padding type. Currently only PKCS5Padding padding type is supported.");
		}
		else if(!encoding.equalsIgnoreCase("BASE64") && !encoding.equalsIgnoreCase("HEX")) {
			throw new IllegalArgumentException("Invalid encoding type. Currently BASE64 and HEX encoding types are supported.");
		}
		else if(!hashAlgorithm.equalsIgnoreCase("HmacMD5") && !hashAlgorithm.equalsIgnoreCase("HmacSHA1")) {
			throw new IllegalArgumentException("Invalid hash algorithm. Currently only HmacMD5 and HmacSHA1 hash algorithms are supported.");
		}
		else {
			this.algorithm = algorithm;
			this.cipherMode = cipherMode;
			this.padding = padding;
			this.encoding = encoding;
			this.hashAlgorithm = hashAlgorithm;
		}
	}

	/**
	 * Initializes the Security Key using the specified constant keyValue.
	 * @return
	 * 		{@link java.security.Key} generated from the binary key value and the algorithm specified.
	 * @throws NoSuchAlgorithmException
	 * @throws Exception
	 */
	private Key generateKey(String keyFilePath) throws NoSuchAlgorithmException, IOException {
		byte[] keyBytes = FileUtils.readFileToByteArray(new File(keyFilePath));
		Key key = new SecretKeySpec(keyBytes, algorithm);
		return key;
	}

	/**
	 * Initializes the cipher for encryption or decryption.
	 * @param mode
	 * 		Mode of operation to be performed on the cipher generated. 1 for Encryption operation and 2 for Decryption operation.
	 * @param initVector
	 * 		Initialization vector to be used for decryption in Cipher-block chaining (CBC) mode.
	 * @param key
	 * 		key to be used to initialize the cipher for encryption or decryption.
	 * @return
	 * 		{@link Cipher} to be used to perform encryption or decryption.
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeySpecException
	 * @throws UnrecoverableKeyException
	 * @throws KeyStoreException
	 * @throws DecoderException
	 * @throws InvalidKeyException
	 * @throws InvalidAlgorithmParameterException
	 */
	private Cipher getCipher(int mode, String initVector, Key key)  throws NoSuchAlgorithmException, NoSuchProviderException,
			NoSuchPaddingException, InvalidKeySpecException,
			UnrecoverableKeyException, KeyStoreException, DecoderException,
			InvalidKeyException, InvalidAlgorithmParameterException {

		// 1. Create a Cipher instance
		Cipher cipher = null;
		cipher = Cipher.getInstance(algorithm + "/" + cipherMode + "/" + padding);

		// 2. Initialize the Cipher
		if (null != initVector && cipherMode.toUpperCase().startsWith(CBC)) {

			byte[] initBytes = null;

			if (encoding.equalsIgnoreCase(HEX)) {
				initBytes = Hex.decodeHex(initVector.toCharArray());
			} else {
				initBytes = Base64.decodeBase64(initVector.getBytes());
			}

			cipher.init(mode, key, new IvParameterSpec(initBytes));
		} else {
			cipher.init(mode, key);
		}

		return cipher;
	}

	/**
	 * Encrypts the specified plaintext using the initialized encryption algorithm to return the ciphertext.
	 * @param plainText
	 * 		{@link String} which needs to be encrypted
	 * @return
	 * 		{@link String} containing the encrypted text
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws UnrecoverableKeyException
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeySpecException
	 * @throws KeyStoreException
	 * @throws InvalidAlgorithmParameterException
	 * @throws DecoderException
	 */
	public String encrypt(Key key, String plainText) throws IllegalBlockSizeException, BadPaddingException,
			UnrecoverableKeyException, InvalidKeyException,
			NoSuchAlgorithmException, NoSuchProviderException,
			NoSuchPaddingException, InvalidKeySpecException,
			KeyStoreException, InvalidAlgorithmParameterException,
			DecoderException {
		String result = null;
		Cipher cipher = getCipher(Cipher.ENCRYPT_MODE, null, key);

		byte[] inputBytes = plainText.getBytes();
		byte[] cipherText = cipher.doFinal(inputBytes);

		if (encoding.equalsIgnoreCase(HEX)) {
			result = new String(Hex.encodeHex(cipherText));
		} else {
			result = new String(Base64.encodeBase64(cipherText));
		}

		// For AES algorithm, store the Initialization Vector in the encrypted
		// data as well, for use during decryption.
		if (cipherMode.equalsIgnoreCase(CBC)) {
			byte[] initVector = cipher.getIV();

			if (encoding.equalsIgnoreCase(HEX)) {
				result = new String(Hex.encodeHex(initVector)) + DELIM + result;
			} else {
				result = new String(Base64.encodeBase64(initVector)) + DELIM + result;
			}
		}

		return result;
	}

	/**
	 * Decrypts the specified ciphertext using the initialized encryption algorithm to return the plaintext.
	 * @param cipherText
	 * 		{@link String} which needs to be decrypted
	 * @return
	 * 		{@link String} containing the decrypted text
	 * @throws DecoderException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws UnrecoverableKeyException
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeySpecException
	 * @throws KeyStoreException
	 * @throws InvalidAlgorithmParameterException
	 */
	public String decrypt(Key key, String cipherText) throws DecoderException, IllegalBlockSizeException,
			BadPaddingException, UnrecoverableKeyException,
			InvalidKeyException, NoSuchAlgorithmException,
			NoSuchProviderException, NoSuchPaddingException,
			InvalidKeySpecException, KeyStoreException,
			InvalidAlgorithmParameterException {
		String result = null;
		String initVector = null;

		if (cipherMode.equalsIgnoreCase(CBC)) {
			String[] parts = cipherText.split(DELIM);
			initVector = parts[0];
			cipherText = parts[1];
		}
		Cipher cipher = getCipher(Cipher.DECRYPT_MODE, initVector, key);

		byte[] inputBytes = null;

		if (encoding.equalsIgnoreCase(HEX)) {
			inputBytes = Hex.decodeHex(cipherText.toCharArray());
		} else {
			inputBytes = Base64.decodeBase64(cipherText.getBytes());
		}

		byte[] plainText = cipher.doFinal(inputBytes);
		result = new String(plainText);

		return result;
	}

	public String hashValue(Key key, String inputText) {

		String messageDigest = null;

		try {
			// Create a MAC object using HASH ALGORITHM and initialize with key
			Mac mac = Mac.getInstance(hashAlgorithm);
			mac.init(key);

			// Encode the string into bytes using UTF-8 and digest it
			byte[] utf8Bytes = inputText.getBytes(UTF8);
			byte[] digest = mac.doFinal(utf8Bytes);

			// Convert the digest into a string by encoding
			if (encoding.equalsIgnoreCase(HEX)) {
				messageDigest = Hex.encodeHexString(digest);
			} else {
				messageDigest = Base64.encodeBase64String(digest);
			}

		} catch (Exception ex) {
			log.error("Exception", ex);
		}

		return messageDigest;
	}

	public static String encryptDefault(String plainText, String keyFilePath) {

		CryptoUtil util = new CryptoUtil();
		String result = null;

		try{
			Key secretKey = util.generateKey(keyFilePath);
			result = util.encrypt(secretKey, plainText);
		}
		catch(Exception ex) {
			log.error("Exception", ex);
		}

		return result;
	}

	public static String decryptDefault(String cipherText, String keyFilePath) {

		CryptoUtil util = new CryptoUtil();
		String result = null;

		try{
			Key secretKey = util.generateKey(keyFilePath);
			result = util.decrypt(secretKey, cipherText);
		}
		catch(Exception ex) {
			log.error("Exception", ex);
		}

		return result;
	}

	public KeyStore loadKeyStore(String keyStoreFileName, String keyStorePin) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {

		File keyStoreFile = new File(keyStoreFileName);

		if(keyStoreFile==null || !keyStoreFile.exists()) {
			throw new FileNotFoundException("KeyStore file '" + keyStoreFileName + "' not found in the Class Path");
		}

		KeyStore keystore = KeyStore.getInstance(JCEKS);
		InputStream jksStream = new FileInputStream(keyStoreFile);
		keystore.load(jksStream, keyStorePin.toCharArray());
		jksStream.close();
		return keystore;
	}

	public Key getSecretKey(String keyStoreFileName, String keyStorePin, String keyAlias, String tokenPin) {

		try {
			KeyStore keystore = loadKeyStore(keyStoreFileName, keyStorePin);
			return keystore.getKey(keyAlias, tokenPin.toCharArray());
		}
		catch(Exception ex) {
			log.error("Exception", ex);
			return null;
		}
	}

	public static int generateRandomNumber() throws NoSuchAlgorithmException {
		SecureRandom random = SecureRandom.getInstance ("SHA1PRNG");
		return random.nextInt();
	}

	public static String bytesToHex(byte[] bytes) {
		final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
		char[] hexChars = new char[bytes.length * 2];
		int v;
		for ( int j = 0; j < bytes.length; j++ ) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	/**
	 * Utility to encrypt or decrypt a text string.
	 * @param args
	 * 		Optional string parameters. Usage is "java CryptoUtil -encrypt plaintext" for encryption or "java CryptoUtil -decrypt ciphertext" for decryption.
	 * 		It prints out the result which is either being the cipher text for encryption and plain text for decryption.
	 */
	public static void main(String[] args) {

		if(args.length < 2) {
			throw new IllegalArgumentException("Required minimum two parameters. Usage \"java CryptoUtil -encrypt plaintext\" or \"java CryptoUtil -decrypt ciphertext\"");
		}

		try {

			CryptoUtil util = new CryptoUtil("AES", "CBC", "PKCS5Padding", "HEX", "HmacMD5");
			String keyFilePath = null;

			if(args[0].equalsIgnoreCase("-keyFile")) {
				keyFilePath = args[1];
			}
			else {
				throw new IllegalArgumentException("Argument keyFile having the base key is missing");
			}

			Key secretKey = util.generateKey(keyFilePath);
			String inputText = null;
			String result = null;

			if(args[2].equalsIgnoreCase("-encrypt")) {
				inputText = args[3];
				result = util.encrypt(secretKey, inputText);
			}
			else if(args[2].equalsIgnoreCase("-decrypt")) {
				inputText = args[3];
				result = util.decrypt(secretKey, inputText);
			}
			else if(args[2].equalsIgnoreCase("-hash")) {
				inputText = args[3];
				result = util.hashValue(secretKey, inputText);
			}
			else {
				throw new IllegalArgumentException("Invalid parameter, valid parameters are '-encrypt' , '-decrypt' or '-hash'");
			}

			log.info("Result Text = " + result);

		} catch (Exception ex) {
			log.error("Exception", ex);
		}
	}
}