import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class RSA {
	
	public static final int AES_Key_Size = 128;
	public static final String PRIVATE_KEY_FILE = "private.key";
	public static final String PUBLIC_KEY_FILE = "public.key";

	Cipher pkCipher;
	byte[] aesKey;
	SecretKeySpec aeskeySpec;
	Key pubKey;
	Key privKey;

	Cipher aesCipher ;
	
	public static void main(String[] args) {
		
		RSA object = new RSA();
		object.makeKey();
		object.encrypt("E://CloudSyncFolder//UserFile.txt",
				"E://CloudSyncFolder//UserFileE.txt");
		//object.saveKey(new File("E://CloudSyncFolder//AESEncrypt.txt"),"E://CloudSyncFolder//public.txt");
		//object.loadKey(new File("E://CloudSyncFolder//AESEncrypt.txt"),"E://CloudSyncFolder//private.txt");
		object.decrypt("E://CloudSyncFolder//UserFileE.txt",
				"E://CloudSyncFolder//UserFile2.txt");
	}
	  
  RSA() {
	  try {
		pkCipher = Cipher.getInstance("RSA");
		aesCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
	} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
		e.printStackTrace();
	}
  }
  
  public void process() throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, IOException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
	  		
	    Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

	    byte[] input = "aa".getBytes();
	    Cipher cipher = Cipher.getInstance("RSA/None/NoPadding", "BC");
	    SecureRandom random = new SecureRandom();
	    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");

	    generator.initialize(1024, random);
	    KeyPair pair = generator.generateKeyPair();
	    pubKey = pair.getPublic();
	    privKey = pair.getPrivate();
	    
	    
	    DataOutputStream out=new DataOutputStream(new FileOutputStream("pri.txt"));
	    byte[] data=privKey.getEncoded();
	    out.write(data);
	    out.close();

	    DataOutputStream out1=new DataOutputStream(new FileOutputStream("pub.txt"));
	    byte[] data1=pubKey.getEncoded();
	    out1.write(data1);
	    out1.close();
	    
	    
	    DataInputStream in=new DataInputStream(new FileInputStream("pri.txt"));
	    byte[] data2=new byte[in.available()];
	    in.readFully(data2);

	    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(data2);
	    KeyFactory kf = KeyFactory.getInstance("RSA");
	    privKey = kf.generatePrivate(keySpec);
	    
	    DataInputStream in1=new DataInputStream(new FileInputStream("pub.txt"));
	    byte[] data3=new byte[in1.available()];
	    in1.readFully(data3);

	    X509EncodedKeySpec keySpec1 = new X509EncodedKeySpec(data3);
	    KeyFactory kf1 = KeyFactory.getInstance("RSA");
	    pubKey = kf1.generatePublic(keySpec1);

	    cipher.init(Cipher.ENCRYPT_MODE, pubKey, random);

	    byte[] cipherText = cipher.doFinal(input);
	    System.out.println("cipher: " + new String(cipherText));
	    cipher.init(Cipher.DECRYPT_MODE, privKey);
	    byte[] plainText = cipher.doFinal(cipherText);
	    System.out.println("plain : " + new String(plainText));
	    
  }

  
  /**
	 * Encrypts and then copies the contents of a given file.
	 */
	public void encrypt(String FileIn, String FileOut) {
	
		try {	
		File in = new File(FileIn);
		File out = new File(FileOut);
		aesCipher.init(Cipher.ENCRYPT_MODE, aeskeySpec);	 

		FileInputStream is = new FileInputStream(in);
		CipherOutputStream os = new CipherOutputStream(
				new FileOutputStream(out), aesCipher);

		copy(is, os);

		os.close();
		}
		catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Decrypts and then copies the contents of a given file.
	 */
	public void decrypt(String FileIn, String FileOut) {
		
		try
		{
		File in = new File(FileIn);
		File out = new File(FileOut);

		aesCipher.init(Cipher.DECRYPT_MODE, aeskeySpec);

		CipherInputStream is = new CipherInputStream(new FileInputStream(in),
				aesCipher);
		FileOutputStream os = new FileOutputStream(out);

		copy(is, os);

		is.close();
		os.close();
		}catch(Exception e) {
			
		}
	}
	
  	public void makeKey() {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("DES");
			kgen.init(AES_Key_Size);
			SecretKey key = kgen.generateKey();
			aesKey = key.getEncoded();
			aeskeySpec = new SecretKeySpec(aesKey, "AES");
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		    byte[] input = "aa".getBytes();
		    Cipher cipher = Cipher.getInstance("RSA/None/NoPadding", "BC");
		    SecureRandom random = new SecureRandom();
		    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");

		    generator.initialize(1024, random);
		    KeyPair pair = generator.generateKeyPair();
		    pubKey = pair.getPublic();
		    privKey = pair.getPrivate();
		    
		    DataOutputStream out=new DataOutputStream(new FileOutputStream("E://CloudSyncFolder//private.txt"));
		    byte[] data=privKey.getEncoded();
		    out.write(data);
		    out.close();

		    DataOutputStream out1=new DataOutputStream(new FileOutputStream("E://CloudSyncFolder//public.txt"));
		    byte[] data1=pubKey.getEncoded();
		    out1.write(data1);
		    out1.close();
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
  	
  	public void saveKey(File out, String publicKeyFile) {
  		try
  		{
  		DataInputStream in1=new DataInputStream(new FileInputStream(publicKeyFile));
	    byte[] data3=new byte[in1.available()];
	    in1.readFully(data3);

	    X509EncodedKeySpec keySpec1 = new X509EncodedKeySpec(data3);
	    KeyFactory kf1 = KeyFactory.getInstance("RSA");
	    pubKey = kf1.generatePublic(keySpec1);
	    
			// write AES key
	 		pkCipher.init(Cipher.ENCRYPT_MODE, pubKey);
	 		CipherOutputStream os = new CipherOutputStream(
	 				new FileOutputStream(out), pkCipher);
	 		os.write(aesKey);
	 		os.close();
	 		
  		}catch(Exception e) { }
  		
  	}
  	
  	public void loadKey(File in, String privateKeyFile) {
  		
  		try 
  		{
  			DataInputStream in1=new DataInputStream(new FileInputStream(privateKeyFile));
  		    byte[] data2=new byte[in1.available()];
  		    in1.readFully(data2);

  		    PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(data2);
  		    KeyFactory kf = KeyFactory.getInstance("RSA");
  		    privKey = kf.generatePrivate(keySpec);
  		    
  		    // read AES key
  			pkCipher.init(Cipher.DECRYPT_MODE, privKey);
  			aesKey = new byte[AES_Key_Size / 8];
  			CipherInputStream is = new CipherInputStream(new FileInputStream(in),pkCipher);
  			is.read(aesKey);
  			aeskeySpec = new SecretKeySpec(aesKey, "AES");
  		    
  			
  		}catch(Exception e) {
  			
  		}
  	}
  	
  	/**
	 * Copies a stream.
	 */
	private void copy(InputStream is, OutputStream os) throws IOException {
		int i;
		byte[] b = new byte[1024];
		while ((i = is.read(b)) != -1) {
			os.write(b, 0, i);
		}
	}
  
}