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

import java.util.List;

/**
 * Represents the result of a database query execution.
 * <p>
 * <strong>Immutability Note:</strong> This class stores references to mutable lists.
 * Callers must not modify the provided lists after construction to maintain consistency
 * between the stored data and the rowCount field.
 */
public class QueryResult {
    private final List<String> columnNames;
    private final List<List<Object>> rows;
    private final long executionTimeMs;
    private final int rowCount;

    public QueryResult(List<String> columnNames, List<List<Object>> rows, long executionTimeMs) {
        this.columnNames = columnNames;
        this.rows = rows;
        this.executionTimeMs = executionTimeMs;
        this.rowCount = rows.size();
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public List<List<Object>> getRows() {
        return rows;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public int getRowCount() {
        return rowCount;
    }

    public int getColumnCount() {
        return columnNames.size();
    }
}
