import javax.swing.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BellChoir {

    public static void main(String[] args) {
        JFileChooser song = new JFileChooser("src/Songs");
        song.showOpenDialog(null);
        String songLocation = song.getSelectedFile().getAbsolutePath();
        SongNotes notesPassed = new SongNotes(songLocation);
        notesPassed.readFile();
        ConcurrentLinkedQueue<String> queue = notesPassed.getMusicNotes();
        System.out.println(queue.poll());
    }
}
