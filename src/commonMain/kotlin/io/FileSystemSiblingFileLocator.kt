package garden.ephemeral.rocket.io

import kotlinx.io.RawSource
import kotlinx.io.files.FileSystem
import kotlinx.io.files.Path

class FileSystemSiblingFileLocator(
    private val fileSystem: FileSystem,
    private val dirPath: Path,
) : SiblingFileLocator {
    override fun resolveSibling(filename: String): RawSource {
        return fileSystem.source(Path(dirPath, filename))
    }
}
