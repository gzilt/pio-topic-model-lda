package org.template.classification

import org.apache.predictionio.controller.{EmptyEvaluationInfo, PDataSource, Params, SanityCheck}
import org.apache.predictionio.data.store.PEventStore
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import grizzled.slf4j.Logger
import org.apache.spark.sql.SparkSession

case class DataSourceParams(
  appName: String,
  evalK: Option[Int]  // define the k-fold parameter.
) extends Params

class DataSource(val dsp: DataSourceParams)
  extends PDataSource[TrainingData,
      EmptyEvaluationInfo, Query, ActualResult] {

  @transient lazy val logger = Logger[this.type]

  /** Helper function used to store data given a SparkContext. */
  private def readEventData(sc: SparkContext) : RDD[TextPoint] = {
    //Get RDD of Events.
    PEventStore.find(
      appName = dsp.appName,
      entityType = Some("source"), // specify data entity type
      eventNames = Some(List("document")) // specify data event name
      // Convert collected RDD of events to and RDD of Observation
      // objects.
    )(sc)
    .map(e => {
      val text : String = e.properties.get[String]("text").trim
      TextPoint(text)
    }).cache
  }

  override
  def readTraining(sc: SparkContext): TrainingData = {
    new TrainingData(readEventData(sc))
  }

}

case class TextPoint (val text: String) extends Serializable

class TrainingData(
  val trainingText: RDD[TextPoint]
) extends Serializable with SanityCheck {

  /** Sanity check to make sure your data is being fed in correctly. */
  def sanityCheck(): Unit = {
    try {
      val obs : Array[String] = trainingText.takeSample(false, 5).map(_.text)

      println()
      (0 until 5).foreach(
        k => println("TextPoint " + (k + 1) +" label: " + obs(k))
      )
      println()
    } catch {
      case (e : ArrayIndexOutOfBoundsException) => {
        println()
        println("Data set is empty, make sure event fields match imported data.")
        println()
      }
    }

  }
}
