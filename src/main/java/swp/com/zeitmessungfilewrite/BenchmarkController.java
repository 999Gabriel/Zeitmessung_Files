package swp.com.zeitmessungfilewrite;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class BenchmarkController {
    @FXML private Button selectFileButton;
    @FXML private Button startBenchmarkButton;
    @FXML private Label filePathLabel;
    @FXML private ProgressBar progressBar;
    @FXML private TableView<BenchmarkResult> resultTable;
    @FXML private TableColumn<BenchmarkResult, String> methodColumn;
    @FXML private TableColumn<BenchmarkResult, String> avgTimeColumn;
    @FXML private TableColumn<BenchmarkResult, String> minTimeColumn;
    @FXML private TableColumn<BenchmarkResult, String> maxTimeColumn;
    @FXML private BarChart<String, Number> resultChart;
    @FXML private TextArea logTextArea;

    private static final String LOG_FILE = "benchmark.log";
    private static final DateTimeFormatter LOG_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int ITERATIONS = 10;

    private Path selectedFilePath;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final ObservableList<BenchmarkResult> results = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Initialize table columns
        methodColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMethodName()));
        avgTimeColumn.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.2f ms", data.getValue().getAvgTime())));
        minTimeColumn.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.2f ms", data.getValue().getMinTime())));
        maxTimeColumn.setCellValueFactory(data -> new SimpleStringProperty(String.format("%.2f ms", data.getValue().getMaxTime())));

        // Bind result list to table
        resultTable.setItems(results);

        // Button initial state
        startBenchmarkButton.setDisable(true);
    }

    @FXML
    private void onSelectFileButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File for Benchmark");
        File file = fileChooser.showOpenDialog(selectFileButton.getScene().getWindow());

        if (file != null) {
            selectedFilePath = file.toPath();
            filePathLabel.setText("Selected file: " + file.getAbsolutePath());
            startBenchmarkButton.setDisable(false);
            logAction("File selected: " + file.getAbsolutePath());
        }
    }

    @FXML
    private void onStartBenchmarkButtonClick() {
        if (selectedFilePath == null) {
            logAction("No file selected for benchmark");
            return;
        }

        // Clear previous results
        results.clear();
        resultChart.getData().clear();
        progressBar.setProgress(0);

        // Disable UI during benchmark
        startBenchmarkButton.setDisable(true);
        selectFileButton.setDisable(true);

        // Create and run benchmark task
        Task<List<BenchmarkResult>> benchmarkTask = new Task<>() {
            @Override
            protected List<BenchmarkResult> call() throws Exception {
                List<BenchmarkResult> benchmarkResults = new ArrayList<>();

                // Define reading methods to benchmark
                List<ReadingMethod> methods = List.of(
                        new ReadingMethod("BufferedReader", BenchmarkController.this::readWithBufferedReader),
                        new ReadingMethod("Files.readAllBytes", BenchmarkController.this::readWithFilesReadAllBytes),
                        new ReadingMethod("Files.readString", BenchmarkController.this::readWithFilesReadString),
                        new ReadingMethod("FileChannel", BenchmarkController.this::readWithFileChannel),
                        new ReadingMethod("Files.lines", BenchmarkController.this::readWithFilesLines)
                );

                double progressStep = 1.0 / methods.size();
                double currentProgress = 0;

                for (ReadingMethod method : methods) {
                    updateMessage("Running benchmark for " + method.getName());
                    logAction("Starting benchmark for method: " + method.getName());

                    List<Double> times = new ArrayList<>();
                    for (int i = 0; i < ITERATIONS; i++) {
                        long startTime = System.nanoTime();
                        String content = method.getReader().read(selectedFilePath);
                        long endTime = System.nanoTime();

                        double timeMs = (endTime - startTime) / 1_000_000.0;
                        times.add(timeMs);

                        updateProgress(currentProgress + (progressStep * (i + 1) / ITERATIONS), 1);
                        Thread.sleep(100); // Short delay between iterations
                    }

                    // Calculate statistics
                    double avgTime = times.stream().mapToDouble(Double::doubleValue).average().orElse(0);
                    double minTime = times.stream().mapToDouble(Double::doubleValue).min().orElse(0);
                    double maxTime = times.stream().mapToDouble(Double::doubleValue).max().orElse(0);

                    BenchmarkResult result = new BenchmarkResult(method.getName(), avgTime, minTime, maxTime);
                    benchmarkResults.add(result);

                    // Update progress
                    currentProgress += progressStep;
                    updateProgress(currentProgress, 1);

                    logAction(String.format("Completed benchmark for %s: Avg=%.2fms, Min=%.2fms, Max=%.2fms",
                            method.getName(), avgTime, minTime, maxTime));
                }

                return benchmarkResults;
            }
        };

        // Handle task events
        benchmarkTask.setOnSucceeded(event -> {
            List<BenchmarkResult> benchmarkResults = benchmarkTask.getValue();
            results.addAll(benchmarkResults);
            updateChart(benchmarkResults);

            // Re-enable UI
            startBenchmarkButton.setDisable(false);
            selectFileButton.setDisable(false);
            progressBar.setProgress(1);

            logAction("Benchmark completed");
        });

        benchmarkTask.setOnFailed(event -> {
            Throwable exception = benchmarkTask.getException();
            logAction("Benchmark failed: " + exception.getMessage());
            exception.printStackTrace();

            // Re-enable UI
            startBenchmarkButton.setDisable(false);
            selectFileButton.setDisable(false);
        });

        // Bind progress
        progressBar.progressProperty().bind(benchmarkTask.progressProperty());

        // Run the task
        executorService.submit(benchmarkTask);
    }

    private void updateChart(List<BenchmarkResult> results) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Average Read Time (ms)");

        for (BenchmarkResult result : results) {
            series.getData().add(new XYChart.Data<>(result.getMethodName(), result.getAvgTime()));
        }

        Platform.runLater(() -> {
            resultChart.getData().clear();
            resultChart.getData().add(series);
        });
    }

    // Reading methods
    private String readWithBufferedReader(Path path) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new java.io.FileReader(path.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    private String readWithFilesReadAllBytes(Path path) throws IOException {
        byte[] bytes = Files.readAllBytes(path);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private String readWithFilesReadString(Path path) throws IOException {
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    private String readWithFileChannel(Path path) throws IOException {
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate((int) channel.size());
            channel.read(buffer);
            buffer.flip();
            return StandardCharsets.UTF_8.decode(buffer).toString();
        }
    }

    private String readWithFilesLines(Path path) throws IOException {
        try (var lines = Files.lines(path, StandardCharsets.UTF_8)) {
            return lines.collect(Collectors.joining("\n"));
        }
    }

    /**
     * Logs an action to both the log file and the UI text area.
     *
     * @param action The action to log
     */
    private void logAction(String action) {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(LOG_FORMATTER);
        String logEntry = timestamp + " - " + action;

        // Update UI
        Platform.runLater(() -> {
            logTextArea.appendText(logEntry + "\n");
            logTextArea.setScrollTop(Double.MAX_VALUE); // Scroll to bottom
        });

        // Write to log file
        String fileLogEntry = logEntry + System.lineSeparator();
        try {
            Files.write(
                    Path.of(LOG_FILE),
                    fileLogEntry.getBytes(),
                    StandardOpenOption.APPEND,
                    StandardOpenOption.CREATE
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper classes

    /**
     * Functional interface for file reading methods
     */
    @FunctionalInterface
    private interface FileReaderMethod {
        String read(Path path) throws IOException;
    }

    /**
     * Class to pair a method name with its implementation
     */
    private static class ReadingMethod {
        private final String name;
        private final FileReaderMethod reader;

        public ReadingMethod(String name, FileReaderMethod reader) {
            this.name = name;
            this.reader = reader;
        }

        public String getName() {
            return name;
        }

        public FileReaderMethod getReader() {
            return reader;
        }
    }

    /**
     * Data class to store benchmark results
     */
    public static class BenchmarkResult {
        private final String methodName;
        private final double avgTime;
        private final double minTime;
        private final double maxTime;

        public BenchmarkResult(String methodName, double avgTime, double minTime, double maxTime) {
            this.methodName = methodName;
            this.avgTime = avgTime;
            this.minTime = minTime;
            this.maxTime = maxTime;
        }

        public String getMethodName() {
            return methodName;
        }

        public double getAvgTime() {
            return avgTime;
        }

        public double getMinTime() {
            return minTime;
        }

        public double getMaxTime() {
            return maxTime;
        }
    }
}