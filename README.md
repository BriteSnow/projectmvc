### Intro

The projectmvc-cloud project is a sample end-to-end application using modern and lightweight MVC patterns. 

### Pre-requisite

1. Java 8
2. maven
3. Postgres 9.3+ in localhost port 5432 (default dev configuration in the /WEB-INF/snow.properties)
4. node.js/npm/gulp, we are using maven to build, but we also call node.js from maven for all of the web files processing and other scripting needed (e.g., recreateDb from .sql file name conventions)
    - once node/npm is install, make sure to install gulp globally to have access to the "gulp" command
    ```$ npm install gulp -g``` 


##### Being deprecated

- /apps/jcruncherEx.jar (from http://jcruncher.org). We used to use jcruncher to compile handlebars and less, but now that we are integrating with node.js we are deprecating this way
    - old note: Make sure to download the 0.9.4 or above. This is to compile the handlebars (3.0.1) and lesscss (1.7.5) as part of the maven built or interactivly during development with```java -jar /apps/jcruncherEx.jar -i```. (in later versions, the goal is to make jcruncher a maven plugin so that we do not have this extra harcoded step).


### Dev Setup


1) Create a folder "projectmvc/" in your projects directory. 

2) Clone maven source 
From *projectmvc/* directory
```
$ git clone git@github.com:BriteSnow/projectmvc.git projectmvc_mvnsrc
```

*__BESTPRACTICE:__ The "_mvnsrc" suffix mean it is a maven src directory (typically checked in as it in git), and the parent folder is the project folder for other files such as design, data, output files)*

3) Then, from the the *projectmvc_mvnsrc* 
```
npm install
```
*__Note:__ This will install all of the modules in the "package.json" in this directory, enabling gulp to be called. 
Important, when adding modules to the *gulpfile.js* make sure to do it with the ``` .... --save``` to make sure it gets added to the *package.json* (such as the next developer can just do a "node install" to install missing modules)**

4) Create the database 
Now that node, gulp, and the node_modules are installed, we can run *gulp* to create the database following the convention. 
```
$ gulp recreateDb
```

*__Note:__ As we can see in the gulpfile.js file, the "recreateDb" task run a psql on postgres/postgres for the "00...sql" file (which will create the "pmvc_user" and "pmvc_db"), and then, run all of the subsequent sql files with the "pmvc_user" on "pmvc_db". This scheme will enable a simple way to do a incremental database update and keep production and development as close as possible.**

5) Initial build

Now that the dev database is created, we can build the application
```
$ mvn clean package
```

6) From same command line, run maven jetty
```
mvn jetty:run
```

7) When in development, automatically reprocess the web files when edit with 
```
gulp watch
```


8) Go to [http://localhost:8080/](http://localhost:8080/)

### Notes

Currently, the functionalities are very limited, but it shows how the whole server and client stack fit together, with some BriteSnow best practices. *settings* are still under development. 

