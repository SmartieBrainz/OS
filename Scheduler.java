import java.util.*;

public class Scheduler {
    private final MemoryManager mm;
    private final Queue<PCB> initialReady; // snapshot at simulation start

    public Scheduler(MemoryManager mm) {
        this.mm = mm;
        // take a copy so we can re‐run algorithms independently
        this.initialReady = mm.getReadyQueue();
    }

    private void reset() {
        // reset PCB fields & reload ready queue to original state
        mm.getReadyQueue().clear();
        for (PCB p : initialReady) {
            // deep‐copy or reset state
            p.setRemainingTime(p.getBurstTime());
            p.setStartTime(-1);
            p.setFinishTime(-1);
            p.setState("Ready");
            p.setArrivalTime(0);
            mm.getReadyQueue().offer(p);
        }
    }

    public SimulationResult runFCFS() {
        reset();
        SimulationResult res = new SimulationResult();
        OS.sysSchedule("FCFS");
        int time = 0;
        PCB current = null;
        while ((current = mm.fetchReadyJob()) != null) {
            OS.sysContextSwitch(null, current);
            if (current.getStartTime() < 0) current.setStartTime(time);
            res.addEvent(current.getId(), time, current.getBurstTime());
            time += current.getBurstTime();
            current.setFinishTime(time);
            OS.sysTerminateProcess(current);
            OS.sysGetStatistics(current);
            mm.deallocateMemory(current);
            res.markCompletion(current);
        }
        return res;
    }

    public SimulationResult runRR(int quantum) {
        reset();
        SimulationResult res = new SimulationResult();
        OS.sysSchedule("RR(q=" + quantum + ")");
        int time = 0;
        Queue<PCB> rq = mm.getReadyQueue();
        while (!rq.isEmpty()) {
            PCB p = rq.poll();
            OS.sysContextSwitch(null, p);
            if (p.getStartTime() < 0) p.setStartTime(time);
            int run = Math.min(quantum, p.getRemainingTime());
            res.addEvent(p.getId(), time, run);
            time += run;
            p.setRemainingTime(p.getRemainingTime() - run);
            if (p.getRemainingTime() > 0) {
                rq.offer(p);
            } else {
                p.setFinishTime(time);
                OS.sysTerminateProcess(p);
                OS.sysGetStatistics(p);
                mm.deallocateMemory(p);
                res.markCompletion(p);
            }
        }
        return res;
    }

    public SimulationResult runPriority() {
        reset();
        SimulationResult res = new SimulationResult();
        OS.sysSchedule("PRIORITY");
        int time = 0;
        List<PCB> rq = new ArrayList<>(mm.getReadyQueue());
        while (!rq.isEmpty()) {
            // detect starvation
            for (PCB p : rq) {
                if (time - p.getArrivalTime() > p.getPriority()) {
                    res.markStarved(p.getId());
                }
            }
            // pick highest priority
            PCB best = Collections.max(rq, Comparator.comparingInt(PCB::getPriority));
            rq.remove(best);
            OS.sysContextSwitch(null, best);
            best.setStartTime(time);
            res.addEvent(best.getId(), time, best.getBurstTime());
            time += best.getBurstTime();
            best.setFinishTime(time);
            OS.sysTerminateProcess(best);
            OS.sysGetStatistics(best);
            mm.deallocateMemory(best);
            res.markCompletion(best);
        }
        return res;
    }
}
