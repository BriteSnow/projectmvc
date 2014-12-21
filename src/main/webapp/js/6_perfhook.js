/*
	Module: PerfHook

	Responsibilities: 
		- Monitor the ajax calles and trigger NEW_PERF event 

	Structures: 
		- perfInfo: {url,params,perf} (where perf is the perf structure form the server)

  Events: 
    - Consume: RemoteDaoHandler_ajax_done
    - Tigger: NEW_PERF,perfInfo
*/
// --------- Perf Hook --------- //
$(function(){
	var $document = $(document);

	// extra: {url:.,params:.,reponseData:.}
	$document.on("RemoteDaoHandler_ajax_done",function(event,extra){
		var perfInfo = {
			url: extra.url,
			params: extra.params,
			perf: extra.responseData.perf
		};

		$document.trigger("NEW_PERF",perfInfo);
	});	
});
// --------- /Perf Hook --------- //