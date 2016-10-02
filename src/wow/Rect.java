package wow;

public class Rect {
  public final Pos from, to;

  public Rect(Pos from, Pos to) {
    this.from = from;
    this.to = to;
  }

  public int width() {
    return to.x - from.x;
  }

  public int height() {
    return to.y - from.y;
  }
}
