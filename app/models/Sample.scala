package models

import com.redis._
import play.api.Play.current

case class Sample(name: String, id: Long)

object Sample {

  // key to use for this collection in Redis
  private val sampleKey = "samples"

  private val config = current.configuration

  private val redisHost = config.getString("janrain.firehose.redis.host").getOrElse("localhost")
  private val redisPort = config.getInt("janrain.firehose.redis.port").getOrElse(6379)

  // not a good idea, but making it a singleton for simplicity
  private lazy val client = new RedisClient(redisHost, redisPort)

  def add(sample: Sample) {
    client.hset(sampleKey, sample.id, sample.name)
  }

  def getById(id: Long): Option[Sample] = {
    client.hget(sampleKey, id).map(name ⇒ Sample(name, id))
  }

  def findAll: Iterable[Sample] = {
    val items = client.hgetall[String, String](sampleKey).getOrElse(Map.empty)
    for (k ← items.keys) yield {
      Sample(items(k), k.toLong)
    }
  }
}
