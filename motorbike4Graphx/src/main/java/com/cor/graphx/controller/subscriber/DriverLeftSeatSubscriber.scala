package com.cor.graphx.controller.subscriber

import com.cor.graphx.model.event.MotorbikeEvent
import com.cor.graphx.model.event.DriverLeftSeatEvent
import org.apache.spark.SparkContext
import scala.collection.mutable.ListBuffer
import com.cor.graphx.model.event.VertexProperty
import org.apache.spark.graphx.Graph
import org.apache.spark.graphx.VertexId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.apache.spark.graphx.Edge

/**
 * Class DriverLeftSeatSubscriber
 */

class DriverLeftSeatSubscriber(sc: SparkContext) {

  //List of events DriverLeftSeat
  var verticesDriverLeftSeat = ListBuffer[(VertexProperty)]()

  /**
   * Method driverLeftSeat
   * Updates verticesDriverLeftSeat
   * @param graph stores events
   * @return graph with DriverLeftSeat events
   */
  def driverLeftSeat(graph: Graph[(VertexProperty), (String, String, Long)]): Graph[(VertexProperty), (Integer, String, Long)] = {

    val groupMotorbike = graph.vertices.groupBy(attr => attr._2.asInstanceOf[MotorbikeEvent].motorbikeId)
    val groupMotorbikeOrder = groupMotorbike.map(f => (f._1, f._2.toList.sortBy(f => f._2.asInstanceOf[MotorbikeEvent].currentTimestamp))).collect
    verticesDriverLeftSeat = verticesDriverLeftSeat ++ groupMotorbikeOrder.flatMap(f => isDriverLeftSeat(f._2))
    val driverLeftSeatVertices = sc.parallelize(verticesDriverLeftSeat).zipWithIndex().map(r => (r._2.asInstanceOf[VertexId], r._1))
    verticesDriverLeftSeat = verticesDriverLeftSeat.filter { attr => (System.currentTimeMillis() - attr.currentTimeStamp) < 3000 }
    val graphDriverLeftSeat = Graph(driverLeftSeatVertices, sc.parallelize(ListBuffer[Edge[(Integer, String, Long)]]()))

    return graphDriverLeftSeat
  }

  /**
   * Method isDriverLeftSeat
   * @param list list with motorbike events
   * @return list with DriverLeftSeat events produced
   * Checks if a DriverLeftSeat event has been produced and return a list with those events
   */
  def isDriverLeftSeat(list: List[(VertexId, VertexProperty)]): ListBuffer[(VertexProperty)] = {
    val logger: Logger = LoggerFactory.getLogger(this.getClass)
    var result = ListBuffer[(VertexProperty)]()
    for (a <- list; b <- list) {
      if (a._2.asInstanceOf[MotorbikeEvent].seat == true
        && b._2.asInstanceOf[MotorbikeEvent].seat == false
        && b._2.asInstanceOf[MotorbikeEvent].currentTimestamp > a._2.asInstanceOf[MotorbikeEvent].currentTimestamp
        && sc.parallelize(verticesDriverLeftSeat).filter(attr => attr.asInstanceOf[DriverLeftSeatEvent].currentTimestamp1 == a._2.asInstanceOf[MotorbikeEvent].idNode && attr.asInstanceOf[DriverLeftSeatEvent].currentTimestamp2 == b._2.asInstanceOf[MotorbikeEvent].idNode).count == 0) {

        val driverLeftSeatEvent = new DriverLeftSeatEvent(System.currentTimeMillis(), a._2.asInstanceOf[MotorbikeEvent].motorbikeId, b._2.asInstanceOf[MotorbikeEvent].location, a._2.asInstanceOf[MotorbikeEvent].seat, b._2.asInstanceOf[MotorbikeEvent].seat, a._2.asInstanceOf[MotorbikeEvent].idNode, b._2.asInstanceOf[MotorbikeEvent].idNode)
        logger.info(driverLeftSeatEvent.toString)
        result += driverLeftSeatEvent
      }
    }

    return result
  }

}