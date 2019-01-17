package net.silentchaos512.wit.lib;

public enum TextAlignment {
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

    public static String[] getValidValues() {
        String[] result = new String[values().length];
        for (int i = 0; i < values().length; ++i) {
            result[i] = values()[i].name();
        }
        return result;
    }
}
