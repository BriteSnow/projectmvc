(function(){

	brite.registerView("ProjectView",{

		create: function(data){
			var view = this;
			view.project = data.project;
			return render("ProjectView",{project:view.project});
		},

		postDisplay: function(data){
			var view = this; 

			// build the list of tabNames
			// {tabName:$tabLi,...}
			view.tabs = {};
			view.$el.find("li[data-tab]").each(function(idx,tabLi){
				var $tabLi = $(tabLi);
				var tabName = $tabLi.attr("data-tab");
				view.tabs[tabName] = $tabLi;

			});

			view.$ticketsTabLi = view.$el.find(".tab-tickets");
			view.$settingsTabLi = view.$el.find(".tab-settings");
			view.$tabCtn = view.$el.find(".tabctn");


			refreshContent.call(view);
		}, 

		daoEvents: {

			"dataChange; Project": function(event){
				var view = this;
				var daoEvent = event.daoEvent;
				var project = daoEvent.result;
				if (project.id === view.project.id){
					view.project = project;
					view.$el.find("[data-prop='Project.name']").empty().html(project.name);
				}
			}, 

			"dataChange; Ticket": function(){
				var view = this;
				refreshContent.call(view);
			}
		}, 

		docEvents: {
			"APP_CTX_CHANGE": function(event){
				var view = this;
				refreshContent.call(this);

				// //console.log("APP_CTX_CHANGE",app.ctx,view.project.id);
				// if (app.ctx.paths.length === 2){
				// 	// if we have just 2 paths (i.e. project/123) then, just the project list. 
				// 	if (view.project.id === app.ctx.projectId){
				// 		refreshTabContent.call(view);
				// 	}					
				// }else if (app.ctx.pathAt(2) === "ticket"){
				// 	//view.$ticketsTabLi.after(render("ProjectView-ticket-tab",{projectId:view.project.id,ticketId:app.ctx.pathAt(3)}));
				// 	console.log("showing ticket " + app.ctx.pathAt(3));
				// }
			}
		}

	});


	// --------- Private Methods --------- //

	// refresh the tab lis with the appropriate one selected, and the tab content to match the selected tab0
	function refreshContent(){

		var view = this;

			

		// first, determine the type of tab we will show
		var tabType = "tickets"; // "tickets", "settings", "ticket"
		if (app.ctx.pathAt(2) === "settings") {
			tabType = "settings";
		}else if (app.ctx.pathAt(2) === "ticket"){
			tabType = "ticket";
		}

		// regardless of the tab, remove the "li.active", and empty the tab content
		view.$el.find("li.active").removeClass("active");
		view.$tabCtn.bEmpty();

		// render tickets
		if (tabType === "tickets"){
			brite.display("ProjectTickets", view.$tabCtn, {project:view.project});
			view.$ticketsTabLi.addClass("active");
		}

		// render the settings
		else if (tabType === "settings"){

			view.$settingsTabLi.addClass("active");
			brite.display("ProjectSettings",view.$tabCtn,view.project);
		}

		// render a specific ticket
		else if (tabType === "ticket"){
			// remove eventual ticket tab
			view.$el.find(".tab-ticket").remove();

			var ticketId = app.ctx.pathAt(3) * 1;
			// build the temporary ticket info
			var ticket = {
				id: ticketId
			};

			// create and activate the ticket tab
			var $ticketTabLi = $(render("ProjectView-ticket-tab-li",{projectId:view.project.id,ticketId:ticketId}));
			$ticketTabLi.addClass("active");
			view.$ticketsTabLi.after($ticketTabLi);

			// render the content 
			brite.display("ProjectTicket",view.$tabCtn.bEmpty(),{ticket:ticket});
		}

	}
	// --------- /Private Methods --------- //


})();