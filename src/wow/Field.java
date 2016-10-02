package wow;

import java.util.Arrays;
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

  public boolean isLife(Pos pos) {
    return lives.contains(pos);
  }

  public long countAround(Pos pos, Set<Pos> lives) {
    return around(pos).filter(lives::contains).count();
  }

  public void step() {

    Set<Pos> lives = this.lives;

    Set<Pos> deathsToLive = lives.parallelStream()
      .flatMap(Field::around)
      .distinct()
      .filter(pos -> 3 == countAround(pos, lives))
      .collect(Collectors.toSet());


    Set<Pos> stayToLive = lives.parallelStream()
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
