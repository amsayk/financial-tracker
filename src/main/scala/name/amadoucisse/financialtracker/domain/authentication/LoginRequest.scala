package name.amadoucisse.financialtracker
package domain
package authentication

import domain.users.{ Identity, Password }

final case class LoginRequest(
  identity: Identity,
  password: Password
)

