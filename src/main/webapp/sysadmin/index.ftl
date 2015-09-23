<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8" />
	<title>TaskManager - Admin</title>

	<link rel="stylesheet" href="/bootstrap/css/bootstrap.css" type="text/css" />

	[@webBundle path="/common/js/" type="js" /]

	<script type='text/javascript' src='/bootstrap/js/bootstrap.min.js'></script>

	[@webBundle path="/sysadmin/js/" type="js" /]
	[@webBundle path="/sysadmin/css/" type="css" /]

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