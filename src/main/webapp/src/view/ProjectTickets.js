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