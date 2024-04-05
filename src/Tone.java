import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * This is and an enumeration for the length of the notes and their corresponding time.
 */
enum NoteLength {
    WHOLE(1.0f),
    HALF(0.5f),
    THIRD(0.33f),
    QUARTER(0.25f),
    EIGHTH(0.125f),
    SIXTEENTH(0.065f);

    private final int timeMs;

    /**
     * This is used to calculate the note length into seconds.
     * @param length the time for the note to be played.
     */
    NoteLength(float length) {
        timeMs = (int) (length * Note.MEASURE_LENGTH_SEC * 1000);
    }

    /**
     * Time in milliseconds used to calculate how long to play the notes.
     * @return timeMs used for calculating the note length.
     */
    public int timeMs() {
        return timeMs;
    }
}

/**
 * Enumeration for what available Notes can be played.
 */
enum Note {
    // REST Must be the first 'Note'
    REST,
    A4,
    B4F,
    B4,
    C4,
    C4S,
    D4,
    E4F,
    E4,
    F4,
    F4S,
    G4,
    G4S,
    A5,
    B5F,
    B5,
    C5,
    D5F,
    D5,
    E5F,
    E5,
    F5,
    F5S,
    G5,
    G5S;

    // Magic
    public static final int SAMPLE_RATE = 48 * 1024; // ~48KHz
    public static final int MEASURE_LENGTH_SEC = 1;

    // Circumference of a circle divided by # of samples
    private static final double step_alpha = (2.0d * Math.PI) / SAMPLE_RATE;

    private final double FREQUENCY_A_HZ = 440.0d;
    private final double MAX_VOLUME = 127.0d;

    private final byte[] sinSample = new byte[MEASURE_LENGTH_SEC * SAMPLE_RATE];

    /**
     * Magic
     */
    Note() {
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

    /**
     * @return Magic
     */
    public byte[] sample() {
        return sinSample;
    }
}

/**
 * This class is used to convert the BellNote into audible sound.
 */
public class Tone {
    private AudioFormat af;
    private SourceDataLine line;

    /**
     * This method sets the AudioFormat that is going to be used and creates the line that music with be played on.
     * @param aff AudioFormat
     * @throws LineUnavailableException if the line is not created
     */
    Tone(AudioFormat aff) {
        this.af = aff;
        try {
            line = AudioSystem.getSourceDataLine(af);
        } catch (LineUnavailableException e) {
            System.err.println(e + "Can not create line");
        }
    }

    /**
     * This method takes the line that has been created and BellNote. It then writes it to the line to be played.
     * @param line the line created to send the notes to be played and creates an audible sound.
     * @param bn   this is the BellNote that is being passed by the ChoirMember to be played.
     */
    public void playNote(SourceDataLine line, BellNote bn) {
        final int ms = Math.min(bn.length.timeMs(), Note.MEASURE_LENGTH_SEC * 1000);
        final int length = Note.SAMPLE_RATE * ms / 1000;
        line.write(bn.note.sample(), 0, length);
        line.write(Note.REST.sample(), 0, Note.SAMPLE_RATE * 50 / 1000);
    }
}

/**
 * This class is used to store the Note and the length that the user wants to play with the text file provided.
 * This
 * class is the bridge between the user's text file passed,
 * and the program converting the data into a playable song.
 */
class BellNote {
    final Note note;
    final NoteLength length;

    BellNote(Note note, NoteLength length) {
        this.note = note;
        this.length = length;
    }
}
