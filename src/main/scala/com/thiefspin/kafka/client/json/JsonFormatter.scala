package com.thiefspin.kafka.client.json

trait JsonFormatter[A] {

  def toJsonString(entity: A): String

  def fromJsonString(json: String): Option[A]

}
