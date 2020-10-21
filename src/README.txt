/**
* @author : Emil && Brett
* ID : Emil Tan (C3271566)
* ID : Brett Grentell (C3149278)
* Program : AES Encryption/Decryption
*/

Running the program :
* Compile the program with the command "javac *.java"
* Run the command "java Application --encrypt(--decrypt) <inputFileName><outputFileName>"

Input format :
* Input must have at least 2 lines
* First line is a 128 bit binary string without space as a plaintext (ciphertext)
* Second line is a 128 bit binary string without space as the key

Application.java :
* Main class that start the program

OutputWriter.java :
* Writing the output of the process into a file
* The name of the file is specified in the 3rd argument (args[2]) in the command line

Sbox.java :
* Contains the sbox table for bytes substitution algorithm in AES

State.java :
* Declaring a state object that handles the input data in group of 4
* Each group contains 4 bytes
