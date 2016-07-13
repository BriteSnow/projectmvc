[#-- If admin, we bypass the _frame.ftl and load directly the targeted template --]
[#if piStarts("/sysadmin")]
[@includeFrameContent /]
[#-- if we have a user in the request, we display the application --]
[#elseif _r.user??]
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

	<title>projectmvc</title>

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
	<link   href='/css/app-bundle.css' type='text/css' rel='stylesheet'/>
	<script src='/js/app-bundle.js'></script>
	<!-- the views handlebars compile templates (TODO: we might want to bundle this in the app-bundle.js) -->
	<script src='/js/templates.js'></script>
	<!-- /App Bundle -->


	<script type='text/javascript'>

	var appVersion = "${appVersion}";

	</script>
</head>

<body>
	[@includeFrameContent /]
</body>

</html>
[#-- if no user, we include the loginpage --]
[#else]
[@includeTemplate name="loginpage.ftl"/] 
[/#if]