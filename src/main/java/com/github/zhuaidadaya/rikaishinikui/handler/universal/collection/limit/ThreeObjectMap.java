package com.github.zhuaidadaya.rikaishinikui.handler.universal.collection.limit;

public class ThreeObjectMap<V> {
    private V o1;
    private V o2;
    private V o3;

    public void put(int slot, V value) {
        switch (slot % 3) {
            case 0 -> strictPut(3, value);
            case 1, - 1 -> strictPut(1, value);
            case 2, - 2 -> strictPut(2, value);
        }
    }

    public void strictPut(int slot, V value) {
        switch (slot) {
            case 1 -> o1 = value;
            case 2 -> o2 = value;
            case 3 -> o3 = value;
            default -> throw new IllegalArgumentException("Cannot index slot " + slot + " in bounds 3");
        }
    }

    public V get(int slot) {
        return switch (slot % 3) {
            case 0 -> strictGet(3);
            case 1, - 1 -> strictGet(1);
            case 2, - 2 -> strictGet(2);
            default -> throw new IllegalArgumentException("Cannot index slot " + slot + " in bounds 3");
        };
    }

    public V strictGet(int slot) {
        return switch (slot) {
            case 1 -> o1;
            case 2 -> o2;
            case 3 -> o3;
            default -> throw new IllegalArgumentException("Cannot index slot " + slot + " in bounds 3");
        };
    }
}
