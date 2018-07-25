package name.amadoucisse.financialtracker
package domain
package users

import io.circe._

case class UserId(value: Long) extends AnyVal

object UserId {
  implicit val encoder: Encoder[UserId] = Encoder.encodeLong.contramap[UserId](_.value)
  implicit val decoder: Decoder[UserId] = Decoder.decodeLong.map(UserId(_))

}
