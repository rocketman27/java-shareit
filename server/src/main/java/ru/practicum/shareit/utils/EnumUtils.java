package ru.practicum.shareit.utils;

public class EnumUtils {

    public static <T extends Enum<T>> T findEnumValue(Class<T> type, String name) {
        if (name == null)
            return null;
        try {
            return Enum.valueOf(type, name.toUpperCase());
        } catch (IllegalArgumentException iae) {
            return null;
        }
    }
}
