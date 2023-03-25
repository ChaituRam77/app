package com.javawhizz.App;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.util.Objects;

@SpringBootApplication
public class AppApplication {

	public static void main(String[] args) throws IOException {

		ClassLoader classLoader = AppApplication.class.getClassLoader();
		File file =  new File(Objects.requireNonNull(classLoader.getResource("serviceAccountKey.json")).getPath());
		FileInputStream serviceAccount = new FileInputStream(file.getAbsoluteFile());


		FirebaseOptions options = new FirebaseOptions.Builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.setDatabaseUrl("https://auction-c9885-default-rtdb.europe-west1.firebasedatabase.app")
				.build();

		FirebaseApp.initializeApp(options);

		SpringApplication.run(AppApplication.class, args);
	}

}
