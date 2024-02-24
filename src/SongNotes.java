import java.io.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SongNotes {
    private final String songLocation;
    private final ConcurrentLinkedQueue<String> musicNotes = new ConcurrentLinkedQueue<>();

    public SongNotes(String songLocation) {
        this.songLocation = songLocation;
    }

    public void readFile() {
        File songFile = new File(songLocation);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(songFile));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        while (true) {
            String note;
            try {
                if ((note = reader.readLine()) == null) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            musicNotes.add(note);
        }
    }

    public ConcurrentLinkedQueue<String> getMusicNotes() {
        return musicNotes;
    }
}
