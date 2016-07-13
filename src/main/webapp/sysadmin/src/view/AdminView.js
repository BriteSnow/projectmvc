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
