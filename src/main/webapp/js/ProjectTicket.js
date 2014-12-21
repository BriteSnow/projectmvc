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