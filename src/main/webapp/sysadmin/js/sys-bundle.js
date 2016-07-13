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
(function(){
	
	brite.registerView("AdminView",{

		create: function(){
			return render("AdminView");
		}, 

		postDisplay: function(){
			var view = this;
			view.$navHeader = view.$el.find("nav:first");
			view.$content = view.$el.find("section.content:first");
			brite.display("PerfView",view.$content);

			app.fetchUser();
		}, 

		events: { 
			"click; .do-logoff": function(){
				app.doGet("/logoff").done(function(){
					window.location.reload();
				});
			}
		},

		docEvents: {
			"USER_REFRESHED": function(event){
				var view = this;
				view.$navHeader.dxPush(app.user);
			}
		}		
	});

})();

(function(){
	
	brite.registerView("PerfView",{

		create: function(){
			return render("PerfView");
		}, 

		postDisplay: function(){
			var view = this;
			view.$tbodyMethods = view.$el.find(".tbody-methods");
			view.$tbodyRequests = view.$el.find(".tbody-requests");
			view.$javaInfo = view.$el.find(".java-info");
			view.$poolInfo = view.$el.find(".pool-info");

			view.$refresh = view.$el.find(".do-perf-refresh");

			refresh.call(view);
		}, 

		events: {
			"click; .do-perf-clear": function(event){
				var view = this;
				var $button = $(event.target);
				var html = $button.html();
				$button.html("...");
				
				$.post("/perf-clear").done(function(response){
					refresh.call(view);
					$button.html(html);
				});
			}, 

			"click; .do-perf-refresh": function(event){
				var view = this;
				refresh.call(view);
			}
		}
		
	});

	// --------- Private Methods --------- //
	function refresh(){
		var view = this;
		var html = view.$refresh.html();
		view.$refresh.html("..........");
		$.get("/perf-get-all").done(function(response){

			view.$tbodyMethods.html(render("PerfView-tbody",{perfs:response.result.methodsPerf}));
			view.$tbodyRequests.html(render("PerfView-tbody",{perfs:response.result.requestsPerf}));
			view.$javaInfo.html(render("PerfView-javaInfo",response.result.javaInfo));
			view.$poolInfo.html(render("PerfView-poolInfo",response.result.poolInfo));


			view.$refresh.html(html);
			
		});
	}
	// --------- /Private Methods --------- //

})();

//# sourceMappingURL=sys-bundle.js.map
