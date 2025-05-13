package htl.ah;

import java.io.FileReader;
import java.io.IOException;

public class ReadFileByteByByte {

    public static void main(String[] args) {
        try (FileReader reader = new FileReader("example.txt")) {
            int data;
            while ((data = reader.read()) != -1) {
                System.out.print((char) data);
            }
            // reader.close(); // not needed because of try-with-resources
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
