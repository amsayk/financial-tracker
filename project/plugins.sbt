addSbtPlugin("org.lyranthe.sbt" % "partial-unification" % "1.1.0")
//addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.1.3")
addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.6")
addSbtPlugin("io.spray" % "sbt-revolver" % "0.9.1")

addSbtPlugin("com.geirsson" % "sbt-scalafmt" % "1.5.1")

// Database migrations
addSbtPlugin("org.flywaydb" % "flyway-sbt" % "4.2.0")

resolvers += "Flyway".at("https://davidmweber.github.io/flyway-sbt.repo")

// Native Packager allows us to create standalone jar
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.4")
