package htl.ah;
//Reading File using Java Program
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner; 

public class ReadWholeFile 
{

    // ========================
    // Read file at once:
    // ========================
	
	public static void main(String[] args)
	{
		String content;
		// Variante 1:
	    Scanner scanner2 = new Scanner(new InputStreamReader(
	    		    ClassLoader.getSystemResourceAsStream("myfile.txt")));
	
		scanner2.useDelimiter("\\Z"); // \\Z is the end of the input (= eof)
		content = scanner2.next();
		scanner2.close();
		System.out.println(content);

		
		
		// Variante 2:
	    try {
            // This line reads the content of the file "example.txt" 
        	// assuming it's encoded in UTF-8.
            // The Path.of("example.txt") method creates a Path object 
        	// representing the file path.
            // The StandardCharsets.UTF_8 parameter specifies the character set 
        	// to use for decoding.
            content = Files.readString(
            		Path.of("example.txt"), 
            		StandardCharsets.UTF_8);
            
            //  Charset.forName("UTF-16") can be used to read files encoded in UTF-16.
            //  Charset.forName("ISO-8859-1") can be used to read files encoded in ISO-8859-1.
            // ...
            
            // This line prints the content of the file to the console.
            System.out.println(content);
        } catch (IOException e) {
            // This block catches any IOException that might occur during file reading and prints the stack trace.
                e.printStackTrace();
        }
	}
}