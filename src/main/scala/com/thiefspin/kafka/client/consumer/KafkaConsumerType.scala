package com.thiefspin.kafka.client.consumer

trait KafkaConsumerType {

  def consume[A](topic: String, transformer: ConsumerTransformer): Unit

}
