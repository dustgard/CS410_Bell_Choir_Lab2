import javax.sound.sampled.SourceDataLine;

/**
 * The ChoirMember Class: This class is a thread that plays a note that is passed to it by the ChoirConductor.
 * When the note is received and a signal given to play the note, the ChoirMember plays the note and when done
 * waits for another note to be passed.
 * It keeps track if it is playing the note and if the timeToPlay is still true.
 * Author: Dustin Gardner
 */

public class ChoirMember implements Runnable {
    public final Thread thread;
    public boolean memberPlayingNote = false;
    private boolean timeToPlay = true;
    private BellNote bellNote;
    private Tone tone;
    private SourceDataLine line;

    /**
     * The constructor takes one parameter that gives the thread the same name as the assigned note.
     * Allowing it to be found easily when compared to the note to be played.
     * The thread is started once the object is created.
     * @param note the name of the note assigned matches the name of the thread.
     */
    ChoirMember(String note) {
        thread = new Thread(this, note);
        thread.start();
    }

    /**
     * This method is called by the ChoirConductor to notify the member to play the BellNote being passed to them.
     * If it is not the members, it will wait until it is notified.
     * @param note the BellNote passed that the ChoirConductor wants the member to play.
     * @param tone the tone object to use to play the note.
     * @param line the line that was created to make an audible sound from the computer.
     */
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

    /**
     * This method checks if it is their turn to play then informs the user that it is playing the note that is passed
     * to it by the ChoirConductor.
     * Then it takes the passed information stored from the notesTurn method and calls tone.playNote effectively starting
     * the audible sound for that note that the member is supposed to play.
     */
    public synchronized void play() {
        if (memberPlayingNote) {
            System.out.println("Member " + Thread.currentThread().getName() + " is Playing note [" + bellNote.note.name() + "]");
            tone.playNote(line, bellNote);
            memberPlayingNote = false;
        }
    }

    /**
     * This methond is used to have the memberStop playing and wait for the Song to finish before the program exits.
     */
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

    /**
     * The run method continues to call the play method until the time to play, and the member is not playing the note
     * at which time the member informs the user it is done playing its part of the song.
     */
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
                    play();
                    notify();
            } while (timeToPlay);
            System.out.println("Member " + Thread.currentThread().getName() + " is finished playing song");
        }
    }
}
