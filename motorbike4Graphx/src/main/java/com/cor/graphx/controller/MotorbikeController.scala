package com.cor.graphx.controller

import java.io.FileInputStream
import java.util.Properties
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import com.cor.graphx.controller.handler.MotorbikeEventHandler

/**
 * Object MotorbikeController
 */
object MotorbikeController {
  /**
   * Method start
   * Starts the application
   */
  def start {
    val logger: org.slf4j.Logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

    Logger.getLogger("org").setLevel(Level.OFF)

    //Spark configuration
    val conf = new SparkConf().setAppName("Graph").setMaster("local").set("spark.driver.memory", "2g")
    val sc = new SparkContext(conf)

    //Create instance for MotorbikeEventHandler
    val motorbikeEventHandler = new MotorbikeEventHandler(sc)
    motorbikeEventHandler.initService

    // Selecting properties
    val (nEvents, delay) = try {
      val prop = new Properties()
      prop.load(new FileInputStream("config.properties"))

      (
        prop.getProperty("NUM_EVENTS").toLong,
        prop.getProperty("DELAY").toBoolean)
    } catch {
      case e: Exception =>
        logger.info("Configuration file not found")
        sys.exit(1)
    }

    //Send events
    if (delay) {
      // Start Demo with delay
      MotorbikeEventGenerator.startSendingMotorbikesReadingsDelay(motorbikeEventHandler, nEvents)
    } else {
      // Start Demo without delay
      MotorbikeEventGenerator.startSendingMotorbikesReadings(motorbikeEventHandler, nEvents)
    }

  }
}