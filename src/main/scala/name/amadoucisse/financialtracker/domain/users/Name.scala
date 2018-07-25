package name.amadoucisse.financialtracker
package domain
package users

import io.circe._

case class Name(value: String) extends AnyVal

object Name {
  implicit val encoder: Encoder[Name] = Encoder.encodeString.contramap[Name](_.value)
  implicit val decoder: Decoder[Name] = Decoder.decodeString.map(Name(_))


}

