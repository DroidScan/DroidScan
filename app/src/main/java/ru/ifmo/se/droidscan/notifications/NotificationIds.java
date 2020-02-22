package ru.ifmo.se.droidscan.notifications;

public enum NotificationIds {
    CAMERA(435);

    private final int value;

    private NotificationIds(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
