import java.util.*;


class MemoryManager implements Runnable {

    private final JobQueue jobQueue;
    private final Queue<PCB> readyQueue = new LinkedList<>();
    private final int maxMemory = 2048;
    private int currentMemory = 0;
    private final Object memoryLock = new Object();
    private int starvationThreshold = 0;

    public MemoryManager(JobQueue jobQueue) {
        this.jobQueue = jobQueue;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                PCB job = jobQueue.getJob(); // Use getJob() from your JobQueue
                if (job != null) {
                    allocateMemory(job);
                } else if (jobQueue.isEmpty() && readyQueue.isEmpty()) {
                    // Exit if both queues are empty
                    break;
                } else {
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void allocateMemory(PCB job) {
        synchronized (memoryLock) {
            if (currentMemory + job.getMemorySize() <= maxMemory) {
                currentMemory += job.getMemorySize();
                job.setState("Ready");
                readyQueue.add(job);
                System.out.println("Allocated " + job.getMemorySize() + " MB to Job " + job.getId() +
                                   ". Available memory: " + (maxMemory - currentMemory) + " MB");
                // Optional: Starvation tracking
                if (starvationThreshold < job.getPriority()) {
                    starvationThreshold = job.getPriority();
                }
            } else {
                jobQueue.addJob(job); // Put the job back
                //System.out.println("Not enough memory for Job " + job.getId() +
                                  // " (Required: " + job.getMemorySize() + " MB). Available: " + (maxMemory - currentMemory) + " MB");
            }
        }
    }

    public void deallocateMemory(PCB job) {
        synchronized (memoryLock) {
            currentMemory -= job.getMemorySize();
            System.out.println("Deallocated " + job.getMemorySize() + " MB from Job " + job.getId() +
                               ". Available memory: " + (maxMemory - currentMemory) + " MB");
        }
    }

    public boolean isStarving(PCB job) {
        // Optional: Basic starvation check
        if (job.getState().equals("Waiting")) {
            return job.getPriority() < starvationThreshold;
        }
        return false;
    }

    public Queue<PCB> getReadyQueue() {
        synchronized (memoryLock) {
            return new LinkedList<>(readyQueue); // Return a copy
        }
    }
}