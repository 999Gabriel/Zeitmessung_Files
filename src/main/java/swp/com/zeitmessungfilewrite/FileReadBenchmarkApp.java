package swp.com.zeitmessungfilewrite;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class FileReadBenchmarkApp extends Application {

    private static final String TEST_FILE_PATH = "test_file.txt";
    private static final int TEST_FILE_SIZE_MB = 1000; // Size in MB

    @Override
    public void start(Stage stage) throws IOException {
        // Create test file if it doesn't exist
        ensureTestFileExists();

        FXMLLoader fxmlLoader = new FXMLLoader(FileReadBenchmarkApp.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 700);

        // Fix: Correct CSS resource path
        scene.getStylesheets().add(getClass().getResource("/swp/com/zeitmessungfilewrite/main-style.css")
                .toExternalForm());

        stage.setTitle("File Reading Performance Benchmark");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Ensures a test file exists for benchmark testing
     */
    private void ensureTestFileExists() throws IOException {
        Path testFile = Path.of(TEST_FILE_PATH);

        if (!Files.exists(testFile)) {
            System.out.println("Creating test file of " + TEST_FILE_SIZE_MB + "MB...");

            // Create a buffer with random content
            StringBuilder buffer = new StringBuilder();
            for (int i = 0; i < 1000; i++) { // 1000 characters per line
                buffer.append(generateRandomString(1000));
                buffer.append("\n");
            }
            String content = buffer.toString();

            // Write content repeatedly until desired file size is reached
            Files.createFile(testFile);
            int iterations = (TEST_FILE_SIZE_MB * 1024 * 1024) / content.length();
            for (int i = 0; i < iterations; i++) {
                Files.write(testFile, content.getBytes(),
                        i == 0 ? StandardOpenOption.TRUNCATE_EXISTING : StandardOpenOption.APPEND);
            }

            System.out.println("Test file created at: " + testFile.toAbsolutePath());
        }
    }

    /**
     * Generates a random string of specified length
     */
    private String generateRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = (int)(chars.length() * Math.random());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        launch();
    }
}