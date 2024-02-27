import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;
import java.util.HashSet;
import java.util.List;


public class BellChoir {

    public static void main(String[] args) throws LineUnavailableException {
        JFileChooser song = new JFileChooser("src/Songs");
        song.showOpenDialog(null);
        String songLocation = song.getSelectedFile().getAbsolutePath();
        SongNotes notesPassed = new SongNotes(songLocation);
        notesPassed.readFile();
        List<String> notes = notesPassed.getMusicNotes();
        HashSet<String> uniqueNotes = notesPassed.getUniqueNotes();
        ChoirConductor conductor = new ChoirConductor(notes, uniqueNotes);
        conductor.assignNotes();
        conductor.playSong();
    }
}


