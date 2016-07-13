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
