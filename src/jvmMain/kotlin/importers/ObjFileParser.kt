package garden.ephemeral.rocket.importers

import garden.ephemeral.rocket.Material
import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.Tuple.Companion.point
import garden.ephemeral.rocket.Tuple.Companion.vector
import garden.ephemeral.rocket.io.SiblingFileLocator
import garden.ephemeral.rocket.shapes.Group
import garden.ephemeral.rocket.shapes.SmoothTriangle
import garden.ephemeral.rocket.shapes.Triangle
import kotlinx.io.Source
import kotlinx.io.buffered

class ObjFileParser(source: Source, siblingFileLocator: SiblingFileLocator, lenient: Boolean = false) {
    val defaultGroup: Group = Group()
    private val namedGroups = mutableMapOf<String, Group>()
    private var currentGroup: Group = defaultGroup
    private val vertices = mutableListOf<Tuple>()
    private val normals = mutableListOf<Tuple>()
    private val textureVertices = mutableListOf<Tuple>()
    var ignoredLines: Int = 0
    private lateinit var materialLibrary: Map<String, Material>
    private var currentMaterial: Material = Material.default

    init {
        CommandReader(source).forEachCommand { command ->
            when (command.name) {
                "mtllib" -> {
                    // mtllib $filename
                    val (mtlFilename) = command.args
                    siblingFileLocator.resolveSibling(mtlFilename).use { source ->
                        materialLibrary = MtlFileParser(source.buffered()).materials
                    }
                }
                "usemtl" -> {
                    // usemtl $name
                    val (materialName) = command.args
                    currentMaterial = materialLibrary[materialName]
                        ?: throw IllegalArgumentException(
                            "OBJ file refers to material $materialName which does not exist in the material library"
                        )
                }
                "v" -> {
                    // v $x $y $z
                    val args = command.args
                    vertices.add(point(args[0].toDouble(), args[1].toDouble(), args[2].toDouble()))
                }
                "vt" -> {
                    // vt $u $v
                    // vt $u $v $z
                    val args = command.args
                    textureVertices.add(
                        if (args.size >= 3) {
                            vector(args[0].toDouble(), args[1].toDouble(), args[2].toDouble())
                        } else {
                            vector(args[0].toDouble(), args[1].toDouble(), 0.0)
                        }
                    )
                }
                "vn" -> {
                    // vn $x $y $z
                    val args = command.args
                    normals.add(vector(args[0].toDouble(), args[1].toDouble(), args[2].toDouble()))
                }
                "g" -> {
                    // g $name
                    val (groupName) = command.args
                    val group = Group()
                    namedGroups[groupName] = group
                    currentGroup = group
                }
                "f" -> {
                    // f $vi $vi $vi ...
                    // f $vi/$vti $vi/$vti $vi/$vti ...
                    // f $vi/$vti/$vni $vi/$vti/$vni $vi/$vti/$vni ...
                    val faceData = command.args.map(this::parseVertexAttributes)

                    // Fan triangulation - assumes that the shape is convex!
                    (1 until faceData.size - 1).forEach { i ->
                        val p1Data = faceData[0]
                        val p2Data = faceData[i]
                        val p3Data = faceData[i + 1]
                        currentGroup.addChild(
                            if (p1Data.normal != null && p2Data.normal != null && p3Data.normal != null) {
                                SmoothTriangle(
                                    p1Data.vertex, p2Data.vertex, p3Data.vertex,
                                    p1Data.textureVertex, p2Data.textureVertex, p3Data.textureVertex,
                                    p1Data.normal, p2Data.normal, p3Data.normal
                                )
                            } else {
                                Triangle(
                                    p1Data.vertex,
                                    p2Data.vertex,
                                    p3Data.vertex,
                                    p1Data.textureVertex,
                                    p2Data.textureVertex,
                                    p3Data.textureVertex
                                )
                            }.apply {
                                material = currentMaterial
                            }
                        )
                    }
                }
                else -> {
                    if (lenient) {
                        ignoredLines++
                    } else {
                        throw UnrecognisedCommandException(command)
                    }
                }
            }
        }
    }

    private fun parseVertexAttributes(string: String): VertexAttributes {
        val parts = string.split('/')
        val vertex = vertex(parts[0].toInt())
        val textureVertex = if (parts.size > 1 && parts[1].isNotEmpty()) {
            textureVertex(parts[1].toInt())
        } else {
            null
        }
        val normal = if (parts.size > 2 && parts[2].isNotEmpty()) {
            normal(parts[2].toInt())
        } else {
            null
        }
        return VertexAttributes(vertex, textureVertex, normal)
    }

    /**
     * Gets a vertex by index.
     *
     * @param i the index, indexed from 1.
     * @return the vertex.
     */
    fun vertex(i: Int): Tuple {
        return getByIndex(vertices, i)
    }

    /**
     * Gets a normal by index.
     *
     * @param i the index, indexed from 1.
     * @return the normal.
     */
    fun normal(i: Int): Tuple {
        return getByIndex(normals, i)
    }

    /**
     * Gets a texture vertex by index.
     *
     * @param i the index, indexed from 1.
     * @return the texture vertex.
     */
    fun textureVertex(i: Int): Tuple {
        return getByIndex(textureVertices, i)
    }

    /**
     * Converts index from OBJ format into an index into the list.
     * If i is positive, it is re-offset from 0.
     * If i is negative, it is offset backwards from the end of the list.
     * If i is zero, you currently get an error.
     * Once the real index has been determined, that element of the list is returned.
     *
     * @param list the list.
     * @param i the index in OBJ format.
     * @return the element from the list.
     */
    private fun getByIndex(list: List<Tuple>, i: Int): Tuple {
        return list[
            if (i < 0) {
                list.size + i
            } else {
                i - 1
            }
        ]
    }

    fun namedGroup(string: String): Group? {
        return namedGroups[string]
    }

    fun objToGroup(): Group {
        return Group().apply {
            addChild(defaultGroup)
            namedGroups.values.forEach(::addChild)
        }
    }

    data class VertexAttributes(val vertex: Tuple, val textureVertex: Tuple?, val normal: Tuple?)
}
