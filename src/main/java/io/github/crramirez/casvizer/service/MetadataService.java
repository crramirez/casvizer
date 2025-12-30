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

import io.github.crramirez.casvizer.dialect.Dialect;
import io.github.crramirez.casvizer.dialect.DialectFactory;
import io.github.crramirez.casvizer.model.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for database metadata introspection.
 */
public class MetadataService {
    
    public List<String> listSchemas(DatabaseConnection dbConnection) throws SQLException {
        Dialect dialect = DialectFactory.getDialect(dbConnection.getDatabaseType());
        String query = dialect.getListSchemasQuery();
        
        List<String> schemas = new ArrayList<>();
        try (Statement stmt = dbConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                schemas.add(rs.getString(1));
            }
        }
        return schemas;
    }

    public List<String> listTables(DatabaseConnection dbConnection, String schema) throws SQLException {
        Dialect dialect = DialectFactory.getDialect(dbConnection.getDatabaseType());
        String query = dialect.getListTablesQuery(schema);
        
        List<String> tables = new ArrayList<>();
        try (Statement stmt = dbConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                tables.add(rs.getString(1));
            }
        }
        return tables;
    }

    public List<ColumnInfo> listColumns(DatabaseConnection dbConnection, String schema, String table) throws SQLException {
        Dialect dialect = DialectFactory.getDialect(dbConnection.getDatabaseType());
        String query = dialect.getListColumnsQuery(schema, table);
        
        List<ColumnInfo> columns = new ArrayList<>();
        try (Statement stmt = dbConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            // Handle SQLite's different PRAGMA result structure
            if (dbConnection.getDatabaseType().equalsIgnoreCase("sqlite")) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    String type = rs.getString("type");
                    boolean nullable = rs.getInt("notnull") == 0;
                    String defaultValue = rs.getString("dflt_value");
                    columns.add(new ColumnInfo(name, type, nullable, defaultValue));
                }
            } else {
                while (rs.next()) {
                    String name = rs.getString("column_name");
                    String type = rs.getString("data_type");
                    boolean nullable = "YES".equals(rs.getString("is_nullable"));
                    String defaultValue = rs.getString("column_default");
                    columns.add(new ColumnInfo(name, type, nullable, defaultValue));
                }
            }
        }
        return columns;
    }

    public static class ColumnInfo {
        private final String name;
        private final String dataType;
        private final boolean nullable;
        private final String defaultValue;

        public ColumnInfo(String name, String dataType, boolean nullable, String defaultValue) {
            this.name = name;
            this.dataType = dataType;
            this.nullable = nullable;
            this.defaultValue = defaultValue;
        }

        public String getName() {
            return name;
        }

        public String getDataType() {
            return dataType;
        }

        public boolean isNullable() {
            return nullable;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        @Override
        public String toString() {
            return String.format("%s (%s)%s", name, dataType, nullable ? " NULL" : " NOT NULL");
        }
    }
}
