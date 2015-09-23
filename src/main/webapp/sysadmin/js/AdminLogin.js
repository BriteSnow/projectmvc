(function(){
	
	brite.registerView("AdminLogin",{

		create: function(){
			return render("AdminLogin");
		},

		postDisplay: function(){
			var view = this;
			view.$txtAlert = view.$el.find(".txt-alert");
		}, 

		events: {

			"click; button.do-login": function(){
				doLogin.call(this);
			},

			"blur focus; input": function(event){
				// Note: for now, we need to do that since events get bound before postDisplay (might change in future brite.js)
				if (this.$txtAlert){
					// remove the alert message on input focus in/out
					this.$txtAlert.html("");
				}
			}, 

			"LOGGED_IN": function(event, extra){
				window.location.reload(true);
			}

		}
		
	});

	// --------- private methods --------- //
	function doLogin(){
		console.log("doLogin");
		var view = this; 
		var data = view.$el.find(".modal-body").dxPull();

		app.doPost("/login",data).done(function(result){
				view.$el.trigger("LOGGED_IN");
		}).fail(function(response){
			view.$txtAlert.html(response.errorCode);			
		});

	}
	// --------- /private methods --------- //
})();