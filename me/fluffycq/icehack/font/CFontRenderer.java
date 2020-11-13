//Deobfuscated with https://github.com/PetoPetko/Minecraft-Deobfuscator3000 using mappings "1.12 stable mappings"!

package me.fluffycq.icehack.font;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;

public class CFontRenderer extends CFont {
  public void setAntiAlias(boolean paramBoolean) {
    super.setAntiAlias(paramBoolean);
    setupBoldItalicIDs();
  }
  
  public List<String> wrapWords(String paramString, double paramDouble) {
    ArrayList<String> arrayList = new ArrayList();
    if (getStringWidth(paramString) > paramDouble) {
      String[] arrayOfString = paramString.split(" ");
      String str = "";
      char c = '￿';
      for (String str1 : arrayOfString) {
        for (byte b = 0; b < (str1.toCharArray()).length; b++) {
          char c1 = str1.toCharArray()[b];
          if (c1 == '§' && b < (str1.toCharArray()).length - 1)
            c = str1.toCharArray()[b + 1]; 
        } 
        if (getStringWidth(String.valueOf((new StringBuilder()).append(str).append(str1).append(" "))) < paramDouble) {
          str = String.valueOf((new StringBuilder()).append(str).append(str1).append(" "));
        } else {
          arrayList.add(str);
          str = String.valueOf((new StringBuilder()).append("§").append(c).append(str1).append(" "));
        } 
      } 
      if (str.length() > 0)
        if (getStringWidth(str) < paramDouble) {
          arrayList.add(String.valueOf((new StringBuilder()).append("§").append(c).append(str).append(" ")));
          str = "";
        } else {
          for (String str1 : formatString(str, paramDouble))
            arrayList.add(str1); 
        }  
    } else {
      arrayList.add(paramString);
    } 
    return arrayList;
  }
  
  public CFontRenderer(Font paramFont, boolean paramBoolean1, boolean paramBoolean2) {
    super(paramFont, paramBoolean1, paramBoolean2);
    setupMinecraftColorcodes();
    setupBoldItalicIDs();
  }
  
  public float drawStringWithShadow(String paramString, double paramDouble1, double paramDouble2, int paramInt) {
    float f = drawString(paramString, paramDouble1 + 1.0D, paramDouble2 + 1.0D, paramInt, true);
    return Math.max(f, drawString(paramString, paramDouble1, paramDouble2, paramInt, false));
  }
  
  public void setFractionalMetrics(boolean paramBoolean) {
    super.setFractionalMetrics(paramBoolean);
    setupBoldItalicIDs();
  }
  
  public int getStringWidth(String paramString) {
    if (paramString == null)
      return 0; 
    int i = 0;
    CFont.CharData[] arrayOfCharData = this.charData;
    boolean bool1 = false;
    boolean bool2 = false;
    int j = paramString.length();
    for (byte b = 0; b < j; b++) {
      char c = paramString.charAt(b);
      if (c == '§' && b < j) {
        int k = "0123456789abcdefklmnor".indexOf(c);
        if (k < 16) {
          bool1 = false;
          bool2 = false;
        } else if (k == 17) {
          bool1 = true;
          if (bool2) {
            arrayOfCharData = this.boldItalicChars;
          } else {
            arrayOfCharData = this.boldChars;
          } 
        } else if (k == 20) {
          bool2 = true;
          if (bool1) {
            arrayOfCharData = this.boldItalicChars;
          } else {
            arrayOfCharData = this.italicChars;
          } 
        } else if (k == 21) {
          bool1 = false;
          bool2 = false;
          arrayOfCharData = this.charData;
        } 
        b++;
      } else if (c < arrayOfCharData.length && c >= '\000') {
        i += (arrayOfCharData[c]).width - 8 + this.charOffset;
      } 
    } 
    return i / 2;
  }
  
  public List<String> formatString(String paramString, double paramDouble) {
    ArrayList<String> arrayList = new ArrayList();
    String str = "";
    char c = '￿';
    char[] arrayOfChar = paramString.toCharArray();
    for (byte b = 0; b < arrayOfChar.length; b++) {
      char c1 = arrayOfChar[b];
      if (c1 == '§' && b < arrayOfChar.length - 1)
        c = arrayOfChar[b + 1]; 
      if (getStringWidth(String.valueOf((new StringBuilder()).append(str).append(c1))) < paramDouble) {
        str = String.valueOf((new StringBuilder()).append(str).append(c1));
      } else {
        arrayList.add(str);
        str = String.valueOf((new StringBuilder()).append("§").append(c).append(String.valueOf(c1)));
      } 
    } 
    if (str.length() > 0)
      arrayList.add(str); 
    return arrayList;
  }
  
  public float drawCenteredString(String paramString, float paramFloat1, float paramFloat2, int paramInt) {
    return drawString(paramString, paramFloat1 - (getStringWidth(paramString) / 2), paramFloat2, paramInt);
  }
  
  public float drawString(String paramString, double paramDouble1, double paramDouble2, int paramInt, boolean paramBoolean) {
    paramDouble1--;
    paramDouble2 -= 2.0D;
    if (paramString == null)
      return 0.0F; 
    if (paramInt == 553648127)
      paramInt = 16777215; 
    if ((paramInt & 0xFC000000) == 0)
      paramInt |= 0xFF000000; 
    if (paramBoolean)
      paramInt = (paramInt & 0xFCFCFC) >> 2 | paramInt & 0xFF000000; 
    CFont.CharData[] arrayOfCharData = this.charData;
    float f = (paramInt >> 24 & 0xFF) / 255.0F;
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    boolean bool4 = false;
    boolean bool5 = false;
    boolean bool6 = true;
    paramDouble1 *= 2.0D;
    paramDouble2 *= 2.0D;
    if (bool6) {
      GL11.glPushMatrix();
      GlStateManager.scale(0.5D, 0.5D, 0.5D);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 771);
      GlStateManager.color((paramInt >> 16 & 0xFF) / 255.0F, (paramInt >> 8 & 0xFF) / 255.0F, (paramInt & 0xFF) / 255.0F, f);
      int i = paramString.length();
      GlStateManager.enableTexture2D();
      GlStateManager.bindTexture(this.tex.getGlTextureId());
      GL11.glBindTexture(3553, this.tex.getGlTextureId());
      for (byte b = 0; b < i; b++) {
        char c = paramString.charAt(b);
        if (c == '§' && b < i) {
          int j = 21;
          try {
            j = "0123456789abcdefklmnor".indexOf(paramString.charAt(b + 1));
          } catch (Exception exception) {}
          if (j < 16) {
            bool2 = false;
            bool3 = false;
            bool1 = false;
            bool5 = false;
            bool4 = false;
            GlStateManager.bindTexture(this.tex.getGlTextureId());
            arrayOfCharData = this.charData;
            if (j < 0 || j > 15)
              j = 15; 
            if (paramBoolean)
              j += 16; 
            int k = this.colorCode[j];
            GlStateManager.color((k >> 16 & 0xFF) / 255.0F, (k >> 8 & 0xFF) / 255.0F, (k & 0xFF) / 255.0F, f);
          } else if (j == 16) {
            bool1 = true;
          } else if (j == 17) {
            bool2 = true;
            if (bool3) {
              GlStateManager.bindTexture(this.texItalicBold.getGlTextureId());
              arrayOfCharData = this.boldItalicChars;
            } else {
              GlStateManager.bindTexture(this.texBold.getGlTextureId());
              arrayOfCharData = this.boldChars;
            } 
          } else if (j == 18) {
            bool4 = true;
          } else if (j == 19) {
            bool5 = true;
          } else if (j == 20) {
            bool3 = true;
            if (bool2) {
              GlStateManager.bindTexture(this.texItalicBold.getGlTextureId());
              arrayOfCharData = this.boldItalicChars;
            } else {
              GlStateManager.bindTexture(this.texItalic.getGlTextureId());
              arrayOfCharData = this.italicChars;
            } 
          } else if (j == 21) {
            bool2 = false;
            bool3 = false;
            bool1 = false;
            bool5 = false;
            bool4 = false;
            GlStateManager.color((paramInt >> 16 & 0xFF) / 255.0F, (paramInt >> 8 & 0xFF) / 255.0F, (paramInt & 0xFF) / 255.0F, f);
            GlStateManager.bindTexture(this.tex.getGlTextureId());
            arrayOfCharData = this.charData;
          } 
          b++;
        } else if (c < arrayOfCharData.length && c >= '\000') {
          GL11.glBegin(4);
          drawChar(arrayOfCharData, c, (float)paramDouble1, (float)paramDouble2);
          GL11.glEnd();
          if (bool4)
            drawLine(paramDouble1, paramDouble2 + ((arrayOfCharData[c]).height / 2), paramDouble1 + (arrayOfCharData[c]).width - 8.0D, paramDouble2 + ((arrayOfCharData[c]).height / 2), 1.0F); 
          if (bool5)
            drawLine(paramDouble1, paramDouble2 + (arrayOfCharData[c]).height - 2.0D, paramDouble1 + (arrayOfCharData[c]).width - 8.0D, paramDouble2 + (arrayOfCharData[c]).height - 2.0D, 1.0F); 
          paramDouble1 += ((arrayOfCharData[c]).width - 8 + this.charOffset);
        } 
      } 
      GL11.glHint(3155, 4352);
      GL11.glPopMatrix();
    } 
    return (float)paramDouble1 / 2.0F;
  }
  
  public float drawString(String paramString, float paramFloat1, float paramFloat2, int paramInt) {
    return drawString(paramString, paramFloat1, paramFloat2, paramInt, false);
  }
  
  public void setFont(Font paramFont) {
    super.setFont(paramFont);
    setupBoldItalicIDs();
  }
  
  private void drawLine(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4, float paramFloat) {
    GL11.glDisable(3553);
    GL11.glLineWidth(paramFloat);
    GL11.glBegin(1);
    GL11.glVertex2d(paramDouble1, paramDouble2);
    GL11.glVertex2d(paramDouble3, paramDouble4);
    GL11.glEnd();
    GL11.glEnable(3553);
  }
  
  public float drawCenteredStringWithShadow(String paramString, float paramFloat1, float paramFloat2, int paramInt) {
    return drawStringWithShadow(paramString, (paramFloat1 - (getStringWidth(paramString) / 2)), paramFloat2, paramInt);
  }
  
  private void setupMinecraftColorcodes() {
    for (byte b = 0; b < 32; b++) {
      int i = (b >> 3 & 0x1) * 85;
      int j = (b >> 2 & 0x1) * 170 + i;
      int k = (b >> 1 & 0x1) * 170 + i;
      int m = (b >> 0 & 0x1) * 170 + i;
      if (b == 6)
        j += 85; 
      if (b >= 16) {
        j /= 4;
        k /= 4;
        m /= 4;
      } 
      this.colorCode[b] = (j & 0xFF) << 16 | (k & 0xFF) << 8 | m & 0xFF;
    } 
  }
  
  private void setupBoldItalicIDs() {
    this.texBold = setupTexture(this.font.deriveFont(1), this.antiAlias, this.fractionalMetrics, this.boldChars);
    this.texItalic = setupTexture(this.font.deriveFont(2), this.antiAlias, this.fractionalMetrics, this.italicChars);
    this.texItalicBold = setupTexture(this.font.deriveFont(3), this.antiAlias, this.fractionalMetrics, this.boldItalicChars);
  }
}
