import sbt._
import Keys._

object ProjectBuild extends Build {

  val commonName = "db-async-common"
  val postgresqlName = "postgresql-async"
  val mysqlName = "mysql-async"
  val commonVersion = "0.1.2-SNAPSHOT"

  lazy val root = Project(
    id = "db-async-base",
    base = file("."),
    settings = Configuration.baseSettings,
    aggregate = Seq(common, postgresql, mysql)
  )

  lazy val common = Project(
    id = commonName,
    base = file(commonName),
    settings = Configuration.baseSettings ++ Seq(
      name := commonName,
      version := commonVersion,
      libraryDependencies := Configuration.commonDependencies
    )
  )

  lazy val postgresql = Project(
    id = postgresqlName,
    base = file(postgresqlName),
    settings = Configuration.baseSettings ++ Seq(
      name := postgresqlName,
      version := commonVersion,
      libraryDependencies ++= Configuration.implementationDependencies
    )
  ) aggregate (common) dependsOn (common)

  lazy val mysql = Project(
    id = mysqlName,
    base = file(mysqlName),
    settings = Configuration.baseSettings ++ Seq(
      name := mysqlName,
      version := commonVersion,
      libraryDependencies ++= Configuration.implementationDependencies
    )
  ) aggregate (common) dependsOn (common)

}

object Configuration {

  val specs2Dependency = "org.specs2" %% "specs2" % "1.14" % "test"

  val commonDependencies = Seq(
    "commons-pool" % "commons-pool" % "1.6",
    "ch.qos.logback" % "logback-classic" % "1.0.9",
    "joda-time" % "joda-time" % "2.2",
    "org.joda" % "joda-convert" % "1.3.1",
    "org.scala-lang" % "scala-library" % "2.10.1",
    "io.netty" % "netty" % "3.6.5.Final",
    specs2Dependency
  ).map( d => d.withSources().withJavadoc()  )

  val implementationDependencies = Seq(
    specs2Dependency
  ).map( d => d.withSources().withJavadoc()  )

  val baseSettings = Defaults.defaultSettings ++ Seq(
    scalacOptions :=
      Opts.compile.encoding("UTF8")
        :+ Opts.compile.deprecation
        :+ Opts.compile.unchecked
        :+ Opts.compile.explaintypes
        :+ "-feature"
    ,
    scalacOptions in doc := Seq("-doc-external-doc:scala=http://www.scala-lang.org/archives/downloads/distrib/files/nightly/docs/library/"),
    scalaVersion := "2.10.1",
    javacOptions := Seq("-source", "1.5", "-target", "1.5", "-encoding", "UTF8"),
    organization := "com.github.mauricio",
    publishArtifact in Test := false,
    publishMavenStyle := true,
    pomIncludeRepository := {
      _ => false
    },
    publishTo <<= version {
      v: String =>
        val nexus = "https://oss.sonatype.org/"
        if (v.trim.endsWith("SNAPSHOT"))
          Some("snapshots" at nexus + "content/repositories/snapshots")
        else
          Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    pomExtra := (
      <url>https://github.com/mauricio/postgresql-async</url>
        <licenses>
          <license>
            <name>APACHE-2.0</name>
            <url>http://www.apache.org/licenses/LICENSE-2.0</url>
            <distribution>repo</distribution>
          </license>
        </licenses>
        <scm>
          <url>git@github.com:mauricio/postgresql-netty.git</url>
          <connection>scm:git:git@github.com:mauricio/postgresql-netty.git</connection>
        </scm>
        <developers>
          <developer>
            <id>mauricio-linhares</id>
            <name>Maurício Linhares</name>
            <url>https://github.com/mauricio</url>
          </developer>
        </developers>
      )
  )

}