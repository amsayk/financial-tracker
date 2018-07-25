package name.amadoucisse.financialtracker
package domain
package users

import io.circe._

case class Email(value: String) extends AnyVal

object Email {
  implicit val encoder: Encoder[Email] = Encoder.encodeString.contramap[Email](_.value)
  implicit val decoder: Decoder[Email] = Decoder.decodeString.map(Email(_))


}
