# Building JAM

## Product to install

 - Download and install node.js 4.x.x+ https://nodejs.org/en/download/
 - Install npm 3.x.x+

```sh
$ npm install -g npm@3
```

## Normal full build via command line

You must do at least once "mvn clean install"
"clean" phase allows jam-jsclient to prepare all the nodejs dependencies. It is specified in "clean" phase because
you do not need to run it often and it takes long time to download all "node_modules"

Once you have done at least one clean install, thereafter can just use: mvn install

## Development process

Assuming you have done "mvn clean install" at least once the steps are:

For standalone Tomcat build:

1. navigate to "yes-cart/manager/jam-jsclient/src/main/typescript" and run "npm run build.dev"
   - this creates a build in dist/dev
2. navigate to "yes-cart/manager/jam" and run "mvn install"
   - this builds the jam war
   - it also includes "jam-jsclient/src/main/typescript/node_modules" and "jam-jsclient/src/main/typescript/dist/dev"

(A far better way) Using Intellij IDEA embeded Tomcat (assuming you have created jam webapp configurations):

1. navigate to "yes-cart/manager/jam-jsclient/src/main/typescript" and run "npm start"
   - this creates a build in dist/dev
   - also it listens to changes you make in "jam-jsclient/src/main/typescript/src" and automatically compiles it into "dis/dev"
2. Start JAM webapp from Intellij IDEA
   - This also includes the "node_modules" and "dist/dev" as part of mvn build
3. Make changes to JS files
   - npm will auto compile and update "dist/dev"
4. navigate to "yes-cart/manager/jam" and run "mvn validate -Pnodejs" when you are ready to preview changes
   - this is a special profile task to copy "dist/dev" to "target/cp"
NOTE: step 4 copies th files, however because all files are plain JS sometimes browser caches them and does not
      update. In this case you need to navigate to /cp/client/index.html to refresh the app.


## Well know problems

 - Under Windows platform maven exec plugin may not run npm. Is this case you may get  
 "CreateProcess error=193, %1 is not a valid Win32 application" error message
 Solution locate and copy npm.cmd to npm.bat and delete npm shell script (without extension). 
 
 
## Base dir property

 - located in typescript/tools/config/seed.config.ts
 
## Updating npm-shrinkwrap.json

 If you need to update dependencies this is the recommended way:

```sh
# backup & remove
cp npm-shrinkwrap.json npm-shrinkwrap.json.bkp
rm npm-shrinkwrap.json
# remove downloaded dependencies that you need to update (e.g. deep-extend)
rm -rf node_modules/deep-extend
# Re-run install
npm install
# redo shrinkwrap
npm shrinkwrap
```

 After re-creating npm-shrinkwrap.json compare it to npm-shrinkwrap.json.bkp to ensure the changes are valid as multiple dependencies may have updated
 
 
## Hints

 - indexing of typescript/node_modules and typescript/dist make take some time, so better to mark this folders as excluded
 - mvn -Pmysql,dev tomcat:run-war
 
 
 
