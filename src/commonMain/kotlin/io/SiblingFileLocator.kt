package garden.ephemeral.rocket.io

import kotlinx.io.RawSource

/**
 * Abstraction of sibling file loading.
 */
interface SiblingFileLocator {

    /**
     * Resolves a file relative to the location of the current file.
     *
     * @param filename the name of a file to search for.
     * @return an open source to the sibling file.
     */
    fun resolveSibling(filename: String): RawSource
}
