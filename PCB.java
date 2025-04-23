class PCB {
    private int id;
    private String state;
    private int priority;
    private int memorySize;
    private int burstTime;

    public PCB(int id, int burstTime, int priority, int memorySize) {
        this.id = id;
        this.burstTime = burstTime;
        this.priority = priority;
        this.memorySize = memorySize;
        this.state = "New";
    }

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