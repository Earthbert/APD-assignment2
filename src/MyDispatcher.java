/* Implement this class. */

import java.util.Comparator;
import java.util.List;

public class MyDispatcher extends Dispatcher {
    private Integer lastHostIndex = 0;

    public MyDispatcher(SchedulingAlgorithm algorithm, List<Host> hosts) {
        super(algorithm, hosts);
    }

    @Override
    public void addTask(Task task) {
        switch (algorithm) {
            case ROUND_ROBIN:
                addTask_RR(task);
                break;
            case LEAST_WORK_LEFT:
                addTask_LWL(task);
                break;
            case SHORTEST_QUEUE:
                addTask_SQ(task);
                break;
            case SIZE_INTERVAL_TASK_ASSIGNMENT:
                addTask_SITA(task);
                break;
            default:
                break;
        }
    }

    private void addTask_RR(Task task) {
        hosts.get(lastHostIndex).addTask(task);
        lastHostIndex = (lastHostIndex + 1) % hosts.size();
    }

    private void addTask_LWL(Task task) {
        hosts.stream().min(Comparator.comparing(Host::getWorkLeft).thenComparing(Host::getId)).get().addTask(task);
    }

    private void addTask_SQ(Task task) {
        hosts.stream().min(Comparator.comparing(Host::getQueueSize).thenComparing(Host::getId)).get().addTask(task);
    }

    private void addTask_SITA(Task task) {
        switch (task.getType()) {
            case SHORT:
                hosts.get(0).addTask(task);
                break;
            case LONG:
                hosts.get(1).addTask(task);
                break;
            case MEDIUM:
                hosts.get(2).addTask(task);
                break;
            default:
                break;
    
        }
    }
}
