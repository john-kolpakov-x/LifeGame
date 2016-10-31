package wow;

import java.math.BigDecimal;
import java.math.RoundingMode;
import math.BigDecimalMath;

public class Calculus {
  public static void main(String[] args) {

    int precision = 130;

    BigDecimal a = new BigDecimal("2.3");
    BigDecimal b = new BigDecimal("3.1");

    a = a.setScale(precision, RoundingMode.HALF_UP);
    b = b.setScale(precision, RoundingMode.HALF_UP);

    BigDecimal pow = BigDecimalMath.pow(a, b);

    BigDecimal sin = BigDecimalMath.sin(a);

    BigDecimal atan1 = BigDecimalMath.atan(BigDecimal.ONE.setScale(precision, RoundingMode.HALF_UP))
      .multiply(BigDecimal.valueOf(4));

    System.out.println("pow = " + pow);
    System.out.println("sin = " + sin);

    System.out.println("atan1 = " + atan1);
  }
}
