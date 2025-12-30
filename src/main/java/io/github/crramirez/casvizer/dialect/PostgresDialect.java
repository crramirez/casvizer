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
package io.github.crramirez.casvizer.dialect;

/**
 * PostgreSQL database dialect implementation.
 */
public class PostgresDialect implements Dialect {

    @Override
    public String quoteIdentifier(String identifier) {
        return "\"" + identifier.replace("\"", "\"\"") + "\"";
    }

    @Override
    public String addPagination(String query, int limit, int offset) {
        if (limit < 0) {
            throw new IllegalArgumentException("Limit must be a non-negative integer");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Offset must be a non-negative integer");
        }
        return query + " LIMIT " + limit + " OFFSET " + offset;
    }

    @Override
    public String getExplainQuery(String query) {
        return "EXPLAIN " + query;
    }

    @Override
    public String getListSchemasQuery() {
        return "SELECT schema_name FROM information_schema.schemata " +
               "WHERE schema_name NOT IN ('pg_catalog', 'information_schema') " +
               "ORDER BY schema_name";
    }

    @Override
    public String getListTablesQuery(String schema) {
        if (schema == null || schema.isEmpty()) {
            schema = "public";
        }
        // Use quoted identifier to prevent SQL injection
        return "SELECT table_name FROM information_schema.tables " +
               "WHERE table_schema = " + quoteStringLiteral(schema) + " AND table_type = 'BASE TABLE' " +
               "ORDER BY table_name";
    }

    @Override
    public String getListColumnsQuery(String schema, String table) {
        if (schema == null || schema.isEmpty()) {
            schema = "public";
        }
        // Use quoted identifiers to prevent SQL injection
        return "SELECT column_name, data_type, is_nullable, column_default " +
               "FROM information_schema.columns " +
               "WHERE table_schema = " + quoteStringLiteral(schema) + " AND table_name = " + quoteStringLiteral(table) + " " +
               "ORDER BY ordinal_position";
    }

    @Override
    public String getName() {
        return "PostgreSQL";
    }
    
    /**
     * Quote a string literal for use in SQL queries.
     */
    private String quoteStringLiteral(String literal) {
        return "'" + literal.replace("'", "''") + "'";
    }
}
