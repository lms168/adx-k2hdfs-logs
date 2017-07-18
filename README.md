# Sbt-Template

## Getting started

### Run in local as a developer

config:`src/main/resources/application.conf`:

```config
main {
  name = "hello"
  http {
    host = "0.0.0.0"
    port = 7777
  }
}

```


```bash
$ cd sbt-template
$ sbt run 
$ Enter number: 1
```

### Build 

```bash
$ cd sbt-template
$ sbt pkg -Dconf=local              #[dev] build target/universal/sbt-template-0.1.0.tgz
$ sbt pkg -Dconf=test               #[test] build target/universal/sbt-template-0.1.0.tgz
$ sbt pkg -Dconf=product            #[online] build target/universal/sbt-template-0.1.0.tgz
$ sbt pkg -Dconf=public             #[onlinetest] build target/universal/sbt-template-0.1.0.tgz

```

### Run in production

```bash
$ tar xvf sbt-template-0.1.0.tgz
$ ./app.sh start                    # Startup project
$ ./app.sh stop                     # Stop the project
$ ./app.sh server                   # Production mode starts
$ ./app.sh run                      # Debug mode starts

```

## Start Event Simulator

`src/main/resources/application.conf`:

```bash
$ cd sbt-template
$ sbt clean stage
$ ./app.sh run
```

## Unit Test

```bash
$ sbt test
```
