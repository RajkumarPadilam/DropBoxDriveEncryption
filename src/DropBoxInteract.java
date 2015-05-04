import com.dropbox.core.*;

import java.io.*;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.util.Locale;

public class DropBoxInteract {

	//  app key and secret from the Dropbox developers website.
	final static String APP_KEY = "k8b53cm5elac8sl";
	final static String APP_SECRET = "7bg8bq88bpq0d8v";
	static String accessToken = null;
	static String SourceFilename = "E://CloudSyncFolder//dataFile1.txt";
	
	public static void main(String[] args) {
		String filename = "data.txt";
		try {
			process(filename);
		} catch (IOException | DbxException e) {
			e.printStackTrace();
		}
	}

	public static void process(String filename) throws IOException,
			DbxException {

		DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);

		DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0",
				Locale.getDefault().toString());
		DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);

		if (accessToken == null) {
			// Have the user sign in and authorize your app.
			String authorizeUrl = webAuth.start();
			System.out.println("1. Go to: " + authorizeUrl);
			System.out
					.println("2. Click \"Allow\" (you might have to log in first)");
			System.out.println("3. Copy the authorization code.");
			String code = new BufferedReader(new InputStreamReader(System.in))
					.readLine().trim();

			DbxAuthFinish authFinish = webAuth.finish(code);
			accessToken = authFinish.accessToken;
		}

		DbxClient client = new DbxClient(config, accessToken);
		System.out.println("Linked account: "
				+ client.getAccountInfo().displayName);
		
		GenerateSignature gs = new GenerateSignature();
		gs.signTheFile("E://CloudSyncFolder//rajkumar.txt");

		/*RSA obj = null;
		obj = new RSA();
		obj.makeKey();
		obj.encrypt(SourceFilename,"E://CloudSyncFolder//dataFileE.txt");

		obj.saveKey(new File("E://CloudSyncFolder//AESEncrypt.txt"),
				"E://CloudSyncFolder//public.txt");

		GenerateSignature gs = new GenerateSignature();
		gs.signTheFile("E://CloudSyncFolder//dataFileE.txt");

		System.out.println(new VerifySignature().verifySignature("dataPK",
				"dataSig", "E://CloudSyncFolder//dataFileE.txt"));*/

		// Uploading onto dropbox
		
		 File inputFile = new File("E://CloudSyncFolder//rajkumar.txt");
		 FileInputStream inputStream = new FileInputStream(inputFile); try {
		 DbxEntry.File uploadedFile =
		 client.uploadFile("/rajkumar.txt", DbxWriteMode.add(),
		 inputFile.length(), inputStream); System.out.println("Uploaded: " +
		 uploadedFile.toString()); } finally { inputStream.close(); }
		 System.out.println("File has been uploaded onto dropbox");
		 
		 File inputFile1 = new File("dataSig");
		 FileInputStream inputStream1 = new FileInputStream(inputFile1); try {
		 DbxEntry.File uploadedFile =
		 client.uploadFile("/dataSig", DbxWriteMode.add(),
		 inputFile1.length(), inputStream1); System.out.println("Uploaded: " +
		 uploadedFile.toString()); } finally { inputStream1.close(); }
		 System.out.println("digital signature has been uploaded onto dropbox");

		// uploading file onto google drive
		//new GoogleDrive().uploadIntoDrive("E://CloudSyncFolder//dataFileE.txt");
		//System.out.println("File has been uploaded onto google drive");

		// downloading file
		/*FileOutputStream outputStream = new FileOutputStream(
				"E://CloudSyncFolder//rajkumar2.des");
		try {
			DbxEntry.File downloadedFile = null;
			do {
				downloadedFile = client.getFile("/rajkumar.des",
						null, outputStream);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} while (downloadedFile == null);
			
		} finally {
			outputStream.close();
		}*/

		//System.out.println("File has been downloaded from drop box");
/*		VerifySignature gs1 = new VerifySignature();

		if (gs1.verifySignature("dataPK", "dataSig",
				"E://CloudSyncFolder//dataEncryptDropbox.txt")) {
			// obj = new DSAEncryption();

			obj.loadKey(new File("E://CloudSyncFolder//AESEncrypt.txt"),
					"E://CloudSyncFolder//private.txt");
			obj.decrypt("E://CloudSyncFolder//dataEncryptDropbox.txt",
					"E://CloudSyncFolder//dataDecryptDropbox.txt");
			
			System.out.println("File has been decrypted");
		}*/
	}

}
