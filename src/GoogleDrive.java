import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files.Get;
import com.google.api.services.drive.Drive.Files.List;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;

public class GoogleDrive {

	private static String CLIENT_ID = "600106913036-orra5t8evsdn9mmo0sqtcn3099nvrpb0.apps.googleusercontent.com";
	private static String CLIENT_SECRET = "5yzScMqxE8gQpUPWOawlLTbt";

	private static String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";

	public static void main(String[] args) throws IOException {
		uploadIntoDrive("QWEWQE");
	}

	public static void uploadIntoDrive(String filename) {

		try {
			HttpTransport httpTransport = new NetHttpTransport();
			JsonFactory jsonFactory = new JacksonFactory();

			GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
					httpTransport, jsonFactory, CLIENT_ID, CLIENT_SECRET,
					Arrays.asList(DriveScopes.DRIVE)).setAccessType("online")
					.setApprovalPrompt("auto").build();

			String url = flow.newAuthorizationUrl()
					.setRedirectUri(REDIRECT_URI).build();
			System.out
					.println("Please open the following URL in your browser then type the authorization code:");
			System.out.println("  " + url);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			String code = null;

			code = br.readLine();

			GoogleTokenResponse response = flow.newTokenRequest(code)
					.setRedirectUri(REDIRECT_URI).execute();
			GoogleCredential credential = new GoogleCredential()
					.setFromTokenResponse(response);

			// Create a new authorized API client
			Drive service = new Drive.Builder(httpTransport, jsonFactory,
					credential).build();

			// Insert a file
			File body = new File();
			body.setTitle("dataEncryptDropbox.txt");
			body.setDescription("A test document");
			body.setMimeType("text/plain");

			java.io.File fileContent = new java.io.File(filename);
			FileContent mediaContent = new FileContent("text/plain",
					fileContent);

			File file = service.files().insert(body, mediaContent).execute();

			System.out.println("File ID: " + file.getId());
			syncFiles("C:\\Users\\DELL\\Google Drive\\dataEncryptDropbox.txt",
					"C:\\Users\\DELL\\Dropbox\\Apps\\RajCloud\\dataEncryptDropbox.txt");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public static void syncFiles(String source, String destination) {

		java.io.File dropBoxFile = new java.io.File(source);
		java.io.File drivefile = new java.io.File(destination);

		// Syncing between google drive and drop box
		boolean syncComplete1 = false;
		boolean syncComplete2 = false;
		while (!syncComplete1 && !syncComplete2) {
			try {
				System.out
						.println("Waiting for files to sync between dropbox and google drive");
				Thread.sleep(4000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			java.io.File dir1 = new java.io.File(
					"C:\\Users\\DELL\\Google Drive");
			java.io.File files1[] = dir1.listFiles();
			
			if(!syncComplete2)
			for (java.io.File file : files1) {
				if (file.getName().contains("dataEncryptDropbox.txt")) {
					syncComplete2 = true;
					try {
						Files.copy(dropBoxFile.toPath(), drivefile.toPath());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			if (syncComplete2) {
				java.io.File dir = new java.io.File("C:\\Users\\DELL\\Dropbox\\Apps\\RajCloud");
				java.io.File files[] = dir.listFiles();
				for (java.io.File file : files) {
					if (file.getName().contains("dataEncryptDropbox.txt"))
						syncComplete1 = true;
				}
			}

		}
		
		System.out.println("File has been synced from drive to dropbox");

	}

}