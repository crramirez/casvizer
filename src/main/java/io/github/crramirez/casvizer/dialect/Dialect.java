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
 * Interface for database-specific SQL dialect operations.
 */
public interface Dialect {
    
    /**
     * Quote an identifier (table name, column name, etc.) according to the database's syntax.
     *
     * @param identifier The identifier to quote
     * @return The quoted identifier
     */
    String quoteIdentifier(String identifier);

    /**
     * Add pagination to a SQL query.
     *
     * @param query The base SQL query
     * @param limit The maximum number of rows to return
     * @param offset The number of rows to skip
     * @return The query with pagination added
     */
    String addPagination(String query, int limit, int offset);

    /**
     * Get the EXPLAIN syntax for this database.
     *
     * @param query The query to explain
     * @return The EXPLAIN query
     */
    String getExplainQuery(String query);

    /**
     * Get the query to list all schemas/databases.
     *
     * @return SQL query to list schemas
     */
    String getListSchemasQuery();

    /**
     * Get the query to list all tables in a schema.
     *
     * @param schema The schema name (null for default)
     * @return SQL query to list tables
     */
    String getListTablesQuery(String schema);

    /**
     * Get the query to list all columns in a table.
     *
     * @param schema The schema name (null for default)
     * @param table The table name
     * @return SQL query to list columns
     */
    String getListColumnsQuery(String schema, String table);

    /**
     * Get the name of this dialect.
     *
     * @return The dialect name
     */
    String getName();
}
