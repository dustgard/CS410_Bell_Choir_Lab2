import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class ChoirMember implements Runnable {
    public final Thread thread;
    private final boolean timeToPlay = true;
    public boolean memberPlayingNote = false;
    private BellNote bellNote;
    private Tone tone;
    private SourceDataLine line;

    ChoirMember(String note) {
        thread = new Thread(this, note);
        thread.start();
    }

    public synchronized void play() throws LineUnavailableException, InterruptedException {
        if (memberPlayingNote) {
            System.out.println("Member " + Thread.currentThread().getName() + " is Playing note [" + bellNote.note.name() + "]");
            tone.playNote(line, bellNote);
            memberPlayingNote = false;
        }
    }

    public synchronized void notesTurn(BellNote note, Tone tone, SourceDataLine line) {
        synchronized (this) {
            this.tone = tone;
            this.line = line;
            memberPlayingNote = true;
            bellNote = note;
            notify();
            while (memberPlayingNote) {
                try {
                    wait();
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    public void run() {
        synchronized (this) {
            do {
                while (!memberPlayingNote) {
                    try {
                        System.out.println("Member " + Thread.currentThread().getName() + " is Waiting");
                        ChoirConductor.conductorSignal = true;
                        wait();
                    } catch (InterruptedException e) {
                    }
                }
                try {
                    System.out.println("Member " + Thread.currentThread().getName() + " is trying to play note [" + bellNote.note.name() + "]");
                    play();
                    notify();
                }  catch (InterruptedException | LineUnavailableException e) {
                }
            } while (ChoirConductor.songStillPlaying);
            System.out.println("Member Not");
        }
    }
}
