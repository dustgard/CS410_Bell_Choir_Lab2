import java.util.HashSet;
import java.util.Queue;

/**
 * BellChoir Lab
 * The BellChoir Lab is a multithreading program that plays a songs from a file.
 * The file is passed to the program by using ANT from the command line.
 * Once the file is passed in the program, the file is parsed into a playable song after validation.
 * The program simulates a conductor signaling each member that has been assigned a bell representing a note
 * from the file, to play at the appropriate time and length according to the song.
 * Author: Dustin Gardner
 */
public class BellChoir {

    public static void main(String[] args) {
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
