import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SongNotes {
    private final String songLocation;
    private final List<String> musicNotes = new ArrayList<>();

    public SongNotes(String[] songLocation) {
        this.songLocation = songLocation[0];
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
            for(Note checkNote : Note.values()){
                if(note.equals(checkNote)){
                    musicNotes.add(note);
                }
            }

//            musicNotes.add(note);
        }
        // Use try with resources
        reader.close();
    }

    public boolean validateFile(String[] filename) {

        if (filename.length > 1) {
            System.out.println("Program only takes one parameter. Example: ant -Dsong=src/Song/MaryLamb.txt run");
            return false;
        }
        File file = new File(filename[0]);
        if (!file.exists()) {
            System.out.println("File not found: You provided " + filename[0]);
            return false;
        }
        String fileCheck = file.getName();
        String format = "";
        int ext = fileCheck.lastIndexOf(".");
        if (ext >= 0) {
            format = fileCheck.substring(ext + 1);
        }
        System.out.println("File format provided: [" + format + "]");
        if (!format.equals("txt")) {
            System.out.println("File format provided is not the correct type: [" + format + "]. Provide txt");
            return false;
        }

        else {
            return format.equals("txt");
        }

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