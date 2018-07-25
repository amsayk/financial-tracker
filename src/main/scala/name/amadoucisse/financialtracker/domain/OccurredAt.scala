package name.amadoucisse.financialtracker.domain

import java.sql.Timestamp

import io.circe._

case class OccurredAt(value: Timestamp) extends AnyVal

object OccurredAt {
  implicit val encoder: Encoder[OccurredAt] = encodeTimestamp.contramap[OccurredAt](_.value)
  implicit val decoder: Decoder[OccurredAt] = decodeTimestamp.map(OccurredAt(_))

  def now = OccurredAt(new Timestamp(System.currentTimeMillis))
}
