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

/**
 * Represents a connection profile with database connection details.
 */
public class ConnectionProfile {
    private String name;
    private String databaseType; // postgres, mysql, sqlite
    private String host;
    private int port;
    private String database;
    private String username;
    private String password;
    private String jdbcUrl;

    public ConnectionProfile() {
    }

    public ConnectionProfile(String name, String databaseType) {
        this.name = name;
        this.databaseType = databaseType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getJdbcUrl() {
        if (jdbcUrl != null && !jdbcUrl.isEmpty()) {
            return jdbcUrl;
        }
        // Build URL from components
        return buildJdbcUrl();
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    private String buildJdbcUrl() {
        if (databaseType == null || databaseType.isEmpty()) {
            throw new IllegalArgumentException("Database type must be specified");
        }
        
        switch (databaseType.toLowerCase()) {
            case "postgres":
            case "postgresql":
                if (host == null || host.isEmpty()) {
                    throw new IllegalArgumentException("Host must be specified for PostgreSQL");
                }
                if (database == null || database.isEmpty()) {
                    throw new IllegalArgumentException("Database must be specified for PostgreSQL");
                }
                if (port <= 0) {
                    throw new IllegalArgumentException("Port must be specified for PostgreSQL");
                }
                return String.format("jdbc:postgresql://%s:%d/%s", host, port, database);
            case "mysql":
                if (host == null || host.isEmpty()) {
                    throw new IllegalArgumentException("Host must be specified for MySQL");
                }
                if (database == null || database.isEmpty()) {
                    throw new IllegalArgumentException("Database must be specified for MySQL");
                }
                if (port <= 0) {
                    throw new IllegalArgumentException("Port must be specified for MySQL");
                }
                return String.format("jdbc:mysql://%s:%d/%s", host, port, database);
            case "sqlite":
                if (database == null || database.isEmpty()) {
                    throw new IllegalArgumentException("Database file path must be specified for SQLite");
                }
                return String.format("jdbc:sqlite:%s", database);
            default:
                throw new IllegalArgumentException("Unsupported database type: " + databaseType);
        }
    }
}
