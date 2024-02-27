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

    public List<String> getMusicNotes() {
        return musicNotes;
    }

    public HashSet<String> getUniqueNotes() {
        List<String> members = new ArrayList<>();
        for (String mus : musicNotes) {
            String mem = mus.substring(0,1);
            members.add(mem);
        }
        HashSet<String> unique = new HashSet<>(members);
        return unique;
    }

}
