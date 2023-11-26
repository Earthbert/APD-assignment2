/* Implement this class. */

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

public class MyHost extends Host {
    private PriorityBlockingQueue<Task> queue = new PriorityBlockingQueue<>(10, Comparator.comparing(Task::getPriority));
    private Task runningTask = null;

    private boolean isShutdown = false;

    @Override
    public void run() {
        while (true) {
            if (isShutdown) {
                return;
            }
            if (runningTask == null) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    System.exit(1);
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
        this.notify();
    }

    @Override
    public int getQueueSize() {
        return runningTask == null ? queue.size() : queue.size() + 1; 
    }

    @Override
    public long getWorkLeft() {
        return queue.stream().mapToLong(Task::getLeft).sum();
    }

    @Override
    public void shutdown() {
        queue.clear();
        this.notify();
        isShutdown = true;
    }

    private void runTask(Task task) {
        long currentTime = System.currentTimeMillis();

        try {
            this.wait(task.getLeft(), 0);
        } catch (InterruptedException e) {
            System.exit(1);
            e.printStackTrace();
        }

        long elapsedTime = System.currentTimeMillis() - currentTime;

        task.setLeft(task.getLeft() - elapsedTime);

        if (task.getLeft() <= 0) {
            task.finish();
            runningTask = queue.poll();
            return;
        } else
            queue.add(runningTask);
    }
}
