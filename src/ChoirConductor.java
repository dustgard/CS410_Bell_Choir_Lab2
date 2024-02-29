import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.util.*;

public class ChoirConductor {
    private final Map<String, ChoirMember> choirMembers = new HashMap();
    private final Queue<BellNote> bellNotes = new LinkedList<>();
    public Tone tone;
    private HashSet<String> unique;
    private List<String> songChords;
    private BellNote bellNote;
    private SourceDataLine line;
    private String noteAssigned;
    private boolean songStillPlaying = true;


    public ChoirConductor(List<String> songLetters, HashSet<String> uniqueNotes) {
        unique = uniqueNotes;
        songChords = songLetters;
    }

    public void assignNotes() throws InterruptedException {
        for (String notes : unique) {
            ChoirMember choirMember = new ChoirMember(notes);
            choirMember.warmUp();
            choirMembers.put(notes, choirMember);
        }
    }

    public void playSong() throws LineUnavailableException {
        final AudioFormat af =
                new AudioFormat(Note.SAMPLE_RATE, 8, 1, true, false);
        tone = new Tone(af);
        line = AudioSystem.getSourceDataLine(af);
        line.open();
        line.start();
        bellNotes.add(new BellNote(Note.REST, NoteLength.QUARTER));
        for (String note : songChords) {
            String split[] = note.split("\\s+");
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

        while (!bellNotes.isEmpty()) {
            BellNote noteToPlay = bellNotes.poll();
            if (!noteToPlay.note.name().equals("REST")) {
                ChoirMember notePlayer = choirMembers.get(noteToPlay.note.name().substring(0, 1));
                notePlayer.notesTurn(line, noteToPlay);

            }
        }
        songStillPlaying = false;
    }

    private class ChoirMember implements Runnable {
        private Thread thread;
        private volatile boolean timeToPlay = false;
        private BellNote noteToPlay;
        private Thread lockOwner;
        ChoirMember(String note) {
            thread = new Thread(this, note);
            noteAssigned = note;

        }

        public void notesTurn(SourceDataLine line, BellNote note) throws LineUnavailableException {
            timeToPlay = true;
            noteToPlay = note;
        }


        public void warmUp() throws InterruptedException {
            thread.start();
        }

        public synchronized void playNote() throws LineUnavailableException {
            while (!timeToPlay) {
                try {
                    System.out.println("Waiting");
                    this.wait();
                } catch (InterruptedException ignore) {}
            }
            System.out.println("Got to the play note");
//            tone.playNote(line, noteToPlay);
        }

        public synchronized void finishedNote(){
            timeToPlay = true;
            this.notifyAll();
        }
        public void run() {
            while(songStillPlaying){
                System.out.println("running");
                try {
                    playNote();
                    timeToPlay = false;
                    System.out.println("passed PlayNote");
                } catch (LineUnavailableException e) {
                }
                System.out.println("Stop");
                finishedNote();
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


