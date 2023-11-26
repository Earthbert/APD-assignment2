/* Implement this class. */

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

public class MyHost extends Host {
    private PriorityBlockingQueue<Task> queue = new PriorityBlockingQueue<>(10,
            Comparator.comparing(Task::getPriority).reversed().thenComparing(Task::getId));
    private Task runningTask = null;

    private boolean isShutdown = false;

    private boolean isInRunningState = false;
    private boolean interogateRunningTask = false;

    @Override
    public void run() {
        while (true) {
            if (isShutdown) {
                return;
            }
            if (runningTask == null) {
                try {
                    synchronized (this) {
                        this.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runningTask = queue.poll();
            } else {
                runTask(runningTask);
            }
        }
    }

    @Override
    public void addTask(Task task) {
        if (isShutdown) {
            return;
        }
        queue.add(task);
        synchronized (this) {
            this.notify();
        }
    }

    @Override
    public int getQueueSize() {
        return runningTask == null ? queue.size() : queue.size() + 1;
    }

    @Override
    public long getWorkLeft() {
        long workLeft = queue.stream().mapToLong(Task::getLeft).sum();
        if (!isInRunningState)
            return runningTask == null ? workLeft : workLeft + runningTask.getLeft();

        interogateRunningTask = true;
        synchronized (this) {
            this.notify();
        }
        synchronized (this) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return workLeft + runningTask.getLeft();
    }

    @Override
    public void shutdown() {
        queue.clear();
        synchronized (this) {
            this.notify();
        }
        isShutdown = true;
    }

    /**
     * Runs a task until it is completed or preempted.
     * 
     * @param task The task to be executed.
     */
    private void runTask(Task task) {
        do {
            if (task.getLeft() <= 0) {
                task.finish();
                runningTask = queue.poll();
                return;
            }

            long currentTime = System.currentTimeMillis();

            try {
                synchronized (this) {
                    isInRunningState = true;
                    this.wait(task.getLeft());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            long elapsedTime = System.currentTimeMillis() - currentTime;

            task.setLeft(task.getLeft() - elapsedTime);

            if (interogateRunningTask) {
                synchronized (this) {
                    this.notify();
                }
            }

            isInRunningState = false;
        } while (!task.isPreemptible() && isShutdown == false);

        if (task.getLeft() <= 0) {
            task.finish();
            runningTask = queue.poll();
            return;
        } else {
            queue.add(runningTask);
            runningTask = queue.poll();
        }
    }
}
