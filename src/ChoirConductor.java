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

    public void assignNotes() throws InterruptedException, LineUnavailableException {
        for (String notes : unique) {
            ChoirMember choirMember = new ChoirMember(notes);

            choirMembers.put(notes, choirMember);
        }
    }

    public void playSong() throws LineUnavailableException, InterruptedException {
        final AudioFormat af =
                new AudioFormat(Note.SAMPLE_RATE, 8, 1, true, false);
        tone = new Tone(af);
        line = AudioSystem.getSourceDataLine(af);
        line.open();
        line.start();
//        bellNotes.add(new BellNote(Note.REST, NoteLength.QUARTER));
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
//            BellNote noteToPlay = bellNotes.poll();
//            BellNote noteToPlay1 = bellNotes.poll();
//            ChoirMember notePlayer = choirMembers.get(noteToPlay1.note.name().substring(0,1));
//            ChoirMember notePlayer2 = choirMembers.get("F");
//            notePlayer.notesTurn(noteToPlay);
//            notePlayer2.notesTurn(noteToPlay1);
//            notePlayer.warmUp();
//            notePlayer2.warmUp();
//            System.out.println(notePlayer.isTimeToPlay());
//            System.out.println(notePlayer2.isTimeToPlay());
        while (!bellNotes.isEmpty()) {
            BellNote noteToPlay = bellNotes.poll();
            if (!noteToPlay.note.name().equals("REST")) {
                ChoirMember notePlayer = choirMembers.get(noteToPlay.note.name().substring(0, 1));
                notePlayer.notesTurn(noteToPlay);
                if(!notePlayer.thread.isAlive()){
                    notePlayer.warmUp();
                }
                System.out.println("running loop");
            }
        }
        line.drain();
        songStillPlaying = false;
    }

    private class ChoirMember implements Runnable {
        private Thread thread;
        private volatile boolean timeToPlay = false;
        private BellNote noteToPlay;
        private Thread lockOwner;
        ChoirMember(String note) throws LineUnavailableException {
            thread = new Thread(this, note);


        }

        public void notesTurn(BellNote note) throws LineUnavailableException {
            timeToPlay = true;
            noteToPlay = note;
        }

        public Thread getThread() {
            return thread;
        }

        public void setThread(Thread thread) {
            this.thread = thread;
        }

        public boolean isTimeToPlay() {
            return timeToPlay;
        }

        public void setTimeToPlay(boolean timeToPlay) {
            this.timeToPlay = timeToPlay;
        }

        public BellNote getNoteToPlay() {
            return noteToPlay;
        }

        public void setNoteToPlay(BellNote noteToPlay) {
            this.noteToPlay = noteToPlay;
        }

        public Thread getLockOwner() {
            return lockOwner;
        }

        public void setLockOwner(Thread lockOwner) {
            this.lockOwner = lockOwner;
        }

        public void warmUp() throws InterruptedException {
            thread.start();
        }

        public synchronized void playNote() throws LineUnavailableException, InterruptedException {
            while (!timeToPlay) {
                try {
                    System.out.println("Waiting");
                    this.wait();
                } catch (InterruptedException ignore) {}
            }
            System.out.println("Got to the play note");
            tone.playNote(line, noteToPlay);

        }

        public synchronized void finishedNote(){
            this.notifyAll();
        }
        public void run() {
            while(songStillPlaying){
                System.out.println("running");
                System.out.println(Thread.currentThread().getName());
                try {
                    playNote();
                    timeToPlay = false;
                    System.out.println("passed PlayNote");
                } catch (LineUnavailableException | InterruptedException e) {
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


