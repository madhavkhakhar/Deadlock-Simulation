A simulator for deadlock detection and avoidance. It consists of Process, Resources and Kernel which handles all the process and resources.

Deadlock is the main class. Input is taken from text file with specific format.
Example:

2   //Number of processes

2   //Number of resources

1 1   // Number of instances of each resource

1C2   //Meaning process 1 computing for 2 seconds.

1R0   //Process 1 requesting for resource 0.

1C1

1R1

1F0   //Process 1 releasing resource 0

1E    //Process 1 completed execution

2C3

2R1

2C2

2R0

2E

The above input generates a deadlock.
