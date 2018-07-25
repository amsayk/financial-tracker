package name.amadoucisse.financialtracker

import java.sql.Timestamp

import io.circe._
import java.text.SimpleDateFormat

package object domain {

  val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")

  implicit val encodeTimestamp: Encoder[Timestamp] =
    Encoder.encodeString.contramap[Timestamp](dateFormat.format(_))
  implicit val decodeTimestamp: Decoder[Timestamp] =
    Decoder.decodeString.map(str => new Timestamp(dateFormat.parse(str).getTime))
}

