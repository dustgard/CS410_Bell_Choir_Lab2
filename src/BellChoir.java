import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Queue;

/**
 * BellChoir Lab
 * The purpose of this lab was to teach that the program does not control the order in which threads run
 * unless designed to do this, but by the OS.
 * The BellChoir Lab is a multithreading program that plays a songs from a file.
 * The file is passed to the program by using ANT from the command line.
 * Once the file is passed in the program, then parse the file into a playable song after validation of the proper
 * file format.
 * The program simulates a conductor signaling each member that has been assigned a bell representing a note
 * from the file, to play at the appropriate time and length according to the song.
 */
public class BellChoir {

    public static void main(String[] args) throws LineUnavailableException, IOException {
        String[] note = args;
        SongNotes notesPassed = new SongNotes(note);
        if (!notesPassed.validateFile(note)) {
            return;
        }
        Queue<BellNote> notes = notesPassed.getMusicNotes();
        HashSet<String> uniqueNotes = notesPassed.getUniqueNotes();
        ChoirConductor conductor = new ChoirConductor(notes, uniqueNotes);
        conductor.assignNotes();
        conductor.playSong();
    }
}
