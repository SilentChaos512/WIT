package net.silentchaos512.wit.lib;

public enum EnumJustification {

  LEFT, CENTER, RIGHT;
  
  public int getPadding(int lengthDiff) {
    
    switch (this) {
      case CENTER:
        return lengthDiff / 2;
      case RIGHT:
        return lengthDiff;
      default:
        return 0;
    }
  }
}
