class PCB {
    private String state;
    private final int id;
    private final int priority;
    private final int memorySize;
    private final int burstTime;

    private int remainingTime;     // for RR
    private int arrivalTime = 0;  // when the job reached the ready queue
    private int startTime  = -1;   // first time on the CPU
    private int finishTime = -1;   // completion time

    public PCB(int id, int burstTime, int priority, int memorySize) {
        this.id = id;
        this.burstTime = burstTime;
        this.priority = priority;
        this.memorySize = memorySize;
        this.remainingTime = burstTime;
        this.state = "New";
    }

    public int getId() { return id; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public int getPriority() { return priority; } 
    public int getMemorySize() { return memorySize; }
    public int getBurstTime() { return burstTime; }
    //-------------------------------------------------------
    public int getRemainingTime() { return remainingTime; }
    public int getArrivalTime() { return arrivalTime; }
    public int getStartTime() { return startTime;  }
    public int getFinishTime() { return finishTime; }
    public void setRemainingTime(int t)  { this.remainingTime = t; }
    public void setArrivalTime(int t) { this.arrivalTime = t; }
    public void setStartTime(int t) { this.startTime = t; }
    public void setFinishTime(int t) { this.finishTime = t; }

    @Override
    public String toString() {
        return "PCB{" +
                "id=" + id +
                ", state='" + state + '\'' +
                ", priority=" + priority +
                ", memorySize=" + memorySize +
                ", burstTime=" + burstTime +
                '}';
    }
}