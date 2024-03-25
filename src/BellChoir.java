import javax.sound.sampled.LineUnavailableException;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

public class BellChoir {

    public static void main(String[] args) throws LineUnavailableException, IOException {
        String[] note = args;
        SongNotes notesPassed = new SongNotes(note);
        if (!notesPassed.validateFile(note)) {
            return;
        }
        notesPassed.readFile();
        List<String> notes = notesPassed.getMusicNotes();
        HashSet<String> uniqueNotes = notesPassed.getUniqueNotes();
        ChoirConductor conductor = new ChoirConductor(notes, uniqueNotes);
        conductor.assignNotes();
        conductor.playSong();
    }
}
