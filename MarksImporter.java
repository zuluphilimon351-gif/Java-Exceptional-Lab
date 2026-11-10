import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

// Custom exception for duplicate student IDs (Unchecked runtime exception)[cite: 3, 4]
class DuplicateStudentException extends RuntimeException {
    public DuplicateStudentException(String message) {
        super(message);
    }
}

public class MarksImporter {
    private static final Logger logger = Logger.getLogger(MarksImporter.class.getName());

    static void importMarks(String filename) throws IOException {
        Set<String> seen = new HashSet<>();

        // Try-with-resources automatically closes the BufferedReader and FileReader[cite: 3, 4]
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                try {
                    String[] parts = line.split(",");

                    // TODO 1: check there are exactly 2 fields[cite: 3]
                    if (parts.length != 2) {
                        throw new IllegalArgumentException("Invalid record format at line " + lineNumber);
                    }

                    String studentId = parts[0].trim();
                    int mark = Integer.parseInt(parts[1].trim());

                    // TODO 2: validate range 0..100[cite: 3]
                    if (mark < 0 || mark > 100) {
                        throw new IllegalArgumentException("Mark out of range (0-100): " + mark);
                    }

                    // TODO 3: detect repeated student ID using seen[cite: 3]
                    if (!seen.add(studentId)) {
                        throw new DuplicateStudentException("Duplicate student ID detected: " + studentId);
                    }

                    // TODO 4: display accepted record[cite: 3]
                    System.out.println("Accepted Record -> ID: " + studentId + ", Mark: " + mark);

                } catch (IllegalArgumentException | DuplicateStudentException ex) {
                    // Log technical details for developers[cite: 4]
                    logger.log(Level.WARNING, "Skipping invalid record: " + ex.getMessage(), ex);
                    // Show friendly message to users (no raw stack traces)[cite: 4]
                    System.out.println("⚠️ Warning: Could not process line " + lineNumber + " -> " + ex.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            importMarks("marks.txt");
        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, "Marks file not found.", ex);
            System.out.println("❌ Error: Marks file not found; check the file name.");
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "An I/O error occurred while reading the file.", ex);
            System.out.println("❌ Error: An unexpected file reading error occurred.");
        } finally {
            // This block always executes regardless of whether exceptions occurred[cite: 4]
            System.out.println("Import attempt finished");
        }
    }
}