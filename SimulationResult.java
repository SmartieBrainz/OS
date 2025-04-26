import java.util.*;

public class SimulationResult {
    public static class GanttEvent {
        public final int pid, start, duration;
        public GanttEvent(int pid, int start, int duration) {
            this.pid = pid; this.start = start; this.duration = duration;
        }
    }
    private final List<GanttEvent> events = new ArrayList<>();
    private final List<Integer> starved = new ArrayList<>();
    private long totalWaiting = 0, totalTurnaround = 0;
    private int n = 0;

    public void addEvent(int pid, int start, int duration) {
        events.add(new GanttEvent(pid, start, duration));
    }
    public void markCompletion(PCB job) {
        int wait = job.getStartTime() - job.getArrivalTime();
        int tat = job.getFinishTime() - job.getArrivalTime();
        totalWaiting += wait;
        totalTurnaround += tat;
        n++;
    }
    public void markStarved(int pid) {
        starved.add(pid);
    }

    public double avgWaiting() {
        return n == 0 ? 0 : (double)totalWaiting/n;
    }
    public double avgTurnaround() {
        return n == 0 ? 0 : (double)totalTurnaround/n;
    }
    public List<GanttEvent> getEvents() { return events; }
    public List<Integer> getStarved() { return starved; }
}
