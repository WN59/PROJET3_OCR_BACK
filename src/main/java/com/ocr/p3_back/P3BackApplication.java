package com.ocr.p3_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée de l'API ChaTop (Spring Boot).
 * Lance le serveur sur le port configuré (3001).
 */
@SpringBootApplication
public class P3BackApplication {

	/**
	 * Démarre l'application Spring.
	 *
	 * @param args arguments de la ligne de commande
	 */
	public static void main(String[] args) {
		SpringApplication.run(P3BackApplication.class, args);
	}

}
