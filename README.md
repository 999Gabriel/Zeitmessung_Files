# File Reading Performance Benchmark

A JavaFX application for benchmarking different file reading methods in Java. This tool helps developers compare the performance of various file reading approaches to make informed decisions about which method to use in their applications.

## Features

- **Multiple Reading Methods**: Tests 5 different file reading approaches:
    - BufferedReader
    - Files.readAllBytes
    - Files.readString
    - FileChannel
    - Files.lines

- **Comprehensive Metrics**: Measures and displays:
    - Average read time
    - Minimum read time
    - Maximum read time

- **Visual Results**: Results presented in:
    - Interactive bar chart visualization
    - Detailed results table
    - Real-time progress tracking

- **Custom File Selection**: Test with your own files of any size

## Getting Started

### Prerequisites

- Java 22 or later
- Maven 3.6 or later

### Installation

1. Clone the repository: git clone https://github.com/999Gabriel/Zeitmessung_Files.git cd Zeitmessung_Files
2. Build the project: mvn clean package
3. Run the application: java -jar target/Zeitmessung_Files-1.0-SNAPSHOT.jar

## Usage

1. **Select a file**: Click the "Select File" button to choose a file to benchmark
2. **Run benchmark**: Click "Run Benchmark" to start the performance test
3. **View results**: Examine the metrics in the results area and chart
4. **Log output**: Check the log area for detailed progress information

### Testing with Generated Files

The application can create test files of customizable sizes for benchmarking:

```java
// Running with a custom size (in MB)
java -jar target/zeitmessungfilewrite-1.0-SNAPSHOT.jar 100