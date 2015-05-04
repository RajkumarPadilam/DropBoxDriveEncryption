import java.io.*;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

public class VerifySignature {

	public static void main(String[] args) {

		VerifySignature vs = new VerifySignature();
		vs.verifySignature("dataPK", "dataSig", "data.txt");
	}

	public boolean verifySignature(String encodedPublicKey, String signatureBytes, String inputData) {

		try {
			
			//Read the encoded public key
			FileInputStream keyfis = new FileInputStream(encodedPublicKey);
			byte[] encKey = new byte[keyfis.available()];  
			keyfis.read(encKey);
			keyfis.close();
			
			
			X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encKey);
			KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
			PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);
			
			//Read the signature bytes
			FileInputStream sigfis = new FileInputStream(signatureBytes);
			byte[] sigToVerify = new byte[sigfis.available()]; 
			sigfis.read(sigToVerify);
			sigfis.close();
			
			//Get a signature object to verify the signature using public key
			Signature sig = Signature.getInstance("SHA1withDSA", "SUN");
			sig.initVerify(pubKey);
			
			//Read data whose signature is to be verified and provide it to signature object
			FileInputStream datafis = new FileInputStream(inputData);
			BufferedInputStream bufin = new BufferedInputStream(datafis);

			byte[] buffer = new byte[1024];
			int len;
			while (bufin.available() != 0) {
			    len = bufin.read(buffer);
			    sig.update(buffer, 0, len);
			};

			bufin.close();
			
			//Verify the signature of the data
			boolean verifies = sig.verify(sigToVerify);
			return verifies;
			

		} catch (Exception e) {

		}
		
		return false;
	}
}
