package garden.ephemeral.rocket.util

import assertk.assertThat
import garden.ephemeral.rocket.Constants.epsilon
import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.Universe
import garden.ephemeral.rocket.isCloseTo
import io.cucumber.java8.En

// Constructed reflectively
@Suppress("unused")
class StokesStepDefinitions(universe: Universe) : En {
    init {
        When("{tuple_var} is a Stokes vector for no light") { tupleVar: String ->
            universe.tuples[tupleVar] = Stokes.None
        }
        When("{tuple_var} is a Stokes vector for unpolarized light") { tupleVar: String ->
            universe.tuples[tupleVar] = Stokes.Unpolarized
        }
        When("{tuple_var} is a Stokes vector for linearly polarized \\(horizontal) light") { tupleVar: String ->
            universe.tuples[tupleVar] = Stokes.LinearlyPolarizedHorizontal
        }
        When("{tuple_var} is a Stokes vector for linearly polarized \\(vertical) light") { tupleVar: String ->
            universe.tuples[tupleVar] = Stokes.LinearlyPolarizedVertical
        }
        When("{tuple_var} is a Stokes vector for linearly polarized \\(+45°) light") { tupleVar: String ->
            universe.tuples[tupleVar] = Stokes.LinearlyPolarizedPlus45
        }
        When("{tuple_var} is a Stokes vector for linearly polarized \\(-45°) light") { tupleVar: String ->
            universe.tuples[tupleVar] = Stokes.LinearlyPolarizedMinus45
        }
        When("{tuple_var} is a Stokes vector for circularly polarized \\(right hand) light") { tupleVar: String ->
            universe.tuples[tupleVar] = Stokes.CircularlyPolarizedRight
        }
        When("{tuple_var} is a Stokes vector for circularly polarized \\(left hand) light") { tupleVar: String ->
            universe.tuples[tupleVar] = Stokes.CircularlyPolarizedLeft
        }

        Then("{tuple_var} = \\({real}, {real}, {real}, {real})") { tupleVar: String, i: Double, q: Double, u: Double, v: Double ->
            assertThat(universe.tuples[tupleVar]!!).isCloseTo(Tuple(i, q, u, v), epsilon)
        }
    }
}
