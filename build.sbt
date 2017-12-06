import AssemblyKeys._

assemblySettings

name := "template-scala-topic-model-LDA"

organization := "io.prediction"

scalaVersion := "2.11.8"

excludeFilter in Runtime in unmanagedResources := "*.html"

resolvers += Resolver.sonatypeRepo("snapshots")   

libraryDependencies ++= {
   Seq(
    "org.apache.predictionio" %% "apache-predictionio-core" % "0.12.0-incubating" % "provided",
    "org.apache.spark"        %% "spark-core"               % "2.1.1"             % "provided",
    "org.apache.spark"        %% "spark-mllib"              % "2.1.1"             % "provided",
    "org.xerial.snappy"        % "snappy-java"                                    % "1.1.1.7"
   )
}