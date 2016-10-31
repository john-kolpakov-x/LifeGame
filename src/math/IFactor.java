package math;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Vector;

/**
 * Factored integers.
 * This class contains a non-negative integer with the prime factor decomposition attached.
 *
 * @author Richard J. Mathar
 * @since 2012-02-14 The internal representation contains the bases, and becomes sparser if few
 * prime factors are present.
 */
public class IFactor implements Cloneable, Comparable<IFactor> {
  /**
   * The standard representation of the number
   */
  public BigInteger n;

  /*
  * The bases and powers of the prime factorization.
  * representation n = primeExp[0]^primeExp[1]*primeExp[2]^primeExp[3]*...
  * The value 0 is represented by an empty vector, the value 1 by a vector of length 1
  * with a single power of 0.
  */
  public Vector<Integer> primeExp;

  final public static IFactor ONE = new IFactor(1);

  final public static IFactor ZERO = new IFactor(0);

  /**
   * Constructor given an integer.
   * constructor with an ordinary integer
   *
   * @param number the standard representation of the integer
   * @author Richard J. Mathar
   */
  public IFactor(int number) {
    n = new BigInteger("" + number);
    primeExp = new Vector<>();
    if (number > 1) {
      int primeIndex = 0;
      Prime primes = new Prime();
                        /* Test division against all primes.
                        */
      while (number > 1) {
        int ex = 0;
                                /* primeIndex=0 refers to 2, =1 to 3, =2 to 5, =3 to 7 etc
                                */
        int p = primes.at(primeIndex).intValue();
        while (number % p == 0) {
          ex++;
          number /= p;
          if (number == 1)
            break;
        }
        if (ex > 0) {
          primeExp.add(p);
          primeExp.add(ex);
        }
        primeIndex++;
      }
    } else if (number == 1) {
      primeExp.add(1);
      primeExp.add(0);
    }
  } /* IFactor */

  /**
   * Constructor given a BigInteger .
   * Constructor with an ordinary integer, calling a prime factor decomposition.
   *
   * @param number the BigInteger representation of the integer
   * @author Richard J. Mathar
   */
  public IFactor(BigInteger number) {
    n = number;
    primeExp = new Vector<>();
    if (number.compareTo(BigInteger.ONE) == 0) {
      primeExp.add(1);
      primeExp.add(0);
    } else {
      int primeIndex = 0;
      Prime primes = new Prime();
      /* Test for division against all primes.
      */
      while (number.compareTo(BigInteger.ONE) == 1) {
        int ex = 0;
        BigInteger p = primes.at(primeIndex);
        while (number.remainder(p).compareTo(BigInteger.ZERO) == 0) {
          ex++;
          number = number.divide(p);
          if (number.compareTo(BigInteger.ONE) == 0)
            break;
        }
        if (ex > 0) {
          primeExp.add(p.intValue());
          primeExp.add(ex);
        }
        primeIndex++;
      }
    }
  } /* IFactor */

  /**
   * Constructor given a list of exponents of the prime factor decomposition.
   *
   * @param powers the vector with the sorted list of exponents.
   *               powers[0] is the exponent of 2, powers[1] the exponent of 3, powers[2] the exponent of 5 etc.
   *               Note that this list does not include the primes, but assumes a continuous prime-smooth basis.
   * @author Richard J. Mathar
   */
  @SuppressWarnings("unused")
  public IFactor(Vector<Integer> powers) {
    primeExp = new Vector<>(2 * powers.size());
    if (powers.size() > 0) {
      n = BigInteger.ONE;
      Prime primes = new Prime();
                        /* Build the full number by the product of all powers of the primes.
                        */
      for (int primeIndex = 0; primeIndex < powers.size(); primeIndex++) {
        int ex = powers.elementAt(primeIndex);
        final BigInteger p = primes.at(primeIndex);
        n = n.multiply(p.pow(ex));
        primeExp.add(p.intValue());
        primeExp.add(ex);
      }
    } else {
      n = BigInteger.ZERO;
    }
  } /* IFactor */

  /**
   * Copy constructor.
   *
   * @param oth the value to be copied
   * @author Richard J. Mathar
   */
  @SuppressWarnings("unused")
  public IFactor(IFactor oth) {
    n = oth.n;
    primeExp = oth.primeExp;
  } /* IFactor */

  /**
   * Deep copy.
   *
   * @author Richard J. Mathar
   * @since 2009-08-14
   */
  @SuppressWarnings("CloneDoesntCallSuperClone")
  public IFactor clone() {
    throw new UnsupportedOperationException();
  } /* IFactor.clone */

  /**
   * Comparison of two numbers.
   * The value of this method is in allowing the Vector.contains() calls that use the value,
   * not the reference for comparison.
   *
   * @param oth the number to compare this with.
   * @return true if both are the same numbers, false otherwise.
   * @author Richard J. Mathar
   */
  public boolean equals(final IFactor oth) {
    return (n.compareTo(oth.n) == 0);
  } /* IFactor.equals */

  /**
   * Multiply with another positive integer.
   *
   * @param oth the second factor.
   * @return the product of both numbers.
   * @author Richard J. Mathar
   */
  public IFactor multiply(final BigInteger oth) {
                /* the optimization is to factorize oth _before_ multiplying
                */
    return (multiply(new IFactor(oth)));
  } /* IFactor.multiply */

  /**
   * Multiply with another positive integer.
   *
   * @param oth the second factor.
   * @return the product of both numbers.
   * @author Richard J. Mathar
   */
  public IFactor multiply(final int oth) {
                /* the optimization is to factorize oth _before_ multiplying
                */
    return (multiply(new IFactor(oth)));
  } /* IFactor.multiply */

  /**
   * Multiply with another positive integer.
   *
   * @param oth the second factor.
   * @return the product of both numbers.
   * @author Richard J. Mathar
   */
  public IFactor multiply(final IFactor oth) {
                /* This might be done similar to the lcm() implementation by adding
                * the powers of the components and calling the constructor with the
                * list of exponents. This here is the simplest implementation, but slow because
                * it calls another prime factorization of the product:
                * return( new IFactor(n.multiply(oth.n))) ;
                */
    return multiplyGcdLcm(oth, 0);
  }

  /**
   * Lowest common multiple of this with oth.
   *
   * @param oth the second parameter of lcm(this,oth)
   * @return the lowest common multiple of both numbers. Returns zero
   * if any of both arguments is zero.
   * @author Richard J. Mathar
   */
  @SuppressWarnings("unused")
  public IFactor lcm(final IFactor oth) {
    return multiplyGcdLcm(oth, 2);
  }

  /**
   * Greatest common divisor of this and oth.
   *
   * @param oth the second parameter of gcd(this,oth)
   * @return the lowest common multiple of both numbers. Returns zero
   * if any of both arguments is zero.
   * @author Richard J. Mathar
   */
  @SuppressWarnings("unused")
  public IFactor gcd(final IFactor oth) {
    return multiplyGcdLcm(oth, 1);
  }

  /**
   * Multiply with another positive integer.
   *
   * @param oth  the second factor.
   * @param type 0 to multiply, 1 for gcd, 2 for lcm
   * @return the product, gcd or lcm of both numbers.
   * @author Richard J. Mathar
   */
  protected IFactor multiplyGcdLcm(final IFactor oth, int type) {
    IFactor prod = new IFactor(0);
                /* skip the case where 0*something =0, falling through to the empty representation for 0
                */
    if (primeExp.size() != 0 && oth.primeExp.size() != 0) {
                        /* Cases of 1 times something return something.
                        * Cases of lcm(1, something) return something.
                        * Cases of gcd(1, something) return 1.
                        */
      if (primeExp.firstElement() == 1 && type == 0) return oth;
      if (primeExp.firstElement() == 1 && type == 2) return oth;
      if (primeExp.firstElement() == 1 && type == 1) return this;
      if (oth.primeExp.firstElement() == 1 && type == 0) return this;
      if (oth.primeExp.firstElement() == 1 && type == 2) return this;
      if (oth.primeExp.firstElement() == 1 && type == 1) return oth;

      {
        int idxThis = 0;
        int idxOth = 0;
        switch (type) {
          case 0:
            prod.n = n.multiply(oth.n);
            break;
          case 1:
            prod.n = n.gcd(oth.n);
            break;
          case 2:
                                        /* the awkward way, lcm = product divided by gcd
                                        */
            prod.n = n.multiply(oth.n).divide(n.gcd(oth.n));
            break;
        }

                                /* scan both representations left to right, increasing prime powers
                                */
        while (idxOth < oth.primeExp.size() || idxThis < primeExp.size()) {
          if (idxOth >= oth.primeExp.size()) {
                                                /* exhausted the list in oth.primeExp; copy over the remaining 'this'
                                                * if multiplying or lcm, discard if gcd.
                                                */
            if (type == 0 || type == 2) {
              prod.primeExp.add(primeExp.elementAt(idxThis));
              prod.primeExp.add(primeExp.elementAt(idxThis + 1));
            }
            idxThis += 2;
          } else if (idxThis >= primeExp.size()) {
                                                /* exhausted the list in primeExp; copy over the remaining 'oth'
                                                */
            if (type == 0 || type == 2) {
              prod.primeExp.add(oth.primeExp.elementAt(idxOth));
              prod.primeExp.add(oth.primeExp.elementAt(idxOth + 1));
            }
            idxOth += 2;
          } else {
            Integer p;
            int ex;
            switch (primeExp.elementAt(idxThis).compareTo(oth.primeExp.elementAt(idxOth))) {
              case 0:
                                                        /* same prime bases p in both factors */
                p = primeExp.elementAt(idxThis);
                switch (type) {
                  case 0:
                                                                /* product means adding exponents */
                    ex = primeExp.elementAt(idxThis + 1) + oth.primeExp.elementAt(idxOth + 1);
                    break;
                  case 1:
                                                                /* gcd means minimum of exponents */
                    ex = Math.min(primeExp.elementAt(idxThis + 1), oth.primeExp.elementAt(idxOth + 1));
                    break;
                  default:
                                                                /* lcm means maximum of exponents */
                    ex = Math.max(primeExp.elementAt(idxThis + 1), oth.primeExp.elementAt(idxOth + 1));
                    break;
                }
                prod.primeExp.add(p);
                prod.primeExp.add(ex);
                idxOth += 2;
                idxThis += 2;
                break;
              case 1:
                                                        /* this prime base bigger than the other and taken later */
                if (type == 0 || type == 2) {
                  prod.primeExp.add(oth.primeExp.elementAt(idxOth));
                  prod.primeExp.add(oth.primeExp.elementAt(idxOth + 1));
                }
                idxOth += 2;
                break;
              default:
                                                        /* this prime base smaller than the other and taken now */
                if (type == 0 || type == 2) {
                  prod.primeExp.add(primeExp.elementAt(idxThis));
                  prod.primeExp.add(primeExp.elementAt(idxThis + 1));
                }
                idxThis += 2;
            }
          }
        }
      }
    }
    return prod;
  } /* IFactor.multiplyGcdLcm */

  /**
   * Integer division through  another positive integer.
   *
   * @param oth the denominator.
   * @return the division of this through the oth, discarding the remainder.
   * @author Richard J. Mathar
   */
  public IFactor divide(final IFactor oth) {
    /* todo: it'd probably be faster to cancel the gcd(this,oth) first in the prime power
    * representation, which would avoid a more strenuous factorization of the integer ratio
    */
    return new IFactor(n.divide(oth.n));
  } /* IFactor.divide */

  /**
   * Summation with another positive integer
   *
   * @param oth the other term.
   * @return the sum of both numbers
   * @author Richard J. Mathar
   */
  public IFactor add(final BigInteger oth) {
                /* avoid refactorization if oth is zero...
                */
    if (oth.compareTo(BigInteger.ZERO) != 0)
      return new IFactor(n.add(oth));
    else
      return this;
  } /* IFactor.add */

  /**
   * Exponentiation with a positive integer.
   *
   * @param exponent the non-negative exponent
   * @return n^exponent. If exponent=0, the result is 1.
   * @author Richard J. Mathar
   */
  @SuppressWarnings("unused")
  public IFactor pow(final int exponent) throws ArithmeticException {
    /* three simple cases first
    */
    if (exponent < 0)
      throw new ArithmeticException("Cannot raise " + toString() + " to negative " + exponent);

    if (exponent == 0) return new IFactor(1);

    if (exponent == 1) return this;

    /*
    * general case, the vector with the prime factor powers, which are component-wise
    * exponentiation of the individual prime factor powers.
    */
    IFactor powers = new IFactor(0);
    for (int i = 0; i < primeExp.size(); i += 2) {
      Integer p = primeExp.elementAt(i);
      int ex = primeExp.elementAt(i + 1);
      powers.primeExp.add(p);
      powers.primeExp.add(ex * exponent);
    }
    return powers;
  } /* IFactor.pow */

  /**
   * Pulling the r-th root.
   *
   * @param r the positive or negative (nonzero) root.
   * @return n^(1/r).
   * The return value falls into the IFactor class if r is positive, but if r is negative
   * a Rational type is needed.
   * @author Richard J. Mathar
   * @since 2009-05-18
   */
  public Rational root(final int r) throws ArithmeticException {
    if (r == 0)
      throw new ArithmeticException("Cannot pull zeroth root of " + toString());
    else if (r < 0) {
                        /* a^(-1/b)= 1/(a^(1/b))
                        */
      final Rational invRoot = root(-r);
      return Rational.ONE.divide(invRoot);
    } else {
      BigInteger powers = BigInteger.ONE;
      for (int i = 0; i < primeExp.size(); i += 2) {
                                /* all exponents must be multiples of r to succeed (that is, to
                                * stay in the range of rational results).
                                */
        int ex = primeExp.elementAt(i + 1);
        if (ex % r != 0)
          throw new ArithmeticException("Cannot pull " + r + "th root of " + toString());

        powers.multiply(new BigInteger("" + primeExp.elementAt(i)).pow(ex / r));
      }
                        /* convert result to a Rational; unfortunately this will loose the prime factorization */
      return new Rational(powers);
    }
  } /* IFactor.root */


  /**
   * The set of positive divisors.
   *
   * @return the vector of divisors of the absolute value, sorted.
   * @author Richard J. Mathar
   * @since 2010-08-27
   */
  public Vector<BigInteger> divisors() {
                /* Recursive approach: the divisors of p1^e1*p2^e2*..*py^ey*pz^ez are
                * the divisors that don't contain  the factor pz, and the
                * divisors that contain any power of pz between 1 and up to ez multiplied
                * by 1 or by a product that contains the factors p1..py.
                */
    Vector<BigInteger> d = new Vector<>();
    if (n.compareTo(BigInteger.ZERO) == 0)
      return d;
    d.add(BigInteger.ONE);
    if (n.compareTo(BigInteger.ONE) > 0) {
      /* Computes sigmaInComplex(p1^e*p2^e2...*py^ey) */
      IFactor dp = dropPrime();

      /* get ez */
      final int ez = primeExp.lastElement();

      Vector<BigInteger> partD = dp.divisors();

      /* obtain pz by lookup in the prime list */
      final BigInteger pz = new BigInteger(primeExp.elementAt(primeExp.size() - 2).toString());

      /*
      * the output contains all products of the form partD[]*pz^ez, ez > 0,
      * and with the exception of the 1, all these are appended.
      */
      for (int i = 1; i < partD.size(); i++) {
        d.add(partD.elementAt(i));
      }

      for (int e = 1; e <= ez; e++) {
        final BigInteger pzEz = pz.pow(e);
        for (int i = 0; i < partD.size(); i++)
          d.add(partD.elementAt(i).multiply(pzEz));
      }
    }
    Collections.sort(d);
    return d;
  } /* IFactor.divisors */

  /**
   * Sum of the divisors of the number.
   *
   * @return the sum of all divisors of the number, 1+....+n.
   * @author Richard J. Mathar
   */
  public IFactor sigma() {
    return sigma(1);
  } /* IFactor.sigma */

  /**
   * Sum of the k-th powers of divisors of the number.
   *
   * @param k The exponent of the powers.
   * @return the sum of all divisors of the number, 1^k+....+n^k.
   * @author Richard J. Mathar
   */
  public IFactor sigma(int k) {
                /* the question is whether keeping a factorization  is worth the effort
                * or whether one should simply multiply these to return a BigInteger...
                */
    if (n.compareTo(BigInteger.ONE) == 0)
      return ONE;
    else if (n.compareTo(BigInteger.ZERO) == 0)
      return ZERO;
    else {
                        /* multiplicative: sigma_k(p^e) = [p^(k*(e+1))-1]/[p^k-1]
                        * sigma_0(p^e) = e+1.
                        */
      IFactor result = IFactor.ONE;
      for (int i = 0; i < primeExp.size(); i += 2) {
        int ex = primeExp.elementAt(i + 1);
        if (k == 0)
          result = result.multiply(ex + 1);
        else {
          Integer p = primeExp.elementAt(i);
          BigInteger num = (new BigInteger(p.toString())).pow(k * (ex + 1)).subtract(BigInteger.ONE);
          BigInteger dena = (new BigInteger(p.toString())).pow(k).subtract(BigInteger.ONE);
                                        /* This division is of course exact, no remainder
                                        * The costly prime factorization is hidden here.
                                        */
          IFactor f = new IFactor(num.divide(dena));
          result = result.multiply(f);
        }
      }
      return result;
    }
  } /* IFactor.sigma */

  /**
   * Divide through the highest possible power of the highest prime.
   * If the current number is the prime factor product p1^e1 * p2*e2* p3^e3*...*py^ey * pz^ez,
   * the value returned has the final factor pz^ez eliminated, which gives
   * p1^e1 * p2*e2* p3^e3*...*py^ey.
   *
   * @return the new integer obtained by removing the highest prime power.
   * If this here represents 0 or 1, it is returned without change.
   * @author Richard J. Mathar
   * @since 2006-08-20
   */
  public IFactor dropPrime() {
    /*
    * the cases n==1 or n ==0
    */
    if (n.compareTo(BigInteger.ONE) <= 0) {
      return this;
    }

    /*
    * The cases n>1
    * Start empty. Copy all but the last factor over to the result
    * the vector with the new prime factor powers, which contain the
    * old prime factor powers up to but not including the last one.
    */
    IFactor powers = new IFactor(0);
    powers.n = BigInteger.ONE;
    for (int i = 0; i < primeExp.size() - 2; i += 2) {
      powers.primeExp.add(primeExp.elementAt(i));
      powers.primeExp.add(primeExp.elementAt(i + 1));
      BigInteger p = new BigInteger(primeExp.elementAt(i).toString());
      int ex = primeExp.elementAt(i + 1);
      powers.n = powers.n.multiply(p.pow(ex));
    }
    return powers;
  } /* IFactor.dropPrime */

  /**
   * Test whether this is a square of an integer (perfect square).
   *
   * @return true if this is an integer squared (including 0), else false
   * @author Richard J. Mathar
   */
  @SuppressWarnings("unused")
  public boolean isSquare() {
    /*
    * check the exponents, located at the odd-indexed positions
    */
    for (int i = 1; i < primeExp.size(); i += 2) {
      if (primeExp.elementAt(i) % 2 != 0)
        return false;
    }
    return true;
  } /* IFactor.isSquare */

  /**
   * The sum of the prime factor exponents, with multiplicity.
   *
   * @return the sum over the primeExp numbers
   * @author Richard J. Mathar
   */
  @SuppressWarnings("unused")
  public int bigOmega() {
    int result = 0;
    for (int i = 1; i < primeExp.size(); i += 2)
      result += primeExp.elementAt(i);
    return (result);
  } /* IFactor.bigOmega */

  /**
   * The sum of the prime factor exponents, without multiplicity.
   *
   * @return the number of distinct prime factors.
   * @author Richard J. Mathar
   * @since 2008-10-16
   */
  @SuppressWarnings("unused")
  public int omega() {
    return primeExp.size() / 2;
  } /* IFactor.omega */

  /**
   * The square-free part.
   *
   * @return the minimum m such that m times this number is a square.
   * @author Richard J. Mathar
   * @since 2008-10-16
   */
  public BigInteger core() {
    BigInteger result = BigInteger.ONE;
    for (int i = 0; i < primeExp.size(); i += 2) {
      if (primeExp.elementAt(i + 1) % 2 != 0) {
        result = result.multiply(new BigInteger(primeExp.elementAt(i).toString()));
      }
    }
    return result;
  } /* IFactor.core */

  /**
   * The Moebius function.
   * 1 if n=1, else, if k is the number of distinct prime factors, return (-1)^k,
   * else, if k has repeated prime factors, return 0.
   *
   * @return the moebius function.
   * @author Richard J. Mathar
   */
  public int moebius() {
    if (n.compareTo(BigInteger.ONE) <= 0)
      return 1;
                /* accumulate number of different primes in k */
    int k = 1;
    for (int i = 0; i < primeExp.size(); i += 2) {
      final int e = primeExp.elementAt(i + 1);
      if (e > 1)
        return 0;
      else if (e == 1)
        /* accumulates (-1)^k */
        k *= -1;
    }
    return (k);
  } /* IFactor.moebius */

  /**
   * Maximum of two values.
   *
   * @param oth the number to compare this with.
   * @return the larger of the two values.
   * @author Richard J. Mathar
   */
  public IFactor max(final IFactor oth) {
    if (n.compareTo(oth.n) >= 0)
      return this;
    else
      return oth;
  } /* IFactor.max */

  /**
   * Minimum of two values.
   *
   * @param oth the number to compare this with.
   * @return the smaller of the two values.
   * @author Richard J. Mathar
   */
  public IFactor min(final IFactor oth) {
    if (n.compareTo(oth.n) <= 0)
      return this;
    else
      return oth;
  } /* IFactor.min */

  /**
   * Maximum of a list of values.
   *
   * @param set list of numbers.
   * @return the largest in the list.
   * @author Richard J. Mathar
   */
  @SuppressWarnings("unused")
  public static IFactor max(final Vector<IFactor> set) {
    IFactor result = set.elementAt(0);
    for (int i = 1; i < set.size(); i++)
      result = result.max(set.elementAt(i));
    return result;
  } /* IFactor.max */

  /**
   * Minimum of a list of values.
   *
   * @param set list of numbers.
   * @return the smallest in the list.
   * @author Richard J. Mathar
   */
  @SuppressWarnings("unused")
  public static IFactor min(final Vector<IFactor> set) {
    IFactor result = set.elementAt(0);
    for (int i = 1; i < set.size(); i++)
      result = result.min(set.elementAt(i));
    return result;
  } /* IFactor.min */

  /**
   * Compare value against another IFactor
   *
   * @param oth The value to be compared against.
   * @return 1, 0 or -1 according to being larger, equal to or smaller than oth.
   * @author Richard J. Mathar
   * @since 2012-02-15
   */
  public int compareTo(final IFactor oth) {
    return n.compareTo(oth.n);
  } /* compareTo */

  /**
   * Convert to printable format
   *
   * @return a string of the form n:prime^pow*prime^pow*prime^pow...
   * @author Richard J. Mathar
   */
  public String toString() {
    String result = n.toString() + ":";
    if (n.compareTo(BigInteger.ONE) == 0)
      result += "1";
    else {
      boolean firstMul = true;
      for (int i = 0; i < primeExp.size(); i += 2) {
        if (!firstMul)
          result += "*";
        if (primeExp.elementAt(i + 1) > 1)
          result += primeExp.elementAt(i).toString() + "^" + primeExp.elementAt(i + 1).toString();
        else
          result += primeExp.elementAt(i).toString();
        firstMul = false;
      }
    }
    return result;
  } /* IFactor.toString */

  /**
   * Test program.
   * It takes a single argument n and prints the integer factorization.<br>
   * java -cp . org.nevec.rjm.IFactor n<br>
   *
   * @param args It takes a single argument n and prints the integer factorization.<br>
   * @author Richard J. Mathar
   */
  public static void main(String[] args) {
    BigInteger n = new BigInteger(args[0]);
    System.out.println(new IFactor(n));
  } /* IFactor.main */
} /* IFactor */
