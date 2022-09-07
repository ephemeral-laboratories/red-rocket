# Complex Trigonometry

Because the Fresnel equations involve an index of refraction which may be a complex number, we will end up having to compute the result of trigonometric functions for complex numbers.

This is not something available out of the box in the standard library, so we are going to have to implement it. The standard library _does_ give us trigonometric functions as well as their hyperbolic counterparts, for double (real) numbers. So we want to implement the complex ones on top of the real ones. (An alternative would be to implement them _from scratch_, which is an approach you might take if the built-in functions were not available, or if it happened to run faster than calling the built-in functions.)

As this is not something which people generally encounter in basic schooling, I thought it was worth refreshing how all of this works.

## A complex arithmetic refresher

(You can skip this section if you already know how complex numbers work.)

Complex numbers are numbers of the form $z = a + i b$, where $i$ represents $\sqrt{-1}$. They fell out of the desire to compute roots of polynomials which have no real solutions, but ended up being useful all throughout engineering and physics.

The $a$ component is conventionally called the "real" part,
while the $i b$ component is conventionally called the "imaginary" part.
The term "imaginary numbers" is frequently pointed out to be bad terminology, with some people preferring to call them "lateral numbers", as the real and imaginary parts are typically pictured as existing on a 2D plane, with the real part on the x-axis and the imaginary part on the y-axis.

If you have two complex numbers:

$$
\begin{align}
z_1 &= a_1 + i b_1 \\
z_2 &= a_2 + i b_2
\end{align}
$$

To add them, you just add the real and imaginary parts separately:

$$
z_1 + z_2 = (a_1 + b_1) + i (a_2 + b_2)
$$

Subtraction is similar:

$$
z_1 - z_2 = (a_1 - b_1) + i (a_2 - b_2)
$$

For multiplication, you can expand the multiplication, then simplify the $i^2$ and regroup:

$$
\begin{align}
z_1 z_2 &= (a_1 + i b_1) (a_2 + i b_2) \\
    &= a_1 a_2 + i a_1 b_2 + i b_1 a_2 + i^2 b_1 b_2 \\
    &= (a_1 a_2 - b_1 b_2) + i (a_1 b_2 + b_1 a_2)
\end{align}
$$

There is the notion of a "complex conjugate", where the imaginary part has its sign flipped:

$$
\overline{z_1} = a_1 - i b_1
$$

When multiplying a complex number by its own conjugate, the imaginary parts drop out, giving you the square of its magnitude:

$$
\begin{align}
z_1 \overline{z_1} &= (a_1 + i b_1) (a_1 - i b_1) \\
    &= a_1^2 + b_1^2
\end{align}
$$

This then gives you a strategy for division. Simply multiply the top and bottom by the conjugate of the bottom:

$$
\begin{align}
{z_1 \over z_2} &= {a_1 + i b_1 \over a_2 + i b_2} \\
    &= \left({a_1 + i b_1 \over a_2 + i b_2}\right) \left({a_2 - i b_2 \over a_2 - i b_2}\right) \\
    &= {a_1 a_2 - i a_1 b_2 + i b_2 a_1 - i^2 b_1 b_2 \over a_2^2 + b_1^2} \\
    &= \left({a_1 a_2 + b_1 b_2 \over a_2^2 + b_1^2}\right) + i \left({b_2 a_1 - a_1 b_2 \over a_2^2 + b_1^2}\right)
\end{align}
$$

## Exponentials for imaginary numbers

Euler gives us this famous formula:

$$
e^{i\theta} = \cos \theta + i \sin \theta
$$

It's possible to derive this by looking at the Taylor series expansions for $e^x$, $\cos\theta$ and $\sin\theta$, but I won't bother to include that as this is a fairly fundamental formula already.

## Derivation of $\cos\theta$ and $\sin\theta$ in terms of exponents

Taking Euler's formula and substituting $-\theta$ for $\theta$:

$$
e^{i(-\theta)} = \cos (-\theta) + i \sin (-\theta)
$$

$\cos$ is an even function and $\sin$ is an odd function, so:

$$
e^{i(-\theta)} = \cos \theta - i \sin \theta
$$

Adding this back to Euler's formula:

$$
\begin{align}
e^{i\theta} + e^{i(-\theta)} &= \cos \theta + i \sin \theta + \cos \theta - i \sin \theta \\
    &= 2 \cos \theta
\end{align}
$$

And therefore:

$$
\cos \theta = {e^{i\theta} + e^{i(-\theta)} \over 2}
$$

Subtracting instead:

$$
e^{i\theta} - e^{i(-\theta)} = 2 i \sin \theta
$$

And therefore:

$$
\sin \theta = -i {e^{i\theta} - e^{i(-\theta)} \over 2}
$$

## $\cosh\theta$ and $\sinh\theta$ in terms of exponents

Both of these functions are defined in terms of exponents already:

$$
\cosh \theta = {e^\theta + e^{-\theta} \over 2}
$$

$$
\sinh \theta = {e^\theta - e^{-\theta} \over 2}
$$

## Derivation of $\cos(i \theta)$ and $\sin(i \theta)$ in terms of $\cosh\theta$ and $\sinh\theta$

Starting with the exponential formula for $\cos\theta$ and substituting in $i \theta$:

$$
\cos (i \theta) = {e^{i (i \theta)} + e^{i (i (- \theta))} \over 2}
$$

Simplifying $i^2$ to -1:

$$
\cos (i \theta) = {e^{- \theta} + e^\theta \over 2}
$$

And therefore:

$$
\cos (i \theta) = \cosh \theta
$$

Similarly for $\sin\theta$:

$$
\sin (i \theta) = -i {e^{i(i \theta)} - e^{i(-i \theta)} \over 2}
$$

$$
\sin (i \theta) = -i {e^{-\theta} - e^\theta \over 2}
$$

$$
\sin (i \theta) = -i \sinh(-\theta)
$$

And as $\sinh$ is an odd function,

$$
\sin (i \theta) = i \sinh\theta
$$

## Derivation of $\cosh(i \theta)$ and $\sinh(i \theta)$ in terms of $\cos\theta$ and $\sin\theta$

Substituting $i \theta$ into the $\cos(i \theta)$ formula derived above:

$$
\cos(i (i \theta)) = \cosh (i \theta)
$$

Flipping it around and simplifying the $i^2$:

$$
\cosh (i \theta) = \cos (-\theta)
$$

And then because $\cos$ is an even function:

$$
\cosh (i \theta) = \cos \theta
$$

Similarly, for $\sinh$:

$$
\sin (i (i \theta)) = i \sinh (i \theta)
$$

Flipping around and multiplying through by $-i$:

$$
-i (i \sinh (i \theta)) = -i \sin (i (i \theta))
$$

Simplifying the $i^2$:

$$
\sinh (i \theta) = -i \sin (-\theta)
$$

And because $\sin$ is an odd function:

$$
\sinh (i \theta) = i \sin \theta
$$

## Derivation of $\cos\theta$ and $\sin\theta$ for complex numbers

The angle addition formula for $\cos$ gives:

$$
\cos (\alpha + \beta) = \cos \alpha \cos \beta - \sin \alpha \sin \beta
$$

Substituting in the real and imaginary parts of a complex number:

$$
\cos (\alpha + i \beta) = \cos \alpha \cos (i \beta) - \sin \alpha \sin (i \beta)
$$

Then using the formulae for $\cos(i \theta)$ and $\sin(i \theta)$ derived above:

$$
\cos (\alpha + i \beta) = \cos \alpha \cosh \beta - i \sin \alpha \sinh \beta
$$

Similarly, the angle addition formula for sine gives:

$$
\sin (\alpha + \beta) = \sin \alpha \cos \beta + \cos \alpha \sin \beta
$$

Substituting in the real and imaginary parts for a complex number:

$$
\sin (\alpha + i \beta) = \sin \alpha \cos (i \beta) + \cos \alpha \sin (i \beta)
$$

And substituting the same as previously:

$$
\sin (\alpha + i \beta) = \sin \alpha \cosh \beta + i \cos \alpha \sinh \beta
$$

## Derivation of cosh(x) and sinh(x) for complex numbers

First we'd like an angle addition formula for $\cosh$.

This is a less than obvious trick, but if you double both terms and then add some terms which cancel each other out, the sides can then be grouped and factored out, resulting in expressions entirely in terms of $\cosh$ and $\sinh$ again:

$$
\begin{align}
\cosh(\alpha + \beta) &= {e^{\alpha + \beta} + e^{-\alpha - \beta} \over 2} \\
    &= {e^{\alpha + \beta} + e^{-\alpha - \beta} + e^{\beta − \alpha} + e^{\alpha − \beta} +
        e^{\alpha + \beta} + e^{−\alpha − \beta} − e^{\beta − \alpha} − e^{\alpha − \beta} \over 4} \\
    &= {e^{\alpha + \beta} + e^{-\alpha - \beta} + e^{\beta − \alpha} + e^{\alpha − \beta} \over 4}  +
       {e^{\alpha + \beta} + e^{−\alpha − \beta} − e^{\beta − \alpha} − e^{\alpha − \beta} \over 4} \\
    &= {e^\alpha + e^{-\alpha} \over 2} {e^\beta + e^{-\beta} \over 2} +
       {e^\alpha - e^{-\alpha} \over 2} {e^\beta - e^{-\beta} \over 2} \\
    &= \cosh \alpha \cosh \beta + \sinh \alpha \sinh \beta
\end{align}
$$

Then substitute in the parts of the complex number:

$$
\cosh(\alpha + i \beta) = \cosh \alpha \cosh (i \beta) + \sinh \alpha \sinh (i \beta)
$$

And then simplify using the $\cosh(i \theta)$ and $\sinh(i \theta)$ formulae above:

$$
\cosh(\alpha + i \beta) = \cosh \alpha \cos \beta + i \sinh \alpha \sin \beta
$$

Similarly for $\sinh$:

$$
\begin{align}
\sinh(\alpha + \beta) &= {e^{\alpha + \beta} - e^{-\alpha - \beta} \over 2} \\
    &= {e^{\alpha + \beta} − e^{−\alpha − \beta} + e^{\beta − \alpha} − e^{\alpha − \beta} +
        e^{\alpha + \beta} − e^{−\alpha − \beta} − e^{\beta − \alpha} + e^{\alpha − \beta} \over 4} \\
    &= {e^{\alpha + \beta} − e^{−\alpha − \beta} + e^{\beta − \alpha} − e^{\alpha − \beta} \over 4} +
       {e^{\alpha + \beta} − e^{−\alpha − \beta} − e^{\beta − \alpha} + e^{\alpha − \beta} \over 4} \\
    &= {e^\alpha + e^{-\alpha} \over 2} {e^\beta - e^{-\beta} \over 2} +
       {e^\alpha - e^{-\alpha} \over 2} {e^\beta + e^{-\beta} \over 2} \\
    &= \cosh \alpha \sinh \beta + \sinh \alpha \cosh \beta
\end{align}
$$

Then substitute in the parts of the complex number:

$$
\sinh(\alpha + i \beta) = \cosh \alpha \sinh (i \beta) + \sinh \alpha \cosh (i \beta)
$$

And then simplify using the $\cosh(i \theta)$ and $\sinh(i \theta)$ formulae above:

$$
\sinh(\alpha + i \beta) = \sinh \alpha \cos \beta + i \cosh \alpha \sin \beta
$$
