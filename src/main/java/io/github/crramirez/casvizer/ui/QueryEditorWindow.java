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
package io.github.crramirez.casvizer.ui;

import casciian.TApplication;
import casciian.TEditor;
import casciian.TTable;
import casciian.TWindow;
import io.github.crramirez.casvizer.model.QueryResult;
import io.github.crramirez.casvizer.service.ConnectionService;
import io.github.crramirez.casvizer.service.QueryService;
import io.github.crramirez.casvizer.service.ExportService;

import java.util.List;

/**
 * Window for editing and executing SQL queries.
 */
public class QueryEditorWindow extends TWindow {
    private final ConnectionService connectionService;
    private final QueryService queryService;
    private final ExportService exportService;
    private TEditor queryEditor;
    private TTable resultsTable;
    private QueryResult currentResult;

    public QueryEditorWindow(TApplication application, ConnectionService connectionService,
                            QueryService queryService, ExportService exportService) {
        super(application, "Query Editor", 0, 0, 80, 30, RESIZABLE);

        this.connectionService = connectionService;
        this.queryService = queryService;
        this.exportService = exportService;

        setupUI();
    }

    private void setupUI() {
        int row = 1;
        addLabel("SQL Query:", 2, row++);
        
        // Add text editor for query input
        queryEditor = addEditor("SELECT 1 as test_column;", 2, row, getWidth() - 4, 6);
        row += 7;
        
        addButton("&Execute", 2, row, this::executeQuery);
        addButton("E&xport Results", 15, row, this::exportResults);
        addButton("&Close", 33, row, this::close);
        
        row += 2;
        addLabel("Results:", 2, row++);
        
        // Add table for results display
        resultsTable = addTable(2, row, getWidth() - 4, getHeight() - row - 1, 5, 10);
        resultsTable.setShowColumnLabels(true);
    }

    private void executeQuery() {
        try {
            String query = queryEditor.getText();
            if (query == null || query.trim().isEmpty()) {
                getApplication().messageBox("Error", "Please enter a SQL query");
                return;
            }
            
            QueryResult result = queryService.executeQuery(
                connectionService.getActiveConnection(), query.trim());
            
            currentResult = result;
            displayResults(result);
        } catch (Exception e) {
            getApplication().messageBox("Error", "Query execution failed: " + e.getMessage());
        }
    }

    private void displayResults(QueryResult result) {
        // Set up table columns and rows
        int colCount = result.getColumnCount();
        int rowCount = result.getRowCount();
        
        resultsTable.setGridSize(colCount, rowCount);
        
        // Set column headers
        List<String> columns = result.getColumnNames();
        for (int i = 0; i < columns.size(); i++) {
            resultsTable.setColumnLabel(i, columns.get(i));
        }
        
        // Set data rows
        List<List<Object>> rows = result.getRows();
        for (int rowIdx = 0; rowIdx < rows.size(); rowIdx++) {
            List<Object> row = rows.get(rowIdx);
            for (int colIdx = 0; colIdx < row.size(); colIdx++) {
                Object value = row.get(colIdx);
                String cellValue = value != null ? value.toString() : "NULL";
                resultsTable.setCellText(colIdx, rowIdx, cellValue);
            }
        }
        
        // Update status message
        String statusMsg = String.format("Rows: %d, Time: %dms", 
            result.getRowCount(), result.getExecutionTimeMs());
        setTitle("Query Editor - " + statusMsg);
    }

    private void exportResults() {
        if (currentResult == null) {
            getApplication().messageBox("Info", "No results to export. Execute a query first.");
            return;
        }
        
        try {
            // Use system-appropriate temporary directory
            String tempDir = System.getProperty("java.io.tmpdir");
            String filename = tempDir + (tempDir.endsWith(java.io.File.separator) ? "" : java.io.File.separator) + "query_results.csv";
            exportService.exportToCSV(currentResult, filename);
            getApplication().messageBox("Success", "Results exported to " + filename);
        } catch (Exception e) {
            getApplication().messageBox("Error", "Export failed: " + e.getMessage());
        }
    }
}
