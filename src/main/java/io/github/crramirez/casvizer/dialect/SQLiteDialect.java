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
 * SQLite database dialect implementation.
 */
public class SQLiteDialect implements Dialect {

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
        return "EXPLAIN QUERY PLAN " + query;
    }

    @Override
    public String getListSchemasQuery() {
        // SQLite doesn't have multiple schemas
        return "SELECT 'main' as schema_name";
    }

    @Override
    public String getListTablesQuery(String schema) {
        return "SELECT name as table_name FROM sqlite_master " +
               "WHERE type = 'table' AND name NOT LIKE 'sqlite_%' " +
               "ORDER BY name";
    }

    @Override
    public String getListColumnsQuery(String schema, String table) {
        // Use string literal for the table name in PRAGMA to avoid issues with identifier quoting
        String escapedTable = table.replace("'", "''");
        return "PRAGMA table_info('" + escapedTable + "')";
    }

    @Override
    public String getName() {
        return "SQLite";
    }
}
