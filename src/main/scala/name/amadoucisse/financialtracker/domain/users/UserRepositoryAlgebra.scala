package name.amadoucisse.financialtracker
package domain
package users

trait UserRepositoryAlgebra[F[_]] {
  def create(user: User): F[User]

  def update(user: User): F[Option[User]]

  def get(id: UserId): F[Option[User]]

  def findByIdentity(identity: Identity): F[Option[User]]

  def delete(userId: UserId): F[Option[User]]

  def deleteByIdentity(identity: Identity): F[Option[User]]

  def list(): F[Vector[User]]
}

