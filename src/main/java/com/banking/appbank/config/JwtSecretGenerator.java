package com.banking.appbank.config;

// import javax.crypto.KeyGenerator;
// import javax.crypto.SecretKey;
// import java.nio.charset.StandardCharsets;
// import java.security.NoSuchAlgorithmException;
// import java.util.Base64;
// import java.util.Scanner;

public class JwtSecretGenerator {

    // public static void main(String[] args) {
    //     Scanner scanner = new Scanner(System.in);

    //     System.out.print("Ingresa una palabra para generar la clave secreta: ");
    //     String userInput = scanner.nextLine();

    //     try {
    //         // Generar una clave secreta de 256 bits (32 bytes) utilizando AES
    //         SecretKey secretKey = generateSecretKeyFromSeed("AES", 256, userInput);

    //         // Codificar la clave secreta en formato Base64
    //         String jwtSecret = Base64.getEncoder().encodeToString(secretKey.getEncoded());

    //         System.out.println("app.jwt-secret=" + jwtSecret);
    //     } catch (NoSuchAlgorithmException e) {
    //         e.printStackTrace();
    //     }
    // }

    // private static SecretKey generateSecretKeyFromSeed(String algorithm, int keySize, String seed) throws NoSuchAlgorithmException {
    //     KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
    //     keyGenerator.init(keySize, new java.security.SecureRandom(seed.getBytes(StandardCharsets.UTF_8)));
    //     return keyGenerator.generateKey();
    // }
}



