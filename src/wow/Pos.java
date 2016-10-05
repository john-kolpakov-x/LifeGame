package wow;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Pos {
  public final int x, y;

  private Pos(int x, int y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Pos pos = (Pos) o;

    if (x != pos.x) return false;
    return y == pos.y;

  }

  @Override
  public int hashCode() {
    int result = x;
    result = 31 * result + y;
    return result;
  }

  private static final Map<Integer, Map<Integer, Pos>> exists = new HashMap<>();

  public static Stream<Pos> allWentStream() {
    return exists.values().stream().flatMap(m -> m.values().stream());
  }

  public static Pos get(int x, int y) {
    Map<Integer, Pos> xRow = exists.get(x);

    if (xRow == null) {
      synchronized (exists) {
        xRow = exists.get(x);

        if (xRow == null) {
          xRow = new HashMap<>();
          exists.put(x, xRow);
        }
      }
    }

    Pos pos = xRow.get(y);
    if (pos == null) {
      synchronized (exists) {
        pos = xRow.get(y);
        if (pos == null) {
          xRow.put(y, pos = new Pos(x, y));
        }
      }
    }

    return pos;
  }

  public Pos add(Pos delta) {
    return get(x + delta.x, y + delta.y);
  }
}
