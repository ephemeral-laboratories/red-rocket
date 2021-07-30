package garden.ephemeral.rocket.importers

class UnrecognisedCommandException(command: Command): RuntimeException("Unrecognised command: $command")