# Adventure Builder [![Build Status](https://travis-ci.com/tecnico-softeng/es19al_04-project.svg?token=xDPBAaQ2epnFt9PRstYY&branch=develop)](https://travis-ci.com/tecnico-softeng/es19al_04-project)[![codecov](https://codecov.io/gh/tecnico-softeng/es19al_04-project/branch/develop/graph/badge.svg?token=kiZWzYgqEC)](https://codecov.io/gh/tecnico-softeng/es19al_04-project)


To run tests execute: mvn clean install

To see the coverage reports, go to <module name>/target/site/jacoco/index.html.

### Rastreabilidade do trabalho

Ordene a tabela por ordem crescente da data de término.

|   Issue id | Owner (ist number) | Owner (github username) | PRs id (with link)  |            Date    |  
| ---------- | -------------------| ----------------------- | ------------------- | ------------------ |
|    #158    | 86460              | CallMeLeopoldo          | [#168](https://github.com/tecnico-softeng/es19al_04-project/pull/168)                | 17-04-2019         |
|    #186    | 86408              | Fogoid                  | [#190](https://github.com/tecnico-softeng/es19al_04-project/pull/190)                | 19-04-2019         |
|    #183    | 86408              | Fogoid                  | [#184](https://github.com/tecnico-softeng/es19al_04-project/pull/184)                | 19-04-2019         |
|    #179    | 86408              | Fogoid                  | [#181](https://github.com/tecnico-softeng/es19al_04-project/pull/181)                | 19-04-2019         |
|    #153    | 86460              | CallMeLeopoldo          | [#185](https://github.com/tecnico-softeng/es19al_04-project/pull/185)              | 19-04-2019           |
|    #155    | 86460              | CallMeLeopoldo          | [#188](https://github.com/tecnico-softeng/es19al_04-project/pull/188)                | 19-04-2019         |
|    #156    | 86460              | CallMeLeopoldo          | [#189](https://github.com/tecnico-softeng/es19al_04-project/pull/189)                | 19-04-2019         |
|    #157    | 86460              | CallMeLeopoldo          | [#187](https://github.com/tecnico-softeng/es19al_04-project/pull/187)                | 19-04-2019         |
|    #160    | 86460              | CallMeLeopoldo          | [#176](https://github.com/tecnico-softeng/es19al_04-project/pull/176)                | 19-04-2019         |
|    #167    | 73751              | jotapero                | [#167](https://github.com/tecnico-softeng/es19al_04-project/issues/163)              | 19-04-2019         |
|    #192    | 86460              | CallMeLeopoldo          | [#194](https://github.com/tecnico-softeng/es19al_04-project/pull/194)                | 20-04-2019         |
|    #193    | 86460              | CallMeLeopoldo          | [#196](https://github.com/tecnico-softeng/es19al_04-project/pull/196)                | 20-04-2019         |
|    #174    | 86408              | Fogoid                  | [#195](https://github.com/tecnico-softeng/es19al_04-project/pull/195)                | 20-04-2019         |
|    #159    | 86408              | Fogoid                  | [#197](https://github.com/tecnico-softeng/es19al_04-project/pull/197)                | 20-04-2019         |
|    #198    | 86408              | Fogoid                  | [#199](https://github.com/tecnico-softeng/es19al_04-project/pull/199)                | 21-04-2019         |
|    #152    | 80888              | franciscomira           | [#200](https://github.com/tecnico-softeng/es19al_04-project/pull/200)
           | 22-04-2018
|    #164    | 80888              | franciscomira           | [#202](https://github.com/tecnico-softeng/es19al_04-project/pull/202)
           | 22-04-2018           


### Infrastructure

This project includes the persistent layer, as offered by the FénixFramework.
This part of the project requires to create databases in mysql as defined in `resources/fenix-framework.properties` of each module.

See the lab about the FénixFramework for further details.

#### Docker (Alternative to installing Mysql in your machine)

To use a containerized version of mysql, follow these stesp:

```
docker-compose -f local.dev.yml up -d
docker exec -it mysql sh
```

Once logged into the container, enter the mysql interactive console

```
mysql --password
```

And create the 6 databases for the project as specified in
the `resources/fenix-framework.properties`.

To launch a server execute in the module's top directory: mvn clean spring-boot:run

To launch all servers execute in bin directory: startservers

To stop all servers execute: bin/shutdownservers

To run jmeter (nogui) execute in project's top directory: mvn -Pjmeter verify. Results are in target/jmeter/results/, open the .jtl file in jmeter, by associating the appropriate listeners to WorkBench and opening the results file in listener context
