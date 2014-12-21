### Intro

The projectmvc-cloud project is a sample end-to-end application using modern and lightweight MVC patterns. 

### Pre-requisite

1. Java 8
2. Postgres 9.3+ in localhost port 5432 (default dev configuration in the /WEB-INF/snow.properties)
3. /apps/jcruncherEx.jar (from http://jcruncher.org). This is to compile the handlebars and lesscss in realtime. The maven pom.xml calls it on build, and you can also start it from the command line for interactive compile, with ```java -jar /apps/jcruncherEx.jar -i``` from the pom.xml root folder. 

### Dev Setup

1) Git clone this repository

2) Login as *postgres* in your postgres db, and run the two lines in *src/main/webapp/WEB-INF/sql/00_create-db.sql*
```
psql -U postgres
```
In psql command line:

```sql
CREATE USER pmvc_user PASSWORD 'welcome';
CREATE DATABASE pmvc_db owner pmvc_user ENCODING = 'UTF-8';
```

3) Exit psql, login as *pmvc_user* to *pmvc_db* and copy paste the *src/main/webapp/WEB-INF/sql/01_create-tables.sql*
```
psql -U pmvc_user pmvc_db
\i src/main/webapp/WEB-INF/sql/01_create-tables.sql
```

4) Download *jcrunderEx.jar* from http://jcruncher.org and put it in the */apps/* directory (on windows that would be C:/apps/)
Note: we are working on making this a maven plugin to remove this manual step and harcoded pom.xml reference.  


5) From command line (from the pom.xml folder) build
```
mvn clean package
```

6) From same command line, run maven jetty
```
mvn jetty:run
```

7) Go to [http://localhost:8080/](http://localhost:8080/)

### Notes

Currently, the functionalities are very limited, and the *teams* and *settings* are still under development. 

