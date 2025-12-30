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

    public QueryEditorWindow(TApplication application, ConnectionService connectionService,
                            QueryService queryService, ExportService exportService) {
        super(application, "Query Editor", 0, 0, 80, 24, RESIZABLE);

        this.connectionService = connectionService;
        this.queryService = queryService;
        this.exportService = exportService;

        setupUI();
    }

    private void setupUI() {
        int row = 1;
        addLabel("SQL Query:", 2, row++);
        
        // Add text area for query input
        // Note: This is simplified - in production would use TEditor or TTextArea
        addLabel("Enter SQL query and press Execute", 2, row++);
        addLabel("Example: SELECT * FROM users LIMIT 10", 2, row++);
        
        row++;
        addButton("&Execute", 2, row, this::executeQuery);
        addButton("E&xport Results", 15, row, this::exportResults);
        addButton("&Close", 33, row, this::close);
        
        row += 2;
        addLabel("Results:", 2, row++);
        addLabel("Query results will appear here", 2, row++);
    }

    private void executeQuery() {
        try {
            // Example query execution
            String query = "SELECT 1 as test_column";
            QueryResult result = queryService.executeQuery(
                connectionService.getActiveConnection(), query);
            
            displayResults(result);
        } catch (Exception e) {
            getApplication().messageBox("Error", "Query execution failed: " + e.getMessage());
        }
    }

    private void displayResults(QueryResult result) {
        int row = 10;
        
        // Display column headers
        StringBuilder header = new StringBuilder();
        for (String col : result.getColumnNames()) {
            header.append(col).append(" | ");
        }
        addLabel(header.toString(), 2, row++);
        
        // Display rows
        for (List<Object> rowData : result.getRows()) {
            StringBuilder rowStr = new StringBuilder();
            for (Object value : rowData) {
                rowStr.append(value != null ? value.toString() : "NULL").append(" | ");
            }
            if (row < getHeight() - 2) {
                addLabel(rowStr.toString(), 2, row++);
            }
        }
        
        // Display execution time
        addLabel(String.format("Rows: %d, Time: %dms", 
            result.getRowCount(), result.getExecutionTimeMs()), 2, row);
    }

    private void exportResults() {
        getApplication().messageBox("Info", "Export functionality coming soon!");
    }
}
