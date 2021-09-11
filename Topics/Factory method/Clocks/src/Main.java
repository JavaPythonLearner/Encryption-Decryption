import java.util.Scanner;

/* Product - Clock */
interface Clock {
    void tick();
}

/* Concrete Product - Sand Clock */
class SandClock implements Clock {
    @Override
    public void tick() {
        System.out.println("...sand noise...");
    }
}

/* Concrete Product - Digital Clock */
class DigitalClock implements Clock {
    @Override
    public void tick() {
        System.out.println("...pim...");
    }
}

/* Concrete Product - Mechanical Clock */
class MechanicalClock implements Clock {
    @Override
    public void tick() {
        System.out.println("...clang mechanism...");
    }
}

class ClockFactory {
    /* It produces concrete clocks corresponding their types : Digital, Sand or Mechanical */
    public Clock produce(String type) {
        // write your code here ...
        Clock clock;
        switch (type.toUpperCase()) {
            case "SAND":
                clock = new SandClock();
                break;
            case "DIGITAL":
                clock = new DigitalClock();
                break;
            case "MECHANICAL":
                clock = new MechanicalClock();
                break;
            default:
                clock = null;
                break;
        }
        return clock;
    }
}

public class Main {
    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        final String type = scanner.next();
        final ClockFactory clockFactory = new ClockFactory();
        final Clock clock = clockFactory.produce(type);
        clock.tick();
        scanner.close();
    }
}