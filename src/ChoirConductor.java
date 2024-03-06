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
    private  Object conductorSignal = new Object();

    public ChoirConductor(List<String> songLetters, HashSet<String> uniqueNotes) throws LineUnavailableException {
        unique = uniqueNotes;
        songChords = songLetters;
        final AudioFormat af =
                new AudioFormat(Note.SAMPLE_RATE, 8, 1, true, false);
        tone = new Tone(af);
        line = AudioSystem.getSourceDataLine(af);
        line.open();
        line.start();
        conductor = new Thread(this, "conductor");
    }

    public void setWaitConductorSignal() throws InterruptedException {
        synchronized (conductorSignal){
            try{
                wait();
            } catch (InterruptedException e) {

            }
        }

    }

    public void assignNotes() {
        for (String notes : unique) {
            ChoirMember choirMember = new ChoirMember(notes);
            choirMembers.put(notes, choirMember);
        }
    }

    public void playSong() {
        bellNotes.add(new BellNote(Note.REST, NoteLength.QUARTER));
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
                        notePlayer.notesTurn(noteToPlay);
                    }
                    System.out.println(Thread.currentThread().getName());

                } else {
                    songStillPlaying = false;
                    line.drain();
                }
            }
    }

    @Override
    public void run() {
        while (songStillPlaying) {
            try {
                memberPlayNote();
                setWaitConductorSignal();

            } catch (LineUnavailableException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public class ChoirMember implements Runnable {
        private final Thread thread;
        public boolean memberPlayingNote = true;
        private boolean playLock = false;
        private BellNote bellNote;
        private Object memberLock = new Object();


        ChoirMember(String note) {
            thread = new Thread(this, note);
            this.warmUp();

        }

        public void warmUp() {
            thread.start();
        }

        public synchronized void play() throws LineUnavailableException, InterruptedException {
            if (playLock) {
                tone.playNote(line, bellNote);
                memberPlayingNote = false;
                playLock = false;

            }
        }

        public synchronized void notesTurn(BellNote note) {
            memberPlayingNote = true;
            bellNote = note;
            playLock = true;
        }

        public void run() {
            while (songStillPlaying) {
                try {
                    play();
                } catch (LineUnavailableException | InterruptedException e) {
                }
            }
        }

        public boolean isMemberPlayingNote() {
            return memberPlayingNote;
        }

    }
}

