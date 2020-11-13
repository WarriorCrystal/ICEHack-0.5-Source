//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.friends;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;

public class Friends {
  public void createFriends() {
    File file1 = new File((Minecraft.getMinecraft()).gameDir.getAbsolutePath(), "\\ICEHack\\friends\\");
    if (!file1.exists())
      file1.mkdir(); 
    File file2 = new File((Minecraft.getMinecraft()).gameDir.getAbsolutePath(), "\\ICEHack\\friends\\friendlist.txt");
    if (!file2.exists())
      try {
        file2.createNewFile();
      } catch (IOException iOException) {
        iOException.printStackTrace();
      }  
  }
  
  public Friends() {
    loadFriends();
  }
  
  public void removeFriend(String paramString) {
    if (isFriend(paramString))
      tempFriends.remove(paramString.toLowerCase()); 
  }
  
  public void addFriend(String paramString) {
    if (!isFriend(paramString.toLowerCase()))
      tempFriends.add(paramString.toLowerCase()); 
  }
  
  public void loadFriends() {
    try (BufferedReader null = new BufferedReader(new FileReader(new File((Minecraft.getMinecraft()).gameDir.getAbsolutePath(), "\\ICEHack\\friends\\friendlist.txt")))) {
      String str;
      while ((str = bufferedReader.readLine()) != null)
        tempFriends.add(str.toLowerCase()); 
    } catch (FileNotFoundException fileNotFoundException) {
      fileNotFoundException.printStackTrace();
    } catch (IOException iOException) {
      iOException.printStackTrace();
    } 
  }
  
  public void saveFriends() {
    BufferedWriter bufferedWriter = null;
    File file = new File((Minecraft.getMinecraft()).gameDir.getAbsolutePath(), "\\ICEHack\\friends\\friendlist.txt");
    try {
      bufferedWriter = new BufferedWriter(new FileWriter(file));
    } catch (IOException iOException) {
      iOException.printStackTrace();
    } 
    if (bufferedWriter != null) {
      try {
        for (String str : tempFriends)
          bufferedWriter.write(String.valueOf((new StringBuilder()).append(str).append("\r\n"))); 
      } catch (IOException iOException) {
        iOException.printStackTrace();
      } 
    } else {
      System.out.println("[icehack] Could not add friend!");
    } 
    try {
      bufferedWriter.close();
    } catch (IOException iOException) {
      iOException.printStackTrace();
    } 
  }
  
  public static boolean isFriend(String paramString) {
    return tempFriends.contains(paramString.toLowerCase());
  }
}
