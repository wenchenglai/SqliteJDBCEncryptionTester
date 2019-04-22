package com.kla.bbp.jdbctester;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JdbctesterApplication implements CommandLineRunner {
	@Autowired
	private PerformanceTester performanceTester;

	@Autowired
	private EncryptionTester encryptionTester;

	@Autowired
	private DecryptionTester decryptionTester;

	public static void main(String[] args) {
		SpringApplication.run(JdbctesterApplication.class, args);
	}

	@Override
	public void run(String... args) {
		//performanceTester.ConvertFromEncryptedToClearText();
		encryptionTester.EncryptNewDb();
		decryptionTester.DecryptNewDb();
		decryptionTester.DecryptNewDbWithWrongPassword();
	}
}
