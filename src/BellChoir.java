import javax.sound.sampled.LineUnavailableException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class BellChoir {



    public static void main(String[] args) throws LineUnavailableException{
        List<ChoirConductor> conductors = new ArrayList<>();
        SongNotes notesPassed = new SongNotes(args[0]);
        SongNotes notesPassed2 = new SongNotes(args[1]);
        notesPassed.readFile();
        notesPassed2.readFile();
        List<String> notes = notesPassed.getMusicNotes();
        List<String> notes2 = notesPassed2.getMusicNotes();
        HashSet<String> uniqueNotes = notesPassed.getUniqueNotes();
        HashSet<String> uniqueNotes2 = notesPassed2.getUniqueNotes();
        ChoirConductor conductor = new ChoirConductor(notes, uniqueNotes);
        ChoirConductor conductor2 = new ChoirConductor(notes2, uniqueNotes2);
        conductor.assignNotes();
        conductor2.assignNotes();
        conductors.add(conductor);
        conductors.add(conductor2);
        for (ChoirConductor cond : conductors){
            cond.playSong();
        }
    }
}
