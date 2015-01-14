[#-- If admin, we bypass the _frame.ftl and load directly the targeted template --]
[#if piStarts("/admin")]
[@includeFrameContent /]
[#-- if we have a user in the request, we display the application --]
[#elseif _r.user??]
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>ProjectMVC: TaskManager</title>

	<link rel="stylesheet" href="/bootstrap/css/bootstrap.min.css" type="text/css" />

	[@webBundle path="/common/js/" type="js" /]

	[@webBundle path="/js/" type="js" /]

	[@webBundle path="/css/" type="css" /]

</head>

<body>
	[@includeFrameContent /]
</body>

</html>
[#-- if no user, we include the loginpage --]
[#else]
[@includeTemplate name="loginpage.ftl"/] 
[/#if]