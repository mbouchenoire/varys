# Varys
[![Build Status](https://travis-ci.org/mbouchenoire/varys.svg?branch=master)](https://travis-ci.org/mbouchenoire/varys)&nbsp;
[![Mainainability](https://sonarcloud.io/api/project_badges/measure?project=mbouchenoire_varys&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=mbouchenoire_varys)&nbsp;
[![Coverage Status](https://coveralls.io/repos/github/mbouchenoire/varys/badge.svg)](https://coveralls.io/github/mbouchenoire/varys)

**Varys** is a local process that crawls trough your dev environment (GitLab, Jenkins...) giving you instant feedback
(via desktop notifications) on things that you care about. This (currently) includes:
- new merge requests (assigned to you, other people, or both),
- updates on those merge requests:
  - status change (merged, closed...),
  - assignee change,
  - new comments,
  - new commits.
- pending merge requests,
- remote build statuses (of your local git branches),
- services downtimes.

## Build & Run from sources

### Windows
```console
C:\Users\user\git> git clone https://github.com/mbouchenoire/varys.git && cd varys
C:\Users\user\git\varys> mvnw clean package
C:\Users\user\git\varys> bin\varys-start.bat config\config.yml # See the Configuration section
```

## Configuration
The configuration file is currently located here:
`config/config.yml`.