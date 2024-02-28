import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.util.*;

public class ChoirConductor implements Runnable {
    private final HashSet<String> unique;
    private final List<Thread> choirMembers = new ArrayList<>();
    private final List<String> songChords;
    private final Queue<BellNote> bellNotes = new LinkedList<>();
    private BellNote bellNote;
    private Tone tone;
    private SourceDataLine line;
    public ChoirConductor(List<String> songLetters, HashSet<String> uniqueNotes) {
        unique = uniqueNotes;
        songChords = songLetters;
    }


    public void assignNotes() {
        for (String notes : unique) {
            Thread th = new Thread(this, notes);
            System.out.println(th.getName());
            choirMembers.add(th);
        }
    }

    public void playSong() throws LineUnavailableException {
        final AudioFormat af =
                new AudioFormat(Note.SAMPLE_RATE, 8, 1, true, false);
        tone = new Tone(af);
            line = AudioSystem.getSourceDataLine(af);
            line.open();
            line.start();


        for(String note : songChords) {
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
            System.out.println(l);
            bellNotes.add(new BellNote(Note.valueOf(split[0]), l));
        }
        bellNotes.add(new BellNote(Note.REST,NoteLength.QUARTER));
        bellNote = bellNotes.poll();
        for (Thread choirMember : choirMembers) {
            choirMember.start();
        }

    }

    public void run() {
        while (!bellNotes.isEmpty()) {
            if (bellNote.note.name().substring(0,1).equals(Thread.currentThread().getName())) {
                try {
                    tone.playNote(line,bellNote);
                } catch (LineUnavailableException e) {
                    throw new RuntimeException(e);
                }
                bellNote = bellNotes.poll();
            }
        }
        if(bellNote.note.name().substring(0,1).equals(Thread.currentThread().getName())) {
            try {
                tone.playNote(line, bellNote);
            } catch (LineUnavailableException e) {
                throw new RuntimeException(e);
            }
        }
        line.drain();
    }
}
