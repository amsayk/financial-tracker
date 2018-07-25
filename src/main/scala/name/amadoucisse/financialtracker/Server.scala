package name.amadoucisse.financialtracker

import config.{AppConfig, DatabaseConfig}
import cats.effect.{Effect, IO}
import fs2.{Stream, StreamApp}
import org.http4s.server.blaze.BlazeBuilder

import service.UserService
import infra.endpoint.UserEndpoints
import infra.repository.doobie.{DoobieUserRepositoryInterpreter}

import tsec.passwordhashers.jca.BCrypt

import scala.concurrent.ExecutionContext.Implicits.global

object Server extends StreamApp[IO] {
  def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, StreamApp.ExitCode] = ServerStream.stream[IO]
}

object ServerStream {

  def stream[F[_]: Effect]: Stream[F, StreamApp.ExitCode] =
    for {
      conf           <- Stream.eval(AppConfig.load[F])
      xa             <- Stream.eval(DatabaseConfig.dbTransactor(conf.db))
      _              <- Stream.eval(DatabaseConfig.initializeDb(conf.db, xa))
      userRepo       = DoobieUserRepositoryInterpreter(xa)
      userService    = UserService(userRepo)
      exitCode       <- BlazeBuilder[F]
        .bindHttp(8080, "localhost")
        .mountService(UserEndpoints.endpoints(userService, BCrypt.syncPasswordHasher[F]), "/")
        .serve
    } yield exitCode
}
