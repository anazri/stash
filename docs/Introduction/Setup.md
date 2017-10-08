# Prerequisites 

- Java 8 or higher
- PostgreSQL server
- A configuration YAML file containing the PostgreSQL credentials

Here is an example of a `config.yml` file: 

```yaml
applicationName: Stash Backend
isAppCreationOpen: true
database:
  url: jdbc:postgresql://localhost:5432/yourdb 
  user: yourUser
  password: yourPassword
  min_size: 8
  max_size: 32
  driver_class: org.postgresql.Driver
  max_wait_for_connection: 1s
  validation_query: "SELECT 1"
  check_connection_while_idle: false
  eviction_interval: 10s
  min_idle_time: 1 minute
  properties:
    char_set: UTF-8

```

# Download the application

The source code can be downloaded from [GitHub](https://github.com/gaboratorium/stash). The latest release 
of the executable application (fat jar) can be also found on 
[GitHub Stash Releases](https://github.com/gaboratorium/stash/releases).

# Exporting a fat jar application

If you have downloaded the source code, you can either run the application
 from your favorite IDE, or you can export the application as a **fat jar (uber jar)**
by running the following command in the root directory of the project:
 `gradle shadowJar`. If you want to make sure that you get a clean build, you can
 run `gradle clean` first to delete existing builds.
 
 The exported `jar` will be found 
 in the `build/libs` directory. 
 
 # Running the fat jar application
 
 To start the application run the following command: 
 `java -jar build/libs/stash-1.0-SNAPSHOT-all.jar`. After running the application
 we will see that an additional parameter has to be provided describing
 our server configuration (the `YML` file we have created eariler). Depending on where our configuration (YAML) file is,
 we can use the following command:
 `java -jar build/libs/stash-1.0-SNAPSHOT-all.jar server src/main/java/com/gaboratorium/stash/config.yml`.

After the application has succesfully connected to the Postgres server, our application
will be available at `localhost:8080` and `localhost:8081`.
