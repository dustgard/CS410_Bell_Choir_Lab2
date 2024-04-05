import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * The SongNotes class is used to parse the text file into usable notes that the program uses to make an audible
 * sound.
 * This class does validation on the file and the file contents to ensure that a correctly formatted file was provided.
 * If the file is not a correct format or has the wrong file contents to play a song, the user is informed with a
 * message on the console with the error location.
 * Author: Dustin Gardner
 */
public class SongNotes {
    private final String songLocation;
    private final Queue<BellNote> bellNotes = new LinkedList<>();
    private Map<String, Integer> errorList = new HashMap<>();

    /**
     * The constructor stores the song location that the user has provided for the song using ant.
     * @param songLocation the arguments passed from the command line using ant and only stores the first argument to
     *                     ensure the program can play the song correctly.
     */
    public SongNotes(String[] songLocation) {
        this.songLocation = songLocation[0];
    }

    /**
     * This method is used to validate the file being passed in.
     * It checks if the file exists, the format and informs the user if any checks did not pass.
     * It also calls one method to check the file contents to make sure the program can play the provided text file
     * as a song.
     * @param filename the location on the computer for the song.txt file.
     * @return true if the file is considered valid and false if there are any errors detected.
     */
    public boolean validateFile(String[] filename) {
        if (filename.length > 1) {
            System.err.println("Program only takes one parameter. Example: ant -Dsong=src/Song/MaryLamb.txt run");
            return false;
        }
        File file = new File(filename[0]);
        if (!file.exists()) {
            System.err.println("File not found: You provided " + filename[0]);
            return false;
        }
        String fileCheck = file.getName();
        String format = "";
        int formatExt = fileCheck.lastIndexOf(".");
        if (formatExt >= 0) {
            format = fileCheck.substring(formatExt + 1);
        }
        if (!format.equals("txt")) {
            System.err.println("File provided [" + fileCheck + "] in not the correct format type: [" + format + "] Please provide a .txt file");
            return false;
        }
        if (!validateNotes()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * This method is used to verify if notes within the file are valid.
     * It adds the required first note as REST, and then, if the lengths in the song.txt file match the correct format
     * for the program, it adds the note to the bellNotes queue to be played.
     * If there is a note that is not in the correct length or note, it notifies the user that there is a problem.
     * @return true if correct file contents, which means all the notes were added to the bellNote List or false if there
     * were any incorrect lines in the song.txt file.
     */
    public boolean validateNotes() {
        File songFile = new File(songLocation);
        try (BufferedReader reader = new BufferedReader(new FileReader(songFile))) {
            bellNotes.add(new BellNote(Note.REST, NoteLength.QUARTER));
            int noteCount = 0;
            while (true) {
                String note = "";
                try {
                    if ((note = reader.readLine()) == null) break;
                } catch (IOException ignore) {
                }
                noteCount++;
                String[] split = note.split("\\s+");
                NoteLength l = null;
                try {
                    switch (split[1]) {
                        case "1":
                            l = NoteLength.WHOLE;
                            break;
                        case "2":
                            l = NoteLength.HALF;
                            break;
                        case "3":
                            l = NoteLength.THIRD;
                            break;
                        case "4":
                            l = NoteLength.QUARTER;
                            break;
                        case "8":
                            l = NoteLength.EIGHTH;
                            break;
                        case "16":
                            l = NoteLength.SIXTEENTH;
                            break;
                    }
                } catch (Exception e) {
                }
                try {
                    Note.valueOf(split[0]);
                    try {
                        NoteLength.valueOf(String.valueOf(l));
                    } catch (IllegalArgumentException e) {
                        System.err.println("[" + split[1] + "] is not the correct length format to be played at line: " + noteCount);
                        errorList.put(split[1], noteCount);
                    }
                    bellNotes.add(new BellNote(Note.valueOf(split[0]), l));
                    if (split.length > 2) {
                        System.err.println("[" + note + "] is not the correct format to be played at line: " + noteCount);
                        errorList.put(note, noteCount);
                    }
                } catch (IllegalArgumentException e) {
                    if (note.equals("")) {
                        System.err.println("Can not be blank at line " + noteCount);
                        errorList.put(note, noteCount);
                    } else {
                        System.err.println("[" + split[0] + "] is not the correct note format to be played at line: " + noteCount);
                        errorList.put(note, noteCount);
                    }
                }
            }
            if (!errorList.isEmpty()) {
                return false;
            }
        } catch (IOException e) {
            System.err.println(e + "File was not found");
        }
        return true;
    }

    /**
     * Used to supply the Queue of BellNotes to the ChoirConductor.
     * @return the queue of BellNotes to be played.
     */
    public Queue<BellNote> getMusicNotes() {
        return bellNotes;
    }

    /**
     * This method takes all the notes and only stores unique values to be used by the conductor to create and
     * assign a thread to only use one bell note.
     * @return a HashSet with only the unique notes in the file based on the key not the length.
     */
    public HashSet<String> getUniqueNotes() {
        List<String> members = new ArrayList<>();
        for (BellNote mus : bellNotes) {
            members.add(mus.note.name());
        }
        HashSet<String> unique = new HashSet<>(members);
        return unique;
    }
}
