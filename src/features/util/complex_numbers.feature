Feature: Complex Numbers

  Scenario Outline: Formatting a complex number
    When z is <value>
    Then z.to_string = '<expected string>'

    Examples:
      | value           | expected string      |
      | complex(0)      | complex(0.0)         |
      | complex(1)      | complex(1.0)         |
      | complex(-1)     | complex(-1.0)        |
      | complex(i)      | complex(1.0i)        |
      | complex(-i)     | complex(-1.0i)       |
      | complex(2i)     | complex(2.0i)        |
      | complex(-2i)    | complex(-2.0i)       |
      | complex(1 + i)  | complex(1.0 + 1.0i)  |
      | complex(3 + 2i) | complex(3.0 + 2.0i)  |
      | complex(-2 - i) | complex(-2.0 - 1.0i) |

  Scenario Outline: Adding two complex numbers
    When z1 is <augend>
    And z2 is <addend>
    Then z1 + z2 = <expected total>

    Examples:
      | augend          | addend          | expected total  |
      | complex(0)      | complex(1)      | complex(1)      |
      | complex(0)      | complex(2)      | complex(2)      |
      | complex(1)      | complex(0)      | complex(1)      |
      | complex(-2)     | complex(3)      | complex(1)      |
      | complex(0)      | complex(i)      | complex(1i)     |
      | complex(0)      | complex(2i)     | complex(2i)     |
      | complex(i)      | complex(0)      | complex(i)      |
      | complex(2i)     | complex(-3i)    | complex(-i)     |
      | complex(3 + 2i) | complex(3 + i)  | complex(6 + 3i) |

  Scenario Outline: Subtracting two complex numbers
    When z1 is <minuend>
    And z2 is <subtrahend>
    Then z1 - z2 = <expected difference>

    Examples:
      | minuend         | subtrahend      | expected difference |
      | complex(0)      | complex(1)      | complex(-1)         |
      | complex(0)      | complex(2)      | complex(-2)         |
      | complex(1)      | complex(0)      | complex(1)          |
      | complex(-2)     | complex(3)      | complex(-5)         |
      | complex(0)      | complex(i)      | complex(-i)         |
      | complex(0)      | complex(2i)     | complex(-2i)        |
      | complex(i)      | complex(0)      | complex(i)          |
      | complex(2i)     | complex(-3i)    | complex(5i)         |
      | complex(3 + 2i) | complex(3 + i)  | complex(i)          |

  Scenario Outline: Multiplying two complex numbers
    When z1 is <multiplicand>
    And z2 is <multiplier>
    Then z1 * z2 = <expected product>

    Examples:
      | multiplicand    | multiplier      | expected product |
      | complex(0)      | complex(1)      | complex(0)       |
      | complex(0)      | complex(2)      | complex(0)       |
      | complex(1)      | complex(0)      | complex(0)       |
      | complex(-2)     | complex(3)      | complex(-6)      |
      | complex(0)      | complex(i)      | complex(0)       |
      | complex(0)      | complex(2i)     | complex(0)       |
      | complex(i)      | complex(i)      | complex(-1)      |
      | complex(i)      | complex(0)      | complex(0)       |
      | complex(1)      | complex(2i)     | complex(2i)      |
      | complex(2i)     | complex(-3i)    | complex(6)       |
      | complex(3 + 2i) | complex(3 + i)  | complex(7 + 9i)  |

#      |
#  public final class Complex {
#  public double :re, im;
#  public Complex(double r, double i) 1 re = r; im = i; 1
#  public Complex assign(Complex z) { re = z.re; im = z.im; return this; 1
#  public Complex plus(Comp.Lex z) { return new Complex(re+z.re,im+z.imL; 1
#  public Complex minus(Complex z) { return new Complextre-z.re,im-z.im); 1
#  public Complex times(Compl.ex z) {
#  return new Complex(re*z.re-im*z.im,im*z.re+re*z.im);
#  I
#  public Complex times(dou1~l.e x) ( return new Complex(re*x,im*x); )
#  public