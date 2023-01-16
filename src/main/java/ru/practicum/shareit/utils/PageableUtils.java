package ru.practicum.shareit.utils;

public class PageableUtils {

    public static boolean isInvalidFromAndSize(int from, int size) {
        return from < 0 || size < 0 || size == 0;
    }
}
