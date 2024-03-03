import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.util.ArrayList;
import java.util.List;

enum NoteLength {
    WHOLE(1.0f),
    HALF(0.5f),
    QUARTER(0.25f),
    EIGHTH(0.125f);

    private final int timeMs;

    NoteLength(float length) {
        timeMs = (int) (length * Note.MEASURE_LENGTH_SEC * 1000);
    }

    public int timeMs() {
        return timeMs;
    }
}


enum Note {
    // REST Must be the first 'Note'

    REST,
    A4,
    A4S,
    B4,
    C4,
    C4S,
    D4,
    D4S,
    E4,
    F4,
    F4S,
    G4,
    G4S,
    A5;

//    REST,
//    A4,
//    A4S,
//    A4F,
//    B4,
//    B4S,
//    B4F,
//    C4,
//    C4S,
//    C4F,
//    D4,
//    D4S,
//    E4,
//    E4S,
//    E4F,
//    F4,
//    F4S,
//    F4F,
//    G4,
//    G4S,
//    G4F,
//    A5,
//    A5S,
//    A5F,
//    B5,
//    B5S,
//    B5F,
//    C5,
//    C5S,
//    C5F,
//    D5,
//    D5S,
//    D5F,
//    E5,
//    E5S,
//    E5F,
//    F5,
//    F5S,
//    F5F,
//    G5,
//    G5S;

    public static final int SAMPLE_RATE = 48 * 1024; // ~48KHz
    public static final int MEASURE_LENGTH_SEC = 1;

    // Circumference of a circle divided by # of samples
    private static final double step_alpha = (2.0d * Math.PI) / SAMPLE_RATE;

    private final double FREQUENCY_A_HZ = 440.0d;
    private final double MAX_VOLUME = 127.0d;


    private final byte[] sinSample = new byte[MEASURE_LENGTH_SEC * SAMPLE_RATE];

    private Note() {
        int n = this.ordinal();
        if (n > 0) {
            // Calculate the frequency!
            final double halfStepUpFromA = n - 1;
            final double exp = halfStepUpFromA / 12.0d;
            final double freq = FREQUENCY_A_HZ * Math.pow(2.0d, exp);

            // Create sinusoidal data sample for the desired frequency
            final double sinStep = freq * step_alpha;
            for (int i = 0; i < sinSample.length; i++) {
                sinSample[i] = (byte) (Math.sin(i * sinStep) * MAX_VOLUME);
            }
        }
    }

    public byte[] sample() {
        return sinSample;
    }
}

public class Tone {

    // Mary had a little lamb
    private static final List<BellNote> song = new ArrayList<>() {{
        add(new BellNote(Note.A5, NoteLength.QUARTER));
        add(new BellNote(Note.G4, NoteLength.QUARTER));
        add(new BellNote(Note.F4, NoteLength.QUARTER));
        add(new BellNote(Note.G4, NoteLength.QUARTER));

        add(new BellNote(Note.A5, NoteLength.QUARTER));
        add(new BellNote(Note.A5, NoteLength.QUARTER));
        add(new BellNote(Note.A5, NoteLength.HALF));

        add(new BellNote(Note.G4, NoteLength.QUARTER));
        add(new BellNote(Note.G4, NoteLength.QUARTER));
        add(new BellNote(Note.G4, NoteLength.HALF));

        add(new BellNote(Note.A5, NoteLength.QUARTER));
        add(new BellNote(Note.A5, NoteLength.QUARTER));
        add(new BellNote(Note.A5, NoteLength.HALF));

        add(new BellNote(Note.A5, NoteLength.QUARTER));
        add(new BellNote(Note.G4, NoteLength.QUARTER));
        add(new BellNote(Note.F4, NoteLength.QUARTER));
        add(new BellNote(Note.G4, NoteLength.QUARTER));

        add(new BellNote(Note.A5, NoteLength.QUARTER));
        add(new BellNote(Note.A5, NoteLength.QUARTER));
        add(new BellNote(Note.A5, NoteLength.QUARTER));
        add(new BellNote(Note.A5, NoteLength.QUARTER));

        add(new BellNote(Note.G4, NoteLength.QUARTER));
        add(new BellNote(Note.G4, NoteLength.QUARTER));
        add(new BellNote(Note.A5, NoteLength.QUARTER));
        add(new BellNote(Note.G4, NoteLength.QUARTER));

        add(new BellNote(Note.F4, NoteLength.WHOLE));
    }};
    private final AudioFormat af;
    private final SourceDataLine line;
    private final Object playLock = new Object();
    public List<BellNote> testList = new ArrayList<>();
    private Object lock = new Object();
    private boolean notePlaying = false;

    Tone(AudioFormat aff) throws LineUnavailableException {
        this.af = aff;
        line = AudioSystem.getSourceDataLine(af);
    }

    public static void main(String[] args) throws Exception {
        final AudioFormat af =
                new AudioFormat(Note.SAMPLE_RATE, 8, 1, true, false);
        Tone t = new Tone(af);
        t.playSong(song);
    }

    void playSong(List<BellNote> song) throws LineUnavailableException {
        try (final SourceDataLine line = AudioSystem.getSourceDataLine(af)) {
            line.open();
            line.start();
            for (BellNote bn : song) {
                playNote(line, bn);
            }
            line.drain();
        }
    }

    public void playNote(SourceDataLine line, BellNote bn) {
        testList.add(bn);
        notePlaying = true;
        final int ms = Math.min(bn.length.timeMs(), Note.MEASURE_LENGTH_SEC * 1000);
        final int length = Note.SAMPLE_RATE * ms / 1000;
        line.write(bn.note.sample(), 0, length);
        line.write(Note.REST.sample(), 0, 50);

    }

    public List<BellNote> testReturn() {
        return testList;
    }
}


class BellNote {
    final Note note;
    final NoteLength length;

    BellNote(Note note, NoteLength length) {
        this.note = note;
        this.length = length;
    }
}