<!DOCTYPE html>
<html>
<!-- 
	This page design goals are: 
	   1) Fast: 	This page has to be extremely fast (this is the first experience the user will have, so, has to be instant). 
								Consequently, this page will inline as much as possible and load the strick minimum to provide the intended functionalities 
	   						(login / register) at maximum speed. 
	   						(note: even the current bootstrap dependency could be removed)
	   2) Preload:	Since this page will be the first entry point, it is a good place to preload (after the page is displayed)
									resources needed once logged in (i.e., .js, .css, and font/incons). This will be done after the page is downloaded
									with a simple ajax loop. 
--> 
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>ProjectMVC: TaskManager - Login Page</title>

	<!-- TODO: needs to remove that to make it faster --> 
	<link rel="stylesheet" href="${_r.contextPath}/bootstrap/css/bootstrap.min.css" type="text/css" />
	<!-- <link rel="stylesheet" href="${_r.contextPath}/bootstrap/css/bootstrap-theme.min.css" type="text/css" /> -->

	<script src="common/js/0_jquery.min.js"></script>
	
	<style>
	html,body{
		height: 100%;
	}
	#authpage{
		position: absolute;
		top: 40%;
		left: 50%;
		margin-left: -240px;
		margin-top: -150px;
		width: 480px;
		border: solid 1px #ddd;
	}
	.panel-heading{
		position: relative;
	}
	.panel-heading > span{
		letter-spacing: .1em;
	}
	.form-group{
		margin-top: 10px;
		margin-bottom: 20px;
	}
	.show-for-register{
		display: none;
	}
	#loginswitch{
		position: absolute;
		top: 5px;
		right: 10px;
	}
	.panel-footer > small{
		float: right;
	}
	</style>

	<script type="text/javascript">
	$(function(){
		var SIGNIN = "SIGNIN", REGISTER = "REGISTER";
		var labels = {
			SIGNIN: "Sign In",
			REGISTER: "Register"
		};
		var submitRooting = {
			SIGNIN: {
				path: "/login",
				data: function(){
					return {
						username: $("#input-username").val(),
						pwd: $("#input-pwd").val()
					};
				}
			},
			REGISTER: {
				path: "/register",
				data: function(){
					return {
						username: $("#input-username").val(),
						pwd: $("#input-pwd").val(),
						pwdRepeat: $("#input-pwd-repeat").val(),
					};
				}				
			}
		};

		var $submit = $("#submit-button");

		// --------- Mode Switch (Signin/Register) --------- //
		// Handle the signin/register switch
		$("#loginswitch").on("click",".btn",function(){
			var $btn = $(this);
			var val = $btn.attr("name");
			// toggle the button (does not use the toggle to make sure the value is always in sync with the UI)
			$btn.parent().find(".btn").removeClass("btn-success").addClass("btn-default");
			$btn.removeClass("btn-default").addClass("btn-success");
			$btn.blur(); // make sure it is blur, we do not want the focus 
			if (REGISTER === val){
				$(".show-for-register").show();
			}else{
				$(".show-for-register").hide();
			}
			// make sure that the username input is always selected
			$("input[name='username']").focus();

			// update the label of the header and button
			$(".login-label").html(labels[val]);
			// update the name of the submit button to be able to do a the right API calls on click
			$("button[type='submit']").attr("name",val);

		});
		// --------- /Mode Switch (Signin/Register) --------- //

		$submit.on("click",submit);
		$(document).on("keyup",function(event){
			if (event.which === 13){
				submit();
			}
		});


		function submit(){
			var type = $submit.attr("name");
			var rooting = submitRooting[type];
			$.ajax(rooting.path,{
				type: "POST",
				data: rooting.data(),
				dataType: "json"
			}).done(function(response){
				if (response.success){
					window.location.reload(true);
				}else{
					var $msg = $("<span/>").html(response.errorMessage).appendTo($("#error-msg"));
					setTimeout(function(){
						$msg.fadeOut();
					},4000);
				}
			});
		}

	});
	</script>
</head>

<body>
	<nav class="navbar navbar-default" role="navigation">
		<!-- Brand and toggle get grouped for better mobile display -->
		<div class="navbar-header">
			<button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-ex1-collapse">
				<span class="sr-only">Toggle navigation</span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
			</button>
			<a class="navbar-brand" href="#">TaskManager</a>
		</div>
	</nav>

	<div id="authpage" class="panel panel-primary">
		<div class="panel-heading"><span class="login-label">Signin</span>
			<div id="loginswitch" class="btn-group">
			  <button name="SIGNIN"   type="button" class="btn btn-success btn-sm">Sign In</button>
			  <button name="REGISTER" type="button" class="btn btn-default btn-sm">Register</button>
			</div>
		</div>

		<div class="panel-body">
			<div class="form">
				<div class="form-group">
					<input id="input-username" name="username" type="text" class="form-control"  autofocus placeholder="Email Address">
				</div>
				<div class="form-group">
					<input id="input-pwd" name="pwd" type="password" class="form-control" placeholder="Password">
				</div>		
				<div class="form-group show-for-register">
					<input id="input-pwd-repeat" type="password" name="pwdRepeat" class="form-control" placeholder="Repeat Password">
				</div>		
				<button id="submit-button" type="submit" name="SIGNIN" class="btn btn-primary login-label">Sign In</button>
			</div>
		</div> <!-- /.panel-body -->

		<div class="panel-footer"><span id="error-msg">&nbsp;</span> <small><a href="#">Forgot password</a></small></div>
	</div>

	<!-- TODO: need to load the webbundle JS and CSS for pre caching. -->
</body>

</html>