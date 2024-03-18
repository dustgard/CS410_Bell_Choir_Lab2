import javax.sound.sampled.LineUnavailableException;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

public class BellChoir {

    public static void main(String[] args) throws LineUnavailableException, IOException {

        if (!validateFile(args)) {
            return;
        }
        SongNotes notesPassed = new SongNotes(args[0]);
        notesPassed.readFile();
        List<String> notes = notesPassed.getMusicNotes();
        HashSet<String> uniqueNotes = notesPassed.getUniqueNotes();
        ChoirConductor conductor = new ChoirConductor(notes, uniqueNotes);
        conductor.assignNotes();
        conductor.playSong();
    }

    public static boolean validateFile(String[] filename) {

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
        if (!format.equals("txt")){
            System.out.println("File format provided is not the correct type: [" + format + "]. Provide txt");
            return false;
        }
        else {
            return format.equals("txt");
        }
    }
}
