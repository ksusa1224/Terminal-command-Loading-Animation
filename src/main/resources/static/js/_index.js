var url = window.location.href.split("/")[3];
if (url == "index.html?register_mail=sended")
{
	alert("登録いただいたメールアドレス宛に、\n仮登録完了のメールを送信しました。\n" +
			"もし数分経っても届かない場合、\nお手数ですがお問い合わせフォームか\ninfo@ankinote.com\n" +
			"までお問い合わせください。")
}
if (url == "index.html?type=temporary")
{
	alert("お客様のアカウントは仮登録の状態のため、ログインできません。登録したメールアドレスにお送りした本登録へのリンクを押下し、登録を完了させてください。");
}

if (url == "index.html?login=error")
{
	alert("Email/Login ID またはパスワードが間違っているため、ログインできません。");
}

if (url == "index.html?withdraw=true")
{
	alert("退会が完了しました。");
}

function check_owner_id_char(obj) {
	if (obj.value.match(/[^A-Za-z0-9\-_]+/)) {
		 //半角英数字以外の文字が存在する場合、エラー
		alert("Login IDに使用できる文字は、半角英数字または\nハイフン（-）アンダースコア（_）のみです。")
		obj.value = "";
		return false;
	}
}

$('#register').submit(function() {
	if ($("input[name=owner_id").val().match(/[^A-Za-z0-9\-_]+/)){
		alert("Login IDに使用できる文字は、半角英数字または\nハイフン（-）アンダースコア（_）のみです。")
		$("input[name=owner_id").val("");
		return false;
	}
});

$('#premium_button').click(function() {
	if ($("input[name=owner_id").val().match(/[^A-Za-z0-9\-_]+/)){
		alert("Login IDに使用できる文字は、半角英数字または\nハイフン（-）アンダースコア（_）のみです。")
		$("input[name=owner_id").val("");
		return false;
	}
});

function user_policy()
{
	if(!$("#user_policy_check").is(':checked'))
	{
		alert("利用規約に同意してください。");
		return false;
	}
}

function browser()
{
	if (!is_capable_browser())
	{
		alert("「暗記ノート」は、現在アクセスしているお客様のブラウザに対応しておりません。\n\n" +
				"対応ブラウザ：\n" +
				"・Chrome\n" +
				"・Internet Explorer 11以降\n" +
				"・Safari(Mac/iOS版)\n" +
				"・Edge\n" +
				"・Opera");
	}
//	$("#browser").dialog();
//	return false;
}

function browser_sample()
{
//	$("#browser").dialog();
}

function is_capable_browser() {
	
	var userAgent = window.navigator.userAgent.toLowerCase();

	if (userAgent.indexOf('opera') != -1) {
	  return true;
	} 
	else if (userAgent.indexOf('chrome') != -1) 
	{
	  return true;
	} 
	else if (userAgent.indexOf('safari') != -1) 
	{
	  return true;
	} 
	else if (detectIE() > 10)
	{
		return true;
	}
	else 
	{
	  return false;
	}	
}

function detectIE() {
    var ua = window.navigator.userAgent;

    var msie = ua.indexOf('MSIE ');
    if (msie > 0) {
        // IE 10 or older => return version number
        return parseInt(ua.substring(msie + 5, ua.indexOf('.', msie)), 10);
    }

    var trident = ua.indexOf('Trident/');
    if (trident > 0) {
        // IE 11 => return version number
        var rv = ua.indexOf('rv:');
        return parseInt(ua.substring(rv + 3, ua.indexOf('.', rv)), 10);
    }

    var edge = ua.indexOf('Edge/');
    if (edge > 0) {
       // Edge (IE 12+) => return version number
       return parseInt(ua.substring(edge + 5, ua.indexOf('.', edge)), 10);
    }

//    // other browser
//    return false;
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

function to_premium() {			
	var email = $("#email_premium").val();
	var owner_id = $("#owner_id_premium").val();
	var owner_name = $("#owner_name_premium").val();
	var password = $("#password_premium").val();

	var is_email_exists;
	jQuery.ajax({
		url: "./is_email_deplicate.html?email=" + email,
		dataType: "html",
		cache: false,
		async: false,
		success: function(data)
		{
			if (data == 'deplicate')
			{
				is_email_exists = true;
			}
			else
			{
				is_email_exists = false;
			}
		},
		error: function(data)
		{
			alert("ajax error");
		}
	});

	var is_owner_id_exists;
	jQuery.ajax({
		url: "./is_owner_id_deplicate.html?owner_id=" + owner_id,
		dataType: "html",
		cache: false,
		async: false,
		success: function(data)
		{
			if (data == 'deplicate')
			{
				is_owner_id_exists = true;
			}
			else
			{
				is_owner_id_exists = false;
			}
		},
		error: function(data)
		{
		}
	});

	var ajax_result;
	
	jQuery.ajax({
		url: "./is_email_and_owner_id_exists.html?email=" + email + "&owner_id=" + owner_id,
		dataType: "json",
		cache: false,
		async: false,
		success: function(data)
		{
			if (data[0] == null && data[1] == null) {
				ajax_result = false;
			}
			else
			{
				ajax_result = data;			
			}
		},
		error: function(data)
		{
		}
	});
		
	// メアドとIDの組み合わせとしては存在しないが、メールアドレスだけは存在する場合
	if (is_email_exists == true && ajax_result == false) {
		$( "#dialog_email" ).dialog();
		return false;
	}
	// メアドとIDの組み合わせとしては存在しないが、IDだけは存在する場合
	if (is_owner_id_exists == true && ajax_result == false){
		$( "#dialog_owner_id" ).dialog();
		return false;
	}
	
	// メアドとIDの組み合わせが存在する場合
	if (is_email_exists && is_owner_id_exists && ajax_result != false){
		
		owner_name = ajax_result[0];
		kakin_type = ajax_result[1];
		
		// 現在無料アカウントの場合
		if (kakin_type == 1) {
			if (confirm("Email: " + email + "\n" + "Owner ID: " + owner_id + "\n" +
				"上記のお客様は、Owner Name: " + owner_name + "様として無料会員で登録されています。\n" +
				"このまま有料会員へのアップグレードを続けますか？")) {
			    // 後続の処理へ
			} else {
			    return false;
			}		
		}
		// 現在プレミアムアカウントの場合
		else if (kakin_type == 2) {
			alert("Email: " + email + "\n" + "Owner ID: " + owner_id + "\n" +
					"上記のお客様は、Owner Name: " + owner_name + "様として\n" + 
					"すでにプレミアム会員で登録されています。");
				return false;
		}
		else 
		{
			return false;
		}	
	}
	

	jQuery.ajax({
		url: "./regist_premium.html?email=" + email + "&owner_id=" + owner_id + "&owner_name=" + owner_name + "&password=" + password,
		dataType: "text",
		cache: false,
		success: function(data)
		{
			window.open(data,"_self");
			return false;
		},
		error: function(data)
		{
			alert("ajax error");
			return false;
		}
	});			

}

function is_owner_id_deplicate(obj)
{
	var owner_id = obj.value;
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
