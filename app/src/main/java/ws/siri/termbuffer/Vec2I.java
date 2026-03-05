package ws.siri.termbuffer;

/**
 * int * int pair
 */
public class Vec2I {
    public int x;
    public int y;

    public Vec2I(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", x, y);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Vec2I))
            return false;

        Vec2I other = (Vec2I) obj;

        return other.x == x && other.y == y;
    }
}
