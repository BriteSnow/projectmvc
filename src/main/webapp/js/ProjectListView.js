(function(){

	brite.registerView("ProjectListView",{

		create: function(){
			return app.projectDao.list({orderBy:"name"}).pipe(function(result){
				return render("ProjectListView",{projects:result});	
			});
		},

		postDisplay: function(){
			var view = this;
			view.$addNewProject = view.$el.find(".add-new-project");
			selectCurrentProject.call(this);
		}, 

		events: {

			"click; .add-new-project a": function(event){
				var view = this;
				view.$addNewProject.addClass("active");
				view.$addNewProject.find("input").focus();
			}, 

			"keyup; .add-new-project input": function(event){
				var view = this;
				var $input = $(event.target);
				var key = event.which;
				var newProject;
				// press enter
				if (key === 13){
					newProject = {name: $input.val()};
					$input.val("");
					app.projectDao.create(newProject).done(function(projectCreated){
						window.location = "#project/" + projectCreated.id;
						view.$addNewProject.removeClass("active");
						$input.val("");						
					});
				}
				// press esc
				else if (key === 27){
					view.$addNewProject.removeClass("active");
					$input.val("");
				}

			}
		},

		daoEvents: {
			"dataChange; Project": refreshLis
		},

		docEvents: {
			"APP_CTX_CHANGE": function(event){
				selectCurrentProject.call(this);
			}
		}

	});	

	// --------- Private Methods --------- //
	function refreshLis(){
		var view = this;
		app.projectDao.list({orderBy:"name"}).done(function(result){
			var html = render("ProjectListView-lis",{projects:result});	
			view.$el.find("ul").bEmpty().html(html);
			var projectId = app.ctx.get().projectId;
			selectCurrentProject.call(view);
		});
	}

	function selectCurrentProject(){
		var view = this;
		var projectId = (app.ctx.pathAt(0) === "project")?app.ctx.pathAsNum(1):null;
		if (projectId !== null){
			view.$el.find("li[data-entity='Project'].active").removeClass("active");
			var $li = view.$el.find("li[data-entity='Project'][data-entity-id='" + projectId + "']");
			$li.addClass("active");
		}
	}
	// --------- /Private Methods --------- //

})();