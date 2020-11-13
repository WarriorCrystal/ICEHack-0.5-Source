package me.fluffycq.icehack.module;

public enum Category {
  MISC,
  HUD,
  EXPLOITS,
  COMBAT("Combat"),
  RENDER("Combat"),
  MOVEMENT("Combat");
  
  static {
    EXPLOITS = new Category("EXPLOITS", 1, "Exploit");
    MOVEMENT = new Category("MOVEMENT", 2, "Movement");
    MISC = new Category("MISC", 3, "Misc");
    RENDER = new Category("RENDER", 4, "Render");
    HUD = new Category("HUD", 5, "HUD");
    $VALUES = new Category[] { COMBAT, EXPLOITS, MOVEMENT, MISC, RENDER, HUD };
  }
  
  Category(String paramString1) {
    this.categoryName = paramString1;
  }
  
  public String toString() {
    return this.categoryName;
  }
}
