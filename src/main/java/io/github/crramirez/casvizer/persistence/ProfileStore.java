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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.github.crramirez.casvizer.model.ConnectionProfile;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores connection profiles in JSON format.
 */
public class ProfileStore {
    private final File profilesFile;
    private final Gson gson;
    private final SecretsStore secretsStore;

    public ProfileStore(String profilesPath) {
        this.profilesFile = new File(profilesPath);
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.secretsStore = new SecretsStore();
        
        // Ensure parent directory exists
        File parentDir = profilesFile.getParentFile();
        if (parentDir != null) {
            if (parentDir.exists()) {
                if (!parentDir.isDirectory()) {
                    throw new IllegalStateException("Parent path is not a directory: " + parentDir.getAbsolutePath());
                }
            } else {
                boolean created = parentDir.mkdirs();
                if (!created && !parentDir.isDirectory()) {
                    throw new IllegalStateException("Failed to create profiles directory: " + parentDir.getAbsolutePath());
                }
            }
        }
    }

    public List<ConnectionProfile> loadProfiles() throws IOException {
        if (!profilesFile.exists()) {
            return new ArrayList<>();
        }

        try (FileReader reader = new FileReader(profilesFile)) {
            Type listType = new TypeToken<List<ConnectionProfile>>() {}.getType();
            List<ConnectionProfile> profiles = gson.fromJson(reader, listType);
            
            if (profiles == null) {
                return new ArrayList<>();
            }
            
            // Decrypt passwords
            for (ConnectionProfile profile : profiles) {
                if (profile.getPassword() != null && !profile.getPassword().isEmpty()) {
                    String decrypted = secretsStore.decrypt(profile.getPassword());
                    profile.setPassword(decrypted);
                }
            }
            
            return profiles;
        }
    }

    public void saveProfiles(List<ConnectionProfile> profiles) throws IOException {
        // Create a copy with encrypted passwords
        List<ConnectionProfile> encryptedProfiles = new ArrayList<>();
        for (ConnectionProfile profile : profiles) {
            ConnectionProfile encrypted = copyProfile(profile);
            if (encrypted.getPassword() != null && !encrypted.getPassword().isEmpty()) {
                encrypted.setPassword(secretsStore.encrypt(encrypted.getPassword()));
            }
            encryptedProfiles.add(encrypted);
        }

        try (FileWriter writer = new FileWriter(profilesFile)) {
            gson.toJson(encryptedProfiles, writer);
        }
    }

    /**
     * Adds a connection profile. If a profile with the same name already exists,
     * it will be silently replaced with the new profile.
     * 
     * @param profile The profile to add
     * @throws IOException if an error occurs while saving
     */
    public void addProfile(ConnectionProfile profile) throws IOException {
        List<ConnectionProfile> profiles = loadProfiles();
        
        // Remove existing profile with same name
        profiles.removeIf(p -> p.getName().equals(profile.getName()));
        
        profiles.add(profile);
        saveProfiles(profiles);
    }

    public void deleteProfile(String profileName) throws IOException {
        List<ConnectionProfile> profiles = loadProfiles();
        profiles.removeIf(p -> p.getName().equals(profileName));
        saveProfiles(profiles);
    }

    private ConnectionProfile copyProfile(ConnectionProfile source) {
        ConnectionProfile copy = new ConnectionProfile(source.getName(), source.getDatabaseType());
        copy.setHost(source.getHost());
        copy.setPort(source.getPort());
        copy.setDatabase(source.getDatabase());
        copy.setUsername(source.getUsername());
        copy.setPassword(source.getPassword());
        copy.setJdbcUrl(source.getJdbcUrl());
        return copy;
    }
}
