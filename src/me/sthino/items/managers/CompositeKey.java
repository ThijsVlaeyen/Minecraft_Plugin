package me.sthino.items.managers;

import java.util.UUID;

public class CompositeKey {
    private final UUID uuid;
    private final int integer;

    public CompositeKey(UUID uuid, int integer) {
        this.uuid = uuid;
        this.integer = integer;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof CompositeKey) {
            CompositeKey c = (CompositeKey) obj;
            return uuid.equals(c.uuid) && integer == c.integer;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode() + integer;
    }
}
