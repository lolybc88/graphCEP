package com.cor.graphx.model.event

/**
 * Class for event BlowOutTire
 */
case class BlowOutTireEvent(timestamp: Long, motorbikeId: Integer, location_a1: String, tirePressure1_a1: Double, tirePressure2_a1: Double, location_a2: String, tirePressure1_a2: Double, tirePressure2_a2: Double, currentTimestamp1: Long, currentTimestamp2: Long) extends VertexProperty(timestamp) {

  /**
   * toString Method
   */
  override def toString(): String = {
    return "BlowOutTireEvent [" + timestamp + "," + motorbikeId + "," + location_a1 + "," + tirePressure1_a1 + "," + tirePressure2_a1 + "," + location_a2 + "," + tirePressure1_a2 + "," + tirePressure2_a2 + "]";
  }

}
