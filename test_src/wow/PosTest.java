import org.testng.annotations.Test;
import wow.Pos;

import static org.fest.assertions.Assertions.assertThat;

public class PosTest {
  @Test
  public void get() throws Exception {
    assertThat(Pos.get(0, 0)).isNotNull();
  }
}
