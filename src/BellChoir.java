import javax.swing.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class BellChoir {

    public static void main(String[] args) {
        JFileChooser song = new JFileChooser("src/Songs");
        song.showOpenDialog(null);
        String songLocation = song.getSelectedFile().getAbsolutePath();
        SongNotes notesPassed = new SongNotes(songLocation);
        notesPassed.readFile();
        List<String> notes = notesPassed.getMusicNotes();
        List<Integer> notess = new ArrayList();
        for (String n : notes) {
            String sub = n.substring(0, 2);
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


