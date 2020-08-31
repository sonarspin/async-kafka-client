package com.sonarspin.kafka.client.consumer

import akka.actor.typed.ActorRef
import com.sonarspin.kafka.client.message.ConsumerWorkerMessage

trait KafkaConsumerType {

  def consume[A](topic: String, transformer: ConsumerTransformer, self: ActorRef[ConsumerWorkerMessage]): Unit

}
