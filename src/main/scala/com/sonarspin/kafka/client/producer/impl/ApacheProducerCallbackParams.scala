package com.sonarspin.kafka.client.producer.impl

import org.apache.kafka.clients.producer.RecordMetadata

final case class ApacheProducerCallbackParams(
                                         topic: String,
                                         msg: String,
                                         recordMetadata: RecordMetadata,
                                         exception: Exception
                                       )
