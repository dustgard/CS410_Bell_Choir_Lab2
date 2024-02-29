import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;
import java.util.HashSet;
import java.util.List;


public class BellChoir {

    public static void main(String[] args) throws LineUnavailableException, InterruptedException {

//
        SongNotes notesPassed = new SongNotes(args[0]);
        notesPassed.readFile();
        List<String> notes = notesPassed.getMusicNotes();
        HashSet<String> uniqueNotes = notesPassed.getUniqueNotes();
        ChoirConductor conductor = new ChoirConductor(notes, uniqueNotes);
        conductor.assignNotes();
        conductor.playSong();
    }
}
