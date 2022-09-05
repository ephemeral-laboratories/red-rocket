package garden.ephemeral.rocket.spectra

import kotlin.math.PI

object PhysicalConstants {
    /** Planck's constant _h_ (units: m² kg s⁻¹) */
    const val Planck: Double = 6.62607004E-34

    /** Speed of light in a vacuum _c_ (units: m s⁻¹) */
    const val SpeedOfLight: Double = 299_792_458.0

    /** Boltzmann constant _kB_ (units: m² kg s⁻² K⁻¹) */
    const val Boltzmann: Double = 1.38064852E-23

    /** Radiation constant _c₁_ = 2π h c² (units: m⁴ kg s⁻³ = W m²) */
    const val RadiationC1: Double = 2 * PI * Planck * SpeedOfLight * SpeedOfLight

    /** Radiation constant _c₂_ = h c / k (units: m K) */
    const val RadiationC2: Double = Planck * SpeedOfLight / Boltzmann

    /** Maximum luminous efficacy constant _Kmax_ (units lm W⁻¹) */
    const val MaximumLuminousEfficacy = 683.002
}
