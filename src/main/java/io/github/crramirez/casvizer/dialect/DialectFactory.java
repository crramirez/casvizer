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
 * Factory for creating database dialect instances.
 */
public class DialectFactory {
    
    public static Dialect getDialect(String databaseType) {
        if (databaseType == null) {
            throw new IllegalArgumentException("Database type cannot be null");
        }
        
        switch (databaseType.toLowerCase()) {
            case "postgres":
            case "postgresql":
                return new PostgresDialect();
            case "mysql":
                return new MySQLDialect();
            case "sqlite":
                return new SQLiteDialect();
            default:
                throw new IllegalArgumentException("Unsupported database type: " + databaseType);
        }
    }
}
