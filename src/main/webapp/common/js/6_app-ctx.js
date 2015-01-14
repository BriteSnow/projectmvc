var app = app || {};

/*
	A simple way to track, normalize, and propagate URL state changes so that Views can behave accordingly.

	Status Events: 
		- APP_CTX_CHANGE (ctx): To register $(document)on("APP_CTX_CHANGE",function(event,ctx){...});			
*/

(function(){
	
	var ctxInfo = {};

	// --------- Public API --------- //
	app.ctx = {
		
		// return the value in path index if present, otherwise, null
		pathAt: function(idx){
			return (ctxInfo.paths.length > idx)?ctxInfo.paths[idx]:null;
		},

		// return the number at this path index, if not numeric or null or out of bound return null;
		pathAsNum: function(idx){
			var num = app.ctx.pathAt(idx);
			return (num !== null && $.isNumeric(num))?(num * 1):null;
		},

		paths: function(){
			return ctxInfo.paths; // TODO: need to clone it
		}, 

		get: function(){
			return $.extend({},ctxInfo);
		},

		init: function(){
			ctxInfo = extractHashCtx();
			triggerCtxChange();
		}

	};
	// --------- /Public API --------- //	

	$(function(){
		$(window).on("hashchange",function(){
			ctxInfo = extractHashCtx();	
			triggerCtxChange();
		});
	});

	// --------- utilities --------- //
	function triggerCtxChange(){
		$(document).trigger("APP_CTX_CHANGE");
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

		return hashCtx;
	}
	// --------- /utilities --------- //

})();