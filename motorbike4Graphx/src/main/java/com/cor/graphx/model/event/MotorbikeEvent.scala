package com.cor.graphx.model.event

/**
 * Class for event Motorbike
 */

case class MotorbikeEvent(idNode: Long, currentTimestamp: Long, timestamp: Long, motorbikeId: Integer, location: String, speed: Double, tirePressure1: Double, tirePressure2: Double, seat: Boolean) extends VertexProperty(currentTimestamp) {
  /**
   * toString Method
   */
  override def toString(): String = {
    return "MotorbikeEvent [" + timestamp + "," + motorbikeId + "," + location + "," + speed + "," + tirePressure1 + "," + tirePressure2 + "," + seat + "]";
  }

}