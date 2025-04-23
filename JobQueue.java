import java.util.LinkedList;
import java.util.Queue;

public class JobQueue {
    private final Queue<PCB> queue = new LinkedList<>();
    
    // synchronized methods to ensure thread safety
    // when multiple threads access the queue at the same time.
    public synchronized void addJob(PCB job) {
        queue.offer(job);
    }

    public synchronized PCB getJob() {
        return queue.poll();
    }

    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }

    public synchronized int size() {
        return queue.size();
    }
}
