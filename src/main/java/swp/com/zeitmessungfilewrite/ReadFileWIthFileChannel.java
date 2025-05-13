package htl.ah;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;

public class ReadFileWIthFileChannel {
	
    public static void main(String[] args) {
    	
    	/*
    	 * A file channel is a SeekableByteChannel that is connected to a file. 
    	 * It has a current position within its file which can be both queried 
    	 * and modified. The file itself contains a variable-length sequence of 
    	 * bytes that can be read and written and whose current size can be queried. 
    	 * The size of the file increases when bytes are written beyond its current size; 
    	 * the size of the filedecreases when it is truncated. 
    	 * The file may also have some associated meta data such as access permissions, 
    	 * content type, and last-modification time; 
    	 * this class does not define methods for meta data access. 
    	 */
    	
        try (FileChannel fileChannel = 
        		FileChannel.open(Path.of("largefile.txt"), StandardOpenOption.READ)) {
           
        	// Allocate a ByteBuffer with a capacity of 4096 bytes 
        	// to read data from the file channel.
            ByteBuffer buffer = ByteBuffer.allocate(4096);
            // Continuously read data from the file channel into the buffer 
            // until there is no more data to read.
            while (fileChannel.read(buffer) > 0) {
                // Flip the buffer to prepare it for reading: 
            	// This sets the limit to the current position and the position to 0.
                buffer.flip();
                // Convert the buffer's content to a string and print it to the console. 
                // The parameters specify the start index, end index, and the character set.
                System.out.print(new String(buffer.array(), 0, buffer.limit()));
                // Clear the buffer to prepare it for the next read operation. 
                // This sets the position to 0 and the limit to the capacity.
                buffer.clear();
            }
        } catch (IOException e) {
            // Catch any IOException that might occur during file reading 
        	// and print the stack trace.
            e.printStackTrace();
        }

    }
}

