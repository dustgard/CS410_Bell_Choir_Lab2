import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class ChoirMember implements Runnable {
    public final Thread thread;
    public boolean memberPlayingNote = false;
    private boolean timeToPlay = true;
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
            timeToPlay = true;
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

    public synchronized void memberStop() {
        synchronized (this) {
            timeToPlay = false;
            notify();
            while (timeToPlay) {
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
                while (!memberPlayingNote && timeToPlay) {
                    try {
                        System.out.println("Member " + Thread.currentThread().getName() + " is Waiting");
                        ChoirConductor.conductorSignal = true;
                        wait();
                    } catch (InterruptedException e) {
                    }
                }
                try {
                    play();
                    notify();
                } catch (InterruptedException | LineUnavailableException e) {
                }
            } while (timeToPlay);
            System.out.println("Member " + Thread.currentThread().getName() + " is finished playing song");
        }
    }
}
