/**
* @author : Emil && Brett
* Last Edited : 15th May 2019
* Purpose : Main program
*/

import java.io.*;
import java.util.*;
import java.math.BigInteger;

public class Application {

  private State statePlainText;
  private State stateCipherText;
  private State stateKey;
  private boolean isDecrypt;
  private State[] stateKeys; //round keys after key expansion
  private String binKeyStr;
  private String binPlainTextStr;
  private OutputWriter writer; //output writer object to write results into a file
  private String outputFileName;

  private String[][][] aval = new String[2][5][11]; // String[(bool)modified][AES Version][Round]
  private String[][] matrix = {
	  {"02","03","01","01"},
    {"01","02","03","01"},
    {"01","01","02","03"},
    {"03","01","01","02"}
  }; //matrix for mix columns
  private String[][] inverseMatrix = {
	  {"0e","0b","0d","09"},
	  {"09","0e","0b","0d"},
	  {"0d","09","0e","0b"},
	  {"0b","0d","09","0e"}
  }; //matrix for inverse mix columns
  private String[][] rcon = {
    { "01", "00", "00", "00" },
	  { "02", "00", "00", "00" },
	  { "04", "00", "00", "00" },
	  { "08", "00", "00", "00" },
	  { "10", "00", "00", "00" },
	  { "20", "00", "00", "00" },
	  { "40", "00", "00", "00" },
	  { "80", "00", "00", "00" },
	  { "1b", "00", "00", "00" },
	  { "36", "00", "00", "00" }
  }; //rcon to be used in the key schedule

  public static void main (String[] args){
    Application sim = new Application(args);
    sim.run();
  }
  public Application (String[] args){

    String fileName = ""; //file name
    String line; //read lines from the file

    if (args.length < 3){
      //Alert error if there is command line format is wrong
      System.out.println("\nCommand line format : java Application --encrypt(decrypt) <input filename> <output filename>\n");
      System.exit(0);
    }
  	if (args[0].equals("--decrypt")){ isDecrypt = true; }
  	else if (args[0].equals("--encrypt")){ isDecrypt = false; }
  	else {
  		//Alert error if not specified
  		System.out.println("\nOnly <--encrypt> and <--decrypt> are allowed.\n");
  		System.exit(0);
  	}
    outputFileName = args[2];
    try { writer = new OutputWriter(outputFileName, isDecrypt); }
    catch (IOException e){ e.printStackTrace(); }
    fileName = args[1];
    //Open file
    try {
      Scanner inputStream    = new Scanner(new FileReader(fileName));
  	  if (isDecrypt){
  		  String binCipherTextStr  = inputStream.nextLine();
  		  String cipherText        = binToHex(binCipherTextStr);
  		  stateCipherText          = new State(cipherText);
  	  }
  	  else {
        //Reading the plaintext
  		  binPlainTextStr  = inputStream.nextLine(); //Read first line as binary plaintext
  		  String plainText = binToHex(binPlainTextStr);
        statePlainText   = new State(plainText);
  	  }
      //Reading the key
      binKeyStr = inputStream.nextLine(); //Read second line as binary key
      String key       = binToHex(binKeyStr);
      stateKey         = new State(key);
      writer.setKey(hexToBin(stateKey));
			keyExpansion();
    }
    catch (FileNotFoundException e){
      //alert error if file is not found
      System.out.println("\nNO SUCH FILE EXISTS\n");
      System.exit(0);
    }
  }
  public int[] differencesKi(int aes_id) {
	  int[][] number = new int[11][128];
    //run the process 128 times with flip bits in differnet positions each time
	  for(int j=0; j < 128; j++) {
		  statePlainText = new State(binToHex(binPlainTextStr)); //Reset the plaintext
		  stateKey = new State(binToHex(binKeyStr)); //reset the key
		  keyExpansion();
		  switch(aes_id) {
		  case 0:
			  AES0(false, 0);
			  break;
		  case 1:
			  AES1(false, 0);
			  break;
		  case 2:
			  AES2(false, 0);
			  break;
		  case 3:
			  AES3(false, 0);
			  break;
		  case 4:
			  AES4(false, 0);
			  break;
		  }
		  statePlainText = new State(binToHex(binPlainTextStr));
		  stateKey = new State(binToHex(ChangeABit(binKeyStr, j)));
		  keyExpansion();
		  switch(aes_id) {
		  case 0:
			  AES0(false, 1);
			  break;
		  case 1:
			  AES1(false, 1);
			  break;
		  case 2:
			  AES2(false, 1);
			  break;
		  case 3:
			  AES3(false, 1);
			  break;
		  case 4:
			  AES4(false, 1);
			  break;
		  }
		  // Now calculate the differences for AES0 Pi.
		  for(int i=0; i < 11; i++) {
			  if(aval[0][aes_id][i] != null)
				  number[i][j] = numberOfDifferentBits(aval[0][aes_id][i], aval[1][aes_id][i]); // The number of bits that are different.
		  }
	  }
    //calculating the average of the different number of bits
	  int[] averages = new int[11];
	  for(int i=0; i < 11; i++) {
		  for(int j=0; j < 128; j++) {
			  averages[i] += number[i][j];
		  }
		  averages[i] = averages[i] / 128;
	  }
	  return averages;
  }
  public int[] differencesPi(int aes_id) {
	  int[][] number = new int[11][128];
    //Run the program 128 times with flip bits in different position each time
	  for(int j=0; j < 128; j++) {
		  statePlainText = new State(binToHex(binPlainTextStr));
		  switch(aes_id) {
		  case 0:
			  AES0(false, 0);
			  if(j < 1)
          //set the cipher text for the encryption
					writer.setCipherText(hexToBin(stateCipherText));
			  break;
		  case 1:
			  AES1(false, 0);
			  break;
		  case 2:
			  AES2(false, 0);
			  break;
		  case 3:
			  AES3(false, 0);
			  break;
		  case 4:
			  AES4(false, 0);
			  break;

		  }
		  statePlainText = new State(binToHex(ChangeABit(binPlainTextStr, j)));
		  switch(aes_id) {
		  case 0:
			  AES0(false, 1);
			  break;
		  case 1:
			  AES1(false, 1);
			  break;
		  case 2:
			  AES2(false, 1);
			  break;
		  case 3:
			  AES3(false, 1);
			  break;
		  case 4:
			  AES4(false, 1);
			  break;
		  }
		  // Now calculate the differences for AES0 Pi.
		  for(int i=0; i < 11; i++) {
			  if(aval[0][aes_id][i] != null)
				  number[i][j] = numberOfDifferentBits(aval[0][aes_id][i], aval[1][aes_id][i]); // The number of bits that are different.
		  }
	  }
    //Calculate the average of the results obtained above
	  int[] averages = new int[11];
	  for(int i=0; i < 11; i++) {
		  for(int j=0; j < 128; j++) {
			  averages[i] += number[i][j];
		  }
		  averages[i] = averages[i] / 128;
	  }
	  return averages;
  }
  public void run(){
    System.out.println("Initiating the program...");
    //starting time
    long startTime = System.nanoTime();
  	//------------------- AES for Pi Changed
    if (isDecrypt){ //If decryption can skip the avalanche analysis
      System.out.println("Method : Decryption");
      writer.setCipherText(hexToBin(stateCipherText));
      System.out.println("Ciphertext identified");
      System.out.println("Decrypting ciphertext...");
      AES0(true, 0);
      writer.setPlainText(hexToBin(statePlainText));
      System.out.println("Ciphertext decrypted");
    }
    else {
      System.out.println("Method : Encryption");
      int[][] differencesArr = new int[5][11];
      writer.setPlainText(binPlainTextStr);
      System.out.println("Encrypting P and Pi under K");
      for(int i=0; i < 5; i++) {
        statePlainText = new State(binPlainTextStr);
        differencesArr[i] = differencesPi(i);
        writer.setAES(differencesArr); //setting the result to output writer
      }
      System.out.println("Encryption complete");
      //------------------- AES for Ki Changed
      int[][] differencesArrKi = new int[5][11];
      System.out.println("Encrypting P under K and Ki");
      for(int i=0; i < 5; i++) {
        statePlainText = new State(binPlainTextStr);
        differencesArrKi[i] = differencesKi(i);
        writer.setAES2(differencesArrKi); //setting the result to output writer
      }
      System.out.println("Encryption complete");
    }
    long endTime = System.nanoTime();
    long duration = (endTime - startTime)/1000000;
    writer.setDuration(duration); //Set running time to the output writer
    System.out.println("Writing the results into the file "+outputFileName);
    writer.writeFile(); //write the file
    System.out.println("DONE");
    System.out.println("Please check the results in the file "+outputFileName);
    System.out.println("Program ended");
  }
  //Return the number of differnet bits between 2 strings
  public int numberOfDifferentBits(String bits1, String bits2) {
	  int count = 0;
	  for(int i=0; i < bits1.length(); i++) {
		  if(bits1.charAt(i) != bits2.charAt(i))
			  count++;
	  }
	  return count;
  }
  //Flip a bit at a targeted position in a string
  public String ChangeABit(String bits, int location) {
	  char bit = bits.charAt(location);
	  bit = bit == '1' ? '0' : '1';
	  if(location == 0)
		  bits = bit + bits.substring(location + 1);
	  else
		  bits = bits.substring(0, location) + bit + bits.substring(location + 1);

	  return bits;
  }
  //Change a string from binary string to a hex string
  public String binToHex (String bin){
    String[] splitStr = bin.split("(?<=\\G........)");
    String str = "";
    for (int i = 0; i < splitStr.length; i++){
      int temp = Integer.parseInt(splitStr[i], 2);
      str += String.format("%02x", temp);
    }
    return str;
  }
  //Change a State object to a binary string
  public String hexToBin (State hex){
    String str = "";
    for (int j = 0; j < 4; j++){
      for (int i = 0; i < 4; i++){
        String temp = Integer.toBinaryString(
                      Integer.parseInt(hex.getBox(i, j), 16)
                      );
        String formatPad = "%"+(8)+"s";
        str += String.format(formatPad, temp).replace(" ", "0");
      }
    }
    return str;
  }
  //Key Expansion method
  public void keyExpansion(){
		stateKeys    = new State[11];
	  stateKeys[0] = stateKey;
	  for (int i = 1; i < stateKeys.length; i++){
			stateKeys[i] = stateKeys[i-1].genRoundKey(rcon[i-1]);
	  }
  }
  public void AES0(boolean isDecrypt, int modified){
		if (isDecrypt){
			for (int round = 10; round > 0; round--){
				stateCipherText.addRoundKey(stateKeys[round]);
				if (round < 10){ stateCipherText.mixColumns(inverseMatrix); }
				stateCipherText.shiftRows(isDecrypt);
				stateCipherText.subBytes(isDecrypt);
			}
			stateCipherText.addRoundKey(stateKeys[0]);
			statePlainText = stateCipherText;
		}
		else {
			// Add to avalanche
			aval[modified][0][0] = hexToBin(statePlainText);
			//Round 0
			statePlainText.addRoundKey(stateKeys[0]);
			//Round 1 to Round 9 standard operations
			for (int round = 1; round <= 10; round++){
				statePlainText.subBytes(isDecrypt);
				statePlainText.shiftRows(isDecrypt);
				if (round < 10){ statePlainText.mixColumns(matrix); }
				statePlainText.addRoundKey(stateKeys[round]);

				// Add to avalanche
				aval[modified][0][round] = hexToBin(statePlainText);
			}
			stateCipherText = statePlainText;
		}
  }
  public void AES1(boolean isDecrypt, int modified){
    if (isDecrypt){
  		for (int round = 10; round > 0; round--){
  			stateCipherText.addRoundKey(stateKeys[round]);
  			if (round < 10){ stateCipherText.mixColumns(inverseMatrix); }
  			statePlainText.shiftRows(isDecrypt);
  		}
  		stateCipherText.addRoundKey(stateKeys[0]);
  		statePlainText = stateCipherText;
  	}
  	else {
  		// Add to avalanche
  		aval[modified][1][0] = hexToBin(statePlainText);
  		//Round 0
  		statePlainText.addRoundKey(stateKeys[0]);
  		//Round 1 to Round 9 standard operations
  		for (int round = 1; round <= 10; round++){
  			statePlainText.shiftRows(isDecrypt);
  			if (round < 10){ statePlainText.mixColumns(matrix); }
  			statePlainText.addRoundKey(stateKeys[round]);

  			// Add to avalanche
  			aval[modified][1][round] = hexToBin(statePlainText);
  		}
  		stateCipherText = statePlainText;
  	}
   }
  public void AES2(boolean isDecrypt, int modified){
     if (isDecrypt){
  		for (int round = 10; round > 0; round--){
  			stateCipherText.addRoundKey(stateKeys[round]);
  			if (round < 10){ stateCipherText.mixColumns(inverseMatrix); }
  			statePlainText.subBytes(isDecrypt);
  		}
  		stateCipherText.addRoundKey(stateKeys[0]);
  		statePlainText = stateCipherText;
  	}
  	else {
  		// Add to avalanche
  		aval[modified][2][0] = hexToBin(statePlainText);
  		//Round 0
  		statePlainText.addRoundKey(stateKeys[0]);
  		//Round 1 to Round 9 standard operations
  		for (int round = 1; round <= 10; round++){
  			statePlainText.subBytes(isDecrypt);
  			if (round < 10){ statePlainText.mixColumns(matrix); }
  			statePlainText.addRoundKey(stateKeys[round]);

  			// Add to avalanche
  			aval[modified][2][round] = hexToBin(statePlainText);
  		}
  		stateCipherText = statePlainText;
  	}
   }
  public void AES3(boolean isDecrypt, int modified){
     if (isDecrypt){
  		for (int round = 10; round > 0; round--){
  			stateCipherText.addRoundKey(stateKeys[round]);
  			statePlainText.shiftRows(isDecrypt);
  			statePlainText.subBytes(isDecrypt);
  		}
  		stateCipherText.addRoundKey(stateKeys[0]);
  		statePlainText = stateCipherText;
  	}
  	else {
  		// Add to avalanche
  		aval[modified][3][0] = hexToBin(statePlainText);
  		//Round 0
  		statePlainText.addRoundKey(stateKeys[0]);
  		//Round 1 to Round 9 standard operations
  		for (int round = 1; round <= 10; round++){
  			statePlainText.subBytes(isDecrypt);
  			statePlainText.shiftRows(isDecrypt);
  			statePlainText.addRoundKey(stateKeys[round]);

  			// Add to avalanche
  			aval[modified][3][round] = hexToBin(statePlainText);
  		}
  		stateCipherText = statePlainText;
  	}
   }
  public void AES4(boolean isDecrypt, int modified){
     if (isDecrypt){
  		for (int round = 10; round > 0; round--){
  			if (round < 10){ stateCipherText.mixColumns(inverseMatrix); }
  			statePlainText.shiftRows(isDecrypt);
  			statePlainText.subBytes(isDecrypt);
  		}
  		statePlainText = stateCipherText;
  	}
  	else {
  		// Add to avalanche
  		aval[modified][4][0] = hexToBin(statePlainText);
  		//Round 0
      statePlainText.addRoundKey(stateKeys[0]);
  		//Round 1 to Round 9 standard operations
  		for (int round = 1; round <= 10; round++){
  			statePlainText.subBytes(isDecrypt);
  			statePlainText.shiftRows(isDecrypt);
  			if (round < 10){ statePlainText.mixColumns(matrix); }
  			// Add to avalanche
  			aval[modified][4][round] = hexToBin(statePlainText);
  		}
  		stateCipherText = statePlainText;
  	}
   }
}
