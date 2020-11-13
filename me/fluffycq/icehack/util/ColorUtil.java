package me.fluffycq.icehack.util;

import java.awt.Color;

public class ColorUtil {
  public ColorUtil(float[] paramArrayOffloat) {
    this(paramArrayOffloat, 1.0F);
  }
  
  public Color GetColorWithBrightness(float paramFloat) {
    return GetRainbowColor(this.m_HSB[0], this.m_HSB[1], paramFloat, this.m_Alpha);
  }
  
  public ColorUtil(float paramFloat1, float paramFloat2, float paramFloat3) {
    this(paramFloat1, paramFloat2, paramFloat3, 1.0F);
  }
  
  public Color GetColorWithLightnessMin(float paramFloat) {
    paramFloat = (100.0F + paramFloat) / 100.0F;
    paramFloat = Math.min(100.0F, this.m_HSB[2] * paramFloat);
    return GetRainbowColor(this.m_HSB[0], this.m_HSB[1], paramFloat, this.m_Alpha);
  }
  
  public ColorUtil(float[] paramArrayOffloat, float paramFloat) {
    this.m_HSB = paramArrayOffloat;
    this.m_Alpha = paramFloat;
    this.m_BaseColor = GetRainbowColorFromArray(paramArrayOffloat, paramFloat);
  }
  
  public static String getColor(String paramString) {
    String str = "";
    switch (paramString) {
      case "&0":
        str = "§0";
        break;
      case "&1":
        str = "§1";
        break;
      case "&2":
        str = "§2";
        break;
      case "&3":
        str = "§3";
        break;
      case "&4":
        str = "§4";
        break;
      case "&5":
        str = "§5";
        break;
      case "&6":
        str = "§6";
        break;
      case "&7":
        str = "§7";
        break;
      case "&8":
        str = "§8";
        break;
      case "&9":
        str = "§9";
        break;
      case "&a":
        str = "§a";
        break;
      case "&b":
        str = "§b";
        break;
      case "&c":
        str = "§c";
        break;
      case "&d":
        str = "§d";
        break;
      case "&e":
        str = "§e";
        break;
      case "&f":
        str = "§f";
        break;
    } 
    return str;
  }
  
  public Color GetColorWithHue(float paramFloat) {
    return GetRainbowColor(paramFloat, this.m_HSB[1], this.m_HSB[2], this.m_Alpha);
  }
  
  public float GetLightness() {
    return this.m_HSB[2];
  }
  
  public float GetSaturation() {
    return this.m_HSB[1];
  }
  
  public static float[] GenerateHSB(Color paramColor) {
    float f7;
    float f9;
    float[] arrayOfFloat = paramColor.getRGBColorComponents(null);
    float f1 = arrayOfFloat[0];
    float f2 = arrayOfFloat[1];
    float f3 = arrayOfFloat[2];
    float f4 = Math.min(f1, Math.min(f2, f3));
    float f5 = Math.max(f1, Math.max(f2, f3));
    float f6 = 0.0F;
    if (f5 == f4) {
      f6 = 0.0F;
      f7 = f5;
    } else if (f5 == f1) {
      f6 = (60.0F * (f2 - f3) / (f5 - f4) + 360.0F) % 360.0F;
      f7 = f5;
    } else if (f5 == f2) {
      f6 = 60.0F * (f3 - f1) / (f5 - f4) + 120.0F;
      f7 = f5;
    } else {
      if (f5 == f3)
        f6 = 60.0F * (f1 - f2) / (f5 - f4) + 240.0F; 
      f7 = f5;
    } 
    float f8 = (f7 + f4) / 2.0F;
    if (f5 == f4) {
      f9 = 0.0F;
    } else {
      float f10 = Math.min(f8, 0.5F);
      float f11 = f5;
      if (f10 <= 0.0F) {
        f9 = (f11 - f4) / (f5 + f4);
      } else {
        f9 = (f11 - f4) / (2.0F - f5 - f4);
      } 
    } 
    return new float[] { f6, f9 * 100.0F, f8 * 100.0F };
  }
  
  public static String GenerateMCColorString(String paramString) {
    byte b1 = 113;
    byte b2 = 24;
    int i = paramString.length();
    char[] arrayOfChar1 = new char[i];
    int j = i - 1;
    int k = j;
    char[] arrayOfChar2 = arrayOfChar1;
    byte b3 = 24;
    byte b4 = 113;
    while (k >= 0) {
      char[] arrayOfChar3 = arrayOfChar2;
      int m = j;
      char c = paramString.charAt(m);
      j--;
      arrayOfChar3[m] = (char)(c ^ 0x71);
      if (j < 0)
        break; 
      char[] arrayOfChar4 = arrayOfChar2;
      int n = j--;
      arrayOfChar4[n] = (char)(paramString.charAt(n) ^ 0x18);
      k = j;
    } 
    return new String(arrayOfChar2);
  }
  
  public static Color GetColorWithHSBArray(float[] paramArrayOffloat) {
    return GetRainbowColorFromArray(paramArrayOffloat, 1.0F);
  }
  
  public float GetAlpha() {
    return this.m_Alpha;
  }
  
  public float GetHue() {
    return this.m_HSB[0];
  }
  
  public Color GetColorWithSaturation(float paramFloat) {
    return GetRainbowColor(this.m_HSB[0], paramFloat, this.m_HSB[2], this.m_Alpha);
  }
  
  public static Color GetRainbowColorFromArray(float[] paramArrayOffloat, float paramFloat) {
    return GetRainbowColor(paramArrayOffloat[0], paramArrayOffloat[1], paramArrayOffloat[2], paramFloat);
  }
  
  public String toString() {
    return String.valueOf((new StringBuilder()).insert(0, "HSLColor[h=").append(this.m_HSB[0]).append(",s=").append(this.m_HSB[1]).append(",l=").append(this.m_HSB[2]).append(",alpha=").append(this.m_Alpha).append("]"));
  }
  
  public Color GetColorWithModifiedHue() {
    return ColorRainbowWithDefaultAlpha((this.m_HSB[0] + 180.0F) % 360.0F, this.m_HSB[1], this.m_HSB[2]);
  }
  
  public ColorUtil(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4) {
    byte b = 3;
    float[] arrayOfFloat = new float[3];
    arrayOfFloat[0] = paramFloat1;
    arrayOfFloat[1] = paramFloat2;
    arrayOfFloat[2] = paramFloat3;
    this.m_HSB = arrayOfFloat;
    this.m_Alpha = paramFloat4;
    this.m_BaseColor = GetRainbowColorFromArray(this.m_HSB, paramFloat4);
  }
  
  public ColorUtil(Color paramColor) {
    this.m_BaseColor = paramColor;
    this.m_HSB = GenerateHSB(paramColor);
    this.m_Alpha = paramColor.getAlpha() / 255.0F;
  }
  
  public static Color GetRainbowColor(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4) {
    float f1;
    if (paramFloat2 < 0.0F || paramFloat2 > 100.0F)
      throw new IllegalArgumentException("Color parameter outside of expected range - Saturation"); 
    if (paramFloat3 < 0.0F || paramFloat3 > 100.0F)
      throw new IllegalArgumentException("Color parameter outside of expected range - Lightness"); 
    if (paramFloat4 < 0.0F || paramFloat4 > 1.0F)
      throw new IllegalArgumentException("Color parameter outside of expected range - Alpha"); 
    paramFloat1 = (paramFloat1 %= 360.0F) / 360.0F;
    paramFloat2 /= 100.0F;
    paramFloat3 /= 100.0F;
    if (paramFloat3 < 0.0D) {
      f1 = paramFloat3 * (1.0F + paramFloat2);
    } else {
      f1 = paramFloat3 + paramFloat2 - paramFloat2 * paramFloat3;
    } 
    paramFloat2 = 2.0F * paramFloat3 - f1;
    paramFloat3 = Math.max(0.0F, FutureClientColorCalculation(paramFloat2, f1, paramFloat1 + 0.33333334F));
    float f2 = Math.max(0.0F, FutureClientColorCalculation(paramFloat2, f1, paramFloat1));
    paramFloat2 = Math.max(0.0F, FutureClientColorCalculation(paramFloat2, f1, paramFloat1 - 0.33333334F));
    paramFloat3 = Math.min(paramFloat3, 1.0F);
    float f3 = Math.min(f2, 1.0F);
    paramFloat2 = Math.min(paramFloat2, 1.0F);
    return new Color(paramFloat3, f3, paramFloat2, paramFloat4);
  }
  
  public Color GetColorWithLightnessMax(float paramFloat) {
    paramFloat = (100.0F - paramFloat) / 100.0F;
    paramFloat = Math.max(0.0F, this.m_HSB[2] * paramFloat);
    return GetRainbowColor(this.m_HSB[0], this.m_HSB[1], paramFloat, this.m_Alpha);
  }
  
  public Color GetLocalColor() {
    return this.m_BaseColor;
  }
  
  public static Color ColorRainbowWithDefaultAlpha(float paramFloat1, float paramFloat2, float paramFloat3) {
    return GetRainbowColor(paramFloat1, paramFloat2, paramFloat3, 1.0F);
  }
  
  private static float FutureClientColorCalculation(float paramFloat1, float paramFloat2, float paramFloat3) {
    if (paramFloat3 < 0.0F)
      paramFloat3++; 
    if (paramFloat3 > 1.0F)
      paramFloat3--; 
    return (6.0F * paramFloat3 < 1.0F) ? (paramFloat1 + (paramFloat2 - paramFloat1) * 6.0F * paramFloat3) : ((2.0F * paramFloat3 < 1.0F) ? paramFloat2 : ((3.0F * paramFloat3 < 2.0F) ? (paramFloat1 + (paramFloat2 - paramFloat1) * 6.0F * (0.6666667F - paramFloat3)) : paramFloat1));
  }
}
