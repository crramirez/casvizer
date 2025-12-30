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
package io.github.crramirez.casvizer.ui;

import casciian.TApplication;
import casciian.TWidget;
import casciian.TWindow;
import casciian.bits.CellAttributes;
import casciian.bits.GraphicsChars;
import casciian.event.TKeypressEvent;
import io.github.crramirez.casvizer.model.ConnectionProfile;
import io.github.crramirez.casvizer.persistence.ProfileStore;
import io.github.crramirez.casvizer.service.ConnectionService;

import java.util.List;

/**
 * Dialog for creating/selecting database connections.
 */
public class ConnectionDialog extends TWindow {
    private final ProfileStore profileStore;
    private final ConnectionService connectionService;
    private final List<ConnectionProfile> profiles;

    public ConnectionDialog(TApplication application, ProfileStore profileStore,
                           ConnectionService connectionService, List<ConnectionProfile> profiles) {
        super(application, "Database Connection", 0, 0, 70, 20, TWindow.CENTERED | TWindow.MODAL);

        this.profileStore = profileStore;
        this.connectionService = connectionService;
        this.profiles = profiles;

        setupUI();
    }

    private void setupUI() {
        int row = 1;
        
        addLabel("Connection Profile:", 2, row++);
        addLabel("", 2, row++);
        
        addLabel("Name:", 2, row);
        addField(15, row++, 30, false, "MyDatabase");
        
        addLabel("Database Type:", 2, row);
        addField(15, row++, 30, false, "postgresql");
        addLabel("  (postgresql, mysql, or sqlite)", 2, row++);
        
        addLabel("Host:", 2, row);
        addField(15, row++, 30, false, "localhost");
        
        addLabel("Port:", 2, row);
        addField(15, row++, 10, false, "5432");
        
        addLabel("Database:", 2, row);
        addField(15, row++, 30, false, "postgres");
        
        addLabel("Username:", 2, row);
        addField(15, row++, 30, false, "");
        
        addLabel("Password:", 2, row);
        addField(15, row++, 30, true, "");
        
        row++;
        addButton("&Connect", 2, row, this::handleConnect);
        addButton("&Save Profile", 15, row, this::handleSaveProfile);
        addButton("&Cancel", 32, row, this::close);
    }

    private void handleConnect() {
        // TODO: Read values from form fields instead of using hardcoded values
        // This requires proper field references and TUI field access methods
        try {
            // Create connection profile from form data
            ConnectionProfile profile = new ConnectionProfile("temp", "postgresql");
            profile.setHost("localhost");
            profile.setPort(5432);
            profile.setDatabase("postgres");
            profile.setUsername("");
            profile.setPassword("");
            
            // Connect
            connectionService.connect(profile);
            
            // Show success message
            getApplication().messageBox("Success", "Connected to database successfully!");
            close();
        } catch (Exception e) {
            getApplication().messageBox("Error", "Connection failed: " + e.getMessage());
        }
    }

    private void handleSaveProfile() {
        // TODO: Read values from form fields instead of using hardcoded values
        // This requires storing references to TField widgets created in setupUI
        // and calling getText() on each to retrieve user input
        try {
            // Create connection profile from form data
            // NOTE: Currently using hardcoded values - form input is not read
            ConnectionProfile profile = new ConnectionProfile("MyProfile", "postgresql");
            profile.setHost("localhost");
            profile.setPort(5432);
            profile.setDatabase("postgres");
            
            profileStore.addProfile(profile);
            getApplication().messageBox("Success", "Profile saved successfully!");
        } catch (Exception e) {
            getApplication().messageBox("Error", "Failed to save profile: " + e.getMessage());
        }
    }
}
