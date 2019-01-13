package net.silentchaos512.wit.lib;

public enum EnumHudPosition {
    TOP_LEFT, TOP_CENTER, TOP_RIGHT, CENTER_LEFT, CENTER_RIGHT, BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT;

    public static final int PADDING = 5;

//    public Tuple getStartingPosition(HudRenderObject renderObject, ScaledResolution res) {
//        Minecraft mc = Minecraft.getInstance();
//        int screenWidth = mc.mainWindow.getScaledWidth();
//        int screenHeight = mc.mainWindow.getScaledHeight();
//        int objectWidth = renderObject.getWidth();
//        int objectHeight = renderObject.getHeight();
//
//        switch (this) {
//            case BOTTOM_LEFT:
//                return new Tuple(PADDING, screenHeight - PADDING - objectHeight);
//            case BOTTOM_CENTER:
//                return new Tuple(screenWidth / 2 - objectWidth / 2, (int) (screenHeight * 0.7f)); // FIXME?
//            case BOTTOM_RIGHT:
//                return new Tuple(screenWidth - PADDING - objectWidth, screenHeight - PADDING - objectHeight);
//            case CENTER_LEFT:
//                return new Tuple(PADDING, screenHeight / 2 - objectHeight / 2);
//            case CENTER_RIGHT:
//                return new Tuple(screenWidth - PADDING - objectWidth, screenHeight / 2 - objectHeight / 2);
//            case TOP_CENTER:
//                return new Tuple(screenWidth / 2 - objectWidth / 2, PADDING);
//            case TOP_LEFT:
//                return new Tuple(PADDING, PADDING);
//            case TOP_RIGHT:
//                return new Tuple(screenWidth - PADDING - objectWidth, PADDING);
//            default:
//                return new Tuple(0, 0);
//        }
//    }

    public static String[] getValidValues() {

        String[] result = new String[values().length];
        for (int i = 0; i < values().length; ++i) {
            result[i] = values()[i].name();
        }
        return result;
    }
}
