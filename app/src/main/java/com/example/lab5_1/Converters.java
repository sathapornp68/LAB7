package com.example.lab5_1;

import androidx.room.TypeConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Room TypeConverter class for converting CheckList items to String and vice-versa.
 */
public class Converters {

    @TypeConverter
    public static String fromCheckListItems(List<CheckListItem> items) {
        if (items == null || items.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            CheckListItem item = items.get(i);
            sb.append(item.isChecked() ? "1:" : "0:").append(item.getText());
            if (i < items.size() - 1) {
                sb.append(";;;");
            }
        }
        return sb.toString();
    }

    @TypeConverter
    public static List<CheckListItem> toCheckListItems(String data) {
        List<CheckListItem> items = new ArrayList<>();
        if (data == null || data.trim().isEmpty()) {
            return items;
        }
        String[] parts = data.split(";;;");
        for (String part : parts) {
            if (part.contains(":")) {
                int colonIndex = part.indexOf(":");
                boolean isChecked = "1".equals(part.substring(0, colonIndex));
                String text = part.substring(colonIndex + 1);
                items.add(new CheckListItem(text, isChecked));
            }
        }
        return items;
    }
}
