var app = app || {};

(function(){
	
	// --------- Remote Das --------- //
	app.projectDao = brite.registerDao(new RemoteDaoHandler("Project"));
	app.ticketDao  = brite.registerDao(new RemoteDaoHandler("Ticket"));
	// --------- /Remote Das --------- //
	
})();
var app = app || {};
/*
	Module: PROP-COMMIT
	Scope: app.domsync.*

	Responsibilities: 
		- Behavior make any data-prop element editable data-prop
		- TODO: provide API to put data into an DOM tree based on the data-prop pattern

	APIs:
		- None (for now), use and trigger DOM Events

	Structures: 
		- propInfo: {type:"",name:""} (ex {type:"Project",name:"name"})

  Events: 
    - Consume: "click;[data-editable]"
    - Trigger: "PROP_EDIT_COMMIT",propInfo when user press enter when editing
    - Trigger: "PROP_EDIT_CANCEL",propInfo if pressed esc
*/
(function(){
		app.domsync = {};
		var $document = $(document);

		$(function(){
			// manage the click to edit logic
			$document.on("click","[data-editable]",function(event){
				var $target  = $(event.target);
				var origVal  = $target.text();
				var origHtml = $target.html();
				var $input = $("<input type='text' class='form-control' />");
				$input.val(origVal);
				$target.empty().append($input);
				$input.select().focus();

				$input.on("keyup",function(event){
					// press enter
					if (event.which === 13){
						commit();
					}
					// press esc
					else if (event.which=== 27){
						cancel();
					}
				});

				// on loose focus, we cancel
				$input.on("blur",function(){
					cancel();
				});


				function commit(){
					var propInfo = getPropInfo($target);
					propInfo.value = $input.val();
					$target.trigger("PROP_EDIT_COMMIT",propInfo);					
				}

				function cancel(){
					$target.empty();
					$target.html(origHtml);
					$target.trigger("PROP_EDIT_CANCEL");					
				}
			});
		});

		// return {type:"",name:""}
		// ex: data-prop="Project.name" >> {type="Project",name:"name"}
		function getPropInfo($prop){
			var dataPropStr = $prop.attr("data-prop");
			var typeAndName = dataPropStr.split(".");
			return {type:typeAndName[0],name:typeAndName[1]};
		}

})();

/*
	Module: COMMIT-TO-DAO

	Reponsibilities: 
		- Listen to the PROP-COMMIT event and call the appropriate DAO for an update

	Structures:
		- PROP-COMMIT.propInfo 
		- britejs.dao entityInfo

	Events:
		- Listen: PROP_EDIT_COMMIT
*/
(function(){
	var $document = $(document);
		// get the PROP_EDIT_COMMIT and change the 
	$document.on("PROP_EDIT_COMMIT",function(event,propInfo){
		var $target = $(event.target);

		var entityInfo = $target.bEntity(propInfo.type);
		if (entityInfo){
			var vals = {};
			vals[propInfo.name] = propInfo.value; 
			brite.dao(entityInfo.type).update(entityInfo.id,vals);
		}else{
			console.log("WARNING: no parent " + propInfo.type + " element for", $target);
		}
	});	
})();
(function(){
})();

// start the application
$(function(){
	brite.display("MainView").done(function(){
		app.ctx.init();
	});
});

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
					// take orgId from 
					var orgId = app.user.orgId;

					newProject = {name: $input.val(),
												orgId: app.user.orgId};

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
(function(){
	
	brite.registerView("ProjectSettings",{

		create: function(data){
			var view = this;
			view.project = data;
			return render("ProjectSettings",{project:view.project});
		},
		
		events: {

			// --------- add team member --------- //
			// display on the 
			"click; .add-member": function(event){
				var view = this;
				var $add = $(event.currentTarget);
				var $addMemberCtn = $add.closest(".add-member-ctn");
				var oldHtml = $addMemberCtn.html();
				$addMemberCtn.data("oldHtml",oldHtml);
				$addMemberCtn.empty();
				var $input = $(render("ProjectSettings-add-member-form")).appendTo($addMemberCtn);
				$input.focus();
			},
			// cancel or do on key press
			"keyup; .add-member-ctn": function(event){
				if (event.which == 27){
					cancelAddMember($(event.currentTarget));	
				}else if (event.which == 13){
					doAddMember($(event.currentTarget));	
				}
				
			}, 

			// --------- /add team member --------- //

		}
	});

	function doAddMember($addMemberCtn){

	}

	function cancelAddMember($addMemberCtn){
		var oldHtml = $addMemberCtn.data("oldHtml");
		$addMemberCtn.empty().html(oldHtml);
	}
})();

(function(){
	
	brite.registerView("ProjectTicket",{

		create: function(data){
			var view = this;
			view.ticket = data.ticket;
			return render("ProjectTicket",view.ticket);
		}, 

		postDisplay: function(){
			var view = this;
			app.ticketDao.get(view.ticket.id).done(function(ticket){
				view.$el.dxPush(ticket);
			});
		}
		
	});

})();
(function(){
	
	brite.registerView("ProjectTickets",{

		create: function(data){
			var view = this;
			view.project = data.project;
			return render("ProjectTickets",view.project);
		}, 


		postDisplay: function(){
			var view = this;
			view.$tbody = view.$el.find("tbody");
			refreshContent.call(view);

		},

		events: {

			"keyup; input.newTicket": function(event){
				var view = this;
				var $input = $(event.target);
				var key = event.which; 
				// press enter
				if (key === 13){
					var newTicket = {};
					newTicket.title = $input.val();
					newTicket.projectId = view.project.id;
					brite.dao("Ticket").create(newTicket).done(function(result){
						// console.log("new ticket created",result);
					});
				}
				// press esc
				else if (key === 27){
					$input.val("");
				}
			}

		} // /events
	});

	// --------- Private Methods --------- //
	function refreshContent(){
		var view = this;
		app.ticketDao.list({filter:{projectId:view.project.id}}).done(function(result){
			var html = render("ProjectTickets-tbody",{id:view.project.id,tickets:result});			
			view.$tbody.bEmpty().html(html);
			view.$el.find("input.newTicket").focus();
		});
	}
	// --------- /Private Methods --------- //	

})();
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

				// we update this view only if it is the same project.
				if (app.ctx.pathAt(0) === "project" && app.ctx.pathAsNum(1) === view.project.id){
					refreshContent.call(this);
				}
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
//# sourceMappingURL=app-bundle.js.map
