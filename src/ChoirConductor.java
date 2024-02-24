import java.util.concurrent.ConcurrentLinkedQueue;

public class ChoirConductor {

    private final ConcurrentLinkedQueue<String> songNotes = new ConcurrentLinkedQueue<>();

    public ChoirConductor(ConcurrentLinkedQueue<String> songNotes) {

    }
}
