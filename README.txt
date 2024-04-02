CS-410_JuiceBottler_Lab

Dustin Gardner CS-410 2/21/2024

Juice Bottler Lab

    The lab was created as an exercise to increase the knowledge of multiprocessing,
    multithreading, thread safe techniques, data parallelization, and task parallelization.
    The Juice Bottler lab is based off a plant assemble line that takes an orange from fetching,
    peeling, squeezing, bottling, and processing (labeling the product).

Lab Tasks:

    Oranges are turned into sellable bottles of orange juice. Each orange process state in the
    Orange class take a set time to complete.

       1. Fetch
       2. Peel
       3. Juice
       4. Bottle
       5. Process (Completed product for sale)

       Requirements:

            1. Must have AT LEAST two Plants running (data parallelization)
            2. Must have multiple Workers per plant operating on the Oranges (task parallelization)
            3. The Final project must be committed and pushed up to GitHub.
            4. Optional: Using ANT for building and running the java program.

Juice Bottler program design explanation:

	The JuiceBottler class:

        1. Runs the main program.
        2. Creates the Plants objects (Three in my version).
        3. Sets the amount of time that the plants will work.
        4. Informs the user of the plant�s phases:
            a. Plant open.
            b. Plant operating.
            c. Plant closed.
        5. Informs the user the total orange production for each state:
            a. Total fetched.
            b. Total peeled.
            c. Total squeezed.
            d. Total Bottled.
            e. Total Processed.
            f. Total Wasted (orange that didn't make it into a finished bottle).

     Plant class:

        1. Creates queue for each orange state:
            a. Accessed in synchronized fashion (thread safe).
        2. Sets the number of oranges per bottle.
        3. Creates 11 threads (workers that are created for grabbing an orange, changing the state,
            and placing it in the next states queue.
        4. Informs the JuiceBottler program that the workers are on the clock (Threads have started)
            or off the clock (Threads are finished.
        5. Stopping the workers after JuiceBottler program allotted work time has ended.
        6. Keeping track of all production numbers per task.

	Orange class:

        1. Changes state for orange object fetch, peeled, squeezed, bottled, processed

Setting up:

    GitHub's Juice Bottler application cloning:
    Ensure git is installed on your machine through terminal:
        macOS: git -v
        Windows: git --version
    Clone repository:
        git clone https://github.com/dustgard/CS-410_JuiceBottler_Lab

Starting the Juice Bottler application Command Prompt using Apache Ant:

    To start the application a command prompt needs to be open in the directory
    for the program that was cloned.
        1. Ensure ant is installed: (Ant install instructions at bottom)
            MacOS: command: ant -v
            Windows: command: ant --version
        2. command: to build project using ant:
            MacOS and Windows: ant
        3. command: to run java program
            MacOS and Windows: ant run


Starting the application on IDE:

    Open the IDE and before running the application allow it to build and make sure
    that the selected Class is the JuiceBottler.java:

    Hit the play button and view results on the console.

Installing Apache Ant:

    Instructions for install found at: https://ant.apache.org/manual/install.html
    and https://www.tutorialspoint.com/ant/ant_environment.html

    When testing successful installation pay action to setting path variables/environmental variables.

    Possible error message after installing ant:

        Examples of macOS error fixes:
            After installing ant, move it to /usr/local/ using this
            command mv /Users/admin/Downloads/apache-ant-1.9.4  /usr/local/
            Now try to set environment variables using nano $HOME/.profile

            Check below lines.
            export PATH=$PATH:/usr/local/ant/bin/
            export ANT_HOME=/usr/local/ant
            export  JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.7.0._71..jdk/Contents/Home/bin

