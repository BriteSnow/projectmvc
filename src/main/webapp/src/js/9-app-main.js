(function(){
})();

// start the application
$(function(){
	brite.display("MainView").done(function(){
		app.ctx.init();
	});
});
