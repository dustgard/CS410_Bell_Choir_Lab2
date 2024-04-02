import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;

public class ChoirConductor implements Runnable {
    public static boolean songStillPlaying = true;
    public static boolean conductorSignal;
    private final Map<String, ChoirMember> choirMembers = new HashMap();
    private final Tone tone;
    private final HashSet<String> unique;
    private final Queue<BellNote> bellNotes;
    private final SourceDataLine line;
    private final Thread conductor;

    public ChoirConductor(Queue<BellNote> songLetters, HashSet<String> uniqueNotes) throws LineUnavailableException {
        unique = uniqueNotes;
        bellNotes = songLetters;
        final AudioFormat af =
                new AudioFormat(Note.SAMPLE_RATE, 8, 1, true, false);
        tone = new Tone(af);
        line = AudioSystem.getSourceDataLine(af);
        line.open();
        line.start();
        conductor = new Thread(this, "Conductor");
        conductorSignal = true;
    }

    public void assignNotes() {
        for (String notes : unique) {
            ChoirMember choirMember = new ChoirMember(notes);
            choirMembers.put(notes, choirMember);
        }
    }

    public void playSong() {
        System.out.println("Starting conductor");
        conductor.start();
    }

    private void memberPlayNote() throws LineUnavailableException, InterruptedException {
        if (songStillPlaying) {
            if (!bellNotes.isEmpty()) {
                BellNote noteToPlay = bellNotes.poll();
                if (noteToPlay.note.name().equals("REST")) {
                    ChoirMember notePlayer = choirMembers.get("A");
                    System.out.println(Thread.currentThread().getName() + ": Sending Member " + notePlayer.thread.getName() + " Note [" + noteToPlay.note.name() + "]");
                    notePlayer.notesTurn(noteToPlay, tone, line);
                }
                ChoirMember notePlayer = choirMembers.get(noteToPlay.note.name().substring(0, 1));
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

    @Override
    public void run() {
        synchronized (this) {
            do {
                while (!conductorSignal) {
                    try {
                        System.out.println(Thread.currentThread().getName() + " is Waiting");
                        wait();
                    } catch (InterruptedException e) {
                    }
                }
                try {
                    conductorSignal = false;
                    memberPlayNote();
                    notify();
                } catch (LineUnavailableException e) {
                } catch (InterruptedException e) {
                }
            } while (songStillPlaying);
            System.out.println("Conductor took a bow");

        }
    }
}
