package tests;

import frc.team2522.robot.libs.CircularList;

import org.junit.*;
import static org.junit.Assert.*;

public class TestCircularList {
    @Test
    public void TestMaxSize() {
        CircularList<Double> circularList = new CircularList<Double>(3);
        circularList.add(0.0);
        circularList.add(1.0);
        circularList.add(2.0);

        assertTrue(circularList.size() == 3);
        assertTrue(circularList.get(0) == 0.0);
        assertTrue(circularList.get(1) == 1.0);
        assertTrue(circularList.get(2) == 2.0);
    }

    @Test
    public void TestWrapValues() {
        CircularList<Double> circularList = new CircularList<Double>(3);
        circularList.add(1.0);
        circularList.add(2.0);
        circularList.add(3.0);
        circularList.add(4.0);

        assertTrue(circularList.size() == 3);
        assertTrue(circularList.get(0) == 2.0);
        assertTrue(circularList.get(1) == 3.0);
        assertTrue(circularList.get(2) == 4.0);
    }

    @Test
    public void TestWrapHead() {
        CircularList<Double> circularList = new CircularList<Double>(3);
        circularList.add(1.0);
        circularList.add(2.0);
        circularList.add(3.0);
        circularList.add(4.0);
        circularList.add(5.0);
        circularList.add(6.0);
        circularList.add(7.0);

        assertTrue(circularList.size() == 3);
        assertTrue(circularList.get(0) == 5.0);
        assertTrue(circularList.get(1) == 6.0);
        assertTrue(circularList.get(2) == 7.0);
    }
}

