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
    // assume we’ll signal “reader done” by interrupting this thread
    while (!Thread.currentThread().isInterrupted()) {
        PCB job = jobQueue.getJob();

        if (job != null) {
            allocateMemory(job);
        } else {
            // if there might still be jobs coming, just wait
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    // once interrupted, exit—by then jobQueue should have been drained
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

    public synchronized PCB fetchReadyJob() {
        return readyQueue.poll();        // atomic remove for the scheduler thread
    }

     /** Used by RR to re-queue a partially executed process. */
     public synchronized void requeueJob(PCB job) {
        readyQueue.offer(job);
    }

    /** Removes and returns the highest-priority job in the ready queue. */
    public synchronized PCB pollHighestPriorityJob() {
        PCB best = null;
        for (PCB p : readyQueue) {
            if (best == null || p.getPriority() > best.getPriority()) {
                best = p;
            }
        }
        if (best != null) readyQueue.remove(best);
        return best;
    }

    
}