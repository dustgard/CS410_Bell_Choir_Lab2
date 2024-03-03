import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class ChoirConductor implements Runnable {
    private final Map<String, ChoirMember> choirMembers = new HashMap();
    private final Queue<BellNote> bellNotes = new LinkedList<>();
    private final Tone tone;
    private final HashSet<String> unique;
    private final List<String> songChords;
    private final SourceDataLine line;
    private final Thread conductor;
    public boolean songStillPlaying = true;
    public boolean stillPlayingNote = false;
    public Lock conductorLock = new ReentrantLock();


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
        conductorLock.lock();
        try {

            if (songStillPlaying) {
                if (!bellNotes.isEmpty()) {
                    BellNote noteToPlay = bellNotes.poll();
                    ChoirMember notePlayer = choirMembers.get(noteToPlay.note.name().substring(0, 1));
                    if(notePlayer != null) {
                        notePlayer.notesTurn(noteToPlay);
                        while (notePlayer.isMemberPlayingNote()) {
                            Thread.sleep(1);
                        }
                    }

                } else {
                    songStillPlaying = false;
                    line.drain();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            conductorLock.unlock();
        }
    }

    @Override
    public void run() {
        while (songStillPlaying) {
            try {
                memberPlayNote();
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
        private boolean memberLock = false;
        private boolean playLock = false;
        private BellNote bellNote;


        ChoirMember(String note) {
            thread = new Thread(this, note);
            this.warmUp();

        }

        public void warmUp() {
            thread.start();
        }

        public synchronized void play() throws LineUnavailableException {
            if (playLock) {
                System.out.println(Thread.currentThread() + " " + bellNote.note.name());
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
                } catch (LineUnavailableException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        public boolean isMemberPlayingNote() {
            return memberPlayingNote;
        }

    }
}


//    public void run() {
//        while (!bellNotes.isEmpty()) {
//            if (bellNote.note.name().substring(0,1).equals(Thread.currentThread().getName())) {
//                try {
//                    tone.playNote(line,bellNote);
//                } catch (LineUnavailableException e) {
//                    throw new RuntimeException(e);
//                }
//                bellNote = bellNotes.poll();
//            }
//        }
//        if(bellNote.note.name().substring(0,1).equals(Thread.currentThread().getName())) {
//            try {
//                tone.playNote(line, bellNote);
//            } catch (LineUnavailableException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        line.drain();
//    }

