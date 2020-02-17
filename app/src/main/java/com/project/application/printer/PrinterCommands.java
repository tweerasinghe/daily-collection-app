package com.project.application.printer;

public class PrinterCommands {
    public static final byte[] INIT = {27, 64};
    public static byte[] FEED_LINE = {10};
    public static byte[] FEED_LINE_1 = {1};
    public static byte[] CENTER = {0x1b, 'a', 0x01}; // center alignment

    private static byte[] arrayOfByte1 = {27, 33, 0};
    public static byte[] formatSize = {27, 33, ((byte) (0x20 | arrayOfByte1[2]))};


    public static byte[] SELECT_FONT_A = {27, 33, 0};

    public static byte[] SET_BAR_CODE_HEIGHT = {29, 104, 100};
    public static byte[] PRINT_BAR_CODE_1 = {29, 107, 2};
    public static byte[] SEND_NULL_BYTE = {0x00};

    public static byte[] SELECT_PRINT_SHEET = {0x1B, 0x63, 0x30, 0x02};
    public static byte[] FEED_PAPER_AND_CUT = {0x1D, 0x56, 66, 0x00};
    public static byte[] SELECT_CYRILLIC_CHARACTER_CODE_TABLE = {0x1B, 0x74, 0x11};

//    public static byte[] SELECT_BIT_IMAGE_MODE = {0x1B, 0x2A, 33, -128, 0};

    //    public static byte[] SELECT_BIT_IMAGE_MODE = {0x1B, 0x2A, 33, (byte) 99, 0};
    public static byte[] SET_LINE_SPACING_24 = {0x1B, 0x33, 24};
    public static byte[] SET_LINE_SPACING_1 = {0x1B, 0x33, 1};
    public static byte[] SET_LINE_SPACING_30 = {0x1B, 0x33, 30};

    public static byte[] TRANSMIT_DLE_PRINTER_STATUS = {0x10, 0x04, 0x01};
    public static byte[] TRANSMIT_DLE_OFFLINE_PRINTER_STATUS = {0x10, 0x04, 0x02};
    public static byte[] TRANSMIT_DLE_ERROR_STATUS = {0x10, 0x04, 0x03};
    public static byte[] TRANSMIT_DLE_ROLL_PAPER_SENSOR_STATUS = {0x10, 0x04, 0x04};


    public static byte[] SELECT_BIT_IMAGE_MODE(int imageWidth) {

        int hb = (byte) (imageWidth & 0xFF);
        int lb = (byte) ((imageWidth >> 8) & 0xFF);

        byte[] SELECT_BIT_IMAGE_MODE = {0x1B, 0x2A, 33, (byte) hb,(byte)lb};
        return SELECT_BIT_IMAGE_MODE;
    }
}
