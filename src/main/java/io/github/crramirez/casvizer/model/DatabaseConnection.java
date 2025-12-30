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
package io.github.crramirez.casvizer.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Represents a database connection with its metadata.
 */
public class DatabaseConnection {
    private final ConnectionProfile profile;
    private Connection connection;
    private boolean connected;

    public DatabaseConnection(ConnectionProfile profile) {
        this.profile = profile;
        this.connected = false;
    }

    public void connect() throws SQLException {
        String url = profile.getJdbcUrl();
        String username = profile.getUsername();
        String password = profile.getPassword();

        if (username != null && !username.isEmpty()) {
            connection = DriverManager.getConnection(url, username, password);
        } else {
            connection = DriverManager.getConnection(url);
        }
        connected = true;
    }

    public void disconnect() throws SQLException {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } finally {
            connected = false;
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public ConnectionProfile getProfile() {
        return profile;
    }

    public boolean isConnected() {
        if (!connected || connection == null) {
            return false;
        }
        try {
            return !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public String getDatabaseType() {
        return profile.getDatabaseType();
    }
}
