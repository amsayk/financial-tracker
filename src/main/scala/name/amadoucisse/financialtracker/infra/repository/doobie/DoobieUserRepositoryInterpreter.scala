package name.amadoucisse.financialtracker
package infra
package repository
package doobie

import cats.Monad
import cats.data.OptionT
import cats.implicits._

import _root_.doobie._
import _root_.doobie.implicits._

import domain.OccurredAt
import domain.users.{Identity, User, UserId, UserRepositoryAlgebra}

private object UserSQL {

  def insert(user: User): Update0 = sql"""
    INSERT INTO users (identity, first_name, last_name, email, password)
    VALUES (${user.identity}, ${user.firstName}, ${user.lastName}, ${user.email}, ${user.password})
  """.update

  def byIdentity(identity: Identity): Query0[User] = sql"""
    SELECT
      identity,
      first_name,
      last_name,
      email,
      password,
      created_at,
      updated_at,
      id
    FROM users
    WHERE identity = $identity
  """.query[User]

  def update(user: User, id: UserId): Update0 = sql"""
    UPDATE users
    SET
      first_name = ${user.firstName},
      last_name = ${user.lastName},
      email = ${user.email},
      updated_at = ${user.updatedAt}
    WHERE id = $id
  """.update

  def select(userId: UserId): Query0[User] = sql"""
    SELECT
      identity,
      first_name,
      last_name,
      email,
      password,
      created_at,
      updated_at,
      id
    FROM users
    WHERE id = $userId
  """.query

  def delete(userId: UserId): Update0 = sql"""
    DELETE FROM users WHERE id = $userId
  """.update

  val selectAll: Query0[User] = sql"""
    SELECT
      identity,
      first_name,
      last_name,
      email,
      password,
      created_at,
      updated_at,
      id
    FROM users
  """.query
}

final class DoobieUserRepositoryInterpreter[F[_]: Monad](val xa: Transactor[F])
    extends UserRepositoryAlgebra[F] {

  def create(user: User): F[User] =
    UserSQL
      .insert(user)
      .withUniqueGeneratedKeys[Long]("id")
      .map(id => user.copy(id = UserId(id).some))
      .transact(xa)

  def update(user: User): F[Option[User]] =
    OptionT
      .fromOption[F](user.id)
      .flatMapF { id =>
        val newUser = user.copy(updatedAt = OccurredAt.now.some)
        UserSQL
          .update(newUser, id)
          .run
          .transact(xa) *> get(id)
      }
      .value

  def get(id: UserId): F[Option[User]] = UserSQL.select(id).option.transact(xa)

  def findByIdentity(identity: Identity): F[Option[User]] =
    UserSQL.byIdentity(identity).option.transact(xa)

  def delete(userId: UserId): F[Option[User]] =
    OptionT(get(userId)).semiflatMap(user => UserSQL.delete(userId).run.transact(xa).as(user)).value

  def deleteByIdentity(identity: Identity): F[Option[User]] =
    OptionT(findByIdentity(identity)).mapFilter(_.id).flatMapF(delete).value

  def list(): F[Vector[User]] = UserSQL.selectAll.to[Vector].transact(xa)
}

object DoobieUserRepositoryInterpreter {
  def apply[F[_]: Monad](xa: Transactor[F]): DoobieUserRepositoryInterpreter[F] =
    new DoobieUserRepositoryInterpreter(xa)
}
