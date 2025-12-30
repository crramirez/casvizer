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
import io.github.crramirez.casvizer.model.QueryResult;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for executing database queries with pagination support.
 */
public class QueryService {
    
    /**
     * Maximum allowed offset value for paginated queries to prevent resource exhaustion.
     */
    private static final int MAX_QUERY_OFFSET = 1_000_000;
    
    public QueryResult executeQuery(DatabaseConnection dbConnection, String query) throws SQLException {
        return executeQuery(dbConnection, query, -1, 0);
    }

    public QueryResult executeQuery(DatabaseConnection dbConnection, String query, int limit, int offset) throws SQLException {
        long startTime = System.currentTimeMillis();
        
        // Add pagination if specified
        if (limit > 0) {
            // Validate offset to prevent resource exhaustion from extremely large values
            if (offset > MAX_QUERY_OFFSET) {
                throw new IllegalArgumentException("Offset too large: " + offset + ". Maximum allowed is " + MAX_QUERY_OFFSET);
            }
            Dialect dialect = DialectFactory.getDialect(dbConnection.getDatabaseType());
            query = dialect.addPagination(query, limit, offset);
        }
        
        List<String> columnNames = new ArrayList<>();
        List<List<Object>> rows = new ArrayList<>();
        
        try (Statement stmt = dbConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            // Get column names
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metaData.getColumnName(i));
            }
            
            // Get rows
            while (rs.next()) {
                List<Object> row = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getObject(i));
                }
                rows.add(row);
            }
        }
        
        long executionTime = System.currentTimeMillis() - startTime;
        return new QueryResult(columnNames, rows, executionTime);
    }

    public int executeUpdate(DatabaseConnection dbConnection, String query) throws SQLException {
        try (Statement stmt = dbConnection.getConnection().createStatement()) {
            return stmt.executeUpdate(query);
        }
    }

    public String getExplainPlan(DatabaseConnection dbConnection, String query) throws SQLException {
        Dialect dialect = DialectFactory.getDialect(dbConnection.getDatabaseType());
        String explainQuery = dialect.getExplainQuery(query);
        
        StringBuilder result = new StringBuilder();
        try (Statement stmt = dbConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(explainQuery)) {
            
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    if (i > 1) {
                        result.append(" | ");
                    }
                    result.append(rs.getString(i));
                }
                result.append("\n");
            }
        }
        
        return result.toString();
    }
}
