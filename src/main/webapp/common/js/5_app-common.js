var app = app || {};

// --------- AJAX Wrapper --------- //
// Very simple AJAX wrapper that allow us to simply normalize request/response, and eventually put some hooks such as
// performance and error reporting. 
(function(){
	app.doGet = function(path, data){
		return ajax(false,path,data);
	};

	app.doPost = function(path, data){
		return ajax(true,path,data);
	};

	function ajax(isPost, path, data){
		var dfd = $.Deferred();
		var method = (isPost)?"POST":"GET";
		var jqXHR = $.ajax({
			url: path, 
			type: method,
			dataType: "json",
			data: data
		});

		jqXHR.done(function(response){
			// TODO: need test reponse.result
			if (response.success){
				dfd.resolve(response.result);
			}else{
				dfd.reject(response);
			}
		});

		jqXHR.fail(function(jqx, textStatus, error){
			dfd.reject(error);
			// TODO: need to normalize error
		});

		return dfd.promise();
	}
})();
// --------- /AJAX Wrapper --------- //

// --------- User get API --------- //
// A simple API that get/refresh the user data and put it in app.user and trigger
// a document "USER_REFRESHED"
(function(){
	app.user = null;

	app.fetchUser = function(){
		// get the authenticated user
		app.doGet("/auth/get-user").done(function(user){
			app.user = user;
			$(document).trigger("USER_REFRESHED");
			
		});
	};
	
})();
// --------- /User get API --------- //

