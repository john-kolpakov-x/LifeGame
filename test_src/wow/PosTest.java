package wow;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import org.testng.annotations.Test;


import static org.fest.assertions.Assertions.assertThat;

public class PosTest {
  @Test
  public void get() throws Exception {
    assertThat(Pos.get(0, 0)).isNotNull();
  }

  private static final double GIG = 1_000_000_000.0;

  @Test
  public void hello() throws Exception {

    final AtomicBoolean working = new AtomicBoolean(true);

    new Thread(() -> {
      try {
        File file = new File("build/_hello_is_working_");
        file.getParentFile().mkdirs();
        file.createNewFile();

        while (true) {
          Thread.sleep(500);
          if (!file.exists()) break;
        }

        working.set(false);

      } catch (Exception e) {
        throw new RuntimeException(e);
      }

    }).start();

    long time1_2 = 0, time2_3 = 0, time3_4 = 0, count = 0, res1 = 0, res2 = 0;

    long[] arr = new long[10_000_000];
    int u = 0;

    while (working.get()) {

      long time1 = System.nanoTime();

      for (int i = 0, n = arr.length; i < n; i++) {
        arr[i] = i + 200 + u;
      }

      u++;

      long time2 = System.nanoTime();

      res1 = Arrays.stream(arr).parallel().filter(x -> x > 20001).sum();

      long time3 = System.nanoTime();

      res2 = Arrays.stream(arr).filter(x -> x > 20001).sum();

      long time4 = System.nanoTime();

      time1_2 += time2 - time1;
      time2_3 += time3 - time2;
      time3_4 += time4 - time3;
      count++;
    }

    System.out.println("res1 = " + res1);
    System.out.println("res2 = " + res2);
    System.out.println("time1_2 = " + time1_2 / GIG / count);
    System.out.println("time2_3 = " + time2_3 / GIG / count);
    System.out.println("time3_4 = " + time3_4 / GIG / count);
    System.out.println("count = " + count);

  }
}
