(function(){

	brite.registerView("MainView",{parent:"body"}, {

		create: function(){
			return render("MainView");
		}, 

		init: function(){
			var view = this;
			// ADVANCED FEATURE: if not sure, you can add this brite.display in the postDisplay. 
			// Here, we display the leftPanel before the Main view is displayed. We return the whenInit, because we want 
			// to wait until the ProjectListView is fully initialized, but not wait until it is considered postDisplay
			return brite.display("ProjectListView",view.$el.find(".MainView-left")).whenInit;
		},

		postDisplay: function(){
			var view = this;
			view.$contentPanel = view.$el.find(".MainView-content");
			view.$navHeader = view.$el.find("header:first");
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
			}, 

			"APP_CTX_CHANGE": function(event){
				var view = this;
				if ("project" === app.ctx.pathAt(0) && $.isNumeric(app.ctx.pathAt(1))){
					var newProjectId = app.ctx.pathAt(1) * 1;

					if (newProjectId !== view.currentProjectId){
						app.projectDao.get(newProjectId).done(function(project){
							// call the brite.js bEmpty jQuery extension to make sure to 
							// destroy eventual brite.js sub views
							view.$contentPanel.bEmpty();
							// display the projectt
							brite.display("ProjectView",view.$contentPanel,{project:project});

							view.currentProjectId = newProjectId;
						});						
					}
				}
			}

		}

	});

})();
