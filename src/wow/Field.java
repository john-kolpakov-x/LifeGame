package wow;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Field {

  private Set<Pos> lives = new HashSet<>();

  public void set(int x, int y, boolean isLife) {
    Pos pos = Pos.get(x, y);
    if (isLife) {
      lives.add(pos);
    } else {
      lives.remove(pos);
    }
  }

  public boolean get(int x, int y) {
    return lives.contains(Pos.get(x, y));
  }

  public Set<Pos> getLives() {
    return Collections.unmodifiableSet(lives);
  }

  public Rect area() {

    int xMin = 0, xMax = 0, yMin = 0, yMax = 0;
    boolean first = true;

    for (Pos pos : lives) {
      if (first) {
        xMin = xMax = pos.x;
        yMin = yMax = pos.y;
        first = false;
      } else {
        if (xMin > pos.x) xMin = pos.x;
        if (xMax < pos.x) xMax = pos.x;
        if (yMin > pos.y) yMin = pos.y;
        if (yMax < pos.y) yMax = pos.y;
      }
    }

    return new Rect(Pos.get(xMin, yMin), Pos.get(xMax, yMax));
  }

  public long countAround(Pos pos, Set<Pos> lives) {
    return around(pos).filter(lives::contains).count();
  }

  public synchronized void step() {

    Set<Pos> lives = this.lives;

    Set<Pos> deathsToLive = lives.stream()
      .flatMap(Field::around)
      .distinct()
      .filter(pos -> 3 == countAround(pos, lives))
      .collect(Collectors.toSet());


    Set<Pos> stayToLive = lives.stream()
      .filter(pos -> {
        long count = countAround(pos, lives);
        return count == 2 || count == 3;
      })
      .collect(Collectors.toSet());

    stayToLive.addAll(deathsToLive);

    this.lives = stayToLive;

  }

  private static Stream<Pos> around(Pos pos) {
    return Arrays.stream(new Pos[]{
      Pos.get(pos.x - 1, pos.y - 1),
      Pos.get(pos.x - 1, pos.y),
      Pos.get(pos.x - 1, pos.y + 1),

      Pos.get(pos.x, pos.y + 1),

      Pos.get(pos.x + 1, pos.y + 1),
      Pos.get(pos.x + 1, pos.y),
      Pos.get(pos.x + 1, pos.y - 1),

      Pos.get(pos.x, pos.y - 1),
    });
  }

}
