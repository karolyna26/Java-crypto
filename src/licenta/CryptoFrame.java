package licenta;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.naming.directory.NoSuchAttributeException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class CryptoFrame extends JFrame {

	final DefaultComboBoxModel<String> model, model3;
	private JComboBox<String> comboOperationType, comboEncryptionType, comboResourceType;
	private Container contentPane;
	private JLabel jLabelHelper, jLabelAlg, jLabelKeyLength;
	private JTextField jTFAlgorithm, JTFKeyLength, JTFHelper;
	private JButton sendEmail, performCrypto;
	private GridBagConstraints grid;
	private PrivateKey privateKey;
	private PublicKey publicKey;
	private int getKeyfromComp;
	private CryptoFrame cf;
	private String encrypted;
	private String decrypted;
	private byte[] encryptedBytes;

	public CryptoFrame() {
		// TODO Auto-generated constructor stub
		contentPane = this.getContentPane();
		setTitle("Fereastra Cripto");
		setSize(400, 400);
		setLocation(new Point(1200, 100));
		setLayout(new FlowLayout());
		setResizable(false);
		model = new DefaultComboBoxModel<>();
		model3 = new DefaultComboBoxModel<>();

		initComponents();
		contentPane.add(comboOperationType);
		contentPane.add(comboEncryptionType);
		contentPane.add(comboResourceType);
		contentPane.add(jLabelHelper);
		contentPane.add(JTFHelper);
		contentPane.add(jLabelAlg);
		contentPane.add(jTFAlgorithm);
		contentPane.add(jLabelKeyLength);
		contentPane.add(JTFKeyLength);
		contentPane.add(performCrypto);
		contentPane.add(sendEmail);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	private void initComponents() {
		initComboBoxes();

		this.jLabelHelper = new JLabel();
		this.jLabelAlg = new JLabel();

		this.JTFHelper = new JTextField(10);
		this.jTFAlgorithm = new JTextField(3);
		this.jLabelKeyLength = new JLabel();
		this.JTFKeyLength = new JTextField(4);

		this.sendEmail = new JButton();
		this.performCrypto = new JButton();

		this.jLabelHelper.setVisible(false);
		this.jLabelAlg.setVisible(false);
		this.jLabelKeyLength.setVisible(false);
		this.JTFHelper.setVisible(false);
		this.jTFAlgorithm.setVisible(false);
		this.JTFKeyLength.setVisible(false);
		this.performCrypto.setVisible(false);
		this.sendEmail.setVisible(false);
	}

	private void initComboBoxes() {
		this.comboEncryptionType = new JComboBox<>();
		this.comboResourceType = new JComboBox<>();

		this.comboEncryptionType.setVisible(false);
		this.comboResourceType.setVisible(false);

		String[] criptableElements = { " ", "Criptare Asimetrica", "Criptare Simetrica", "Comparatie Timpi" };
		this.comboOperationType = new JComboBox<String>(criptableElements);
		this.comboOperationType.setName("comboFirst");
		this.comboOperationType.setSelectedIndex(0);

		comboOperationType.addItemListener(getComboOperationTypeListner()); //simetric, asimetric, comparatie

		comboEncryptionType.addItemListener(getComboEncryptionTypeListner()); //criptare, decriptare

		comboResourceType.addActionListener(getComboResourceTypeListner()); //text, fisier, imagine
	}

	private ItemListener getComboOperationTypeListner() {
		String[] encryptType = { " ", "Criptare", "Decriptare" };
		return new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent ie) {
				comboEncryptionType.setModel(model);
				model.removeAllElements();

				if (ie != null && ie.getSource() != null && comboOperationType.getSelectedIndex() == 1
						|| comboOperationType.getSelectedIndex() == 2 || comboOperationType.getSelectedIndex() == 3) {

					if (ie.getStateChange() == ItemEvent.SELECTED) {
						for (String s : encryptType) {
							model.addElement(s);
							comboEncryptionType.setVisible(true);
							comboEncryptionType.setSelectedIndex(0);
						}
					}
				} else {
					comboEncryptionType.setVisible(false);
				}
			}
		};
	}

	private ItemListener getComboEncryptionTypeListner() {
		String[] type = { " ", "Text", "Fisier", "Imagine" };
		return new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent ie) {
				// TODO Auto-generated method stub
				comboResourceType.setModel(model3);
				model3.removeAllElements();
				if (comboEncryptionType.isShowing() && ie != null && ie.getSource() != null
						&& comboEncryptionType.getSelectedIndex() == 1 || comboEncryptionType.getSelectedIndex() == 2) {
					if (ie.getStateChange() == ItemEvent.SELECTED) {
						for (String s : type) {
							model3.addElement(s);
							comboResourceType.setVisible(true);
						}
					}
				} else {
					comboResourceType.setVisible(false);
				}
			}
		};
	}

	private ActionListener getComboResourceTypeListner() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// encrypt asymmetric text
				if (comboResourceType.isShowing() && e.getSource() != null && comboOperationType.getSelectedIndex() == 1
						&& comboEncryptionType.getSelectedIndex() == 1 && comboResourceType.getSelectedIndex() == 1) {					
					changeStateCryptoElements(true, true, true, true);
					clearCryptoTextFields();

					// add label for text inserting
					jLabelHelper.setText("Introduceti textul: ");

					// add TF for inserting plainText
					JTFHelper.setMaximumSize(new Dimension(100, 28));
					JTFHelper.setPreferredSize(new Dimension(100, 28));

					// add label for setting the algorithm
					jLabelAlg.setText("Introduceti algoritmul de criptare: ");

					// add TF for algorithm insertion
					jTFAlgorithm.setMaximumSize(new Dimension(100, 28));
					jTFAlgorithm.setPreferredSize(new Dimension(100, 28));

					// add label for keylength
					jLabelKeyLength.setText("Introduceti lungimea cheii: ");

					// add TF for key length
					jTFAlgorithm.setMaximumSize(new Dimension(150, 80));
					jTFAlgorithm.setPreferredSize(new Dimension(150, 80));

					// perform asymmetric encryption..
					performCrypto.setText("Criptare");


					removeAllActionListenersFromButton(performCrypto);
					performCrypto.addActionListener(getPerformAssymetricListener());
					// encrypt symmetric text
				} else if (comboResourceType.isShowing() && e.getSource() != null && comboOperationType.getSelectedIndex() == 2
						&& comboEncryptionType.getSelectedIndex() == 1 && comboResourceType.getSelectedIndex() == 1) {
					changeStateCryptoElements(true, false, true, true);
					clearCryptoTextFields();
					
					// add label for text inserting
					jLabelHelper.setText("Introduceti textul: ");

					// add TF for inserting plainText
					JTFHelper.setMaximumSize(new Dimension(100, 28));
					JTFHelper.setPreferredSize(new Dimension(100, 28));

					// add label for setting the algorithm
					jLabelAlg.setText("Introduceti algoritmul de criptare: ");

					// add TF for algorithm insertion
					jTFAlgorithm.setMaximumSize(new Dimension(100, 28));
					jTFAlgorithm.setPreferredSize(new Dimension(100, 28));

					// perform symmetric encryption..
					performCrypto.setText("Criptare");
					
	
					removeAllActionListenersFromButton(performCrypto);
					performCrypto.addActionListener(getPerformSymmetricListener());
				}  
				//decrypt asymmetric text
				else if(comboResourceType.isShowing() && e.getSource() != null && comboOperationType.getSelectedIndex() == 1) {
					if(comboEncryptionType.getSelectedIndex() == 2 && comboResourceType.getSelectedIndex() == 1) {
						changeStateCryptoElements(true, true, true, false);
						clearCryptoTextFields();
						
						// add label for text inserting
						jLabelHelper.setText("Introduceti textul: ");

						// add TF for inserting plainText
						JTFHelper.setMaximumSize(new Dimension(100, 28));
						JTFHelper.setPreferredSize(new Dimension(100, 28));

						// add label for setting the algorithm
						jLabelAlg.setText("Introduceti algoritmul de decriptare: ");

						// add TF for algorithm insertion
						jTFAlgorithm.setMaximumSize(new Dimension(100, 28));
						jTFAlgorithm.setPreferredSize(new Dimension(100, 28));

						// add label for keylength
						jLabelKeyLength.setText("Introduceti lungimea cheii: ");

						// add TF for key length
						jTFAlgorithm.setMaximumSize(new Dimension(150, 80));
						jTFAlgorithm.setPreferredSize(new Dimension(150, 80));

						// perform asymmetric decryption..
						performCrypto.setText("Decriptare");
						removeAllActionListenersFromButton(performCrypto);
						performCrypto.addActionListener(getPerformAsymmetricDecryptionListener());
						
					}
				
				}   
				//decrypt symmetric text
				
				else if (comboResourceType.isShowing() && e.getSource() != null && comboOperationType.getSelectedIndex() == 2) {
					if(comboEncryptionType.getSelectedIndex() == 2 && comboResourceType.getSelectedIndex() == 1) {
						
						changeStateCryptoElements(true, false, true, false);
						clearCryptoTextFields();
						
						// add label for text inserting
						jLabelHelper.setText("Introduceti textul: ");

						// add TF for inserting plainText
						JTFHelper.setMaximumSize(new Dimension(100, 28));
						JTFHelper.setPreferredSize(new Dimension(100, 28));

						// add label for setting the algorithm
						jLabelAlg.setText("Introduceti algoritmul de decriptare: ");

						// add TF for algorithm insertion
						jTFAlgorithm.setMaximumSize(new Dimension(100, 28));
						jTFAlgorithm.setPreferredSize(new Dimension(100, 28));

						

						// perform symmetric decryption..
						performCrypto.setText("Decriptare");
						removeAllActionListenersFromButton(performCrypto);
						

					}
					}
				else {
					clearCryptoTextFields();
					changeStateCryptoElements(false, false, false, false);
				}

			}
		};

	}

	private ActionListener getPerformAssymetricListener() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (JTFKeyLength.getText() != null && JTFKeyLength.getText() != " "
						&& JTFKeyLength.getText().length() != 0 && JTFHelper.getText() != null
						&& JTFHelper.getText() != " " && JTFHelper.getText().length() != 0
						&& jTFAlgorithm.getText() != null && jTFAlgorithm.getText() != " "
						&& jTFAlgorithm.getText().length() != 0) {

					AsymmetricCrypto ac = new AsymmetricCrypto();

					AsymmetricKey ak = new AsymmetricKey(Integer.parseInt(JTFKeyLength.getText()), jTFAlgorithm.getText());

					ak.createKeys();

					ak.writePrivateKey("KeyPair/privateKey", ak.getPrivateKey().getEncoded());
					ak.writePublicKey("KeyPair/publicKey", ak.getPublicKey().getEncoded());

					try {
						ac.cipherAlgorithm(jTFAlgorithm.getText());
						privateKey = ac.getPrivateKey("KeyPair/privateKey", jTFAlgorithm.getText());
						publicKey = ac.getPublicKey("KeyPair/publicKey", jTFAlgorithm.getText());
						encrypted = ac.encryptText(JTFHelper.getText(), privateKey);
						File file = new File("KeyPair/encrypted_text.txt");
						FileWriter fw = new FileWriter(file);
						BufferedWriter bw = new BufferedWriter (fw);
						if (file.exists()) {
							file.delete();
							file.createNewFile();
							bw.write(encrypted);
							bw.close();
						}
						
					} catch (IOException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException
							| InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}

					System.out.println("Criptarea a fost facuta cu succes !");
					JOptionPane.showMessageDialog(cf, encrypted, "Rezultat criptare", JOptionPane.INFORMATION_MESSAGE);
					
					createSendEmailButton("carolinaangelica26@gmail.com", encrypted);
				} else {
					String message = "Nu ati completat unul dintre campurile necesare criptarii !";
					JOptionPane.showMessageDialog(cf, message, "!", JOptionPane.ERROR_MESSAGE);
				}
			}
		};
	}
// crapa la decriptare. to be investigated
		private ActionListener getPerformAsymmetricDecryptionListener () {
			return new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					if (JTFKeyLength.getText() != null && JTFKeyLength.getText() != " "
							&& JTFKeyLength.getText().length() != 0 && JTFHelper.getText() != null
							&& JTFHelper.getText() != " " && JTFHelper.getText().length() != 0
							&& jTFAlgorithm.getText() != null && jTFAlgorithm.getText() != " "
							&& jTFAlgorithm.getText().length() != 0) {

						AsymmetricCrypto ac = new AsymmetricCrypto();
						//AsymmetricKey ak2 = new AsymmetricKey();
						//AsymmetricKey ak = new AsymmetricKey(Integer.parseInt(JTFKeyLength.getText()), jTFAlgorithm.getText());

						//ak.createKeys();

						//ak.writePrivateKey("KeyPair/privateKey", ak.getPrivateKey().getEncoded());
						//ak.writePublicKey("KeyPair/publicKey", ak.getPublicKey().getEncoded());

						try {
							ac.cipherAlgorithm(jTFAlgorithm.getText());
							privateKey = ac.getPrivateKey("KeyPair/privateKey", jTFAlgorithm.getText());
							publicKey = ac.getPublicKey("KeyPair/publicKey", jTFAlgorithm.getText());
							//byte [] fromFile = ac.getFileAsBytes(new File ("KeyPair/encrypted_text.txt"));
							decrypted = ac.decryptText(JTFHelper.getText(), publicKey);
							System.out.println("Textul decriptat este: " + decrypted);

						} catch (IOException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException
								 | InvalidKeySpecException | NoSuchAlgorithmException | NoSuchPaddingException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}

						System.out.println("Decriptarea a fost facuta cu succes !");
						JOptionPane.showMessageDialog(cf, decrypted, "Rezultat decriptare", JOptionPane.INFORMATION_MESSAGE);
						
						createSendEmailButton("carolinaangelica26@gmail.com", decrypted);
					} else {
						String message = "Nu ati completat unul dintre campurile necesare decriptarii !";
						JOptionPane.showMessageDialog(cf, message, "!", JOptionPane.ERROR_MESSAGE);
					}

				}
			};
			
		}
	private ActionListener getPerformSymmetricListener() {
		return new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (JTFHelper.getText() != null && JTFHelper.getText() != " " && JTFHelper.getText().length() != 0
						&& jTFAlgorithm.getText() != null && jTFAlgorithm.getText() != " "
						&& jTFAlgorithm.getText().length() != 0) {

					try {
						SymmetricCrypto sc = new SymmetricCrypto(jTFAlgorithm.getText());
						SymmetricKey sk = new SymmetricKey();
						sc.cipherAlgorithm(sc.getAlgorithm());
						byte[] textToEncrypt = JTFHelper.getText().getBytes("UTF8");
						encryptedBytes = SymmetricCrypto.encryptText(textToEncrypt, sk.key(jTFAlgorithm.getText()));
						encrypted = new String(encryptedBytes, "UTF8");
						System.out.println("Encrypted text after encryption: " + encrypted);
						JOptionPane.showMessageDialog(cf, encrypted, "Rezultat criptare",
								JOptionPane.INFORMATION_MESSAGE);

						createSendEmailButton("carolinaangelica26@gmail.com", encrypted);
					} catch (NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException
							| InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
						Logger.getLogger(CryptoFrame.class.getName()).log(Level.SEVERE, null, ex);
					}
				} else {
					String message = "Nu ati completat unul dintre campurile necesare criptarii !";
					JOptionPane.showMessageDialog(cf, message, "!", JOptionPane.ERROR_MESSAGE);
				}
			}
		};
	}

	private void createSendEmailButton(String receiverAddress, String result) {
		removeAllActionListenersFromButton(sendEmail);
		sendEmail.setVisible(true);
		sendEmail.setText("Trimite Email");

		sendEmail.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendMailWithTheResult(receiverAddress, result);
			}
		});
	}

	private void changeStateCryptoElements(boolean state, boolean assymetric, boolean isApplicable, boolean noKey) {
		jLabelHelper.setVisible(state && isApplicable);
		jLabelAlg.setVisible(state);
		jLabelKeyLength.setVisible(state && assymetric && noKey);
		JTFHelper.setVisible(state && isApplicable);
		jTFAlgorithm.setVisible(state);
		JTFKeyLength.setVisible(state && assymetric && noKey);
		performCrypto.setVisible(state);
		sendEmail.setVisible(false);
	}
	
	private void clearCryptoTextFields () {
		JTFHelper.setText(null);
		JTFKeyLength.setText(null);
		jTFAlgorithm.setText(null);
		
	}

	private boolean sendMailWithTheResult(String receiverAddress, String result) {
		EmailService ems = new EmailService();
		ems.setSubject("Test Subject..");
		ems.setBody("Test Body...");
		Properties props = System.getProperties();

		props.put("fromEmail", "utmblog2018@gmail.com");
		props.put("password", "Utmblog123");
		props.put("toEmail", receiverAddress);
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.SocketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); // SSL Factory
																						// Class
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
		File file = new File("./encrypted.txt");
		BufferedWriter bw;

		if (file.exists()) {
			file.delete();
		}
		try {
			file.createNewFile();
			FileWriter fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			bw.write(result);
			bw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}

		EmailService.sendAttachmentEmail(session, fromEmail, toEmail, ems.getSubject(), ems.getBody(), file);

		return true;
	}
	
	private void removeAllActionListenersFromButton(JButton button) {
		for( ActionListener al : button.getActionListeners() ) {
			button.removeActionListener( al );
	    }
	}
}
