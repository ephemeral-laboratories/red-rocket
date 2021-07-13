package garden.ephemeral.rocket.color

class IncompatibleColorException(val first: Color, val second: Color) :
    Throwable("Incompatible colors: $first vs. $second")
