package htl.ah;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class ReadFileLineByLine {
 
	public static void main(String[] args) {
		
		// Variante 1:
		// ################################
		try (BufferedReader reader = new BufferedReader(new FileReader("example.txt"))) {
		    // Variante 1 A:
			// ----------------
			String line;
		    while ( (line = reader.readLine()) != null ) {
		        System.out.println(line);
		    }
		    
		    // Variante 1 B:
			// ----------------
		    line = reader.readLine();
		    while (line != null) {
		    	System.out.println(line);
		    	// read next line
		    	line = reader.readLine();
		    }
		    
		    // Bei Verwendung von streams:
		    // Variante 1 C:
			// ----------------
		    // Beschreibung:
		    // beim Stream-Objekt reader wird die Methode lines() aufgerufen
		    // forEach() wird aufgerufen, um die einzelnen Zeilen abzuarbeiten
		    // :: bedeutet Methodenreferenz
		    // für jede Zeile wird die Methode println() mit der Zeile aufgerufen
		    
		    reader.lines().forEach(System.out::println);
		    // entspricht:
		    reader.lines().forEach(actLine -> System.out.println(actLine));
		    
		    
		} catch (IOException e) {
		    e.printStackTrace();
		}
        
		// BufferedReader mittels Files	
		// ################################ 
		// Erklärung:
		// Files.newBufferedReader() erstellt einen BufferedReader
		try (BufferedReader reader = Files.newBufferedReader(Path.of("largefile.txt"))) {
	         reader.lines().forEach(line -> { System.out.println(line); });     
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		
		
		// Variante 2: Klasse Files (siehe auch ReadWholeFile.java)
		// #########################################################
		// Erklärung:
		// Files.readString() liest den gesamten Inhalt einer Datei in einen String.
		// Diese Methode ist nützlich, wenn Sie den gesamten Inhalt der Datei auf einmal lesen möchten.
		// Sie können auch Files.readAllLines() verwenden, um den Inhalt der Datei in eine Liste von Strings zu lesen.
		// Files.readAllLines() liest den Inhalt der Datei zeilenweise und gibt eine Liste von Strings zurück,
		// wobei jeder String eine Zeile der Datei darstellt.
        
		try {
			 List<String> allLines = Files.readAllLines(Paths.get("sample.txt"));
			 for (String line : allLines) {
				 	System.out.println(line);
			 }
		} catch (IOException e) {
			 e.printStackTrace();
		}
		
		
		
		// Variante 3:  Scanner
		// ################################
		// Erklärung:
		// Scanner ist eine Klasse in Java, die es ermöglicht,
		// Daten aus verschiedenen Eingabeströmen zu lesen, einschließlich Dateien.
		// Der Scanner kann verwendet werden, um Daten zeilenweise zu lesen.
		// Der Konstruktor Scanner(File source) erstellt einen neuen Scanner,
		// der auf die angegebene Datei verweist.
		// Die Methode hasNextLine() überprüft, ob es eine weitere Zeile gibt,
		// und die Methode nextLine() liest die nächste Zeile.
		try {
		    File obj = new File("myfile.txt");
		    Scanner reader = new Scanner(obj);
		   
		    // Traversing File Data
		    while (reader.hasNextLine()) {
		         String data = reader.nextLine();
		         System.out.println(data);
		    }
		    reader.close();
		}
		// Exception Cases
		catch (FileNotFoundException e) {
		     System.out.println("An error has occurred.");
		     e.printStackTrace();
		}
 
	     
	    // Variante 4:  RandomAccessFile
		// ################################
	    // Erklärung:
	    // RandomAccessFile ist eine Klasse, die es ermöglicht,
	    // auf eine Datei sowohl sequenziell als auch zufällig zuzugreifen.
	    // Sie ermöglicht das Lesen und Schreiben von Daten an beliebigen Positionen in der Datei.
	    // Der Konstruktor RandomAccessFile(String name, String mode)
	    // öffnet die Datei mit dem angegebenen Namen im angegebenen Modus.
	    // Der Modus "r" steht für "read" (lesen),
	    // was bedeutet, dass die Datei nur zum Lesen geöffnet wird.
	    // Der Modus "rw" steht für "read/write" (lesen/schreiben),
	    // was bedeutet, dass die Datei sowohl zum Lesen 
	    // als auch zum Schreiben geöffnet wird.	
	    // Die Methode readLine() liest eine Zeile aus der Datei
	    // und gibt sie als String zurück.
	    try (RandomAccessFile file = new RandomAccessFile("sample.txt", "r"))
		{	  
			 String str;

			 // Position ändern: file.seek(100);
			 // Position auslesen: long position = file.getFilePointer();
			 // Dateigröße: long length = file.length();
			 // Datei schließen: file.close();
			  
			 while ((str = file.readLine()) != null) {
				  System.out.println(str);
			 }
		 } catch (IOException e) {
			 e.printStackTrace();
		 }	
    }
}
