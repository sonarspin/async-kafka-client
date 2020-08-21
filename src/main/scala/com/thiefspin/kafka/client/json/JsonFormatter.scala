package com.thiefspin.kafka.client.json

trait JsonFormatter[A] {

  def toJsonString(entity: A): String

}
