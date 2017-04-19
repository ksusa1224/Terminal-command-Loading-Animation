		
		var url = window.location.href.split("/")[3];
		if (url == "index.html?register_mail=sended")
		{
			alert("登録いただいたメールアドレス宛に、仮登録完了のメールを送信しました。")
		}
		if (url == "index.html?type=temporary")
		{
			alert("お客様のアカウントは仮登録の状態のため、ログインできません。登録したメールアドレスにお送りした本登録へのリンクを押下し、登録を完了させてください。");
		}
		
		var validated = false;
		function is_email_deplicate()
		{
			var email = $("#email").val();
			jQuery.ajax({
				url: "./is_email_deplicate.html?email=" + email,
				dataType: "html",
				cache: false,
				success: function(data)
				{
					if (data == 'deplicate')
					{
					    $( "#dialog_email" ).dialog();
					    validated = false;
					    return false;
					}
					else
					{
						validated = true;						
					}
				},
				error: function(data)
				{
				}
			});
		}
		
		function is_email_deplicate_premium()
		{
			var email = $("#email_premium").val();
			jQuery.ajax({
				url: "./is_email_deplicate.html?email=" + email,
				dataType: "html",
				cache: false,
				success: function(data)
				{
					if (data == 'deplicate')
					{
					    $( "#dialog_email" ).dialog();
					    validated = false;
					    return false;
					}
					else
					{
						validated = true;						
					}
				},
				error: function(data)
				{
				}
			});
		}
		
		function to_premium() {			
			var email = $("#email_premium").val();
			var owner_id = $("#owner_id_premium").val();
			var owner_name = $("#owner_name_premium").val();
			var password = $("#password_premium").val();
			jQuery.ajax({
				url: "./regist_premium.html?email=" + email + "&owner_id=" + owner_id + "&owner_name=" + owner_name + "&password=" + password,
				dataType: "text",
				cache: false,
				success: function(data)
				{
					alert(data);
					window.open(data);
					return false;
				},
				error: function(data)
				{
					alert("error");
					return false;
				}
			});			
		}
		
		function is_owner_id_deplicate()
		{
			var owner_id = $("#owner_id").val();
			jQuery.ajax({
				url: "./is_owner_id_deplicate.html?owner_id=" + owner_id,
				dataType: "html",
				cache: false,
				success: function(data)
				{
					if (data == 'deplicate')
					{
					    $( "#dialog_owner_id" ).dialog();
					    validated = false;
					    return false;
					}
					else
					{
						validated = true;
					}
				},
				error: function(data)
				{
				}
			});
		}
		
		function is_owner_id_deplicate_premium()
		{
			var owner_id = $("#owner_id_premium").val();
			jQuery.ajax({
				url: "./is_owner_id_deplicate.html?owner_id=" + owner_id,
				dataType: "html",
				cache: false,
				success: function(data)
				{
					if (data == 'deplicate')
					{
					    $( "#dialog_owner_id" ).dialog();
					    validated = false;
					    return false;
					}
					else
					{
						validated = true;
					}
				},
				error: function(data)
				{
				}
			});
		}

		$("#register").on('submit', function(e){
	        if(validated == false)
	        {
	            e.preventDefault();
	        }
	    });
	    
	    function remind(url, title, w, h)
	    {
    	    // Fixes dual-screen position                         Most browsers      Firefox
    	    var dualScreenLeft = window.screenLeft != undefined ? window.screenLeft : screen.left;
    	    var dualScreenTop = window.screenTop != undefined ? window.screenTop : screen.top;

    	    var width = window.innerWidth ? window.innerWidth : document.documentElement.clientWidth ? document.documentElement.clientWidth : screen.width;
    	    var height = window.innerHeight ? window.innerHeight : document.documentElement.clientHeight ? document.documentElement.clientHeight : screen.height;

    	    var left = ((width / 2) - (w / 2)) + dualScreenLeft;
    	    var top = ((height / 2) - (h / 2)) + dualScreenTop;
    	    var newWindow = window.open(url, title, 'scrollbars=yes,directories=no,titlebar=no,toolbar=no,location=no,status=no,menubar=no, width=' + w + ', height=' + h + ', top=' + top + ', left=' + left);

    	    // Puts focus on the newWindow
    	    if (window.focus) {
    	        newWindow.focus();
    	    }	    	
	    }
