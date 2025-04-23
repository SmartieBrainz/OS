import java.io.BufferedReader;
import java.io.IOException;

public class JobReader implements Runnable {
    private final String filePath;
    private final JobQueue jobQueue;

    public JobReader(String filePath, JobQueue jobQueue) {
        this.filePath = filePath;
        this.jobQueue = jobQueue;
    }

    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":|;");

                if (parts.length != 4) {
                    System.err.println("Invalid job format: " + line);
                    continue;
                }
                
                int id = Integer.parseInt(parts[0]);
                int burstTime = Integer.parseInt(parts[1]);
                int priority = Integer.parseInt(parts[2]);
                int memorySize = Integer.parseInt(parts[3]);

                PCB pcb = new PCB(id, burstTime, priority, memorySize);
                jobQueue.addJob(pcb);
                System.out.println("Added job: " + pcb);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
