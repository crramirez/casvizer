# Casvizer

Database visualization TUI (Text User Interface) tool based on [Casciian](https://github.com/crramirez/casciian). Think of it as a terminal-based DBVisualizer or DBeaver.

## Description

Casvizer is a powerful database client with a text-based user interface that supports multiple database systems. It provides a rich set of features for database management, querying, and visualization, all within your terminal.

### Features

- **Multi-Database Support**: Connect to PostgreSQL, MySQL, and SQLite databases
- **Connection Profiles**: Save and manage multiple database connection profiles with encrypted credentials
- **Database Browser**: Navigate database schemas, tables, and columns
- **Query Editor**: Execute SQL queries with syntax highlighting and results visualization
- **Query Pagination**: Built-in support for paginated query results
- **Export Data**: Export query results to CSV, SQL, or text formats
- **Query Explanation**: View query execution plans (EXPLAIN)
- **Secure Storage**: Encrypted password storage for connection profiles

### Architecture

The application follows a layered architecture:

1. **UI Layer** (Casciian-based TUI)
   - Main application window with menus
   - Connection dialog for managing profiles
   - Database browser for schema exploration
   - Query editor for SQL execution

2. **Domain/Services Layer**
   - `ConnectionService`: Manages database connections
   - `MetadataService`: Database introspection and metadata retrieval
   - `QueryService`: Query execution with pagination support
   - `ExportService`: Data export to various formats

3. **Driver/DB Adapters**
   - `Dialect` interface for database-specific operations
   - Implementations: `PostgresDialect`, `MySQLDialect`, `SQLiteDialect`
   - Handles quoting, pagination syntax, EXPLAIN queries, and schema operations

4. **Persistence Layer**
   - `ProfileStore`: Stores connection profiles in JSON format
   - `SecretsStore`: Encrypts/decrypts sensitive credentials

## Prerequisites

- Java 21 or later
- Gradle 9.2.1 or later (included via wrapper)
- For native image compilation: GraalVM Java 25 with native-image
- For packaging: fpm (installed via `gem install fpm`)

## Building

### Standard JAR Build

```bash
./gradlew clean build
```

This creates a JAR file in `build/libs/casciianapp-<version>.jar`

### Running the Application

```bash
./gradlew installDist
./build/install/casvizer/bin/casvizer
```

Or with Java directly:

```bash
export JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64
java -jar build/libs/casvizer-<version>.jar
```

### Using Casvizer

1. **Start the application** - You'll see a welcome screen
2. **Create a connection** - Use `Connection > New Connection` menu
3. **Browse database** - Use `Tools > Database Browser` to explore schemas and tables
4. **Execute queries** - Use `Tools > Query Editor` to run SQL queries

### Connection Profiles

Connection profiles are stored in `~/.casvizer/profiles.json` with encrypted passwords. You can set a custom encryption key using the `CASVIZER_MASTER_PASSWORD` environment variable.

### Native Image Compilation (Required for Packaging)

The DEB and RPM packages require a native binary. You need GraalVM Java 25 with native-image installed.

#### Installing GraalVM

You can install GraalVM using SDKMAN:

```bash
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 25.0.0.r25-graalce
```

Or download directly from [GraalVM Downloads](https://www.graalvm.org/downloads/).

#### Building Native Binary

1. Ensure GraalVM Java 25 is installed and configured
2. Run:

```bash
./gradlew nativeCompile
```

This creates a native executable at `build/native/nativeCompile/casvizer`

### Creating DEB and RPM Packages

**Important:** The packages require a native binary. You must first compile the native binary using GraalVM (see above).

#### Prerequisites for Packaging

```bash
# Install fpm and dependencies
sudo apt-get install ruby ruby-dev build-essential rpm
sudo gem install fpm
```

#### Building Packages

After compiling the native binary, build both DEB and RPM packages:

```bash
./gradlew buildPackages
```

Or build individually:

```bash
./gradlew buildDeb    # Creates DEB package in build/distributions/deb/
./gradlew buildRpm    # Creates RPM package in build/distributions/rpm/
```

The packages will include only:
- `/usr/bin/casvizer` - Native executable binary

#### Installing the Packages

**Debian/Ubuntu:**
```bash
sudo dpkg -i build/distributions/deb/casvizer_0.1.0-1_amd64.deb
sudo apt-get install -f  # Install dependencies if needed
```

**RedHat/CentOS/Fedora:**
```bash
sudo rpm -ivh build/distributions/rpm/casvizer-0.1.0-1.x86_64.rpm
```

After installation, you can run the application:
```bash
casvizer
```

## Customizing and Extending

This database visualization tool is designed to be extended with additional features:

1. **Add new database dialects**: Implement the `Dialect` interface for other databases
2. **Enhance UI components**: Extend the TUI windows with more advanced features
3. **Add export formats**: Implement new export formats in `ExportService`
4. **Customize connection profiles**: Extend `ConnectionProfile` with additional properties
5. **Add authentication methods**: Support different authentication mechanisms

## Project Structure

```
casvizer/
├── build.gradle              # Gradle build configuration
├── settings.gradle           # Gradle settings
├── gradle.properties         # Project version and properties
├── src/
│   └── main/
│       └── java/
│           └── io/github/crramirez/casvizer/
│               ├── Casvizer.java          # Main application
│               ├── model/                 # Domain models
│               │   ├── ConnectionProfile.java
│               │   ├── DatabaseConnection.java
│               │   └── QueryResult.java
│               ├── dialect/               # Database dialects
│               │   ├── Dialect.java
│               │   ├── DialectFactory.java
│               │   ├── PostgresDialect.java
│               │   ├── MySQLDialect.java
│               │   └── SQLiteDialect.java
│               ├── service/               # Business logic services
│               │   ├── ConnectionService.java
│               │   ├── MetadataService.java
│               │   ├── QueryService.java
│               │   └── ExportService.java
│               ├── persistence/           # Data persistence
│               │   ├── ProfileStore.java
│               │   └── SecretsStore.java
│               └── ui/                    # TUI components
│                   ├── ConnectionDialog.java
│                   ├── DatabaseBrowserWindow.java
│                   └── QueryEditorWindow.java
└── README.md
```

## License

Apache License 2.0 - Copyright 2025 Carlos Rafael Ramirez

## Dependencies

- [Casciian 1.0](https://github.com/crramirez/casciian) - Java Text User Interface library
- PostgreSQL JDBC Driver 42.7.4
- MySQL Connector/J 9.1.0
- SQLite JDBC Driver 3.47.1.0
- Gson 2.11.0 - JSON parsing
- Night Config 3.8.1 - TOML parsing
- Jasypt 1.9.3 - Password encryption

