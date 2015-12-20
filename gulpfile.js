var hbsp = require("hbsp");
var vdev = require("vdev");
var path = require("path");
var concat = require('gulp-concat');
var gulp = require("gulp");
var del = require("del");
var fs = require("fs");

var ext_replace = require('gulp-ext-replace');

var hbsPrecompile = hbsp.precompile;

var dbPrefix = "pmvc";
var webappDir = "src/main/webapp/";
var sqlDir = "src/main/webapp/WEB-INF/sql/";

// --------- postcss require --------- //
var postcss = require('gulp-postcss');
var cssImport = require('postcss-import'); // to allow mixin imports
var autoprefixer = require('autoprefixer');
var postcssMixins = require("postcss-mixins");
var postcssSimpleVars = require("postcss-simple-vars");
var postcssNested = require("postcss-nested");
var cssnext = require('cssnext');

var processors = [
	cssImport,
	postcssMixins,
	postcssSimpleVars,
	postcssNested,
	cssnext,
	autoprefixer({ browsers: ['last 2 versions'] })
];
// --------- /postcss require --------- //

var cssDir = path.join(webappDir,"/css/");
var sysadminCssDir = path.join(webappDir,"/sysadmin/css/");

gulp.task('default',['clean','tmpl-_base','tmpl-sysadmin','pcss-_base','pcss-sysadmin']);

// --------- Web Assets Processing --------- //
gulp.task('watch', ['default'], function(){

	gulp.watch(path.join(webappDir,"/tmpl/",'*.tmpl'), ['tmpl-_base']);
	gulp.watch(path.join(webappDir,"/sysadmin/tmpl/",'*.tmpl'), ['tmpl-sysadmin']);

	gulp.watch(path.join(webappDir,"/pcss/",'*.pcss'), ['pcss-_base']);
	gulp.watch(path.join(webappDir,"/sysadmin/pcss/",'*.pcss'), ['pcss-sysadmin']);
	
});

gulp.task('clean', function(){
	var dirs = [cssDir, sysadminCssDir];

	
	var dir;
	for (var i = 0; i < dirs.length ; i ++){
		dir = dirs[i];
				// make sure the directories exists (they might not in fresh clone)
		if (!fs.existsSync(dir)) {
			fs.mkdir(dir);
		}
				// delete the .css files (this makes sure we do not )
		del.sync(dir + "*.css");
	}
});

gulp.task('tmpl-_base', function() {
		gulp.src(path.join(webappDir,"/tmpl/",'*.tmpl'))
				.pipe(hbsPrecompile())
				.pipe(concat("templates.js"))
				.pipe(gulp.dest(path.join(webappDir,"/js/")));
});

gulp.task('tmpl-sysadmin', function() {
		gulp.src(path.join(webappDir,"/sysadmin/tmpl/",'*.tmpl'))
			.pipe(hbsPrecompile())
			.pipe(concat("templates.js"))
			.pipe(gulp.dest(path.join(webappDir,"/sysadmin/js/")));
});

gulp.task('pcss-_base', function() {
		gulp.src(path.join(webappDir,"/pcss/",'*.pcss'))
			.pipe(postcss(processors))
			.pipe(ext_replace(".css"))
			.pipe(gulp.dest(cssDir));
});

gulp.task('pcss-sysadmin', function() {
		gulp.src(path.join(webappDir,"/sysadmin/pcss/",'*.pcss'))
			.pipe(postcss(processors))
			.pipe(ext_replace(".css"))
			.pipe(gulp.dest(sysadminCssDir));
});
// --------- /Web Assets Processing --------- //

gulp.task('recreateDb', function(){
		vdev.psql("postgres", null, "postgres", vdev.listSqlFiles(sqlDir,{to:0}));      
		vdev.psql(dbPrefix + "_user", null, dbPrefix + "_db", vdev.listSqlFiles(sqlDir,{from:1}));
});