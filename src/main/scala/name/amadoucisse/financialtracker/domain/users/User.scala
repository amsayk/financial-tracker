package name.amadoucisse.financialtracker
package domain
package users

case class User(
  identity: Identity,
  firstName: Name,
  lastName: Name,
  email: Email,
  password: Option[Password],
  createdAt: Option[OccurredAt],
  updatedAt: Option[OccurredAt],
  id: Option[UserId] = None,
 )

object User {

}
