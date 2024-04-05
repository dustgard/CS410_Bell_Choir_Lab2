import java.io.*;
import java.util.*;

/**
 * The SongNotes class is used to parse the song.txt file into usable notes that the program use to make and audible
 * sound.
 * This class does validation on the file and the file contents to ensure that a correctly formatted file was provided.
 * If the file is not a correct format or has the wrong file contents to play a song, the user is informed with a
 * message on the console with the error and how to fix it.
 * Author: Dustin Gardner
 */
public class SongNotes {
    private final String songLocation;
    private final List<String> musicNotes = new ArrayList<>();
    private final Queue<BellNote> bellNotes = new LinkedList<>();

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
     * It also calls two methods to check the file contents to make sure the program can play the provided text file
     * as a song.
     * @param filename the location on the computer for the song.txt file.
     * @return true if the file is considered valid and false if there are any errors detected.
     */
    public boolean validateFile(String[] filename) {
        if (filename.length > 1) {
            System.out.println("Program only takes one parameter. Example: ant -Dsong=src/Song/MaryLamb.txt run");
            return false;
        }
        File file = new File(filename[0]);
        if (!file.exists()) {
            System.out.println("File not found: You provided " + filename[0]);
            return false;
        }
        String fileCheck = file.getName();
        String format = "";
        int ext = fileCheck.lastIndexOf(".");
        if (ext >= 0) {
            format = fileCheck.substring(ext + 1);
        }
        if (!format.equals("txt")) {
            System.out.println("File provided [" +fileCheck + "] in not the correct format type: [" + format + "] Please provide a .txt file");
            return false;
        }
        if (!validateNotes()) {
            return false;
        }
        if (!validateNoteLengths()) {
            return false;
        } else {
            return format.equals("txt");
        }
    }

    /**
     * This method is used to verify if notes within the file are valid and if there are too many notes on a single line
     * or are blank.
     * It then adds the valid notes to the musicNotes List
     * and verifies that the good note count and the total note count is the same.
     * If it is not, then the file contents are accepted.
     * @return true if the count match, which means all the notes were added to the musicNotes List or false if there
     * were any incorrect lines in the song.txt file.
     */
    public boolean validateNotes() {
        File songFile = new File(songLocation);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(songFile));
        } catch (FileNotFoundException ignore) {
            System.err.println(ignore + "File was not found");
        }
        int noteCount = 0;
        int goodNoteCount = 0;
        while (true) {
            String note = "";
            try {
                if ((note = reader.readLine()) == null) break;
            } catch (IOException ignore) {
            }
            for (Note checkNote : Note.values()) {
                String[] noteSplit = note.split(" ");
                if (noteSplit.length < 2 || noteSplit.length > 2) {
                    System.out.println("Too many or few Note parameters");
                    return false;
                }
                if (noteSplit[0].equals(checkNote.toString())) {
                    musicNotes.add(note);
                    goodNoteCount++;
                }
            }
            noteCount++;
        }
        // Use try with resources
        try {
            reader.close();
        } catch (IOException ignore) {

        }
        if (noteCount != goodNoteCount) {
            System.out.println("File notes are not correct");
            return false;
        }
        System.out.println("True");
        return true;
    }

    /**
     * This method is used to validate the lengths that are associated with the notes in the song.txt file.
     * It adds the required first note as REST, and then, if the lengths in the song.txt file match the correct format
     * for the program, it adds the note to the bellNotes queue to be played.
     * If there is a note that is not in the correct length it notifies the user that there is a problem.
     * @return true if all the notes are the correct length and false if it is not one of the switch cases.
     */
    public boolean validateNoteLengths() {
        bellNotes.add(new BellNote(Note.REST, NoteLength.QUARTER));
        for (String note : musicNotes) {
            String[] split = note.split("\\s+");
            NoteLength l;
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
                default:
                    System.out.println("Note length wrong");
                    return false;
            }
            bellNotes.add(new BellNote(Note.valueOf(split[0]), l));
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
     * assign a thread to only use one bell to be played.
     * @return a HashSet with only the unique notes in the file based on the key not the length.
     */
    public HashSet<String> getUniqueNotes() {
        List<String> members = new ArrayList<>();
        for (String mus : musicNotes) {
            String[] mem = mus.split(" ");
            String noteSplit = mem[0];
            members.add(noteSplit);
        }
        HashSet<String> unique = new HashSet<>(members);
        return unique;
    }
}
