package licenta;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SymmetricCrypto extends Symmetric {
	private static Cipher cipher;
	private String plainText;
	private byte[] textToEncrypt = null;
	private String algorithm;
	private static final SecureRandom random = new SecureRandom();
	/*
	 * public void setAlgorithm (String algorithm) { this.algorithm = algorithm; }
	 */

	public String getAlgorithm() {
		return algorithm;
	}

	public void setPlainText(String plainText) {
		this.plainText = plainText;
	}

	public String getPlainText() {

		return plainText;
	}

	public byte[] getTextToEncrypt() throws UnsupportedEncodingException {

		return textToEncrypt = this.getPlainText().getBytes("UTF8");
	}

	Cipher cipherAlgorithm(String algorithm) throws NoSuchAlgorithmException, NoSuchPaddingException {
		cipher = Cipher.getInstance(algorithm);
		return cipher;
	}

	 IvParameterSpec getIV (String algorithm) {
		try {
			cipherAlgorithm(algorithm);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final byte[] ivBytes = new byte[cipher.getBlockSize()];
		
	    random.nextBytes(ivBytes);
	    return new IvParameterSpec(ivBytes);
	}
	public SymmetricCrypto() {
	}

	public SymmetricCrypto(String algorithm) {
		this.algorithm = algorithm;
	}

	public SymmetricCrypto(String plainText, String algorithm) {
		this.plainText = plainText;
		this.algorithm = algorithm;
	}

	static byte[] encryptText(byte[] textToEncrypt, SecretKey secretKey, IvParameterSpec iv)
			throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

		cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv, random);
		byte[] encryptedBytes = cipher.doFinal(textToEncrypt);
		return encryptedBytes;
	}

	static byte[] decryptedText(byte[] encryptedBytes, SecretKey secretKey, IvParameterSpec iv)
			throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

		cipher.init(Cipher.DECRYPT_MODE, secretKey, iv, random);
		byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
		return decryptedBytes;

	}

	static void encryptFile(Key key, File inputFile, File outputFile) {
		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;
		try {
			cipher.init(Cipher.ENCRYPT_MODE, key);

			inputStream = new FileInputStream(inputFile);
			byte[] inputBytes = new byte[(int) inputFile.length()];
			inputStream.read(inputBytes);
			byte[] outputBytes = cipher.doFinal(inputBytes);
			outputStream = new FileOutputStream(outputFile);
			outputStream.write(outputBytes);

		} catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | IOException e1) {
			// TODO Auto-generated catch block

			e1.printStackTrace();
		} finally {

			closeQuietly(inputStream, outputStream);

		}

	}

	static void decryptedFile(Key key, File inputFile, File outputFile) {
		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;
		try {
			cipher.init(Cipher.DECRYPT_MODE, key);

			inputStream = new FileInputStream(inputFile);
			byte[] inputBytes = new byte[(int) inputFile.length()];
			inputStream.read(inputBytes);
			byte[] outputBytes = cipher.doFinal(inputBytes);
			outputStream = new FileOutputStream(outputFile);
			outputStream.write(outputBytes);

		} catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | IOException e1) {
			// TODO Auto-generated catch block

			e1.printStackTrace();
		} finally {

			closeQuietly(inputStream, outputStream);

		}
	}

	static void encryptImage(Key key, File inputFile, File outputFile, String algorithm) {
		CipherOutputStream cos = null;
		FileOutputStream fos = null;
		FileInputStream fis = null;

		int i;
		try {

			cipher.init(Cipher.ENCRYPT_MODE, key);
			fis = new FileInputStream(inputFile);
			fos = new FileOutputStream(outputFile);
			cos = new CipherOutputStream(fos, cipher);// SymmetricCrypto.cipherAlgorithm(algorithm));
			byte[] buff = new byte[1024];
			while ((i = fis.read(buff)) != -1) {
				fos.write(buff);
			}
		} catch (InvalidKeyException | IOException // NoSuchPaddingException | NoSuchAlgorithmException
		e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	static void decryptImage(Key key, File inputFile, File outputFile) {
		CipherOutputStream cos = null;
		FileOutputStream fos = null;
		FileInputStream fis = null;
		int i;
		try {

			cipher.init(Cipher.DECRYPT_MODE, key);
			fis = new FileInputStream(inputFile);
			fos = new FileOutputStream(outputFile);
			cos = new CipherOutputStream(fos, cipher);
			byte[] buff = new byte[1024];
			while ((i = fis.read(buff)) != -1) {
				fos.write(buff);
			}
		} catch (InvalidKeyException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public String encryptTime(long executionTime) {

		return String.valueOf(executionTime);

	}

	@Override
	public String decpryptTime(long executionTime) {

		return String.valueOf(executionTime);
	}

	static void closeQuietly(Closeable... close) {
		for (Closeable c : close) {
			if (c != null) {
				try {
					c.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
