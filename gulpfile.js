var hbsp = require("hbsp");
var vdev = require("vdev");
var path = require("path");
var concat = require('gulp-concat');
var gulp = require("gulp");
var del = require("del");
var fs = require("fs");
var sourcemaps = require('gulp-sourcemaps');


var ext_replace = require('gulp-ext-replace');

var hbsPrecompile = hbsp.precompile;

var appName = "projectmvc";

var webappDir = "src/main/webapp/";
var sqlDir = "src/main/webapp/WEB-INF/sql/";

// --------- postcss require --------- //
var postcss = require('gulp-postcss');
var cssImport = require('postcss-import'); // to allow mixin imports
var postcssMixins = require("postcss-mixins");
var postcssSimpleVars = require("postcss-simple-vars");
var postcssNested = require("postcss-nested");
var cssnext = require('postcss-cssnext');

var processors = [
	cssImport,
	postcssMixins,
	postcssSimpleVars,
	postcssNested,
	cssnext({ browsers: ['last 2 versions'] })
];
// --------- /postcss require --------- //

var jsDir = path.join(webappDir,"/js/");
var cssDir = path.join(webappDir,"/css/");

var jsSysDir = path.join(webappDir,"/sysadmin/js/");
var cssSysDir = path.join(webappDir,"/sysadmin/css/");

gulp.task('default',['clean','tmpl','pcss','common-bundle','app-bundle','sys-tmpl', 'sys-pcss', 'sys-app-bundle']);

// --------- Web Assets Processing --------- //
gulp.task('watch', ['default'], function(){

	// Watch the common-bundle
	gulp.watch(path.join(webappDir,"/src/js-lib/*.js"), ['common-bundle']);
	gulp.watch(path.join(webappDir,"/src/js-common/*.js"), ['common-bundle']);

	// for the main app
	gulp.watch(path.join(webappDir,"/src/js/*.js"), ['app-bundle']);
	gulp.watch(path.join(webappDir,"/src/pcss/*.pcss"), ['pcss']);
	gulp.watch(path.join(webappDir,"/src/view/*.tmpl"), ['tmpl']);
	gulp.watch(path.join(webappDir,"/src/view/*.pcss"), ['pcss']);
	gulp.watch(path.join(webappDir,"/src/view/*.js"), ['app-bundle']);

	// for the sys admin
	gulp.watch(path.join(webappDir,"/sysadmin/src/js/*.js"), ['sys-app-bundle']);
	gulp.watch(path.join(webappDir,"/sysadmin/src/pcss/*.pcss"), ['sys-pcss']);
	gulp.watch(path.join(webappDir,"/sysadmin/src/view/*.tmpl"), ['sys-tmpl']);
	gulp.watch(path.join(webappDir,"/sysadmin/src/view/*.pcss"), ['sys-pcss']);
	gulp.watch(path.join(webappDir,"/sysadmin/src/view/*.js"), ['sys-app-bundle']);




});


gulp.task('clean', function(){
	var dirs = [cssDir, cssSysDir, jsDir, jsSysDir];
	
	var dir;
	for (var i = 0; i < dirs.length ; i ++){
		dir = dirs[i];
		// make sure the directories exists (they might not in fresh clone)
		if (!fs.existsSync(dir)) {
			fs.mkdir(dir);
		}
		// delete the .css and .js files (this makes sure we do not )
		del.sync(dir + "*.css");
		del.sync(dir + "*.js");
		del.sync(dir + "*.map");
	}
});


gulp.task('pcss', function() {
	gulp.src([path.join(webappDir,"src/pcss/*.pcss"),path.join(webappDir,"src/view/*.pcss")])
		.pipe(sourcemaps.init())
		.pipe(postcss(processors))
		.pipe(concat('app-bundle.css'))
		.pipe(sourcemaps.write('./'))
		.pipe(gulp.dest(cssDir));
});


gulp.task('tmpl', function() {
	gulp.src(path.join(webappDir,"/src/view/*.tmpl"))
		.pipe(hbsPrecompile())
		.pipe(concat("templates.js"))
		.pipe(gulp.dest(path.join(webappDir,"js/")));
});

// common bundle is the javascripts libs and application common code that are used in both the app and the sysadmin
gulp.task('common-bundle', function() {
	gulp.src([path.join(webappDir,"/src/js-lib/*.js"), path.join(webappDir,"/src/js-common/*.js")])
		.pipe(sourcemaps.init())
		.pipe(concat("common-bundle.js"))
		.pipe(sourcemaps.write('./'))
		.pipe(gulp.dest(path.join(webappDir,"js/")));
});

// app-bundle.js is the application javascripts, which is the app javascripts + view controller javascripts. 
gulp.task('app-bundle', function() {
	gulp.src([path.join(webappDir,"/src/js/*.js"), path.join(webappDir,"/src/view/*.js")])
		.pipe(sourcemaps.init())
		.pipe(concat("app-bundle.js"))
		.pipe(sourcemaps.write('./'))
		.pipe(gulp.dest(path.join(webappDir,"js/")));
});


gulp.task('sys-pcss', function() {
	gulp.src([path.join(webappDir,"sysadmin/src/view/*.pcss")])
		.pipe(sourcemaps.init())
		.pipe(postcss(processors))
		.pipe(concat('sys-bundle.css'))
		.pipe(sourcemaps.write('./'))
		.pipe(gulp.dest(cssSysDir));
});

gulp.task('sys-tmpl', function() {
	gulp.src(path.join(webappDir,"/sysadmin/src/view/*.tmpl"))
		.pipe(hbsPrecompile())
		.pipe(concat("templates.js"))
		.pipe(gulp.dest(path.join(webappDir,"/sysadmin/js/")));
});

gulp.task('sys-app-bundle', function() {
	gulp.src([path.join(webappDir,"/sysadmin/src/view/*.js")])
		.pipe(sourcemaps.init())
		.pipe(concat("sys-bundle.js"))
		.pipe(sourcemaps.write('./'))
		.pipe(gulp.dest(path.join(webappDir,"/sysadmin/js/")));
});

// --------- /Web Assets Processing --------- //

gulp.task('recreateDb', function(){
	vdev.pg.psqlImport({user:"postgres", db:"postgres"}, vdev.pg.listSqlFiles(sqlDir,{to:0}));      
	vdev.pg.psqlImport({user: appName + "_user", db: appName + "_db"}, vdev.pg.listSqlFiles(sqlDir,{from:1}));
});
