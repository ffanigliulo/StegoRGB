import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class Key_Generator {

	/*
	 *Generate the secret key
	 *@return The boolean[] array representing the secret key
	*/
	protected static boolean[] generateSecretKey() {

		//Creating the key generator

		KeyGenerator gen = null;
		try {
		  gen = KeyGenerator.getInstance("AES");
		} catch (NoSuchAlgorithmException e1) {
		  System.out.println("Algoritmo non supportato");
		  System.exit(1);
		}

		//Generating the secret key

		gen.init(new SecureRandom());

		Key k = gen.generateKey();
		byte[] key = k.getEncoded();
		
		String stringKey = Base64.getEncoder().encodeToString(key);
		
		JTextArea textarea= new JTextArea(stringKey);
		textarea.setEditable(false);
		JOptionPane.showMessageDialog(null, textarea, "This is your SECRET KEY, keep it safe!", JOptionPane.WARNING_MESSAGE);
		
		Stego_Model	model = new Stego_Model();
		boolean[] boolSecretKey = model.bytesToBooleans(key);
		return boolSecretKey;
		}
}
