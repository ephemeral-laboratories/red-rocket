# Units

The Ray Tracer Challenge book doesn't talk about units as it is doing all the maths by
adding colours together.

If we want to do things more physically, we have to consider what units each value is
in. Methods dealing with numeric values should document what units they are using.

## Length units

| Term             | Symbol | Unit       | Unit symbol |
|------------------|--------|------------|-------------|
| Length, distance | r, d   | metres     | m           |
| Wavelength       | λ      | nanometres | nm          |

### Spectra

For a given unit, you can derive a _spectral_ variant of it where instead of a single value,
there is a spectrum of values by wavelength.

For spectra with values in units, this adds an additional nm<sup>-1</sup> onto the end of
the units. This can be easily seen by considering that the sum over the spectrum
is a constant value regardless of which resolution you chose to sample it. When summing
it, you are multiplying by some value in nm, which cancels out the nm<sup>-1</sup>.

For spectra with values from 0..1 (e.g., reflectance spectra), the values are considered
to be dimensionless. Summing this kind of spectrum makes no real sense.

## Radiometric units

_Radiometric_ quantities are used for the physical transport of light.
They use a subscript of "e", for "energetic".

| Term              | Symbol          | Unit                                 | Unit symbol                      |
|-------------------|-----------------|--------------------------------------|----------------------------------|
| Radiant flux      | Φ<sub>e</sub>   | watts                                | W = J s<sup>-1</sup>             |
| Radiant intensity | I<sub>e,Ω</sub> | watts per steradian                  | W sr<sup>-1</sup>                |
| Irradiance        | E<sub>e</sub>   | watts per square metre               | W m<sup>-2</sup>                 |
| Radiant exitance  | M<sub>e</sub>   | watts per square metre               | W m<sup>-2</sup>                 |
| Radiance          | L<sub>e,Ω</sub> | watts per steradian per square metre | W sr<sup>-1</sup> m<sup>-2</sup> |

Notably, a ray carries _radiance_, and the radiance value is the same at all points along a ray.

## Photometric units

_Photometric_ quantities are used when interpreting light as colours as seen by a human.
They use a subscript of "v", for "visual".

These are related to radiometric units via _luminous efficacy_, in lumens per watt (lm W<sup>-1</sup>).
Notably, the maximum luminous efficacy of the human visual system is given as 682.002 lm W<sup>-1</sup>,
which occurs for monochromatic light at 555 nm.

| Term               | Symbol          | Unit    | Unit symbol             |
|--------------------|-----------------|---------|-------------------------|
| Luminous flux      | Φ<sub>v</sub>   | lumens  | lm                      |
| Luminous intensity | I<sub>v,Ω</sub> | candela | cd = lm sr<sup>-1</sup> |
| Illuminance        | E<sub>v</sub>   | lux     | lx = lm m<sup>-2</sup>  |
| Luminous exitance  | M<sub>v</sub>   | lux     | lx = lm m<sup>-2</sup>  |
| Luminance          | L<sub>v,Ω</sub> | nit     | nt = cd m<sup>-2</sup>  |
