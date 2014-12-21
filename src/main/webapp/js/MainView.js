(function(){

	brite.registerView("MainView",{parent:"body"}, {

		create: function(){
			return render("MainView");
		}, 

		init: function(){
			var view = this;
			// display the list view
			return brite.display("ProjectListView",view.$el.find(".MainView-leftPanel")).whenInit;
		},

		postDisplay: function(){
			var view = this;
			view.$contentPanel = view.$el.find(".MainView-contentPanel");
		}, 

		docEvents: {

			"APP_CTX_CHANGE": function(event,ctx){
				var view = this;
				if (view.projectId !== ctx.projectId){
					view.projectId = ctx.projectId;
					
					app.projectDao.get(view.projectId).done(function(project){
						// call the brite.js bEmpty jQuery extension to make sure to 
						// destroy eventual brite.js sub views
						view.$contentPanel.bEmpty();
						// display the projectt
						brite.display("ProjectView",view.$el.find(".MainView-contentPanel"),{project:project});
					});
				}
			}

		}

	});

})();
