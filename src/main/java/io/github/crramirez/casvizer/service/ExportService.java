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

import io.github.crramirez.casvizer.model.QueryResult;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for exporting query results to various formats.
 */
public class ExportService {
    
    public void exportToCSV(QueryResult result, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Write header
            writer.println(String.join(",", result.getColumnNames()));
            
            // Write rows
            for (List<Object> row : result.getRows()) {
                List<String> values = new ArrayList<>();
                for (Object value : row) {
                    String strValue = value != null ? value.toString() : "";
                    // Escape quotes, newlines, and wrap in quotes if contains comma, quote, or newline
                    if (strValue.contains(",") || strValue.contains("\"") || strValue.contains("\n") || strValue.contains("\r")) {
                        // Replace newlines with space or escaped newline
                        strValue = strValue.replace("\r\n", " ").replace("\n", " ").replace("\r", " ");
                        strValue = "\"" + strValue.replace("\"", "\"\"") + "\"";
                    }
                    values.add(strValue);
                }
                writer.println(String.join(",", values));
            }
        }
    }

    public void exportToSQL(QueryResult result, String tableName, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            List<String> columns = result.getColumnNames();
            
            // Quote table name and column names to prevent SQL injection
            String quotedTableName = quoteIdentifier(tableName);
            List<String> quotedColumns = new ArrayList<>();
            for (String column : columns) {
                quotedColumns.add(quoteIdentifier(column));
            }
            
            for (List<Object> row : result.getRows()) {
                StringBuilder sql = new StringBuilder("INSERT INTO ");
                sql.append(quotedTableName);
                sql.append(" (");
                sql.append(String.join(", ", quotedColumns));
                sql.append(") VALUES (");
                
                for (int i = 0; i < row.size(); i++) {
                    if (i > 0) {
                        sql.append(", ");
                    }
                    Object value = row.get(i);
                    if (value == null) {
                        sql.append("NULL");
                    } else if (value instanceof String) {
                        // Escape single quotes and backslashes for SQL
                        String strValue = value.toString()
                            .replace("\\", "\\\\")
                            .replace("'", "''");
                        sql.append("'").append(strValue).append("'");
                    } else {
                        sql.append(value.toString());
                    }
                }
                sql.append(");");
                writer.println(sql.toString());
            }
        }
    }
    
    /**
     * Quote an identifier (table or column name) for SQL.
     * Uses double quotes which work for PostgreSQL and MySQL with ANSI_QUOTES.
     */
    private String quoteIdentifier(String identifier) {
        if (identifier == null || identifier.isEmpty()) {
            throw new IllegalArgumentException("Identifier must not be null or empty");
        }
        return "\"" + identifier.replace("\"", "\"\"") + "\"";
    }

    public void exportToText(QueryResult result, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            List<String> columns = result.getColumnNames();
            
            // Calculate column widths
            int[] widths = new int[columns.size()];
            for (int i = 0; i < columns.size(); i++) {
                widths[i] = columns.get(i).length();
            }
            
            for (List<Object> row : result.getRows()) {
                for (int i = 0; i < row.size(); i++) {
                    String value = row.get(i) != null ? row.get(i).toString() : "NULL";
                    widths[i] = Math.max(widths[i], value.length());
                }
            }
            
            // Write header
            for (int i = 0; i < columns.size(); i++) {
                if (i > 0) {
                    writer.print(" | ");
                }
                writer.print(String.format("%-" + widths[i] + "s", columns.get(i)));
            }
            writer.println();
            
            // Write separator
            for (int i = 0; i < columns.size(); i++) {
                if (i > 0) {
                    writer.print("-+-");
                }
                writer.print("-".repeat(widths[i]));
            }
            writer.println();
            
            // Write rows
            for (List<Object> row : result.getRows()) {
                for (int i = 0; i < row.size(); i++) {
                    if (i > 0) {
                        writer.print(" | ");
                    }
                    String value = row.get(i) != null ? row.get(i).toString() : "NULL";
                    writer.print(String.format("%-" + widths[i] + "s", value));
                }
                writer.println();
            }
        }
    }
}
