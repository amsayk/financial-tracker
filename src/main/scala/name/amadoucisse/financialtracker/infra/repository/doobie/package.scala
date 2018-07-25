package name.amadoucisse.financialtracker
package infra
package repository

import java.sql.Timestamp

import domain.OccurredAt
import domain.users.{Email, Identity, Password, Name, UserId}

package object doobie {
  import _root_.doobie._

  implicit val EmailMeta: Meta[Email] =
    Meta[String].xmap[Email](x => Email(x), _.value)

  implicit val PasswordMeta: Meta[Password] =
    Meta[String].xmap[Password](x => Password(x), _.value)

  implicit val IdentityMeta: Meta[Identity] =
    Meta[String].xmap[Identity](x => Identity(x), _.value)

  implicit val NameMeta: Meta[Name] =
    Meta[String].xmap[Name](x => Name(x), _.value)

  implicit val IdMeta: Meta[UserId] =
    Meta[Long].xmap[UserId](x => UserId(x), _.value)

  implicit val OccurredAtMeta: Meta[OccurredAt] =
    Meta[Timestamp].xmap[OccurredAt](x => OccurredAt(x), _.value)
}

