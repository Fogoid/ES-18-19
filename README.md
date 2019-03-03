﻿# Adventure Builder [![Build Status](https://travis-ci.com/tecnico-softeng/prototype-2018.svg?token=fJ1UzWxWjpuNcHWPhqjT&branch=master)](https://travis-ci.com/tecnico-softeng/prototype-2018) [![codecov](https://codecov.io/gh/tecnico-softeng/prototype-2018/branch/master/graph/badge.svg?token=OPjXGqoNEm)](https://codecov.io/gh/tecnico-softeng/prototype-2018)


To run tests execute: mvn clean install

To see the coverage reports, go to <module name>/target/site/jacoco/index.html.


|   Number   |          Name           |               Email                |   GitHub Username  | Group |
| ---------- | ----------------------- | ---------------------------------- | -------------------| ----- |
|     73046  |   André Antunes         |  ander.r.antunes@ist.utl.pt        | Antunes10          |   1   |
|     80888  |   Francisco Mira        |  francisco.mira96@gmail.com        | franciscomira      |   1   |
|     86408  |   Diogo Fernandes       | diogo.barradas.fernandes@gmail.com | Fogoid             |   1   |
|     86460  |   Leandro Salgado       |  lmocs98@gmail.com                 | CallMeLeopoldo     |   2   |
|     86495  |   Pedro Barbosa         | pedrobarbosa262@gmail.com          | quickacid          |   2   |
|     73751  |   José Rosendo          |   j.pd19@hotmail.com               | jotapero           |   2   |

- **Group 1:** André Antunes, Francisco Mira, Diogo Fernandes
- **Group 2:** Leandro Salgado, Pedro Barbosa, José Resendo

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

