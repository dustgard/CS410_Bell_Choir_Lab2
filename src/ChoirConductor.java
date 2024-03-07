import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.util.*;


public class ChoirConductor implements Runnable {
    private final Map<String, ChoirMember> choirMembers = new HashMap();
    private final Queue<BellNote> bellNotes = new LinkedList<>();
    private final Tone tone;
    private final HashSet<String> unique;
    private final List<String> songChords;
    private final SourceDataLine line;
    private final Thread conductor;
    public boolean songStillPlaying = true;
    private boolean conductorSignal;

    public ChoirConductor(List<String> songLetters, HashSet<String> uniqueNotes) throws LineUnavailableException {
        unique = uniqueNotes;
        songChords = songLetters;
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
//        bellNotes.add(new BellNote(Note.REST, NoteLength.QUARTER));
        for (String note : songChords) {
            String[] split = note.split("\\s+");
            NoteLength l = null;
            switch (split[1]) {
                case "1":
                    l = NoteLength.WHOLE;
                    break;
                case "2":
                    l = NoteLength.HALF;
                    break;
                case "4":
                    l = NoteLength.QUARTER;
                    break;
                case "8":
                    l = NoteLength.EIGHTH;
                    break;
            }
            bellNotes.add(new BellNote(Note.valueOf(split[0]), l));
        }

        System.out.println("Starting conductor");
        conductor.start();
    }

    private void memberPlayNote() throws LineUnavailableException, InterruptedException {
        if (songStillPlaying) {
            if (!bellNotes.isEmpty()) {
                BellNote noteToPlay = bellNotes.poll();
                ChoirMember notePlayer = choirMembers.get(noteToPlay.note.name().substring(0, 1));
                if (notePlayer != null) {
                    System.out.println(Thread.currentThread().getName() + ": Sending Member " + notePlayer.thread.getName() + " Note [" + noteToPlay.note.name() + "]");
                    notePlayer.notesTurn(noteToPlay);
                }
            } else {
                System.out.println("Song Finished");
                songStillPlaying = false;
                line.drain();
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
        }
    }

public class ChoirMember implements Runnable {
    private final Thread thread;
    public boolean memberPlayingNote = false;
    private BellNote bellNote;


    ChoirMember(String note) {
        thread = new Thread(this, note);
        thread.start();

    }

    public synchronized void play() throws LineUnavailableException, InterruptedException {
        if (memberPlayingNote) {
            System.out.println("Member " + Thread.currentThread().getName() + " is Playing note [" + bellNote.note.name() + "]");
            tone.playNote(line, bellNote);
            memberPlayingNote = false;
        }
    }

    public synchronized void notesTurn(BellNote note) {
        synchronized (this) {
            memberPlayingNote = true;
            bellNote = note;
            notify();
            while (memberPlayingNote) {
                try {
                    wait();
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    public void run() {
        synchronized (this) {
            do {
                while (!memberPlayingNote) {
                    try {
                        System.out.println("Member " + Thread.currentThread().getName() + " is Waiting");
                        conductorSignal = true;
                        wait();
                    } catch (InterruptedException e) {
                    }
                }
                try {
                    System.out.println("Member " + Thread.currentThread().getName() + " is trying to play note [" + bellNote.note.name() + "]");
                    play();
                    notify();
                } catch (LineUnavailableException e) {
                } catch (InterruptedException e) {
                }

            } while (songStillPlaying);
        }
    }
}
}

