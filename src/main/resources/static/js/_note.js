function load()
{
	// スマホの場合
	if( /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) ) {
	}
	else
	{
		 $(".qa").css("font-size","14px !important");
		 $(".qa").css("line-height","18px !important");		
	}
}
$(function() {
	// スマホの場合
	if( /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) ) {
	}
	else
	{
		 $(".qa").css("font-size","14px !important");
		 $(".qa").css("line-height","18px !important");		
	}
});