import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;

/**
 * The ChoirConductor Class: This class is the thread that controls when the ChoirMembers play.
 * It is responsible for creating a thread for each unique note in the song that is provided by the user.
 * When the Conductor thread starts, it reads the first BellNote and sends it to the ChoirMember to play.
 * Once the ChoirMember is done playing the note, the Conductor then sends the next note to the correct thread
 * depending on the designated ChoirMember assigned BellNote.
 * When the song is finished, the Conductor thread ends.
 * Author: Dustin Gardner
 */

public class ChoirConductor implements Runnable {
    public static boolean songStillPlaying = true;
    public static boolean conductorSignal;
    private final Map<String, ChoirMember> choirMembers = new HashMap();
    private final HashSet<String> unique;
    private final Queue<BellNote> bellNotes;
    private final Thread conductor;
    private Tone tone;
    private SourceDataLine line = null;

    /**
     * The constructor takes two parameters that give the necessary information to play the notes and
     * create the required threads.
     * @param songLetters is are the BellNotes that are needed to play the song.
     * @param uniqueNotes is used to create a thread based on the unique notes of the song that is played. These are
     *                    the treads that are going to be playing the notes.
     */
    public ChoirConductor(Queue<BellNote> songLetters, HashSet<String> uniqueNotes) {
        unique = uniqueNotes;
        bellNotes = songLetters;
        final AudioFormat af =
                new AudioFormat(Note.SAMPLE_RATE, 8, 1, true, false);
        try {
            tone = new Tone(af);
        } catch (LineUnavailableException e) {
            System.err.println(e + "AudioFormat creation failed and can not be passed to Tone");
        }
        try {
            line = AudioSystem.getSourceDataLine(af);
        } catch (LineUnavailableException e) {
            System.err.println(e + "AudioFormat creation failed");
        }
        try {
            line.open();
        } catch (LineUnavailableException e) {
            System.err.println(e + "No line to open");
        }
        line.start();
        conductor = new Thread(this, "Conductor");
        conductorSignal = true;
    }

    /**
     * The method takes the unique notes passed to the constructor and created a thread for each one setting the
     * thread name to the notes that they will play.
     * Then prints to the console to inform the user how many threads are going to play notes.
     */
    public void assignNotes() {
        for (String notes : unique) {
            ChoirMember choirMember = new ChoirMember(notes);
            choirMembers.put(notes, choirMember);
            System.out.println(notes);
        }
    }

    /**
     * Used to start the conductor thread effectively starting the song with the run method.
     */
    public void playSong() {
        System.out.println("Starting conductor");
        conductor.start();
    }

    /**
     * This method is used to grab the next note from the bellNotes Queue and tell which ChoirMember to start to play
     * a note based on the parameters passed to them.
     * The method checks to see if there are any notes left to play then sends the correct note using the note name
     * grabbed to the matching thread name.
     * The user is informed which note is being passed to what thread.
     * If there is no note left to play the line drains, ChoirMember are told to stop playing and the user is informed
     * the song is finished playing.
     * The line is closed at the very end.
     */
    private void memberPlayNote() {
        if (songStillPlaying) {
            if (!bellNotes.isEmpty()) {
                BellNote noteToPlay = bellNotes.poll();
                // The first note is a REST, and there is no thread named REST, so it is sent to the A5 ChoirMember.
                if (noteToPlay.note.name().equals("REST")) {
                    ChoirMember notePlayer = choirMembers.get("A5");
                    System.out.println(Thread.currentThread().getName() + ": Sending Member " + notePlayer.thread.getName() + " Note [" + noteToPlay.note.name() + "]");
                    notePlayer.notesTurn(noteToPlay, tone, line);
                }
                // Splits the BellNote to then match the note name to the thread name.
                String[] noteSplit = noteToPlay.note.name().split(" ");
                ChoirMember notePlayer = choirMembers.get(noteSplit[0]);
                if (notePlayer != null) {
                    System.out.println(Thread.currentThread().getName() + ": Sending Member " + notePlayer.thread.getName() + " Note [" + noteToPlay.note.name() + "]");
                    notePlayer.notesTurn(noteToPlay, tone, line);
                }
            } else {
                songStillPlaying = false;
                line.drain();
                for (ChoirMember member : choirMembers.values()) {
                    member.memberStop();
                }
                System.out.println("Song Finished");
                line.close();
            }
        }
    }

    /**
     * The run method is required for all threads and is controlling the song being played by calling the memberPlayNote
     * method each time after the previous note is done playing.
     * The run method continues in a loop until the songStillPlaying variable is set to false.
     * When this variable is set false, the song is over and the program will exit.
     */
    @Override
    public void run() {
        synchronized (this) {
            do {
                memberPlayNote();
                notify();
            } while (songStillPlaying);
            System.out.println("Conductor took a bow");
        }
    }
}
