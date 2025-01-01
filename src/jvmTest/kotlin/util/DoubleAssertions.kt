package garden.ephemeral.rocket.util

import assertk.Assert

// Because for NaN, anything == NaN returns false
fun Assert<Double>.isNaN() = given { actual -> actual.isNaN() }
