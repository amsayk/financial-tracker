package name.amadoucisse.financialtracker
package domain
package users

import io.circe._

case class Identity(value: String) extends AnyVal

object Identity {
  implicit val encoder: Encoder[Identity] = Encoder.encodeString.contramap[Identity](_.value)
  implicit val decoder: Decoder[Identity] = Decoder.decodeString.map(Identity(_))


}
