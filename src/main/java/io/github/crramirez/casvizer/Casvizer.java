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
package io.github.crramirez.casvizer;

import casciian.TApplication;
import casciian.TWindow;
import casciian.event.TMenuEvent;
import casciian.menu.TMenu;
import io.github.crramirez.casvizer.model.ConnectionProfile;
import io.github.crramirez.casvizer.persistence.ProfileStore;
import io.github.crramirez.casvizer.service.ConnectionService;
import io.github.crramirez.casvizer.service.MetadataService;
import io.github.crramirez.casvizer.service.QueryService;
import io.github.crramirez.casvizer.service.ExportService;
import io.github.crramirez.casvizer.ui.ConnectionDialog;
import io.github.crramirez.casvizer.ui.DatabaseBrowserWindow;
import io.github.crramirez.casvizer.ui.QueryEditorWindow;

import java.nio.file.Paths;
import java.util.List;

/**
 * Main Casvizer application - Database visualization TUI tool.
 */
public class Casvizer extends TApplication {
    
    /**
     * Application version number.
     */
    private static final String VERSION = "0.1.0";
    
    private final ConnectionService connectionService;
    private final MetadataService metadataService;
    private final QueryService queryService;
    private final ExportService exportService;
    private final ProfileStore profileStore;
    
    private static final int MENU_NEW_CONNECTION = 2001;
    private static final int MENU_DISCONNECT = 2002;
    private static final int MENU_DATABASE_BROWSER = 2003;
    private static final int MENU_QUERY_EDITOR = 2004;
    private static final int MENU_ABOUT = 2005;

    /**
     * Constructor.
     *
     * @throws Exception if any error occurs
     */
    public Casvizer() throws Exception {
        super(BackendType.XTERM);
        
        // Initialize services
        this.connectionService = new ConnectionService();
        this.metadataService = new MetadataService();
        this.queryService = new QueryService();
        this.exportService = new ExportService();
        
        // Initialize profile store
        String userHome = System.getProperty("user.home");
        String profilesPath = Paths.get(userHome, ".casvizer", "profiles.json").toString();
        this.profileStore = new ProfileStore(profilesPath);
        
        // Setup menus
        setupMenus();
        
        // Show welcome window
        showWelcomeWindow();
    }
    
    @Override
    public void onExit() {
        // Cleanup: disconnect all database connections
        try {
            connectionService.disconnectAll();
        } catch (Exception e) {
            System.err.println("Error disconnecting database connections: " + e.getMessage());
        }
        super.onExit();
    }

    private void setupMenus() {
        TMenu connectionMenu = addMenu("&Connection");
        connectionMenu.addItem(MENU_NEW_CONNECTION, "&New Connection...");
        connectionMenu.addItem(MENU_DISCONNECT, "&Disconnect");
        connectionMenu.addSeparator();
        connectionMenu.addDefaultItem(TMenu.MID_EXIT);

        TMenu toolsMenu = addMenu("&Tools");
        toolsMenu.addItem(MENU_DATABASE_BROWSER, "&Database Browser");
        toolsMenu.addItem(MENU_QUERY_EDITOR, "&Query Editor");

        TMenu helpMenu = addMenu("&Help");
        helpMenu.addItem(MENU_ABOUT, "&About");
    }

    @Override
    protected boolean onMenu(TMenuEvent menu) {
        int id = menu.getId();
        
        switch (id) {
            case MENU_NEW_CONNECTION:
                showConnectionDialog();
                return true;
                
            case MENU_DISCONNECT:
                disconnectDatabase();
                return true;
                
            case MENU_DATABASE_BROWSER:
                showDatabaseBrowser();
                return true;
                
            case MENU_QUERY_EDITOR:
                showQueryEditor();
                return true;
                
            case MENU_ABOUT:
                showAboutDialog();
                return true;
                
            default:
                return super.onMenu(menu);
        }
    }

    private void showWelcomeWindow() {
        TWindow window = addWindow("Welcome to Casvizer", 0, 0, 60, 12,
                TWindow.CENTERED | TWindow.MODAL);

        int row = 1;
        window.addLabel("Casvizer - Database Visualization Tool", 2, row++, "ttext");
        window.addLabel("", 2, row++);
        window.addLabel("A TUI-based database client supporting:", 2, row++);
        window.addLabel("  - PostgreSQL", 2, row++);
        window.addLabel("  - MySQL", 2, row++);
        window.addLabel("  - SQLite", 2, row++);
        window.addLabel("", 2, row++);
        window.addLabel("Select 'Connection > New Connection' to start", 2, row++);
        
        row++;
        window.addButton("&OK", 2, row, window::close);
    }

    private void showConnectionDialog() {
        try {
            List<ConnectionProfile> profiles = profileStore.loadProfiles();
            new ConnectionDialog(this, profileStore, connectionService, profiles);
        } catch (Exception e) {
            showErrorDialog("Connection Error", "Failed to load profiles: " + e.getMessage());
        }
    }

    private void disconnectDatabase() {
        if (!connectionService.hasActiveConnection()) {
            showMessageDialog("Info", "No active connection to disconnect.");
            return;
        }
        
        try {
            connectionService.disconnectAll();
            showMessageDialog("Success", "Disconnected from database.");
        } catch (Exception e) {
            showErrorDialog("Disconnect Error", "Failed to disconnect: " + e.getMessage());
        }
    }

    private void showDatabaseBrowser() {
        if (!connectionService.hasActiveConnection()) {
            showMessageDialog("Info", "Please connect to a database first.");
            return;
        }
        
        try {
            new DatabaseBrowserWindow(this, connectionService, metadataService, queryService);
        } catch (Exception e) {
            showErrorDialog("Browser Error", "Failed to open database browser: " + e.getMessage());
        }
    }

    private void showQueryEditor() {
        if (!connectionService.hasActiveConnection()) {
            showMessageDialog("Info", "Please connect to a database first.");
            return;
        }
        
        try {
            new QueryEditorWindow(this, connectionService, queryService, exportService);
        } catch (Exception e) {
            showErrorDialog("Query Editor Error", "Failed to open query editor: " + e.getMessage());
        }
    }

    @Override
    protected void showAboutDialog() {
        TWindow window = addWindow("About Casvizer", 0, 0, 50, 10,
                TWindow.CENTERED | TWindow.MODAL);

        int row = 1;
        window.addLabel("Casvizer v" + VERSION, 2, row++, "ttext");
        window.addLabel("", 2, row++);
        window.addLabel("Database Visualization TUI Tool", 2, row++);
        window.addLabel("Built with Casciian", 2, row++);
        window.addLabel("", 2, row++);
        window.addLabel("Copyright 2025 Carlos Rafael Ramirez", 2, row++);
        
        row++;
        window.addButton("&OK", 2, row, window::close);
    }

    private void showMessageDialog(String title, String message) {
        messageBox(title, message);
    }

    private void showErrorDialog(String title, String message) {
        messageBox(title, "ERROR: " + message);
    }

    /**
     * Main entry point.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        try {
            Casvizer app = new Casvizer();
            (new Thread(app)).start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
