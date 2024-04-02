import java.io.*;
import java.util.*;

public class SongNotes {
    private final String songLocation;
    private final List<String> musicNotes = new ArrayList<>();
    private final Queue<BellNote> bellNotes = new LinkedList<>();

    public SongNotes(String[] songLocation) {
        this.songLocation = songLocation[0];
    }

    public boolean validateFile(String[] filename) throws IOException {

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
        System.out.println("File format provided: [" + format + "]");
        if (!format.equals("txt")) {
            System.out.println("File format provided is not the correct type: [" + format + "]. Provide txt");
            return false;
        }

        if (!validateNotes()) {
            return false;
        }

        if (!validateMusicNotes()) {
            return false;
        } else {
            return format.equals("txt");
        }

    }

    public boolean validateNotes() throws IOException {
        File songFile = new File(songLocation);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(songFile));
        } catch (FileNotFoundException ignore) {
            //Handle the exception

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
                if(noteSplit.length <2 || noteSplit.length >2){
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
        reader.close();
        if (noteCount != goodNoteCount) {
            System.out.println("File notes are not correct");
            return false;

        }
        System.out.println("True");
        return true;
    }

    public boolean validateMusicNotes() {
        bellNotes.add(new BellNote(Note.REST, NoteLength.QUARTER));
        for (String note : musicNotes) {
            String[] split = note.split("\\s+");
            NoteLength l = null;
            switch (split[1]) {
                case "1":
                    l = NoteLength.WHOLE;
                    break;
                case "2":
                    l = NoteLength.HALF;
                    break;
                case "4":
                    l = NoteLength.QUARTER;
                    break;
                case "8":
                    l = NoteLength.EIGHTH;
                    break;
                default:
                    System.out.println("Note length wrong");
                    return false;
            }
            bellNotes.add(new BellNote(Note.valueOf(split[0]), l));
        }
        return true;
    }

    // Take String to notes first
    // Tic has validation
    public Queue<BellNote> getMusicNotes() {
        return bellNotes;
    }

    public HashSet<String> getUniqueNotes() {
        List<String> members = new ArrayList<>();
        for (String mus : musicNotes) {
            String[] mem = mus.split(" ");
            String noteSplit = mem[0];
            members.add(noteSplit);
        }
        HashSet<String> unique = new HashSet<>(members);
        // Too many times with the data when can be done once. Map is better for this. Less work.
        return unique;
    }
}
