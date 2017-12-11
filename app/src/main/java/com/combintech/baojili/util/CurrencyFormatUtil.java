package com.combintech.baojili.util;

import android.widget.EditText;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by Hendry Setiadi
 */

public class CurrencyFormatUtil {
    private static final NumberFormat dotFormat = NumberFormat.getNumberInstance(new Locale("in", "id"));
    private static final NumberFormat commaFormat = NumberFormat.getNumberInstance(new Locale("en", "US"));

    public static class ThousandString {
        private String formattedString;
        private int selection;

        public ThousandString(String formattedString, int selection) {
            this.formattedString = formattedString;
            this.selection = selection;
        }

        public String getFormattedString() {
            return formattedString;
        }

        public int getSelection() {
            return selection;
        }

        public void setFormattedString(String formattedString) {
            this.formattedString = formattedString;
        }

        public void setSelection(int selection) {
            this.selection = selection;
        }
    }

    public static ThousandString getThousandSeparatorString(String textToFormat, boolean useComma, int selectionStart) {
        String formattedString = textToFormat;
        int cursorEnd = selectionStart;
        char separatorString = ',';

        int sourceLength = textToFormat.length();
        try {
            if (sourceLength > 0) {
                if (useComma) {
                    formattedString = commaFormat.format(Double.parseDouble(textToFormat));
                    separatorString = ',';
                } else {
                    formattedString = dotFormat.format(Double.parseDouble(textToFormat));
                    separatorString = '.';
                }
                // same with before, just return as is.
                int resultLength = formattedString.length();
                if (resultLength == sourceLength) {
                    return new ThousandString(textToFormat, resultLength);
                }

                int tempCursorPos = selectionStart;

                // Handler untuk tanda koma
                if (resultLength - selectionStart == 1) {
                    // Untuk majuin cursor ketika nambah koma
                    if (formattedString.length() < 4) {
                        tempCursorPos += 1;
                    } else if (formattedString.charAt(tempCursorPos) != separatorString) {
                        // Untuk mundur ketika mencoba menghapus koma
                        tempCursorPos += 1;
                    }
                } else if (resultLength - selectionStart == -1) {
                    // Mundurin cursor ketika hapus koma
                    tempCursorPos -= 1;
                } else if (resultLength > 3 && selectionStart < resultLength && selectionStart > 0) {
                    if (formattedString.charAt(selectionStart - 1) == separatorString) {
                        // Mundurin cursor ketika menambah digit dibelakang koma
                        tempCursorPos -= 1;
                    }
                } else {
                    tempCursorPos = resultLength;
                }

                // Set posisi cursor
                if (tempCursorPos < resultLength && tempCursorPos > -1) {
                    cursorEnd = tempCursorPos;
                } else if (tempCursorPos < 0) {
                    cursorEnd = 0;
                } else {
                    cursorEnd = resultLength;
                }
                return new ThousandString(formattedString, cursorEnd);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ThousandString(textToFormat, selectionStart);
        }
        return new ThousandString(textToFormat, selectionStart);
    }
}
