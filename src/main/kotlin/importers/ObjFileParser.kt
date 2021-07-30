package garden.ephemeral.rocket.importers

import garden.ephemeral.rocket.Material
import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.Tuple.Companion.point
import garden.ephemeral.rocket.Tuple.Companion.vector
import garden.ephemeral.rocket.shapes.Group
import garden.ephemeral.rocket.shapes.SmoothTriangle
import garden.ephemeral.rocket.shapes.Triangle
import java.io.File

class ObjFileParser(file: File, lenient: Boolean = false) {
    val defaultGroup: Group = Group()
    private val namedGroups: MutableMap<String, Group> = mutableMapOf()
    private var currentGroup: Group = defaultGroup
    private val vertices: MutableList<Tuple> = mutableListOf()
    private val normals: MutableList<Tuple> = mutableListOf()
    private val textureVertices: MutableList<Tuple> = mutableListOf()
    var ignoredLines: Int = 0
    private lateinit var materialLibrary: Map<String, Material>
    private var currentMaterial: Material = Material.default

    init {
        CommandReader(file).forEachCommand { command ->
            when (command.name) {
                "mtllib" -> {
                    val (mtlFilename) = command.args
                    materialLibrary = MtlFileParser(file.resolveSibling(mtlFilename)).materials
                }
                "usemtl" -> {
                    val (materialName) = command.args
                    currentMaterial = materialLibrary[materialName]
                        ?: throw IllegalArgumentException("OBJ file refers to material $materialName " +
                                "which does not exist in the material library")
                }
                "v" -> {
                    val args = command.args
                    vertices.add(point(args[0].toDouble(), args[1].toDouble(), args[2].toDouble()))
                }
                "vt" -> {
                    val args = command.args
                    textureVertices.add(if (args.size >= 3) {
                        vector(args[0].toDouble(), args[1].toDouble(), args[2].toDouble())
                    } else {
                        vector(args[0].toDouble(), args[1].toDouble(), 0.0)
                    })
                }
                "vn" -> {
                    val args = command.args
                    normals.add(vector(args[0].toDouble(), args[1].toDouble(), args[2].toDouble()))
                }
                "g" -> {
                    val (groupName) = command.args
                    val group = Group()
                    namedGroups[groupName] = group
                    currentGroup = group
                }
                "f" -> {
                    val faceData = command.args.map(this::parseVertexAttributes)

                    // Fan triangulation - assumes that the shape is convex!
                    (1 until faceData.size - 1).forEach { i ->
                        val p1Data = faceData[0]
                        val p2Data = faceData[i]
                        val p3Data = faceData[i + 1]
                        currentGroup.addChild(
                            if (p1Data.normal != null && p2Data.normal != null && p3Data.normal != null) {
                                SmoothTriangle(p1Data.vertex, p2Data.vertex, p3Data.vertex,
                                    p1Data.textureVertex, p2Data.textureVertex, p3Data.textureVertex,
                                    p1Data.normal, p2Data.normal, p3Data.normal)
                            } else {
                                Triangle(p1Data.vertex, p2Data.vertex, p3Data.vertex,
                                    p1Data.textureVertex, p2Data.textureVertex, p3Data.textureVertex)
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

    fun vertex(i: Int): Tuple {
        return vertices[i - 1]
    }

    fun normal(i: Int): Tuple {
        return normals[i - 1]
    }

    fun textureVertex(i: Int): Tuple {
        return textureVertices[i - 1]
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