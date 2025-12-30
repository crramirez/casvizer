/*
 * Casvizer - Database visualization TUI tool
 *
 * Copyright 2025 Carlos Rafael Ramirez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.crramirez.casvizer.persistence;

import org.jasypt.util.text.BasicTextEncryptor;

/**
 * Handles encryption and decryption of sensitive data like passwords.
 * 
 * Security Note: Uses a default encryption key if CASVIZER_MASTER_PASSWORD 
 * environment variable is not set. For production use, always set a strong 
 * master password via the environment variable.
 */
public class SecretsStore {
    private final BasicTextEncryptor encryptor;
    private static final String DEFAULT_PASSWORD = "casvizer-secret-key-change-me";

    /**
     * Constructor. Initializes the encryptor with either the environment-provided
     * password or a default password with a warning.
     */
    public SecretsStore() {
        this.encryptor = new BasicTextEncryptor();
        // In production, this should come from environment variable or secure key store
        String password = System.getenv("CASVIZER_MASTER_PASSWORD");
        if (password == null || password.isEmpty()) {
            password = DEFAULT_PASSWORD;
            // Warn user about insecure default
            System.err.println("WARNING: Using default encryption password. " +
                "Set CASVIZER_MASTER_PASSWORD environment variable for better security.");
        }
        encryptor.setPassword(password);
    }

    /**
     * Encrypts plaintext string.
     * 
     * @param plainText The text to encrypt
     * @return Encrypted text
     */
    public String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        return encryptor.encrypt(plainText);
    }

    /**
     * Decrypts encrypted text.
     * 
     * @param encryptedText The text to decrypt
     * @return Decrypted text
     * @throws IllegalStateException if decryption fails
     */
    public String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return encryptedText;
        }
        try {
            return encryptor.decrypt(encryptedText);
        } catch (Exception e) {
            System.err.println("ERROR: Failed to decrypt value in SecretsStore. " +
                "Check that CASVIZER_MASTER_PASSWORD is correctly configured and that " +
                "stored secrets have not been corrupted.");
            e.printStackTrace(System.err);
            throw new IllegalStateException(
                "Failed to decrypt secret. Verify CASVIZER_MASTER_PASSWORD and stored data integrity.",
                e
            );
        }
    }
}
