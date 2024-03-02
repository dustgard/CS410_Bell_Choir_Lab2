import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.util.*;

public class ChoirConductor {
    private final Map<String, ChoirMember> choirMembers = new HashMap();
    private final Queue<BellNote> bellNotes = new LinkedList<>();
    private Tone tone;
    private HashSet<String> unique;
    private List<String> songChords;
    private SourceDataLine line;
    private boolean songStillPlaying = true;



    public ChoirConductor(List<String> songLetters, HashSet<String> uniqueNotes) throws LineUnavailableException {
        unique = uniqueNotes;
        songChords = songLetters;
        final AudioFormat af =
                new AudioFormat(Note.SAMPLE_RATE, 8, 1, true, false);
        tone = new Tone(af);
        line = AudioSystem.getSourceDataLine(af);
        line.open();
        line.start();
    }

    public void assignNotes(){
        for (String notes : unique) {
            ChoirMember choirMember = new ChoirMember(notes);
            choirMembers.put(notes, choirMember);
        }
    }

    public void playSong(){
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
            System.out.println("in loop");
            BellNote noteToPlay = bellNotes.poll();
            System.out.println(noteToPlay.note.name());
                ChoirMember notePlayer = choirMembers.get(noteToPlay.note.name().substring(0, 1));
                System.out.println(notePlayer);
                notePlayer.setPlayed();
                notePlayer.notesTurn(noteToPlay);
                System.out.println("running loop");
        }
        line.drain();
        songStillPlaying = false;
    }

    private class ChoirMember implements Runnable {
        private Thread thread;
        private BellNote bellNote;
        private boolean played = false;
        private Object lock = new Object();

        ChoirMember(String note){
            thread = new Thread(this, note);
//            warmUp();

        }

        public void warmUp(){
            thread.start();
        }

        public void play() throws LineUnavailableException {
            tone.playNote(line, bellNote);
            played = false;
        }

        public void notesTurn(BellNote note) {
            System.out.println(note.note.name());
            synchronized (lock){
                while(!played);
                try {
                    System.out.println("Waiting");
                    lock.wait();
                } catch (InterruptedException ignored){
                }
            }
            System.out.println("Running");
            bellNote = note;
            setPlayed();
        }

        public void setPlayed() {
            synchronized (lock) {
                played = true;
                lock.notifyAll();
            }
        }

        public void run() {
            while(songStillPlaying) {
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


