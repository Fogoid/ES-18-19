# Adventure Builder [![Build Status](https://travis-ci.com/tecnico-softeng/es19al_04-project.svg?token=xDPBAaQ2epnFt9PRstYY&branch=develop)](https://travis-ci.com/tecnico-softeng/es19al_04-project)[![codecov](https://codecov.io/gh/tecnico-softeng/es19al_04-project/branch/develop/graph/badge.svg?token=kiZWzYgqEC)](https://codecov.io/gh/tecnico-softeng/es19al_04-project)


To run tests execute: mvn clean install

To see the coverage reports, go to <module name>/target/site/jacoco/index.html.

### Rastreabilidade do trabalho

Ordene a tabela por ordem crescente da data de término.
                 |                         |                                                                                                                 |                    |

|   Issue id | Owner (ist number)      | Owner (github username) | PRs id (with link)  |            Date    |  
| ---------- | ----------------------- | ----------------------- | ------------------- | ------------------ |
|    210     |          86495          |    quickacid            |        231          |    06-05-2019      |
|    #209    |          86408          |    Fogoid               |        [#234](https://github.com/tecnico-softeng/es19al_04-project/pull/234) | 07-05-2019 |    
|     213    |   73751                 |       jotapero          |        [#249](https://github.com/tecnico-softeng/es19al_04-project/commit/52cfba438f9e5b55ecab064a6d2be11a5ed55647)       |     10-05-2019     |
|    226     |          86495          |    quickacid            |        232          |    07-05-2019      |
|    208     |          86460          |    CallMeLeopoldo       |        233          |    07-05-2019      |
|    219     |          86495          |    quickacid            |        240          |    08-05-2019      |
|    211     |          86460          |    CallMeLeopoldo       |        236          |    08-05-2019      |
|    #227    |          86408          |    Fogoid               |        [#238](https://github.com/tecnico-softeng/es19al_04-project/pull/238) | 08-05-2019 |    
|    214     |          86495          |    quickacid            |        245          |    09-05-2019      |
|    225     |          86460          |    CallMeLeopoldo       |        235          |    09-05-2019      |
|    #217    |          86408          |    Fogoid               |        [#241](https://github.com/tecnico-softeng/es19al_04-project/pull/241) | 09-05-2019 |    
|    #215    |          86408          |    Fogoid               |        [#242](https://github.com/tecnico-softeng/es19al_04-project/pull/242) | 09-05-2019 |  
|    216     |          86460          |    CallMeLeopoldo       |        246          |    10-05-2019      |
|    #212    |          86408          |    Fogoid               |        [#248](https://github.com/tecnico-softeng/es19al_04-project/pull/248) | 10-05-2019 |  
|    #224    |          86408          |    Fogoid               |        [#252](https://github.com/tecnico-softeng/es19al_04-project/pull/252) | 10-05-2019 | 
|     222    |   73751                 |       jotapero          |        [#250](https://github.com/tecnico-softeng/es19al_04-project/commit/8bf32f366cf06cda5dbcf60926e5d6bfa2c1caac)       |     10-05-2019     |
|     223    |   73751                 |       jotapero          |        [#247](https://github.com/tecnico-softeng/es19al_04-project/commit/8292755b9798d60424672b88bd10220defa3dac1)       |     10-05-2019     |
|     229    |   73751                 |       jotapero          |        [#237](https://github.com/tecnico-softeng/es19al_04-project/pull/237)                                              |     08-05-2019     |

|            |                         |                         |                     |                    |
|            |                         |                         |                     |                    |
|            |                         |                         |                     |                    |
|            |                         |                         |                     |                    |



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
word
```

And create the 6 databases for the project as specified in
the `resources/fenix-framework.properties`.

To launch a server execute in the module's top directory: mvn clean spring-boot:run

To launch all servers execute in bin directory: startservers

To stop all servers execute: bin/shutdownservers

To run jmeter (nogui) execute in project's top directory: mvn -Pjmeter verify. Results are in target/jmeter/results/, open the .jtl file in jmeter, by associating the appropriate listeners to WorkBench and opening the results file in listener context
