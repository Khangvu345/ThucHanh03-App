package com.example.baith3;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Định dạng tiền tệ
 */
public class MoneyFormatter {

    private MoneyFormatter() {
        // Lop tien ich, khong can tao doi tuong
    }

    public static String formatVnd(long amount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        DecimalFormat formatter = new DecimalFormat("#,###", symbols);
        return formatter.format(amount) + " đ";
    }
}
