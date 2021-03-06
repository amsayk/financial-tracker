package name.amadoucisse.financialtracker
package infra
package endpoint

import cats.data.{EitherT, OptionT}
import cats.effect.Effect
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityDecoder, HttpService}

import scala.language.higherKinds
import domain._
import domain.users._
import domain.authentication._
import service.UserService
import tsec.common.Verified
import tsec.passwordhashers.{PasswordHash, PasswordHasher}

class UserEndpoints[F[_]: Effect, A, K] extends Http4sDsl[F] {
  // import Pagination._
  /* Jsonization of our User type */

  implicit val userDecoder: EntityDecoder[F, User] = jsonOf
  implicit val loginReqDecoder: EntityDecoder[F, LoginRequest] = jsonOf

  implicit val signupReqDecoder: EntityDecoder[F, SignupRequest] = jsonOf

  private def loginEndpoint(userService: UserService[F], cryptService: PasswordHasher[F, A]): HttpService[F] =
    HttpService[F] {
      case req @ POST -> Root / "login" =>
        val action: EitherT[F, UserAuthenticationFailedError, User] = for {
          login <- EitherT.liftF(req.as[LoginRequest])
          identity = login.identity
          user <- userService.getUserByIdentity(identity).leftMap(_ => UserAuthenticationFailedError(identity.value))

          checkResult <- (for {
            password <- OptionT(user.password.pure[F])
            checkResult <- OptionT.liftF(cryptService.checkpw(login.password.value, PasswordHash[A](password.value)))
          } yield checkResult).toRight(UserAuthenticationFailedError(identity.value))

          resp <-
            if (checkResult == Verified) EitherT.rightT[F, UserAuthenticationFailedError](user)
            else EitherT.leftT[F, User](UserAuthenticationFailedError(identity.value))
        } yield resp

        action.value.flatMap {
          case Right(user) => Ok(user.asJson)
          case Left(UserAuthenticationFailedError(name)) => BadRequest(s"Authentication failed for user $name")
        }
    }

  private def signupEndpoint(userService: UserService[F], crypt: PasswordHasher[F, A]): HttpService[F] =
    HttpService[F] {
      case req @ POST -> Root / "users" =>
        val action = for {
          signup <- req.as[SignupRequest]
          hashPassword <- crypt.hashpw(signup.password)
          user <- signup.asUser(hashPassword).pure[F]
          result <- userService.createUser(user).value
        } yield result

        action.flatMap {
          case Right(saved) => Ok(saved.asJson)
          case Left(UserAlreadyExistsError(existing)) =>
            Conflict(s"The user with user name ${existing.identity} already exists")
        }
    }

  private def updateEndpoint(userService: UserService[F]): HttpService[F] =
    HttpService[F] {
      case req @ PUT -> Root / "users" / name =>
        val action = for {
          user <- req.as[User]
          updated = user.copy(identity = Identity(name))
          result <- userService.update(updated).value
        } yield result

        action.flatMap {
          case Right(saved) => Ok(saved.asJson)
          case Left(UserNotFoundError) => NotFound("User not found")
        }
    }

  private def listEndpoint(userService: UserService[F]): HttpService[F] =
    HttpService[F] {
      case GET -> Root / "users" =>
        for {
          retrived <- userService.list()
          resp <- Ok(retrived.asJson)
        } yield resp
    }

  private def searchByIdentityEndpoint(userService: UserService[F]): HttpService[F] =
    HttpService[F] {
      case GET -> Root / "users" / identity =>
        userService.getUserByIdentity(Identity(identity)).value.flatMap {
          case Right(found) => Ok(found.asJson)
          case Left(UserNotFoundError) => NotFound("The user was not found")
        }
    }

  private def deleteUserEndpoint(userService: UserService[F]): HttpService[F] =
    HttpService[F] {
      case DELETE -> Root / "users" / identity =>
        for {
          _ <- userService.deleteByIdentity(Identity(identity))
          resp <- Ok()
        } yield resp
    }


  def endpoints(userService: UserService[F], cryptService: PasswordHasher[F, A]): HttpService[F] =
    loginEndpoint(userService, cryptService)  <+>
    signupEndpoint(userService, cryptService) <+>
    updateEndpoint(userService) <+>
    deleteUserEndpoint(userService) <+>
    searchByIdentityEndpoint(userService) <+>
    listEndpoint(userService)
}

object UserEndpoints {
  def endpoints[F[_]: Effect, A, K](
    userService: UserService[F],
    cryptService: PasswordHasher[F, A]
  ): HttpService[F] =
    new UserEndpoints[F, A, K].endpoints(userService, cryptService)
}

