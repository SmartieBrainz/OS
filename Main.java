import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        JobQueue jobQueue = new JobQueue();
        // 1) start reader
        Thread reader = new Thread(new JobReader("job.txt", jobQueue));
        reader.start();

        // 2) start memory manager
        MemoryManager mm = new MemoryManager(jobQueue);
        Thread memmgr = new Thread(mm);
        memmgr.start();

        // wait for both to finish
        reader.join();

        // give MemoryManager a moment to allocate all of them
        Thread.sleep(200);

        memmgr.interrupt();
        memmgr.join();

        // 3) ask user which to run or alla
        Scanner sc = new Scanner(System.in);
        System.out.println("Choose: 1=FCFS, 2=RR, 3=Priority");
        int choice = sc.nextInt();

        Scheduler sched = new Scheduler(mm);
        SimulationResult fcfs=null, rr=null, prio=null;

        if (choice==1) {
            fcfs = sched.runFCFS();
            printResult("FCFS", fcfs);
        }
        if (choice==2) {
            rr = sched.runRR(7);
            printResult("RR(7)", rr);
        }
        if (choice==3) {
            prio = sched.runPriority();
            printResult("PRIORITY", prio);
        }
    }

    private static void printResult(String name, SimulationResult res) {
        System.out.printf("%n=== %s GANTT ===%n", name);
        for (SimulationResult.GanttEvent e : res.getEvents()) {
            System.out.printf("[%3d ->%3d] P%d%n",
                    e.start, e.start + e.duration, e.pid);
        }
        if (!res.getStarved().isEmpty()) {
            System.out.print("Starved: ");
            for (int pid : res.getStarved()) System.out.print(pid + " ");
            System.out.println();
        }
        System.out.printf("Avg waiting: %.2f ms%n", res.avgWaiting());
        System.out.printf("Avg turnaround: %.2f ms%n", res.avgTurnaround());
    }
}
