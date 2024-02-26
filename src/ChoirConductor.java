import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ChoirConductor implements Runnable {

    private int tempo = 800;
    private final List<Integer> songNotes;
    private final HashSet<String> unique;
    private final ArrayList<Thread> choirMembers = new ArrayList<>();
    private String note;
    private Integer intNote;
    private final List<String> songChords;

    public ChoirConductor(List<Integer> song, List<String> songLetters, HashSet<String> uniqueNotes) {
        songNotes = song;
        unique = uniqueNotes;
        songChords = songLetters;
        intNote = songNotes.removeFirst();
        note = songChords.removeFirst();

    }


    public void assignNotes() {
        for (String notes : unique) {
            Thread th = new Thread(this, notes);
            System.out.println(th.getName());
            choirMembers.add(th);

        }
    }

    public void playSong() {
        for (Thread choirMember : choirMembers) {
            choirMember.start();
        }

    }

    public int stepLength(String note){
        int l = Integer.valueOf(note.substring(3));
        switch (l){
            case 4:
                return 500;
            case 3:
                return 1000;
            case 2:
                return 1500;
            case 1:
                return 2000;
        }
        return tempo;
    }

    public synchronized void playNote() {
        try {
            Synthesizer synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            MidiChannel[] channel = synthesizer.getChannels();
                channel[0].noteOn(intNote, 50);
                try {
                    Thread.sleep(stepLength(note));
                } catch (InterruptedException e) {
                } finally {
                    channel[0].noteOff(intNote);
                }
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
        if(!songChords.isEmpty()) {
            intNote = songNotes.removeFirst();
            note = songChords.removeFirst();
        }
    }

    public void run() {
        while (!songChords.isEmpty()) {
                if (note.equals(Thread.currentThread().getName())) {
                    playNote();
                }
            }
        if (note.equals(Thread.currentThread().getName())) {
                playNote();
        }
        }
    }
