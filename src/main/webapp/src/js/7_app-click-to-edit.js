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