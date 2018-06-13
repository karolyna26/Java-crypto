package licenta;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SymmetricKey implements Criptable {
	
 
	
	

	
	SecretKey key (final String algorithm) throws NoSuchAlgorithmException  {
		KeyGenerator keygen = KeyGenerator.getInstance(algorithm);
		SecureRandom secure = new SecureRandom ();
		
		//int [] keyBitsSize = {56, 128, 168, 256, 448};
		Map <String, Integer> key = new HashMap<>();
		key.put("DES", 56);
		key.put("RC2", 128);
		key.put("DESede", 168);
		key.put("AES", 256);
		
		for (Map.Entry m:key.entrySet()) {
			   
			   if (m.getKey().equals(algorithm)) {
				   int value = (int) m.getValue();
				keygen.init( value, secure);
			   }
				   
		}
		final SecretKey secretKey= keygen.generateKey();
		return secretKey;
	}
	
	
	public SymmetricKey() {
		// TODO Auto-generated constructor stub
		
	}

	Key keySpecFile (String algorithm, String key) throws NoSuchAlgorithmException  {
		Key keygen = null;
		List <String> keys = new ArrayList<>();
		keys.add("DES");
		keys.add("RC2");
		keys.add("DESede");
		keys.add("AES");
		
		for (String k: keys) {
			   
			   if (k==algorithm) {
	 keygen = new SecretKeySpec(key.getBytes(), algorithm); 
		}
	 }

		return keygen;
	}


	Key keyImage (String algorithm) throws NoSuchAlgorithmException {
		KeyGenerator keygen = KeyGenerator.getInstance(algorithm);
		Key key = keygen.generateKey();
		
		return key;
		
	}

}
