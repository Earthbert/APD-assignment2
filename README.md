# Assignment 2

## Daraban Albert-Timotei

### MyDispatcher

For the dispatcher implementation, I followed the algorithm described in the assignment, separating the logic in addTask_ALG functions specific for each algorithm.

### MyHost

* Variables
  * queue: A priority queue that holds tasks. Tasks with higher priority are dequeued before tasks with lower priority. If two tasks have the same priority, the task with the lower ID is dequeued first.
  * runningTask: The task that is currently being executed.
  * isShutdown: A flag indicating whether the host has been shut down.
  * isInRunningState: A flag indicating whether a task is currently being executed.
  * interrogateRunningTask: A flag used to synchronize the getWorkLeft method.

* Methods
  * run: The main loop of the host. It waits for tasks to be added to the queue and executes them.
  * addTask: Adds a task to the queue and notifies the run method.
  * getQueueSize: Returns the number of tasks in the queue, including the currently running task.
  * getWorkLeft: Returns the total time left to execute all tasks in the queue, including the currently running task.
  * shutdown: Clears the queue and sets the isShutdown flag to true.
  * runTask: Executes a task. Execution is simulated using wait(execution time). If the task is preemptible and the host is not shut down, the task is added back to the queue if it was preempted.
