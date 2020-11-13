package me.fluffycq.icehack.font;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;

public class CFont {
  public boolean isAntiAlias() {
    return this.antiAlias;
  }
  
  public void setFont(Font paramFont) {
    this.font = paramFont;
    this.tex = setupTexture(paramFont, this.antiAlias, this.fractionalMetrics, this.charData);
  }
  
  public int getStringHeight(String paramString) {
    return getHeight();
  }
  
  public void setFractionalMetrics(boolean paramBoolean) {
    if (this.fractionalMetrics != paramBoolean) {
      this.fractionalMetrics = paramBoolean;
      this.tex = setupTexture(this.font, this.antiAlias, paramBoolean, this.charData);
    } 
  }
  
  public CFont(Font paramFont, boolean paramBoolean1, boolean paramBoolean2) {
    this.font = paramFont;
    this.antiAlias = paramBoolean1;
    this.fractionalMetrics = paramBoolean2;
    this.tex = setupTexture(paramFont, paramBoolean1, paramBoolean2, this.charData);
  }
  
  public int getStringWidth(String paramString) {
    int i = 0;
    for (char c : paramString.toCharArray()) {
      if (c < this.charData.length && c >= '\000')
        i += (this.charData[c]).width - 8 + this.charOffset; 
    } 
    return i / 2;
  }
  
  public void drawChar(CharData[] paramArrayOfCharData, char paramChar, float paramFloat1, float paramFloat2) throws ArrayIndexOutOfBoundsException {
    try {
      drawQuad(paramFloat1, paramFloat2, (paramArrayOfCharData[paramChar]).width, (paramArrayOfCharData[paramChar]).height, (paramArrayOfCharData[paramChar]).storedX, (paramArrayOfCharData[paramChar]).storedY, (paramArrayOfCharData[paramChar]).width, (paramArrayOfCharData[paramChar]).height);
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
  }
  
  protected void drawQuad(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5, float paramFloat6, float paramFloat7, float paramFloat8) {
    float f1 = paramFloat5 / this.imgSize;
    float f2 = paramFloat6 / this.imgSize;
    float f3 = paramFloat7 / this.imgSize;
    float f4 = paramFloat8 / this.imgSize;
    GL11.glTexCoord2f(f1 + f3, f2);
    GL11.glVertex2d((paramFloat1 + paramFloat3), paramFloat2);
    GL11.glTexCoord2f(f1, f2);
    GL11.glVertex2d(paramFloat1, paramFloat2);
    GL11.glTexCoord2f(f1, f2 + f4);
    GL11.glVertex2d(paramFloat1, (paramFloat2 + paramFloat4));
    GL11.glTexCoord2f(f1, f2 + f4);
    GL11.glVertex2d(paramFloat1, (paramFloat2 + paramFloat4));
    GL11.glTexCoord2f(f1 + f3, f2 + f4);
    GL11.glVertex2d((paramFloat1 + paramFloat3), (paramFloat2 + paramFloat4));
    GL11.glTexCoord2f(f1 + f3, f2);
    GL11.glVertex2d((paramFloat1 + paramFloat3), paramFloat2);
  }
  
  public int getHeight() {
    return (this.fontHeight - 8) / 2;
  }
  
  public Font getFont() {
    return this.font;
  }
  
  public void setAntiAlias(boolean paramBoolean) {
    if (this.antiAlias != paramBoolean) {
      this.antiAlias = paramBoolean;
      this.tex = setupTexture(this.font, paramBoolean, this.fractionalMetrics, this.charData);
    } 
  }
  
  protected DynamicTexture setupTexture(Font paramFont, boolean paramBoolean1, boolean paramBoolean2, CharData[] paramArrayOfCharData) {
    BufferedImage bufferedImage = generateFontImage(paramFont, paramBoolean1, paramBoolean2, paramArrayOfCharData);
    try {
      return new DynamicTexture(bufferedImage);
    } catch (Exception exception) {
      exception.printStackTrace();
      return null;
    } 
  }
  
  protected BufferedImage generateFontImage(Font paramFont, boolean paramBoolean1, boolean paramBoolean2, CharData[] paramArrayOfCharData) {
    int i = (int)this.imgSize;
    BufferedImage bufferedImage = new BufferedImage(i, i, 2);
    Graphics2D graphics2D = (Graphics2D)bufferedImage.getGraphics();
    graphics2D.setFont(paramFont);
    graphics2D.setColor(new Color(255, 255, 255, 0));
    graphics2D.fillRect(0, 0, i, i);
    graphics2D.setColor(Color.WHITE);
    graphics2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, paramBoolean2 ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
    graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, paramBoolean1 ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, paramBoolean1 ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
    FontMetrics fontMetrics = graphics2D.getFontMetrics();
    int j = 0;
    int k = 0;
    int m = 1;
    for (byte b = 0; b < paramArrayOfCharData.length; b++) {
      char c = (char)b;
      CharData charData = new CharData();
      Rectangle2D rectangle2D = fontMetrics.getStringBounds(String.valueOf(c), graphics2D);
      charData.width = (rectangle2D.getBounds()).width + 8;
      charData.height = (rectangle2D.getBounds()).height;
      if (k + charData.width >= i) {
        k = 0;
        m += j;
        j = 0;
      } 
      if (charData.height > j)
        j = charData.height; 
      charData.storedX = k;
      charData.storedY = m;
      if (charData.height > this.fontHeight)
        this.fontHeight = charData.height; 
      paramArrayOfCharData[b] = charData;
      graphics2D.drawString(String.valueOf(c), k + 2, m + fontMetrics.getAscent());
      k += charData.width;
    } 
    return bufferedImage;
  }
  
  public boolean isFractionalMetrics() {
    return this.fractionalMetrics;
  }
  
  protected class CharData {}
}
