/**
* @author : Emil && Brett
* Last Edited : 15th May 2019
* Purpose : Writing the output into a file
*/

import java.io.*;
import java.util.*;

public class OutputWriter {

  private BufferedWriter writer;
  private boolean isDecrypt;
  private String plainText;
  private String cipherText;
  private String key;
  private long duration;
  private int[][] aes; //P and Pi under K
  private int[][] aes2; //P under K and Ki

  public OutputWriter(String fileName, boolean isDecrypt)
  throws IOException {
    this.isDecrypt = isDecrypt;
    writer = new BufferedWriter(
             new FileWriter(fileName)
             );
  }

  public void writeFile(){
    ArrayList<String> lines = new ArrayList<String>();
    if (isDecrypt){ //Print decrypt output
      lines.add("DECRYPTION");
      lines.add("Ciphertext C: "+cipherText);
      lines.add("Key K: "+key);
      lines.add("Plaintext P: "+plainText);
    }
    else { //Print encrypt output
      lines.add("ENCRYPTION");
      lines.add("Plaintext P: "+plainText);
      lines.add("Key K: "+key);
      lines.add("Ciphertext C: "+cipherText);
      lines.add("Running time: "+duration+" ms");
      lines.add("Avalanche: ");
      lines.add("P and Pi under K");
      lines.add("Round\t\tAES0\t\tAES1\t\tAES2\t\tAES3\t\tAES4");
      for (int round = 0; round < 11; round++){
        lines.add(
          String.format(
            "%5d%15d%16d%16d%16d%16d",
            round, aes[0][round], aes[1][round],
            aes[2][round], aes[3][round], aes[4][round]
          )
        );
      }
      lines.add("P under K and Ki");
      lines.add("Round\t\tAES0\t\tAES1\t\tAES2\t\tAES3\t\tAES4");
      for (int round = 0; round < 11; round++){
        lines.add(
          String.format(
            "%5d%15d%16d%16d%16d%16d",
            round, aes2[0][round], aes2[1][round],
            aes2[2][round], aes2[3][round], aes2[4][round]
          )
        );
      }
    }
    try {
      for (String line : lines){
        writer.write(line); //Print output
        writer.newLine(); //Next line
      }
      writer.close();
    }
    catch (IOException e){ e.printStackTrace(); }
  }
  //Setters
  public void setPlainText(String plainText){ this.plainText = plainText; }
  public void setCipherText(String cipherText){ this.cipherText = cipherText; }
  public void setKey(String key){ this.key = key; }
  public void setDuration(long duration){ this.duration = duration; }
  public void setAES(int[][] aes){ this.aes = aes; }
  public void setAES2(int[][] aes2){ this.aes2 = aes2; }
}
