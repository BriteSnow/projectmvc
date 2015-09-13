var hbsp = require("hbsp");
var vdev = require("vdev");
var path = require("path");
var concat = require('gulp-concat');
var gulp = require("gulp");
var less = require("gulp-less");
var del = require("del");
var fs = require("fs");

var hbsPrecompile = hbsp.precompile;

var dbPrefix = "pmvc";
var webappDir = "src/main/webapp/";
var sqlDir = "src/main/webapp/WEB-INF/sql/";

var cssDir = path.join(webappDir,"/css/");
var adminCssDir = path.join(webappDir,"/admin/css/");

gulp.task('default',['clean','tmpl-_base','tmpl-admin','less-_base','less-admin']);

// --------- Web Assets Processing --------- //
gulp.task('watch', ['default'], function(){

	gulp.watch(path.join(webappDir,"/tmpl/",'*.tmpl'), ['tmpl-_base']);
	gulp.watch(path.join(webappDir,"/admin/tmpl/",'*.tmpl'), ['tmpl-admin']);

	gulp.watch(path.join(webappDir,"/less/",'*.less'), ['less-_base']);
	gulp.watch(path.join(webappDir,"/admin/less/",'*.less'), ['less-admin']);
	
});

gulp.task('clean', function(){
	var dirs = [cssDir, adminCssDir];

	
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

gulp.task('tmpl-admin', function() {
    gulp.src(path.join(webappDir,"/admin/tmpl/",'*.tmpl'))
        .pipe(hbsPrecompile())
        .pipe(concat("templates.js"))
        .pipe(gulp.dest(path.join(webappDir,"/admin/js/")));
});

gulp.task('less-_base', function() {
    gulp.src(path.join(webappDir,"/less/",'*.less'))
        .pipe(less())
        .pipe(gulp.dest(cssDir));
});

gulp.task('less-admin', function() {
    gulp.src(path.join(webappDir,"/admin/less/",'*.less'))
        .pipe(less())
        .pipe(gulp.dest(adminCssDir));
});
// --------- /Web Assets Processing --------- //

gulp.task('recreateDb', function(){
    vdev.psql("postgres", null, "postgres", vdev.listSqlFiles(sqlDir,{to:0}));      
    vdev.psql(dbPrefix + "_user", null, dbPrefix + "_db", vdev.listSqlFiles(sqlDir,{from:1}));
});