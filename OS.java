public class OS {
    public static void sysCreateProcess(PCB job) {
        System.out.printf("[sys_create_process] pid=%d burst=%d priority=%d memory=%dMB%n",
                job.getId(), job.getBurstTime(), job.getPriority(), job.getMemorySize());
    }

    public static void sysAllocateMemory(PCB job) {
        System.out.printf("[sys_allocate_memory] pid=%d size=%dMB -> %s%n",
                job.getId(), job.getMemorySize(), "SUCCESS");
    }

    public static void sysLoadReadyQueue(PCB job) {
        System.out.printf("[sys_load_ready] pid=%d%n", job.getId());
    }

    public static void sysSchedule(String algo) {
        System.out.printf("[sys_schedule] algorithm=%s%n", algo);
    }

    public static void sysContextSwitch(PCB from, PCB to) {
        if (from == null) {
            System.out.printf("[sys_context_switch] -> pid=%d%n", to.getId());
        } else {
            System.out.printf(
                "[sys_context_switch] from pid=%d to pid=%d%n",
                from.getId(), to.getId()
            );
        }
    }

    public static void sysTerminateProcess(PCB job) {
        System.out.printf("[sys_terminate_process] pid=%d%n", job.getId());
    }

    public static void sysGetStatistics(PCB job) {
        int tat = job.getFinishTime() - job.getArrivalTime();
        int wait = job.getStartTime() - job.getArrivalTime();
        System.out.printf(
            "[sys_get_stats] pid=%d turnaround=%dms waiting=%dms%n", 
            job.getId(), tat, wait
        );
    }
}
