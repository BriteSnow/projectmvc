(function(){
	
	brite.registerView("AdminView",{

		create: function(){
			return render("AdminView");
		}, 

		postDisplay: function(){
			var view = this;
			view.$content = view.$el.find("section.content:first");
			brite.display("PerfView",view.$content);
		}
		
	});

})();
