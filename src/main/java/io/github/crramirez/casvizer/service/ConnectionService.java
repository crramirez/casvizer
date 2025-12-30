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
package io.github.crramirez.casvizer.service;

import io.github.crramirez.casvizer.model.ConnectionProfile;
import io.github.crramirez.casvizer.model.DatabaseConnection;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for managing database connections.
 */
public class ConnectionService {
    private final Map<String, DatabaseConnection> connections = new HashMap<>();
    private DatabaseConnection activeConnection;

    public DatabaseConnection connect(ConnectionProfile profile) throws SQLException {
        DatabaseConnection connection = new DatabaseConnection(profile);
        connection.connect();
        connections.put(profile.getName(), connection);
        activeConnection = connection;
        return connection;
    }

    public void disconnect(String profileName) throws SQLException {
        DatabaseConnection connection = connections.get(profileName);
        if (connection != null) {
            connection.disconnect();
            connections.remove(profileName);
            if (activeConnection == connection) {
                activeConnection = null;
            }
        }
    }

    public void disconnectAll() throws SQLException {
        for (DatabaseConnection connection : connections.values()) {
            connection.disconnect();
        }
        connections.clear();
        activeConnection = null;
    }

    public DatabaseConnection getActiveConnection() {
        return activeConnection;
    }

    public void setActiveConnection(String profileName) {
        DatabaseConnection connection = connections.get(profileName);
        if (connection != null && connection.isConnected()) {
            activeConnection = connection;
        }
    }

    public List<DatabaseConnection> getAllConnections() {
        return new ArrayList<>(connections.values());
    }

    public boolean hasActiveConnection() {
        return activeConnection != null && activeConnection.isConnected();
    }
}
