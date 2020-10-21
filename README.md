## Overview 
A 10 round AES encryption and decryption of a single plaintext block. The program take 128 bit plaintext block and a 128 bit key as input, and produce a 128 bit ciphertext. More details in this [file](https://github.com/emiltan97/aes/blob/master/specs.pdf).
## Running the program 
1. Compile the program with the command "javac *.java"
2. Run the command "java Application --encrypt(--decrypt) <inputFileName><outputFileName>"
### Input format
1. Input must have at least 2 lines
2. First line is a 128 bit binary string without space as a plaintext (ciphertext)
3. Second line is a 128 bit binary string without space as the key
