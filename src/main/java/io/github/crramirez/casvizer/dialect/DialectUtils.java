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
 * Utility class for common dialect operations.
 */
public class DialectUtils {
    
    private DialectUtils() {
        // Utility class should not be instantiated
    }
    
    /**
     * Quote a string literal for use in SQL queries by escaping single quotes.
     * 
     * @param literal The string literal to quote
     * @return Quoted string literal safe for SQL
     */
    public static String quoteStringLiteral(String literal) {
        return "'" + literal.replace("'", "''") + "'";
    }
}
