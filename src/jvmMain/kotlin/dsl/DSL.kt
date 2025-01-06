package garden.ephemeral.rocket.dsl

import garden.ephemeral.rocket.Material
import garden.ephemeral.rocket.Matrix
import garden.ephemeral.rocket.PointLight
import garden.ephemeral.rocket.Tuple
import garden.ephemeral.rocket.World
import garden.ephemeral.rocket.camera.Camera
import garden.ephemeral.rocket.camera.SamplingStrategy
import garden.ephemeral.rocket.camera.toPNG
import garden.ephemeral.rocket.camera.toPPM
import garden.ephemeral.rocket.color.Color
import garden.ephemeral.rocket.importers.ObjFileParser
import garden.ephemeral.rocket.shapes.Cone
import garden.ephemeral.rocket.shapes.Cube
import garden.ephemeral.rocket.shapes.Cylinder
import garden.ephemeral.rocket.shapes.Group
import garden.ephemeral.rocket.shapes.Plane
import garden.ephemeral.rocket.shapes.Shape
import garden.ephemeral.rocket.shapes.Sphere
import garden.ephemeral.rocket.util.Angle
import kotlinx.io.files.Path
import java.time.Duration
import java.time.Instant

fun render(block: RenderBuilder.() -> Unit) {
    RenderBuilder().apply(block).render()
}

class RenderBuilder {
    lateinit var camera: Camera
    lateinit var world: World

    fun World(block: WorldBuilder.() -> Unit) {
        world = WorldBuilder().apply(block).build()
    }

    fun Camera(
        hSize: Int,
        vSize: Int,
        fieldOfView: Angle,
        samplingStrategy: SamplingStrategy = SamplingStrategy.center,
        block: Camera.() -> Unit
    ) {
        camera = Camera(hSize, vSize, fieldOfView, samplingStrategy).apply(block)
    }

    fun render() {
        val t0 = Instant.now()
        val canvas = camera.render(world)
        val t1 = Instant.now()
        println("Render time: ${Duration.between(t0, t1)}")

        canvas.toPPM(Path("out.ppm"))
        canvas.toPNG(Path("out.png"))
    }
}

abstract class ObjectContainerBuilder {
    protected val children = mutableListOf<Shape>()

    fun Cone(block: Cone.() -> Unit) {
        children.add(Cone().apply(block))
    }

    // fun CSG(block: CSG.() -> Unit): CSG = CSG().apply(block)

    fun Cube(block: Cube.() -> Unit) {
        children.add(Cube().apply(block))
    }

    fun Cylinder(block: Cylinder.() -> Unit) {
        children.add(Cylinder().apply(block))
    }

    fun Group(block: GroupBuilder.() -> Unit) {
        children.add(GroupBuilder().apply(block).build())
    }

    fun Plane(block: Plane.() -> Unit) {
        children.add(Plane().apply(block))
    }

    // fun SmoothTriangle(block: SmoothTriangle.() -> Unit): SmoothTriangle = SmoothTriangle().apply(block)

    fun Sphere(block: Sphere.() -> Unit) {
        children.add(Sphere().apply(block))
    }

    // fun Triangle(block: Triangle.() -> Unit): Triangle = Triangle().apply(block)

    fun ObjFile(path: String, block: Group.() -> Unit) {
        children.add(ObjFileParser(Path(path)).objToGroup().apply(block))
    }
}

class GroupBuilder : ObjectContainerBuilder() {
    var name = ""
    var transform = Matrix.identity4x4
    var material = Material.default

    fun build(): Group {
        return Group().apply {
            name = this@GroupBuilder.name
            transform = this@GroupBuilder.transform
            material = this@GroupBuilder.material
            children.addAll(this@GroupBuilder.children)
        }
    }
}

class WorldBuilder : ObjectContainerBuilder() {
    private val lights = mutableListOf<PointLight>()

    fun PointLight(position: Tuple, intensity: Color) {
        lights.add(garden.ephemeral.rocket.PointLight(position, intensity))
    }

    fun build(): World {
        return World().apply {
            objects.addAll(this@WorldBuilder.children)
            lights.addAll(this@WorldBuilder.lights)
        }
    }
}
