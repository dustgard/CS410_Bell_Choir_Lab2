import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SongNotes {
    private final String songLocation;
    private final List<String> musicNotes = new ArrayList<>();

    public SongNotes(String songLocation) {
        this.songLocation = songLocation;
    }

    public void readFile() {
        File songFile = new File(songLocation);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(songFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        while (true) {
            String note;
            try {
                if ((note = reader.readLine()) == null) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            musicNotes.add(note);
        }
    }

    public List<String> getMusicNotes() {
        return musicNotes;
    }

    public HashSet<String> getUniqueNotes() {
        HashSet<String> unique = new HashSet<>(musicNotes);
        return unique;
    }

    public int getValueFromNote(String noteAndOctave) {
        // I got this from here:
        // http://www.harmony-central.com/MIDI/Doc/table2.html
        if (!noteAndOctave
                .matches("^(C|C#|D|D#|E|F|F#|G|G#|A|A#|B)(-1|[0-9])$")) {
            System.out.println("note " + noteAndOctave
                    + " is not a muscial note.");
            System.exit(1);
        }

        char note = noteAndOctave.charAt(0);

        int noteValue = -100;

        switch (note) {
            case 'C':
                noteValue = 0;
                break;
            case 'D':
                noteValue = 2;
                break;
            case 'E':
                noteValue = 4;
                break;
            case 'F':
                noteValue = 5;
                break;
            case 'G':
                noteValue = 7;
                break;
            case 'A':
                noteValue = 9;
                break;
            case 'B':
                noteValue = 11;
                break;
            default:
                System.out.println("This should never be reached.");
                System.exit(1);
                break;
        }

        boolean sharp = noteAndOctave.contains("#");

        // if it's sharp, the note value goes up by one
        if (sharp) {
            //System.out.println("There is a sharp");
            noteValue += 1;
        }

        //System.out.println("notevalue: " + noteValue);

        int octaveIndex = -100;

        // set the starting index for the octave
        if (sharp)
            octaveIndex = 2;
        else
            octaveIndex = 1;

        //System.out.println("octaveIndex is " + octaveIndex);

        int octave = -100;

        try {
            // figure out the octave
            octave = Integer.parseInt(noteAndOctave.substring(octaveIndex));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        //System.out.println("octave is " + octave);

        return (octave + 1) * 12 + noteValue;
    }


}
