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
import casciian.TWindow;
import io.github.crramirez.casvizer.model.DatabaseConnection;
import io.github.crramirez.casvizer.service.ConnectionService;
import io.github.crramirez.casvizer.service.MetadataService;
import io.github.crramirez.casvizer.service.QueryService;

import java.util.List;

/**
 * Window for browsing database structure (schemas, tables, columns).
 */
public class DatabaseBrowserWindow extends TWindow {
    private final ConnectionService connectionService;
    private final MetadataService metadataService;
    private final QueryService queryService;
    
    // UI layout constants
    private static final int BOTTOM_MARGIN = 4;

    public DatabaseBrowserWindow(TApplication application, ConnectionService connectionService,
                                MetadataService metadataService, QueryService queryService) {
        super(application, "Database Browser", 0, 0, 80, 24, RESIZABLE);

        this.connectionService = connectionService;
        this.metadataService = metadataService;
        this.queryService = queryService;

        setupUI();
        loadDatabaseStructure();
    }

    private void setupUI() {
        int row = 1;
        addLabel("Database Structure:", 2, row++);
        addLabel("", 2, row++);
        
        // Add tree or list view for database objects
        addLabel("Loading database structure...", 2, row++);
        
        row = getHeight() - 3;
        addButton("&Refresh", 2, row, this::loadDatabaseStructure);
        addButton("&Close", 15, row, this::close);
    }

    private void loadDatabaseStructure() {
        try {
            DatabaseConnection connection = connectionService.getActiveConnection();
            if (connection == null) {
                return;
            }
            
            List<String> schemas = metadataService.listSchemas(connection);
            
            // Display schemas in the window
            int row = 3;
            for (String schema : schemas) {
                if (row < getHeight() - BOTTOM_MARGIN) {
                    addLabel("Schema: " + schema, 2, row++);
                    
                    // List tables in schema
                    List<String> tables = metadataService.listTables(connection, schema);
                    for (String table : tables) {
                        if (row < getHeight() - BOTTOM_MARGIN) {
                            addLabel("  Table: " + table, 2, row++);
                        }
                    }
                }
            }
        } catch (Exception e) {
            getApplication().messageBox("Error", "Failed to load database structure: " + e.getMessage());
        }
    }
}
