package licenta;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

public class Main {

	public Main() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		/*SymmetricCrypto so = new SymmetricCrypto("Test trimitem acum un rahat pe mail", "AES/CTR/NOPADDING");
		SymmetricKey sk = new SymmetricKey();
		//CryptoFrame cf = new CryptoFrame();
		//cf.setVisible(true);
		final SecretKey secretK = sk.key("AES");
		final IvParameterSpec iv = so.getIV(so.getAlgorithm());
		so.cipherAlgorithm(so.getAlgorithm());
		System.out.println("Text before encryption is: " + so.getPlainText());
		so.getTextToEncrypt();
		long before = System.currentTimeMillis();
		System.out.println("Time in millis beofre is: " + before);
		byte[] encryptedBytes = SymmetricCrypto.encryptText(so.getTextToEncrypt(), secretK, iv);
		System.out.println(sk.key("AES"));
		System.out.println("Encrypted text after encryption: " + new String(encryptedBytes, "UTF8"));
		byte [] decrypted = SymmetricCrypto.decryptedText(encryptedBytes, secretK, iv);
		System.out.println("Decrypted text after decryption: " + new String(decrypted, "UTF-8"));
		long after = System.currentTimeMillis();
		System.out.println("Time in millis after is: " + after);
		long difference = after - before;
		System.out.println(so.encryptTime(difference));*/
		
		AsymmetricKey ak = new AsymmetricKey(3072, "RSA");

		ak.createKeys();

		ak.writePrivateKey("KeyPair/privateKey", ak.getPrivateKey().getEncoded());
		ak.writePublicKey("KeyPair/publicKey", ak.getPublicKey().getEncoded());
		
		AsymmetricCrypto ac = new AsymmetricCrypto("RSA");
		
		String msg = "Cryptography is fun!";
		String encrypted_msg = ac.encryptText(msg);
		String decrypted_msg = ac.decryptText(encrypted_msg);
		System.out.println("Original Message: " + msg + "\nEncrypted Message: " + encrypted_msg + "\nDecrypted Message: " + decrypted_msg);
		
		if(new File("KeyPair/text.txt").exists()){
			ac.encryptFile(ac.getFileInBytes(new File("KeyPair/text.txt")), new File("KeyPair/text_encrypted.txt"));
			ac.decryptFile(ac.getFileInBytes(new File("KeyPair/text_encrypted.txt")), new File("KeyPair/text_decrypted.txt"));
		}else{
			System.out.println("Create a file text.txt under folder KeyPair");
		}

		EmailService ems = new EmailService();
		ems.setSubject("Test Subject..");
		ems.setBody("Test Body...");
		Properties props = System.getProperties();

		props.put("fromEmail", "utmblog2018@gmail.com");
		props.put("password", "Utmblog123");
		props.put("toEmail", "carolinaangelica26@gmail.com");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.SocketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); // SSL Factory Class
		props.put("mail.smtp.auth", "true"); // Enabling SMTP Authentication
		props.put("mail.smtp.port", "465"); // SMTP Port

		Authenticator auth = new Authenticator() {
			// override the getPasswordAuthentication method
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(System.getProperty("fromEmail"), System.getProperty("password"));
			}
		};

		Session session = Session.getInstance(props, auth);

		String fromEmail = System.getProperty("fromEmail");
		String toEmail = System.getProperty("toEmail");
		File file = new File("./a.txt");
		BufferedWriter bw;

		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file);
		bw = new BufferedWriter(fw);
		//bw.write(new String(encryptedBytes, "UTF8"));
		bw.close();

		// EmailService.sendAttachmentEmail(session, fromEmail, toEmail,
		// ems.getSubject(), ems.getBody(), file);

	}

}
