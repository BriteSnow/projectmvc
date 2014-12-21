var app = app || {};

(function(){
	
	// --------- Remote Das --------- //
	app.projectDao = brite.registerDao(new RemoteDaoHandler("Project"));
	app.ticketDao  = brite.registerDao(new RemoteDaoHandler("Ticket"));
	// --------- /Remote Das --------- //

	// --------- Mock Data (inMemoryDao) --------- //
	// InMemoryDao with mock data for simple client only development. 
	// Note: To make InMemoryDas to RemoteDas transition simpler, 
	//       make sure that both DasHandler implement the same interface with the same semantic

/*
	app.mock = {data:{}};

	app.mock.data.users = [{id:"001",username:"mike",name:"Mike Donavan",city:"San Francisco"},
	{id:"002",username:"jen",name:"Jennifer Aniston",city:"Los Angeles"}];
	
	
	app.mock.data.projects = [{id:"001",name:"Sofie Minecraft"},
	{id:"002",name:"Clash of clan"},
	{id:"003",name:"Angry Bird"},
	{id:"004",name:"Personal CRM"}];

	app.mock.data.tasks = [{id:"001",project_id:"001",subject:"Install minecraft client"},
	{id:"002",project_id:"001",subject:"Register minecraft"},
	{id:"003",project_id:"001",subject:"Create first world"},
	{id:"004",project_id:"001",subject:"Create roller coaster"}];

	// User entity: {id,username,name,city}
	app.userDao = brite.registerDao(new brite.InMemoryDaoHandler("User",app.mock.data.users));	

	// Project entity: {id,name}
	app.projectDao = brite.registerDao(new brite.InMemoryDaoHandler("Project",app.mock.data.projects));	

	// Task entity: {id,project_id,subject,content}
	app.taskDao = brite.registerDao(new brite.InMemoryDaoHandler("Task",app.mock.data.tasks));
*/	
	// --------- /Mock Data (inMemoryDao) --------- //

})();