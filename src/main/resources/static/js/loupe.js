$(function() {

	loupe();
	//$('.a').attr('onMouseOver', 'this.style.opacity=1;');
///	$('.a').attr('onClick', 'change_seitou_color(this);');
	
});

function remove_loupe()
{
	$loope.remove();
	$magnifyingGlass.remove();
	$magnifiedContent.remove();
	$magnifyingLens.remove();
}

function loupe()
{
	// 虫眼鏡
	var scale = 1.2;
	
	var $magnifyingGlass = $('<div class="magnifying_glass"></div>');
	var $magnifiedContent = $('<div class="magnified_content"></div>');
	var $magnifyingLens = $('<div class="magnifying_lens"></div>');
	var $loope = $('<div id="loupe"></div>');
	
	//setup
	$magnifiedContent.css({
	    backgroundColor: $("html").css("background-color") || $("body").css("background-color"),
	    backgroundImage: $("html").css("background-image") || $("body").css("background-image"),
	    backgroundAttachment: $("html").css("background-attachment") || $("body").css("background-attachment"),
	    backgroundPosition: $("html").css("background-position") || $("body").css("background-position")
	});
	
	$loope.css({
		position: "absolute",
		left:550,
		top:100
	});
	
	$magnifyingGlass.css({
		position: "absolute",
		left:562,
		top:107
	});	
	
    $magnifiedContent.css({
        left: -562 * scale,
        top: -107 * scale
    });
	
	//$magnifiedContent.html(innerShiv($(document.body).html())); //fix html5 for ie<8, must also include script
	$magnifiedContent.html($(document.body).html());
	
	$magnifyingGlass.append($magnifiedContent);
	$magnifyingGlass.append($magnifyingLens); //comment this line to allow interaction
	$(document.body).append($magnifyingGlass);
	$(document.body).append($loope);
	
	function updateViewSize() {
	    $magnifiedContent.css({
	        width: $(document).width(),
	        height: $(document).height()
	    });
	}
	
	//begin
	updateViewSize();
	
	//events
	$(window).resize(updateViewSize);
	
	$magnifyingGlass.mousedown(function(e) {
	    e.preventDefault();
	    $(this).data("drag", {
	        mouse: {
	            top: e.pageY,
	            left: e.pageX
	        },
	        offset: {
	            top: $(this).offset().top,
	            left: $(this).offset().left
	        }
	    });
	});
	
	$loope.mousedown(function(e) {
	    e.preventDefault();
	    $(this).data("drag", {
	        mouse: {
	            top: e.pageY,
	            left: e.pageX
	        },
	        offset: {
	            top: $(this).offset().top,
	            left: $(this).offset().left
	        }
	    });
	});
	
	
	
	$(document.body).mousemove(function(e) {
	    if ($loope.data("drag")) {
	        var drag = $loope.data("drag");
	
	        var left = drag.offset.left + (e.pageX - drag.mouse.left);
	        var top = drag.offset.top + (e.pageY - drag.mouse.top);
	
	        $magnifyingGlass.css({
	            left: left,
	            top: top
	        });
	        $magnifiedContent.css({
	            left: -left * scale,
	            top: -top * scale
	        });
	        
	        var loupe = jQuery('#loupe');
	    	//var offset = $(".magnifying_glass").offset();

	        loupe.css({
	            left: left-10,
	            top: top-8,
	        });	
	    }
	}).mouseup(function() {
		$loope.removeData("drag");
	});	
	
	
	
	$(document.body).mousemove(function(e) {
	    if ($magnifyingGlass.data("drag")) {
	        var drag = $magnifyingGlass.data("drag");
	
	        var left = drag.offset.left + (e.pageX - drag.mouse.left);
	        var top = drag.offset.top + (e.pageY - drag.mouse.top);
	
	        $magnifyingGlass.css({
	            left: left,
	            top: top
	        });
	        $magnifiedContent.css({
	            left: -left * scale,
	            top: -top * scale
	        });
	        
	        var loupe = jQuery('#loupe');
	    	//var offset = $(".magnifying_glass").offset();

	        loupe.css({
	            left: left-15,
	            top: top-10,
	        });	
	    }
	}).mouseup(function() {
	    $magnifyingGlass.removeData("drag");
	});	
	
}
