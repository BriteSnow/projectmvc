### Intro

The projectmvc-cloud project is a sample end-to-end application using modern and lightweight MVC patterns. 

### Pre-requisite

1. Install Java 8

2. Install maven

3. Postgres 9.3+ in localhost port 5432 (default dev configuration in the /WEB-INF/snow.properties)

4. Install node.js/npm ([node.js install/download](https://nodejs.org/en/download/))
    - On mac, you can [intall node with brew](http://blog.teamtreehouse.com/install-node-js-npm-mac)
    - __IMPORTANT:__ Make sure you do not have a folder ```~/node_modules/``` otherwise local packages will be installed there which is not what we want. If you have such folder, delete it (then, local modules will be install per project folder).

5. Upgrade npm with: 

    ```
    npm -v
    sudo npm install npm -g
    ```

6. Upgrade node with (this way might not work on windows, google the correct way to update node to the latest): 

    ```
    # Clear NPM's cache:
    sudo npm cache clean -f 
    # Install a little helper called 'n'
    sudo npm install -g n   
    # Update node to the latest stable
    sudo n stable
    ```

7. Install gulp. 

    ```
    npm install gulp -g
    ```

    - Make sure to install gulp globally first (with -g) to have access to the "gulp" command.

    - _We are using maven to build, but we also call node.js from maven for all of the web files processing and other scripting needed (e.g., recreateDb from .sql file name conventions)_

8. Check node and npm version 

    ```
    npm -v
    ```
    Should be above 2.14.1
    ```

    node -v
    ```
    Should be above v0.12.7

##### Being deprecated

    - /apps/jcruncherEx.jar (from http://jcruncher.org). We used to use jcruncher to compile handlebars and less, but now that we are embracing node.js into our maven build process for more power and flexibility.

    - old note: Make sure to download the 0.9.4 or above. This is to compile the handlebars (3.0.1) and lesscss (1.7.5) as part of the maven built or interactivly during development with ```java -jar /apps/jcruncherEx.jar -i```. (in later versions, the goal is to make jcruncher a maven plugin so that we do not have this extra harcoded step).


### Dev Setup

Once java 8, postgresql, maven, git, npm/node, and gulp are installed do the following steps: 

1. Create a folder "projectmvc/" in your projects directory. 

2. Clone maven source 

    ```
    git clone git@github.com:BriteSnow/projectmvc.git projectmvc_mvnsrc
    ```

   - __BEST PRACTICE:__ The "_mvnsrc" suffix means it is a maven src directory (typically checked in as it in git), and the parent folder is the project folder for other files such as design, data, output, other project related files that does not need to be checked in)

3. Then, from the the *projectmvc_mvnsrc* install the node_modules for this project.

    ```
    npm install
    ```

    - __Note:__ This will install all of the modules in the "package.json" in the *projectmvc_mvnsrc/node_modules* directory, enabling gulp to be called.

    - __Important:__ Important when adding modules to the *gulpfile.js* make sure to do it with the ```npm install .... --save``` to make sure it gets added to the *package.json* (such as the next developer can just do a "node install" to install missing modules)

4. Create the database 

  Now that node, gulp, and the node_modules are installed, we can run *gulp* to create the database following the convention. 

    ```
    $ gulp recreateDb
    ```

  - __Note:__ As we can see in the gulpfile.js file, the "recreateDb" task run a psql on postgres/postgres for the "00...sql" file (which will create the "pmvc_user" and "pmvc_db"), and then, run all of the subsequent sql files with the "pmvc_user" on "pmvc_db". This scheme will enable a simple way to do a incremental database update and keep production and development as close as possible.

5. Initial build

  Now that the dev database is created, we can build the application

    ```
    $ mvn clean package
    ```

6. From same command line, run maven jetty

    ```
    mvn jetty:run
    ```

7. When in development, automatically reprocess the web files when edit with 

    ```
    gulp watch
    ```

Usually, you have one terminal running ```mvn jetty:run``` and another one (typically another terminal tab on mac) with ```hulp watch```

8. Go to [http://localhost:8080/](http://localhost:8080/)

### Notes

Currently, the functionalities are very limited, but it shows how the whole server and client stack fit together, with some BriteSnow best practices. *settings* are still under development. 

