# Varys
[![Build Status](https://travis-ci.org/mbouchenoire/varys.svg?branch=master)](https://travis-ci.org/mbouchenoire/varys)&nbsp;
[![Mainainability](https://sonarcloud.io/api/project_badges/measure?project=mbouchenoire_varys&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=mbouchenoire_varys)&nbsp;
[![Reliability](https://sonarcloud.io/api/project_badges/measure?project=mbouchenoire_varys&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=mbouchenoire_varys)&nbsp;
[![Security](https://sonarcloud.io/api/project_badges/measure?project=mbouchenoire_varys&metric=security_rating)](https://sonarcloud.io/dashboard?id=mbouchenoire_varys)&nbsp;
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=mbouchenoire_varys&metric=coverage)](https://sonarcloud.io/dashboard?id=mbouchenoire_varys)


**Varys** is a local process that crawls trough your dev environment (GitLab, Jenkins...) giving you instant feedback
(via desktop notifications) on things that you care about. This (currently) includes:
- new merge requests assigned to you,
- updates on those merge requests:
  - status change (merged, closed, wip...),
  - assignee change,
  - new comments / commits,
  - conflicts (new / resolved),
- reminders for pending merge requests
- failed / unstable builds (of your local git branches),
- services downtimes.

## Build & Run from sources

### Linux
```console
user:~$ git clone https://github.com/mbouchenoire/varys.git && cd varys
user:~/varys$ mvnw clean package
user:~/varys$ bin/varys-start.sh config/config.yml # See the Configuration section
```

### Windows
```console
C:\Users\user\git> git clone https://github.com/mbouchenoire/varys.git && cd varys
C:\Users\user\git\varys> mvnw clean package
C:\Users\user\git\varys> bin\varys-start.bat config\config.yml # See the Configuration section
```

## Configuration
The configuration file is currently located here:
`config/config.yml`.
