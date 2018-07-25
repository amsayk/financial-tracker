package name.amadoucisse.financialtracker
package service

import cats.Monad
import cats.implicits._
import cats.data.EitherT

import domain.users.{ UserId, Identity, UserRepositoryAlgebra, User }

import domain.{UserAlreadyExistsError, UserNotFoundError}

final class UserService[F[_]: Monad](userRepo: UserRepositoryAlgebra[F]) {

  def createUser(user: User): EitherT[F, UserAlreadyExistsError, User] =
    EitherT.liftF(userRepo.create(user))

  def getUser(userId: UserId): EitherT[F, UserNotFoundError.type, User] =
    EitherT.fromOptionF(userRepo.get(userId), UserNotFoundError)

  def getUserByIdentity(identity: Identity): EitherT[F, UserNotFoundError.type, User] =
    EitherT.fromOptionF(userRepo.findByIdentity(identity), UserNotFoundError)

  def deleteUser(userId: UserId): F[Unit] = userRepo.delete(userId).as(())

  def deleteByIdentity(identity: Identity): F[Unit] =
    userRepo.deleteByIdentity(identity).as(())

  def update(user: User): EitherT[F, UserNotFoundError.type, User] =
    EitherT.fromOptionF(userRepo.update(user), UserNotFoundError)

  def list(): F[Vector[User]] =
    userRepo.list()
}

object UserService {
  def apply[F[_]: Monad](repository: UserRepositoryAlgebra[F]): UserService[F] =
    new UserService[F](repository)
}
