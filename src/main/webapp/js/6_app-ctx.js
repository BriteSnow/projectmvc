var app = app || {};

/*
	Application context module. 

	Scope: app.ctx.*

	- An application context (ctx) capture the state of the user interface 
		(what should be displayed to the user).

	- Other module can change the app contact with the app.ctx.push(props) {name:value,...}
		
	- This module also listen the DOM "hashchange" event to approprietaly change the 
		app context and trigger the APP_CTX_CHANGE event
	
	Module Structures: 

		- The app context object structure is as follow: 
			ctx = {
				projectId: "" // current project id
				taskId: "" // current task (optional)
				paths: []     // "#/project/123" >> ["project","123"]
			}

	Module API: 

		- app.ctx.push(props) 
			. props is a js object of name/value pair to be pushed to the application context			

	Module Events: 

		- APP_CTX_CHANGE (ctx): To register $(document)on("APP_CTX_CHANGE",function(event,ctx){...});			
*/

(function(){
	
	var ctx = {};

	// --------- Public API --------- //
	app.ctx = {
		
		// return the value in path index if present, otherwise, null
		pathAt: function(idx){
			return (ctx.paths.length > idx)?ctx.paths[idx]:null;
		},

		paths: function(){
			return ctx.paths;
		}, 

		get: function(){
			return $.extend({},ctx);
		}, 

		push: function(ctxProps){
			$.extend(ctx,ctxProps);
			triggerCtxChange();
		}, 

		init: function(baseCtx){
			var hashCtx = extractHashCtx();
			var newCtx = $.extend({},baseCtx,hashCtx);
			app.ctx.push(newCtx);			
		}, 

		showProject: function(projectId){
			window.location = "#project/" + projectId;
		}

	};
	// --------- /Public API --------- //	

	$(function(){
		$(window).on("hashchange",function(){
			
			var hashCtx = extractHashCtx();	
			app.ctx.push(hashCtx);
			// TODO: parse the hash URL and call the ctx.push
		});
	});



	// --------- utilities --------- //
	function triggerCtxChange(){
		$(document).trigger("APP_CTX_CHANGE",$.extend({},ctx));
	}

	function extractHashCtx(){
		var hash = window.location.hash;
		var hashCtx = {}; // partial ctx
		if (hash){
			hash = hash.substring(1);
			// TODO: need to add support for params
			var pathAndParam = hash.split("!"); 
			hashCtx.paths = pathAndParam[0].split("/");
		}else{
			hashCtx.paths = [];
		}
		// extract and normalize the info from the path
		if (hashCtx.paths.length > 1){
			var path0 = hashCtx.paths[0];
			// get the project info
			if (path0 === "project"){
				hashCtx.projectId = hashCtx.paths[1] * 1;
			}
		}

		return hashCtx;
	}
	// --------- /utilities --------- //

})();