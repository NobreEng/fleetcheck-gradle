# FleetCheck – Gradle

Projeto do Worksheet 4 (Build Systems). Esta versão usa **Gradle**, com o mesmo código Java, recursos e testes da versão Maven.

Para compilar, testar e gerar tudo (no Windows):

```
gradlew.bat clean build
java -jar build/libs/fleetcheck-1.0.0.jar
gradlew.bat cyclonedxBom
```

Resultado esperado:

```
FleetCheck 1.0
Vehicles loaded: 4
Vehicles requiring service: 2
Average mileage: 37000 km
```

Execução verde no GitHub Actions (Evidence 8.5):
https://github.com/NobreEng/fleetcheck-gradle/actions/runs/37218453194

---

## Evidências e respostas

### 8.1 – Build sem o Jackson (Evidence 8.1)

O build falhou, como esperado. Uma das linhas de erro:

```
App.java:3: error: package com.fasterxml.jackson.core.type does not exist
```

A dependência que falta é `com.fasterxml.jackson.core:jackson-databind`.

### 8.2 – Dependências e comparação com o Maven (Evidence 8.2)

Com `gradle dependencies --configuration runtimeClasspath`:

- **Direta:** `jackson-databind:2.22.2` (a que eu escrevi no `build.gradle`).
- **Transitivas:** `jackson-core:2.22.2` e `jackson-annotations:2.22`, que vêm por causa do databind.

Comparando com o `mvn dependency:tree`, as dependências da aplicação são as mesmas. A única diferença visível é que o Gradle também mostra o `jackson-bom`, um ficheiro que o Jackson usa para manter as versões dos seus módulos alinhadas. Ou seja, mudar de build system não mudou as dependências da aplicação, só a forma de as declarar e de as mostrar.

### 8.3 – O JAR (Evidence 8.3)

O JAR normal só tinha as classes do meu projeto e não arrancava. Depois de configurar o bloco `jar { ... }`, o JAR passou a:

- ter a `Main-Class` definida no manifest;
- levar lá dentro o conteúdo de todas as dependências de runtime (o Jackson).

Ficou uma aplicação completa, igual ao que o Shade faz no Maven.

### 8.4 – O que o Gradle Wrapper removeu

Removeu a suposição de que o Gradle está instalado na máquina, e na versão certa. O `gradlew` descarrega a versão fixada em `gradle/wrapper/gradle-wrapper.properties` (aqui a 9.6.0), por isso o build é igual em qualquer sítio, incluindo no GitHub Actions.

### 8.6 – Porque é que o SBOM tem dependências que eu não escrevi? (Evidence 8.6)

O SBOM (`build/reports/cyclonedx/bom.json`) é feito a partir de todas as dependências que o Gradle realmente resolve, não só das que eu escrevi. No `build.gradle` só declarei o `jackson-databind`, mas o ficheiro também tem o `jackson-core`, o `jackson-annotations` e o `jackson-bom`, porque são dependências transitivas.

### 8.7 – Maven vs Gradle

| Tarefa | Maven | Gradle |
|---|---|---|
| Configuração do build | `pom.xml` | `build.gradle` |
| Build completo | `mvnw.cmd clean verify` | `gradlew.bat clean build` |
| Adicionar dependência | `<dependency>...</dependency>` | `implementation 'grupo:artefacto:versão'` |
| Ver dependências | `mvn dependency:tree` | `gradle dependencies` |
| Wrapper | `mvnw.cmd` | `gradlew.bat` |
| Pasta de saída | `target/` | `build/` |
| Localização do JAR | `target/` | `build/libs/` |
| SBOM | plugin CycloneDX para Maven | plugin CycloneDX para Gradle |

### Pergunta final: mudou o software ou o processo de build?

Mudou o processo de build. O código-fonte é o mesmo e o resultado da aplicação também. O que mudou foi a forma de configurar, compilar, testar, empacotar e gerar o SBOM. O build system é a ferramenta que está à volta do software, não faz parte dele.

## Notas

- Com o Gradle 9 foi preciso acrescentar `testRuntimeOnly 'org.junit.platform:junit-platform-launcher'`, senão os testes não arrancavam ("Failed to load JUnit Platform").
- O teste usado é o mesmo da versão Maven (`FleetServiceTest`). A correção do defeito (`>` para `>=` no `FleetService`) também já está feita.
