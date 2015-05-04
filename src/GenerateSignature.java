import java.security.*;
import java.io.*;

public class GenerateSignature {

	public static void main(String[] args) {
		GenerateSignature gs = new GenerateSignature();
		gs.signTheFile("data.txt");
	}

	public void signTheFile(String aFilename) {

		try {

			// Generate the keys for the DSA algorithm used for signing
			KeyPairGenerator keyGen = KeyPairGenerator
					.getInstance("DSA", "SUN");

			// Gives an instance of SecureRandom that uses the SHA1PRNG
			// algorithm that is passed to keyGen
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
			keyGen.initialize(1024, random);

			// Generate the key pair and to store the keys in PrivateKey and
			// PublicKey objects
			KeyPair pair = keyGen.generateKeyPair();
			PrivateKey priv = pair.getPrivate();
			PublicKey pub = pair.getPublic();

			// gets a Signature object for generating or verifying signatures
			// using the DSA algorithm
			Signature dsa = Signature.getInstance("SHA1withDSA", "SUN");
			dsa.initSign(priv); // requires a private key

			// Supply the data to the Signature object
			FileInputStream fis = new FileInputStream(aFilename);
			BufferedInputStream bufin = new BufferedInputStream(fis);
			byte[] buffer = new byte[1024];
			int len;
			while ((len = bufin.read(buffer)) >= 0) {
				dsa.update(buffer, 0, len);
			}
			;
			bufin.close();

			// Generate Digital Signature and save the signature in a file
			byte[] realSig = dsa.sign();
			FileOutputStream sigfos = new FileOutputStream("dataSig");
			sigfos.write(realSig);
			sigfos.close();

			/* save the public key in a file */
			byte[] key = pub.getEncoded();
			FileOutputStream keyfos = new FileOutputStream("dataPK");
			keyfos.write(key);
			keyfos.close();
		} catch (Exception e) {

		}
	}
}
