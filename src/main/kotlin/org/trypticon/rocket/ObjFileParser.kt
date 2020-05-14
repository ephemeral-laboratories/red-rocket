package org.trypticon.rocket

import org.trypticon.rocket.Tuple.Companion.point
import org.trypticon.rocket.Tuple.Companion.vector
import org.trypticon.rocket.shapes.Group
import org.trypticon.rocket.shapes.SmoothTriangle
import org.trypticon.rocket.shapes.Triangle
import java.io.File

class ObjFileParser(file: File) {
    val defaultGroup: Group = Group()
    private val namedGroups: MutableMap<String, Group> = mutableMapOf()
    private var currentGroup: Group = defaultGroup
    private val vertices: MutableList<Tuple> = mutableListOf()
    private val normals: MutableList<Tuple> = mutableListOf()
    var ignoredLines: Int = 0
    private val whitespace = Regex("\\s+")

    init {
        file.forEachLine { line ->
            val command = line.trim().split(whitespace)
            when (command[0]) {
                "v" -> {
                    vertices.add(point(command[1].toDouble(), command[2].toDouble(), command[3].toDouble()))
                }
                "vn" -> {
                    normals.add(vector(command[1].toDouble(), command[2].toDouble(), command[3].toDouble()))
                }
                "g" -> {
                    val group = Group()
                    namedGroups[command[1]] = group
                    currentGroup = group
                }
                "f" -> {
                    val faceData = command.subList(1, command.size).map(this::parseFace)

                    // Fan triangulation - assumes that the shape is convex!
                    (1 until faceData.size - 1).forEach { i ->
                        val p1Data = faceData[0]
                        val p2Data = faceData[i]
                        val p3Data = faceData[i + 1]
                        currentGroup.addChild(
                            if (p1Data.size > 1) {
                                SmoothTriangle(p1Data[0], p2Data[0], p3Data[0], p1Data[1], p2Data[1], p3Data[1])
                            } else {
                                Triangle(p1Data[0], p2Data[0], p3Data[0])
                            }
                        )
                    }
                }
                else -> {
                    ignoredLines++
                }
            }
        }
    }

    private fun parseFace(string: String): List<Tuple> {
        // Three possible formats supposedly:
        //     1
        //     1/2/3
        //     1//3
        return if (string.contains('/')) {
            val parts = string.split('/')
            listOf(vertex(parts[0].toInt()), normal(parts[2].toInt()))
        } else {
            listOf(vertex(string.toInt()))
        }
    }

    fun vertex(i: Int): Tuple {
        return vertices[i - 1]
    }

    fun normal(i: Int): Tuple {
        return normals[i - 1]
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
}