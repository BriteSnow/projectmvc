<script type="text/javascript">
	$(function(){
		var $test = $("#test-das-div");
		var projectDao = brite.registerDao(new RemoteDaoHandler("Project"));

		function refresh(){
			projectDao.list().done(function(response){
				console.log("response",response);
				$test.empty();
				var p,i = 0,l = response.length;
				for (;i<l;i++){
					p = response[i];
					$test.append('<li data-entity="Project" data-entity-id="' + p.id + '" class="list-group-item">' + p.name +'</li>\n');
				}
			});
		}

		$test.on("click","[data-entity-id]",function(){
			var $entity = $(this);
			var projectId = $entity.attr("data-entity-id");
			projectDao.delete(projectId).done(function(){
				refresh();
			});

		});


		refresh();
	});
</script>

<ul id="test-das-div" class="list-group">
</ul>
