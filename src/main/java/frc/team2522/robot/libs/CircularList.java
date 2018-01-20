package frc.team2522.robot.libs;

import java.util.ArrayList;

// Note: This is a lazy implementation:
//      only get, add, and size are implemented

public class CircularList<E> extends ArrayList<E> {
    private final int maxSize;
    private int head = 0;

    public CircularList(int size) {
        super(size);
        this.maxSize = size;
    }

    @Override
    public E get(int index)  {
        return super.get((head + index) % maxSize);
    }

    @Override
    public boolean add(E e) {
        if(super.size() < maxSize) {
            return super.add(e);
        }

        if(head + 1 == maxSize) {
            super.set(head, e);
            head = 0;
        } else {
            super.set(head++, e);
        }

        return true;
    }
}
