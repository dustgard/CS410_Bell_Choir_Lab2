import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;


public class ChoirMember implements Runnable {
    private SourceDataLine line;
    private Thread thread;
    private String noteAssigned;
    private BellNote bellNote;
    private SourceDataLine audioLine;
    private Tone tones;

    public ChoirMember(String note) {

        thread = new Thread(this, note);
        noteAssigned = note;
    }

    public void notesTurn(SourceDataLine line, BellNote note, Tone tone) throws LineUnavailableException {
        audioLine = line;
        bellNote = note;
        tones = tone;
        tone.playNote(line, bellNote);
    }


    public void warmUp() {
        thread.start();
    }

    public void run() {
    }
}
