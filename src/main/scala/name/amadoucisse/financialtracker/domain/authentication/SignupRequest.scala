package name.amadoucisse.financialtracker
package domain
package authentication

import users.User

import tsec.passwordhashers.PasswordHash

import domain.users.{ Identity, Name, Password, Email }

import cats.syntax.option._

final case class SignupRequest(
  identity: String,
  firstName: String,
  lastName: String,
  email: String,
  password: String,
) {
  def asUser[A](hashedPassword: PasswordHash[A]) : User = User(
    identity = Identity(identity),
    firstName = Name(firstName),
    lastName = Name(lastName),
    email = Email(email),
    password = Password(hashedPassword.toString).some,
    createdAt = OccurredAt.now.some,
    updatedAt = OccurredAt.now.some
  )
}

