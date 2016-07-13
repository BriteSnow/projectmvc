<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8" />
	<title>TaskManager - Admin</title>

	<!-- Common Bundle --> 
	<!-- This bundle includes jquery, other 3rd party libs, and common app/sysadmin util lib
	     like render, applicaiton context and others --> 
	<script type='text/javascript' src='/js/common-bundle.js'></script>
	<!-- /Common Bundle --> 
	
	<!-- Boostrap -->
	<link   href='/bootstrap/css/bootstrap.min.css' type='text/css' rel='stylesheet' />
	<script src='/bootstrap/js/bootstrap.min.js' type='text/javascript' ></script>
	<!-- /Boostrap -->

	<!-- App Bundle -->
	<link   href='/sysadmin/css/sys-bundle.css' type='text/css' rel='stylesheet'/>
	<script src='/sysadmin/js/sys-bundle.js'></script>
	<!-- the views handlebars compile templates (TODO: we might want to bundle this in the app-bundle.js) -->
	<script src='/sysadmin/js/templates.js'></script>
	<!-- /App Bundle -->



	[#if _r.user??]
	<script type="text/javascript">
	$(function(){
		
		brite.display("AdminView","body");
	});
	</script>
	[#else]
	<script type="text/javascript">	
	$(function(){		
		brite.display("AdminLogin","body");
	});
	</script>	
	[/#if]


</head>

<body>
	
</body>
</html>