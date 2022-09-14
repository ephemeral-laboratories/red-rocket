# Complex Reflection and Refraction

The Ray Tracer Challenge skipped this topic, noting that the polarisation of light is a
complex phenomenon that most tracers don't pay attention to, and instead presented
Schlick's approximation, which is the common shortcut taken in computer graphics applications.

Some modern ray tracers do fully support polarised light though, so I thought it looked like
a reasonably good enhancement to have. But supporting this does mean dealing with complex indices
of refraction in materials, as well as learning how to represent polarised light.

This document goes into the underlying maths for this support.

See also:

* [Complex Trigonometry](ComplexTrigonometry.md)

## The Fresnel equations

The Fresnel equations (or Fresnel coefficients) describe the reflection and transmission of light
when it hits an interface between different optical media.

The derivation will not be given here, as these are relatively well-documented equations:

$$
\begin{align}
r_s &= {n_1 \cos \theta_i - n_2 \cos \theta_t \over n_1 \cos \theta_i + n_2 \cos \theta_t} \\
r_p &= {n_1 \cos \theta_t - n_2 \cos \theta_i \over n_1 \cos \theta_t + n_2 \cos \theta_i} \\
t_s &= {2 n_1 \cos \theta_i \over n_1 \cos \theta_i + n_2 \cos \theta_t} \\
t_p &= {2 n_1 \cos \theta_i \over n_1 \cos \theta_t + n_2 \cos \theta_i}
\end{align}
$$

Where:
* $n_1$ represents the _refractive index_ of the medium the light is travelling from
* $n_2$ represents the refractive index of the medium the light is travelling into
* $\theta_i$ represents the _angle of incidence_
* $\theta_t$ represents the _angle of transmission_ (i.e. refraction)
* $r$ represent the Fresnel coefficients for reflection
* $t$ represent the Fresnel coefficients for transmission
* Subscripts $s$ and $p$ represent polarisation perpendicular and parallel to the plane of incidence, respectively.

Because refractive indices are complex values, the four resulting coefficients are also complex values.

## Total internal reflection

**TODO: Missing section**

## Treatment of complex angles for refraction

Usually you would compute the angle of refraction using Snell's Law.

But, if you feed a complex index of refraction into Snell's Law, you get back a complex angle.
To get a vector you can use for the next ray, you have to somehow use this complex angle,
to determine the real angle the ray will go.

The following reasoning is taken from:

_Yun Liu, Jian Qian and Yubo Tian,
"Succinct formulas for decomposition of complex refraction angle,"
IEEE Antennas and Propagation Society International Symposium. Digest.
Held in conjunction with: USNC/CNC/URSI North American Radio Sci. Meeting (Cat. No.03CH37450),
2003, pp. 487-490 vol.3, doi: [10.1109/APS.2003.1219892](https://doi.org/10.1109/APS.2003.1219892)._

I cleaned up and renamed some variables for the sake of readability.

The incident, reflected and refracted waves are expressed as:

$$
\begin{align}
\overrightarrow{E_i} &= \overrightarrow{E_{i0}} \exp(-i(\overrightarrow{k_i} \cdot \overrightarrow{r})) \\
\overrightarrow{E_r} &= \overrightarrow{E_{r0}} \exp(-i(\overrightarrow{k_r} \cdot \overrightarrow{r})) \\
\overrightarrow{E_t} &= \overrightarrow{E_{t0}} \exp(-i(\overrightarrow{k_t} \cdot \overrightarrow{r}))
\end{align}
$$

Where:
* $\overrightarrow{E}$ represents the _electric field vector_
* $\overrightarrow{E_0}$ represents the electric field vector at its maximum amplitude
* $\overrightarrow{k}$ is the _wave vector_
* $\overrightarrow{r}$ is the _position vector_
* Subscripts $i$, $r$, $t$ refer to the incident, reflected, transmitted waves, respectively

The magnitude of the wave vector is commonly written as $k$.
For a wave travelling hitting the interface between two media where the second medium is in the $z$ direction,
you can split the wave vector into two components—an $x$ component parallel to the interface
and a $z$ component perpendicular to the interface:

$$
\begin{align}
k_{ix} &= k_i \sin \theta_i \\
k_{iz} &= k_i \cos \theta_i \\
k_{tx} &= k_t \sin \theta_t \\
k_{tz} &= k_t \cos \theta_t
\end{align}
$$

The law of refraction (Snell's Law) says that the $x$ components are equal:

$$
k_{tx} = k_{ix}
$$

The wave vector is directly proportional to the index of refraction.
However, we know that the index of refraction can be a complex number,
which means that $k_t$ is also a complex number.
This means that $\theta_t$ must also be complex.
The strategy taken here is to split it into its real and imaginary components:

$$
\begin{align}
\exp(-j(\overrightarrow{k_2} \cdot \overrightarrow{r})) &= \exp(-j(k_{2x}x+k_{2z}z)) \\
&= \exp(-j(k_2 \sin \theta_t x + k_2 \cos \theta_t z))\\
&= \exp(-j[\Re(k_2\sin\theta_t)x+\Re(k_2\cos\theta_t)z] + [\Im(k_2 \sin\theta_t)+\Im(k_2\cos\theta_t)]) \\
&= \exp(-j[\Re(k_2\sin\theta_t)x+\Re(k_2\cos\theta_t)z]) \exp(\Im(k_2 \sin\theta_t)+\Im(k_2\cos\theta_t))
\end{align}
$$

The result is an expression which is the product of two exponential expressions.
The first factor represents the change in phase (i.e. the oscillation) of the wave:

$$
\exp(-j[\Re(k_2\sin\theta_t)x+\Re(k_2\cos\theta_t)z])
$$

While the second factor represents the attenuation (i.e. the decay) of the wave:

$$
\exp(\Im(k_2 \sin\theta_t)+\Im(k_2\cos\theta_t))
$$

From this, we can say that the planes of constant phase are:

$$
\Re(k_2\sin\theta_t)x+\Re(k_2\cos\theta_t)z = const
$$

While the planes of constant amplitude are:

$$
\Im(k_2\sin\theta_t)x+\Im(k_2\cos\theta_t)z = const
$$

Therefore, the angle of the planes of constant phase (i.e., the direction of propagation) is:

$$
\psi = \tan^{-1}\left[{\Re(k_2 \sin\theta_t) \over \Re(k_2 \cos\theta_t)}\right]
$$

While the angle of the planes of constant amplitude (i.e., the direction of attenuation) is:

$$
\varphi = \tan^{-1}\left[{\Im(k_2\sin\theta_t) \over \Im(k_2\cos\theta_t)}\right]
$$

Because of the law of refraction above, we know that $k_2 \sin\theta_t$ is real,
so these can be further simplified to:

$$
\begin{align}
\psi &= \tan^{-1}\left[{1 \over \Re(\cot\theta_t)}\right] \\
\varphi &= 0
\end{align}
$$

As a sanity check, for the case where $\theta_t$ is real, this further simplifies to:

$$
\psi = \tan^{-1}\left[{1 \over \cot\theta_t}\right]
= \tan^{-1}\left(\tan\theta_t\right) = \theta_t
$$

In ray tracing, what you actually want for calculating the resulting ray direction is the cosine:

$$
\cos\psi = \cos \left( \tan^{-1} \left[ {1 \over \Re(\cot \theta_t)} \right] \right)
= {1 \over \sqrt{({1 \over \Re(\cot\theta_t)})^2 + 1}}
$$

This way, you can skip some unnecessary trigonometric operations—
$\cot \theta_t$ can be computed cheaply because you already have its $\sin$ and $\cos$ from other calculations,
and the rest of the calculations are relatively inexpensive in comparison to $\cos$ and $\tan^{-1}$.
