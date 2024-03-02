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
            }
            bellNotes.add(new BellNote(Note.valueOf(split[0]), l));
        }

        System.out.println("Starting conductor");
        conductor.start();
    }

    private void memberPlayNote() throws LineUnavailableException, InterruptedException {
        System.out.println("memberPlayNote");
        while (songStillPlaying) {
            if (!bellNotes.isEmpty()) {
                BellNote noteToPlay = bellNotes.poll();
                System.out.println(noteToPlay.note.name());
                ChoirMember notePlayer = choirMembers.get(noteToPlay.note.name().substring(0, 1));
                notePlayer.notesTurn(noteToPlay);
                notePlayer.setPlayed();
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
            } catch (LineUnavailableException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public class ChoirMember implements Runnable {
        private final Object memberLock = new Object();
        private final Thread thread;
        private BellNote bellNote;
        private boolean playing = true;


        ChoirMember(String note) {
            thread = new Thread(this, note);
            this.warmUp();

        }

        public void warmUp() {
            thread.start();
        }

        public void play() throws LineUnavailableException {
            synchronized (memberLock) {
                System.out.println("notesTurn");
                while (playing) {
                    try {
                        System.out.println("Waiting");
                        memberLock.wait();
                    } catch (InterruptedException ignored) {
                        System.out.println("error");
                    }
                }
                System.out.println("Acquired");
                System.out.println(Thread.currentThread().getName());
                System.out.println(bellNote.note.name());
                tone.playNote(line, bellNote);
                playing = true;
            }
        }

        public void notesTurn(BellNote note) throws LineUnavailableException {
            bellNote = note;
        }

        public void setPlayed() {
            System.out.println("setPlayed");
            synchronized (memberLock) {
                playing = false;
                memberLock.notifyAll();
            }
        }

        public void run() {
            while (songStillPlaying) {
                System.out.println("Note trying to be played#######################################################");
                try {
                    play();
                } catch (LineUnavailableException e) {
                    throw new RuntimeException(e);
                }
            }
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


