import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SongNotes {
    private final String songLocation;
    private final List<String> musicNotes = new ArrayList<>();

    public SongNotes(String songLocation) {
        this.songLocation = songLocation;
    }

    public void readFile() throws IOException {
        File songFile = new File(songLocation);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(songFile));
        } catch (FileNotFoundException ignore) {
            //Handle the exception

        }
        while (true) {
            String note = "";
            try {
                if ((note = reader.readLine()) == null) break;
            } catch (IOException ignore) {
            }
            musicNotes.add(note);
        }
        // Use try with resources
        reader.close();
    }

    // Take String to notes first
    // Tic has validation
    public List<String> getMusicNotes() {
        return musicNotes;
    }

    public HashSet<String> getUniqueNotes() {
        List<String> members = new ArrayList<>();
        for (String mus : musicNotes) {
            String mem = mus.substring(0, 1);
            members.add(mem);
        }
        HashSet<String> unique = new HashSet<>(members);
        // Too many times with the data when can be done once. Map is better for this. Less work.
        return unique;
    }
}
