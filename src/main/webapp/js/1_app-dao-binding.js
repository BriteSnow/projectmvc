var app = app || {};

(function(){
	
	// --------- Remote Das --------- //
	app.projectDao = brite.registerDao(new RemoteDaoHandler("Project"));
	app.ticketDao  = brite.registerDao(new RemoteDaoHandler("Ticket"));
	// --------- /Remote Das --------- //
	
})();