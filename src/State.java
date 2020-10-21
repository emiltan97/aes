/**
* @author : Emil && Brett
* Last Edited : 15th May 2019
* Purpose : declaring state object
*/

public class State {
  //Private variables
  private final int MAX_ROWS    = 4;
  private final int MAX_COLUMNS = 4;
  private String box[][]        = new String[MAX_ROWS][MAX_COLUMNS];
  private Sbox sbox             = new Sbox(); //Sbox for bytes substitutions
  //Default constructor
  public State (){}
  //Custom constructor
  public State (String str){
    String[] splitStr = str.split("(?<=\\G..)");
    int index = 0;
    //insert every 2 characters in the string using for loop
    for (int j = 0; j < MAX_COLUMNS; j++){
      for (int i = 0; i < MAX_ROWS; i++){
        box[i][j] = splitStr[index];
        index++;
      }
    }
  }
  //Functions
  //TODO delete the method below
  public void print(){
    for (int i = 0; i < MAX_ROWS; i++){
      for (int j = 0; j < MAX_COLUMNS; j++){
        System.out.print(box[i][j]+" ");
      }
      System.out.println();
    }
    System.out.println();
  }
  //Add round key algorithm in AES
  public void addRoundKey(State key){
    for (int i = 0; i < MAX_ROWS; i++){
      for (int j = 0; j < MAX_COLUMNS; j++){
        //XOR operations
        int hex     = Integer.parseInt(box[i][j], 16); //Convert hex string to decimal
        int hex2    = Integer.parseInt(key.getBox(i,j), 16); //Convert hex string to decimal
        int temp    = hex ^ hex2; //XOR
        box[i][j]   = String.format("%02x", temp); //format string with leading zero
      }
    }
  }
  //bytes substitutions algorithm in AES
  public void subBytes(boolean isDecrypt){
    String pos; //Coordinates of the content retrieved on the table
    String temp; //Content retrieved from the table
    for (int i = 0; i < MAX_ROWS; i++){
      for (int j = 0; j < MAX_COLUMNS; j++){
        pos       = box[i][j];
        temp      = sbox.getBox(pos, isDecrypt);
        box[i][j] = temp;
      }
    }
  }
  //rows shifting algorithm in AES
  public void shiftRows(boolean isDecrypt){
    for (int i = 0; i < MAX_ROWS; i++){
      for (int times = 0; times < i; times++){
        if (isDecrypt){ //Right shifting in decrypt
    			int j = 3;
    			String temp = box[i][j];
    			while (j > 0){
    				box[i][j] = box[i][j-1];
    				j--;
    			}
    			box[i][0] = temp;
    		}
    		else { //Left shifting in encrypt
    			int j = 0;
    			String temp = box[i][j];
    			while (j+1 < MAX_COLUMNS){ //shifting
    			  box[i][j] = box[i][j+1];
    			  j++;
    			}
    			box[i][MAX_COLUMNS-1] = temp;
    		}
      }
    }
  }
  //mix columns algorithm in AES
  public void mixColumns(String[][] matrix){
    String results[] = new String[MAX_ROWS]; //results from XOR operation of the corresponding column
    int temp[] = new int[MAX_COLUMNS]; //temporary result of multiplication
    int result; //temporary result from XOR operation of a SINGLE box
    for (int j = 0; j < MAX_COLUMNS; j++){
      for (int i = 0; i < MAX_ROWS; i++){
        for (int j2 = 0; j2 < MAX_COLUMNS; j2++){
          temp[j2] = product(box[j2][j], matrix[i][j2]); //Multiplication in Galois
        }
        result = temp[0] ^ temp[1] ^ temp[2] ^ temp[3]; //XOR operation of the column
        results[i] = String.format("%02x", result);
      }
      for (int i = 0; i < MAX_ROWS; i++){ box[i][j] = results[i]; }
    }
  }
  //generate round key according to the key schedule
  public State genRoundKey(String[] rcon){
    State newKey = new State();
    //Take out the last column of the current round key
    String[] arr = new String[MAX_ROWS];
    for (int i = 0; i < MAX_ROWS; i++){
      arr[i] = box[i][MAX_COLUMNS-1];
    }
    //Do the subBytes
    for (int i = 0; i < MAX_ROWS; i++){
      arr[i] = sbox.getBox(arr[i], false);
    }
    //Do the shifting
    String temp = arr[0];
    for (int i = 0; i < (MAX_ROWS-1); i++){
      arr[i] = arr[i+1];
    }
    arr[MAX_ROWS-1] = temp;
  	//Do the XOR
  	for (int i = 0; i < MAX_ROWS; i++){
      //converting the required parameters from hex string to decimal
  		int a = Integer.parseInt(box[i][0], 16);
  		int b = Integer.parseInt(arr[i], 16);
  		int c = Integer.parseInt(rcon[i], 16);
      int d = c ^ b;
  		arr[i] = String.format("%02x", (a ^ d));
  	}
  	//generate new roundkey
  	for (int i = 0; i < MAX_ROWS; i++){
  		newKey.box[i][0] = arr[i];
  	}
  	for (int j = 1; j < MAX_COLUMNS; j++){
  		for (int i = 0; i < MAX_ROWS; i++){
        //converting the required parameters from hex string to decimal
  			int a = Integer.parseInt(box[i][j], 16);
  			int b = Integer.parseInt(newKey.box[i][j-1], 16);
  			newKey.box[i][j] = String.format("%02x", (a ^ b));
  		}
  	}
	  return newKey;
  }
  //Multiplication method used in Galois Field
  public int product(String hexA, String hexB) {
    int a = Integer.parseInt(hexA, 16);
    int b = Integer.parseInt(hexB, 16);
    int c = 0;
    for (int i = 0; i < 8; i++) {
      c = ((b & 0x01) > 0) ? c ^ a : c;
      boolean ho = ((a & 0x80) > 0);
      a = ((a << 1) & 0xFE);
      if (ho) { a = a ^ 0x1b; }
      b = ((b >> 1) & 0x7F);
    }
    return c;
  }
  //Setters
  public void setBox(int i, int j, String box){ this.box[i][j] = box; }
  //Getters
  public String getBox(int i, int j){ return box[i][j]; }
}
