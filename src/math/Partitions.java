/**
 * @file Construction of the list of partitions or compositions of positive integers .
 */

package math;

import java.util.Iterator;

/*!************************************************
* @brief The class Partitions constructs the partitions of an integer into positive integers.
* @author Richard J. Mathar
* @since 2014-06-19
*/
public class Partitions implements Iterator {
  /**
   * The sum of the parts
   */
  int n;

  /**
   * The current (most recent) part emitted.
   * This is equivalent to the minimum of any partitions emitted in the
   * future, because we are emitting them in increasing order.
   */
  int curPart;

  /**
   * The maximum (upper bound) of any part admitted.
   */
  int maxPart;

  /**
   * Parts to be appended to current[]. In a tree-type recursive approach, this is
   * the partition that can generate the trailing parts to be appended to the
   * frozen parts.
   */
  Partitions trail;

  /**
   * A flag which is true if compositions of n are constructed.
   * False if partitions of n are constructed.
   *
   * @since 2015-03-18
   */
  boolean composite;

  /**
   * Constructor defining the integer to be decomposed.
   *
   * @param n           The integer to be decomposed into partitions.
   * @param doComposite If true construct compositions, else partitions of n.
   * @author Richard J. Mathar
   * @since 2015-03-18
   */
  public Partitions(int n, boolean doComposite) {
                /* forward this to the constructor which puts a lower bound of 1 and
                * a maximum cap of n to the parts to be admitted.
                */
    this(n, 1, n, doComposite);
  } /* ctor */

  /**
   * Constructor defining the integer to be decomposed.
   *
   * @param n The integer to be decomposed into partitions.
   * @author Richard J. Mathar
   */
  @SuppressWarnings("unused")
  public Partitions(int n) {
                /* forward this to the constructor which puts a lower bound of 1 and
                * a maximum cap of n to the parts to be admitted.
                */
    this(n, false);
  } /* ctor */

  /**
   * Constructor defining the integer to be decomposed and a lower bound
   * on all parts.
   *
   * @param n           The integer to be decomposed into partitions.
   * @param minP        The minimum of all parts in the partitions to be generated.
   * @param maxP        The upper bound of any part in the partitions.
   * @param doComposite Set to true if compositions instead of partitions are to be generated.
   * @author Richard J. Mathar
   */
  public Partitions(int n, int minP, int maxP, boolean doComposite) {
    this.n = n;
    curPart = minP;
    maxPart = maxP;
    /* possible to have a partition of n with minPart ? */
    if (curPart >= n || curPart > maxPart) {
      /*
      * The requirements of n <=maxP cannot be met and the requirement
      * of minP<=n can be met at most once (by setting the next part to be equal to n=curPart).
      */
      trail = null;
    } else {
      /*
      * The requirements of minP<=n<=maxP cann be met.
      * If we book 'curPart' as the first part in the upcomming partitions,
      * the rest of the partitions need to partition n minus curPart and
      * have to take 'curPart' as their new minimum.
      */
      trail = new Partitions(n - curPart, curPart, maxP, doComposite);
    }
    composite = doComposite;
  } /* ctor */

  /**
   * Constructor defining the integer to be decomposed and a lower bound
   * on all parts.
   *
   * @param n    The integer to be decomposed into partitions.
   * @param minP The minimum of all parts in the partitions to be generated.
   * @param maxP The upper bound of any part in the partitions.
   * @author Richard J. Mathar
   */
  public Partitions(int n, int minP, int maxP) {
    this(n, minP, maxP, false);
  } /* ctor */

  /**
   * Compute the largest part in a partition.
   *
   * @param parts The list of positive integers providing the partition.
   *              The integers do not need to be sorted in any particular way.
   * @return The maximum element in parts[].
   * This is zero if there are no parts.
   * @author Richard J. Mathar
   * @since 2014-07-14
   */
  public static int max(final int[] parts) {
    int m = 0;
    for (int part : parts) {
      m = Math.max(m, part);
    }
    return m;
  } /* max */

  /**
   * Compute the frequency of a part in a partition.
   *
   * @param parts The list of integers with the partition.
   * @param n     The part to be counted in the parts.
   * @return The non-negative count of n in the parts.
   * This is somewhere between 0 and the number of elements in parts[] (inclusive).
   * @author Richard J. Mathar
   * @since 2014-07-14
   */
  @SuppressWarnings("unused")
  public static int frequency(final int[] parts, final int n) {
    int f = 0;
    for (int part : parts) {
      if (part == n) {
        f++;
      }
    }
    return f;
  } /* frequency */

  /**
   * Compute the frequency of part larger than or equal to some minimum in a partition.
   *
   * @param parts The list of integers with the partition.
   * @param m     The minimum of any part to be counted.
   * @return The count of parts in parts[] that are larger or equal to m.
   * @author Richard J. Mathar
   * @since 2014-07-14
   */
  public static int frequencyMin(final int[] parts, final int m) {
    int f = 0;
    for (int part : parts) {
      if (part >= m) {
        f++;
      }
    }
    return f;
  } /* frequency */

  /**
   * Compute the conjugate of a partition.
   *
   * @param parts The list of integers with the partition.
   * @return The conjugate of the partition.
   * @author Richard J. Mathar
   * @since 2014-07-14
   */
  @SuppressWarnings("unused")
  public static int[] conjugate(final int[] parts) {
    /* number of items in the conjugate equals largest part in parts[].*/
    int maxPart = max(parts);
    int[] conj = new int[maxPart];
    for (int m = maxPart; m >= 1; m--) {
      conj[maxPart - m] = frequencyMin(parts, m);
    }
    return conj;
  } /* conjugate */

  /**
   * Removal of elements is not implemented
   *
   * @author Richard J. Mathar
   * @since 2015-03-18
   */
  public void remove() throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
  } /* remove */

  /**
   * Iterator interface.
   *
   * @return true If some of the partitions have not yet been constructed with next().
   * This is equivalent to saying that the next call to next() will not return null.
   * @author Richard J. Mathar
   * @since 2015-03-18
   */
  public boolean hasNext() {
                /* this is a sort of dry run through next(). We can sequeeze
                * at least one other part if the remaining sum (which is n) is >= curPart.
                */
    return !(n <= 0 || curPart > n || curPart > maxPart);
  } /* hasNext */

  /**
   * Return the next partition.
   *
   * @return An integer vector of the parts.
   * null if all partitions have already been emitted.
   * @author Richard J. Mathar
   */
  public int[] next() {
    if (curPart == n && curPart <= maxPart) {
      int[] parts = new int[1];
      parts[0] = curPart;
      curPart++;
      trail = null;
      return parts;
    } else if (trail != null) {
      int[] cNext = trail.next();
      if (cNext != null) {
                                /* could create another partition of the trailing data...
                                */
        int[] parts = new int[cNext.length + 1];
        parts[0] = curPart;
        System.arraycopy(cNext, 0, parts, 1, parts.length - 1);
        return parts;
      } else if (curPart < n && curPart < maxPart) {
        curPart++;
        trail = new Partitions(n - curPart, composite ? 1 : curPart, maxPart, composite);
        return next();
      } else
        return null;
    } else {
      return null;
    }
  } /* next */

  /**
   * Return the current partition.
   *
   * @return An integer vector of the parts.
   * null if all partitions have already been emitted.
   * @author Richard J. Mathar
   */
  public int[] current() {
    if (curPart > maxPart)
      return null;
    if (curPart == n) {
      /*
      * current partition exhausts n, so trail is not used anyway
      */
      int[] parts = new int[1];
      parts[0] = curPart;
      return parts;
    } else if (trail != null) {
      final int[] cNext = trail.current();
      int[] parts = new int[cNext.length + 1];
      parts[0] = curPart;
      System.arraycopy(cNext, 0, parts, 1, parts.length - 1);
      return parts;
    } else {
      return null;
    }
  } /* current */

  /**
   * Print the partitions of an integer.
   * The integer is specified by command line argument.
   * A maximum of each part in the partition may be added as another argument
   * Usage:
   * java -classpath . de.mpg.mpia.rjm.Partitions n [m]
   *
   * @param args n [m]
   * @author Richard J. Mathar
   * @since 2014-06-19
   */
  public static void main(String[] args) {
    if (args.length > 0) {
      int n = new Integer(args[0]);
      int m = n;
      if (args.length > 1) {
        m = new Integer(args[1]);
      }

      Partitions partitionsOfN = new Partitions(n, 1, m);
      /*
      * N will count the number of partitionsOfN generated
      * in the forthcoming loop
      */
      int N = 0;
      int[] p;
      for (; ; ) {
                                /* generate the next partition of n.
                                */
        p = partitionsOfN.next();
        if (p == null)
          break;
                                /* update the number of partitionsOfN generated so far
                                */
        N++;
                                /* print the  partition
                                */
        for (int aP : p) {
          if (aP > 0) {
            System.out.print(" " + aP);
          } else {
            break;
          }
        }

        System.out.println();
      }
      System.out.println("# " + N);

      partitionsOfN = new Partitions(n, 1, m, true);
      /*
      * N will count the number of partitionsOfN generated
      * in the forthcoming loop
      */
      N = 0;
      for (; ; ) {
        /*
        * generate the next partition of n.
        */
        p = partitionsOfN.next();
        if (p == null) {
          break;
        }
        /*
        * update the number of partitionsOfN generated so far
        */
        N++;
        /*
        * print the  partition
        */
        for (int aP : p) {
          if (aP > 0) {
            System.out.print(" " + aP);
          } else {
            break;
          }
        }

        System.out.println();
      }
      System.out.println("# " + N);
    }
  } /* main */

} /* Partitions */
