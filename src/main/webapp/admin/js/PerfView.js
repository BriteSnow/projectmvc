(function(){
	
	brite.registerView("PerfView",{

		create: function(){
			return render("PerfView");
		}, 

		postDisplay: function(){
			var view = this;
			view.$tbodyMethods = view.$el.find(".tbody-methods");
			view.$tbodyRequests = view.$el.find(".tbody-requests");
			view.$javaInfo = view.$el.find(".java-info");
			view.$poolInfo = view.$el.find(".pool-info");

			view.$refresh = view.$el.find(".do-perf-refresh");

			refresh.call(view);
		}, 

		events: {
			"click; .do-perf-clear": function(event){
				var view = this;
				var $button = $(event.target);
				var html = $button.html();
				$button.html("...");
				
				$.post("/perf-clear").done(function(response){
					refresh.call(view);
					$button.html(html);
				});
			}, 

			"click; .do-perf-refresh": function(event){
				var view = this;
				refresh.call(view);
			}
		}
		
	});

	// --------- Private Methods --------- //
	function refresh(){
		var view = this;
		var html = view.$refresh.html();
		view.$refresh.html("..........");
		$.get("/perf-get-all").done(function(response){

			view.$tbodyMethods.html(render("PerfView-tbody",{perfs:response.result.methodsPerf}));
			view.$tbodyRequests.html(render("PerfView-tbody",{perfs:response.result.requestsPerf}));
			view.$javaInfo.html(render("PerfView-javaInfo",response.result.javaInfo));
			view.$poolInfo.html(render("PerfView-poolInfo",response.result.poolInfo));


			view.$refresh.html(html);
			
		});
	}
	// --------- /Private Methods --------- //

})();
