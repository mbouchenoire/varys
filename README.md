# Varys
[![Build Status](https://travis-ci.org/mbouchenoire/jsync.svg?branch=master)](https://travis-ci.org/mbouchenoire/varys)&nbsp;
[![Coverage Status](https://coveralls.io/repos/github/mbouchenoire/jsync/badge.svg?branch=master)](https://coveralls.io/github/mbouchenoire/varys?branch=master)

## Quickstart (temporary)
```console
root@localhost:~/git$ git clone https://github.com/mbouchenoire/varys.git
root@localhost:~/git$ cd varys
root@localhost:~/git/varys$ mvn clean package
root@localhost:~/git/varys$ vi src/main/resources/config.json  # see the Configuration section
root@localhost:~/git/varys$ java -cp org.varys.App target/varys-<version>.jar src/main/resources/config.json
```

## Configuration (temporary)
The configuration file is currently located here:
`src/main/resources/config.json`.

### Configuration values:

#### Common
- `git.parent_directory`: The directory in which you put all your git projects.
Varys will search for `.git` directories as deep as 3 levels below ;

#### Jenkins module (`modules[name=jenkins].config`)
- `jenkins_api.api_token`: You can obtain your Jenkins API token using
 [this procedure](https://stackoverflow.com/questions/45466090/how-to-get-the-api-token-for-jenkins) ;
- `notifications.period`: The time (in seconds) between each notification process ;
- `notifications.filters.local_branches_only`: `true` if you want to receive notifications only
 for builds regarding your local Git branches ;
- `notifications.filters.sucessful_builds`: `false` if you don't want to receive notifications
for successful builds.

#### GitLab module (`modules[name=gitlab].config`)
- `gitlab_api.version`: `4` since GitLab version 9.0, `3` before that ;
- `gitlab_api.private_token`: You can obtain your GitLab personal access token using [this procedure](https://docs.gitlab.com/ee/user/profile/personal_access_tokens.html)
- `notifications.period`: The time (in seconds) between each notification process ;