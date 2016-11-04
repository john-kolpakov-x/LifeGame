package wow;

import java.math.BigDecimal;
import math.BigDecimalMath;

public class Calculus001 {
  public static void main(String[] args) {
    BigDecimal atanOne = BigDecimalMath.atan(BigDecimal.ONE.setScale(100));
    System.out.println("atanOne = " + atanOne);
    System.out.println("pi = " + atanOne.multiply(BigDecimal.valueOf(4L)));
  }
}
