/*
	Module: DevView

	Responsibilities: 
		- Display the dev information, like performance, 

	APIs:

	Structures: 

	Events: 
		- Consume: "click;[data-editable]"
*/
(function(){
	
	var perfInfoList = [];

	brite.registerView("DevView",{

		create: function(){
			return render("DevView");
		},

		postDisplay: function(){
			var view = this;
			view.$content = view.$el.find("section.content");
		}, 

		docEvents: {
			"NEW_PERF": function(event, perfInfo){
				var view = this;
				perfInfoList.unshift(perfInfo);
				refreshPerfList.call(view);
			}
		}

	});

	// --------- Private Methods --------- //
	function refreshPerfList(){
		var view = this;
		var perfInfo, i = 0, l = perfInfoList.length;
		var html = "";
		var sub; 

		for (; i < l; i++){
				perfInfo = $.extend({},perfInfoList[i]);
				fillPositions(perfInfo.perf.req);
				console.log("DevView perf draw",perfInfo);
				html += render("DevView-content",perfInfo);
		}
		view.$content.bEmpty().html(html);
	}
	// --------- /Private Methods --------- //

	// --------- Utilities --------- //
	function fillPositions(rootPerfNode){
			var ratio = 100 / rootPerfNode.duration ;
			var subs = rootPerfNode.subs;
			var sub;
			for (var prop in subs) {
					if (subs.hasOwnProperty(prop)) {
							sub = subs[prop];
							sub.position = {
								width: ratio * sub.duration,
								left: ratio * sub.start
							};
							if (sub.position.width < 10){
								sub.position.small = true;
							}
					}
			}
	}
	// --------- /Utilities --------- //
})();
