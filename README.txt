CS-410_Bell_Choir_Lab2

Dustin Gardner CS-410 4/3/2024

Bell Choir Lab

    The purpose of this lab was to teach that the program does not control the order in which threads run
    unless designed to do this. It is usually done by the OS. The BellChoir Lab is a multithreading program that plays
    a songs from a text file. The file is passed to the program by using ANT from the command line. Once the file is passed
    to the program, it then parse the file into a playable song after validating the file format.
    The program simulates a conductor signaling each member that has been assigned a bell note
    to play at the appropriate time and length according to the song.

Lab Tasks:

   A song file is passed to the program (BellChoir) as an argument using ant. The file is a .txt format and contains
   notes that the ChoirConductor must play. The ChoirConductor will tell the ChoirMembers when to play the note and
   for how long. The ChoirMember plays their note and then the ChoirConductor tells the next ChoirMember to play their
   note and so on until the song is complete.

       Requirements:

            1. Project must be committed and pushed to GitHub
            2. Must use ANT to build/run
            3. Each ChoirMember must be each assigned note in a separate thread
            4. The program must be able to play the instructor provided song "Mary Had a Little Lamb" with the sound output
               being properly recognizable with appropriate timing.
            5. The program must play additional songs provided by the instructor or students.
            6. The program must handle errors and validation

Bell Choir program design explanation:

	BellChoir class:

        1. Runs the main program.
        2. Handle the arguments passed to the program from the command line.
        3. Creates the SongNotes object with the location of the file as a parameter.
        4. Creates the ChoirConductor with notes and uniqueNotes provided from the SongNotes class.
        5. It tells the conductor to assign the unique notes in the song to individual members of the choir.
        6. And finally tells the conductor to start playing the song. When the song is over the program ends.

    SongNotes class:

        1. Stores the songLocation that is passed from BellChoir class
        2. Does validation on the file format, and if the file exists in that location
        3. Does validation on the contents of the song file by note and the length of note
        4. Read the notes per line and store them into a list.
        5. Stores a HashSet that only has unique entries. This allows on new thread on different notes regardless of
           note length.


	ChoirConductor class:

        1. Stores the song notes and unique notes for that song.
        2. Creates the ChoirMember threads based on how many unique notes there are.
        3. Assigns notes to choir members with the unique notes.
        4. The conductor thread tells what member thread to play the note that he passes to them each time.
        5. The conductor waits until the member is done playing the note to send the next note.
        6. Once all the notes are finished, the conductor stops and takes a bow.

    ChoirMember class:

        1. Stores the note and the Tone/Line for the sound.
        2. Plays the note instructed by the ChoirConductor
        3. When the note is done, the ChoirMember waits until the conductor signals them to play again.

    Tone class:

        1. It takes the note that is passed and creates the correct information by parsing the note by note and length
        to play a correct audible sound.
        2. It does magic.

Setting up:

    GitHub's Bell Choir application cloning:
    Ensure git is installed on your machine through terminal:
        macOS: git -v
        Windows: git --version
    Clone repository:
        git clone https://github.com/dustgard/CS410_Bell_Choir_Lab2.git

Starting the Bell Choir application Command Prompt using Apache Ant:

    To start the application, a command prompt needs to be open in the directory
    for the program that was cloned.
        1. Ensure ant is installed: (Ant install instructions at bottom)
            MacOS: command: ant -v
            Windows: command: ant --version
        2. Command: to build a project using ant:
            macOS and Windows: ant
        3. Command: to run java program with the default song "Mary Had a Little Lamb"
            macOS and Windows: ant run
        4. Command: to run java program with ant parameter using a custom song.
            macOS and Windows: ant -Dsong="[enter song location here]" run
            example ant -Dsong="C:\Users\dust\Drive\MaryLamb.txt" run


Installing Apache Ant:

    Instructions for installation found at: https://ant.apache.org/manual/install.html
    and https://www.tutorialspoint.com/ant/ant_environment.html

    When testing successful installation pay action to setting path variables/environmental variables.

    Possible error message after installing ant:

        Examples of macOS error fixes:
            After installing ant, move it to /usr/local/ using this
            command mv /Users/admin/Downloads/apache-ant-1.9.4  /usr/local/
            Now try to set environment variables using nano $HOME/.profile

            Check the below lines.
            Export PATH=$PATH:/usr/local/ant/bin/
            export ANT_HOME=/usr/local/ant
            export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.7.0._71..jdk/Contents/Home/bin

