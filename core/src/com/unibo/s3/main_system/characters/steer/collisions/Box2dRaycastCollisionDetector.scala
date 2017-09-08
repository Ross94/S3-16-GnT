/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.unibo.s3.main_system.characters.steer.collisions

import com.badlogic.gdx.ai.utils.Collision
import com.badlogic.gdx.ai.utils.Ray
import com.badlogic.gdx.ai.utils.RaycastCollisionDetector
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d._

/** A raycast collision detector for box2d.
 * 
 * @author mvenditto */

trait CornerCollisionFix extends RayCastCallback {
  val COLL_POINT_ADJUSTMENT = 0.1f
  val tmp = new Vector2()

  /*Experimental! if collision point is precisely on corner..*/
  private def fixPerimeterStuck(fixture: Fixture, p: Vector2) {
    val c  = fixture.getBody.getWorldCenter

    fixture.getShape match {
      case ps: PolygonShape =>
        ps.getVertex(0, tmp)
      case cs: CircleShape =>
        tmp.set(cs.getRadius, cs.getRadius)
      case _ => tmp.set(0, 0)
    }

    val hw = Math.abs(tmp.x)
    val hh = Math.abs(tmp.y)

    if (p.x == c.x + hw) p.x += COLL_POINT_ADJUSTMENT
    if (p.x == c.x - hw) p.x -= COLL_POINT_ADJUSTMENT
    if (p.y == c.y + hh) p.y += COLL_POINT_ADJUSTMENT
    if (p.y == c.y - hw) p.y -= COLL_POINT_ADJUSTMENT
  }

  abstract override def reportRayFixture(fixture: Fixture, point: Vector2, normal: Vector2, fraction: Float): Float = {
    fixPerimeterStuck(fixture, point)
    super.reportRayFixture(fixture, point, normal, fraction)
  }
}

class Box2dRaycastCallback extends RayCastCallback {
  var outputCollision: Collision[Vector2] = _
  var collided: Boolean = _

  override def reportRayFixture(fixture: Fixture, point: Vector2, normal: Vector2, fraction: Float): Float = {
    if (outputCollision != null) outputCollision.set(point, normal)
    collided = true
    fraction
  }
}

class Box2dRaycastCollisionDetector(val world: World, val callback: Box2dRaycastCallback) extends RaycastCollisionDetector[Vector2] {

	def this(world: World) {
    this(world, new Box2dRaycastCallback() with CornerCollisionFix)
	}

	override def collides (ray: Ray[Vector2]): Boolean = findCollision(null, ray)

  override def findCollision(outputCollision: Collision[Vector2], inputRay: Ray[Vector2]): Boolean = {
    callback.collided = false
    if (!inputRay.start.epsilonEquals(inputRay.end, MathUtils.FLOAT_ROUNDING_ERROR)) {
      callback.outputCollision = outputCollision
      world.rayCast(callback, inputRay.start, inputRay.end)
    }
    callback.collided
  }
}
