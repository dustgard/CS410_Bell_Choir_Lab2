import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import java.util.*;
import java.util.regex.Pattern;

public class BellChoir {

    private static final Map<String, Integer> _baseMap = new HashMap<>();
    private static final Pattern NOTE_FORMAT = Pattern
            .compile("([A-G])([#b]?)(-?\\d{1,2})");

    public static void main(String[] args) {
        JFileChooser song = new JFileChooser("src/Songs");
        song.showOpenDialog(null);
        String songLocation = song.getSelectedFile().getAbsolutePath();
        SongNotes notesPassed = new SongNotes(songLocation);
        notesPassed.readFile();
        List<String> notes = notesPassed.getMusicNotes();
        List<Integer> notess = new ArrayList();
        for(String n : notes){
            String sub = n.substring(0,2);
            int f = notesPassed.getValueFromNote(sub);
            System.out.println(f);
            notess.add(f);
        }
        HashSet<String> uniqueNotes = notesPassed.getUniqueNotes();
        System.out.println(notes);
        ChoirConductor conductor = new ChoirConductor(notess, notes, uniqueNotes);
        conductor.assignNotes();
        conductor.playSong();
    }

    }


