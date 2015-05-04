import java.io.*;
import java.security.*;
import java.security.spec.*;

import javax.crypto.*;
import javax.crypto.spec.*;

/**
 * Utility class for encrypting/decrypting files.
 * 
 * @author Michael Lones
 */
public class DSAEncryption {

	public static final int AES_Key_Size = 128;
	public static final String PRIVATE_KEY_FILE = "private.key";
	public static final String PUBLIC_KEY_FILE = "public.key";

	Cipher pkCipher;
	byte[] aesKey;
	SecretKeySpec aeskeySpec;

	Cipher aesCipher = Cipher.getInstance("AES");

	public static void main(String[] args) {

		try {
			DSAEncryption object = new DSAEncryption();
			object.makeKey();
			object.encrypt("E://CloudSyncFolder//UserFile.txt",
					"E://CloudSyncFolder//UserFileE.txt");
			object.saveKey(new File("E://CloudSyncFolder//AESEncrypt.txt"),"E://CloudSyncFolder//public.der");
			object.loadKey(new File("E://CloudSyncFolder//AESEncrypt.txt"),
					"E://CloudSyncFolder//private.der");
			object.decrypt("E://CloudSyncFolder//UserFileE.txt",
					"E://CloudSyncFolder//UserFileR.txt");

		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Constructor: creates ciphers
	 */
	public DSAEncryption() throws GeneralSecurityException {
		// create RSA public key cipher
		pkCipher = Cipher.getInstance("RSA");
		// create AES shared key cipher

	}

	/**
	 * Creates a new AES key
	 */
	public void makeKey() throws NoSuchAlgorithmException {
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(AES_Key_Size);
			SecretKey key = kgen.generateKey();
			aesKey = key.getEncoded();
			aeskeySpec = new SecretKeySpec(aesKey, "AES");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Decrypts an AES key from a file using an RSA private key
	 */
	public void loadKey(File in, String privateKeyFile)
			throws GeneralSecurityException, IOException {
		// read private key to be used to decrypt the AES key
		File f = new File(privateKeyFile);
		FileInputStream fis = new FileInputStream(f);
		DataInputStream dis = new DataInputStream(fis);
		byte[] keyBytes = new byte[(int) f.length()];
		dis.readFully(keyBytes);
		dis.close();

		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");

		// read AES key
		pkCipher.init(Cipher.DECRYPT_MODE, kf.generatePrivate(spec));
		aesKey = new byte[AES_Key_Size / 8];
		CipherInputStream is = new CipherInputStream(new FileInputStream(in),
				pkCipher);
		is.read(aesKey);
		aeskeySpec = new SecretKeySpec(aesKey, "AES");
	}

	/**
	 * Encrypts the AES key to a file using an RSA public key
	 */
	public void saveKey(File out, String publicKeyFile1) throws IOException,
			GeneralSecurityException {
		// read public key to be used to encrypt the AES key
		
		File publicKeyFile = new File(publicKeyFile1);
		File f = publicKeyFile;
		FileInputStream fis = new FileInputStream(f);
		DataInputStream dis = new DataInputStream(fis);
		byte[] keyBytes = new byte[(int) f.length()];
		dis.readFully(keyBytes);
		dis.close();

		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");

		// write AES key
		pkCipher.init(Cipher.ENCRYPT_MODE, kf.generatePublic(spec));
		CipherOutputStream os = new CipherOutputStream(
				new FileOutputStream(out), pkCipher);
		os.write(aesKey);
		os.close();
	}

	/**
	 * Encrypts and then copies the contents of a given file.
	 */
	public void encrypt(String FileIn, String FileOut) throws IOException,
			InvalidKeyException {
		File in = new File(FileIn);
		File out = new File(FileOut);

		aesCipher.init(Cipher.ENCRYPT_MODE, aeskeySpec);

		FileInputStream is = new FileInputStream(in);
		CipherOutputStream os = new CipherOutputStream(
				new FileOutputStream(out), aesCipher);

		copy(is, os);

		os.close();
	}

	/**
	 * Decrypts and then copies the contents of a given file.
	 */
	public void decrypt(String FileIn, String FileOut) throws IOException,
			InvalidKeyException {
		File in = new File(FileIn);
		File out = new File(FileOut);

		aesCipher.init(Cipher.DECRYPT_MODE, aeskeySpec);

		CipherInputStream is = new CipherInputStream(new FileInputStream(in),
				aesCipher);
		FileOutputStream os = new FileOutputStream(out);

		copy(is, os);

		is.close();
		os.close();
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