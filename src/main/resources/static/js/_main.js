//QA内でのパーツの順番
var id = 2;

function qa_focus(obj)
{
	if (id == 2)
	{
		enter();
	}
}

// HTMLタグごとコピーペーストされるのを防ぐ（文字列のみにする）
$(document).on('paste', function(e){
	$('#qa_input').children('span').each(function () {
		// HTMLタグを取り除く
	    $(this).html($(this).html().replace(/<\/?[^>]+(>|$)/g,""));
		var pastedData = e.originalEvent.clipboardData.getData('text');
	    $("#qa_input span:nth-last-child(2)").html(pastedData);
	    focus_last();
	    event.preventDefault();
	});
});

// 漢字変換後にEnterを押したときにペンの色が変わる
function enter (){
	var q_parts = "<span class='q_input' id='" + id + "'>&#8203;</span>";
	var a_parts = "<span class='a_input' id='" + id + "'>&#8203;</span>";
	var last = $("#qa_input span:last").attr('class');
	var last_a = $("#qa_input span:nth-last-child(2)").text();
	if (id == 2)
	{	
		$("#qa_input").append("<span class='q_input' id='1'>&#8203;</span>");	
		$("#qa_input").append(a_parts);	
		focus_last();
		id++;
	}	
	if (window.event.keyCode == 13)
	{
		if (last == "q_input")
		{
			$("#qa_input").append(a_parts);	
			focus_last();
			id++;
						
			jQuery.ajax({
				url: "../serif.html?a=" + last_a,
				dataType: "html",
				cache: false,
				success: function(data)
				{					
					if (last_a.trim() != '\u200B')
					{
						$("#balloon").css("opacity","1");
						$("#serif").text(data);
					}
				},
				error: function(data)
				{
					alert("ajax error");
				}
			});
			
		}
		else if (last == "a_input")
		{
			$("#qa_input").append(q_parts);						
			focus_last();
			id++;
			$("#balloon").css("opacity","0");
			$("#serif").text("");
		}		
		event.preventDefault();
	}
}

// 最後の要素にカーソルを移動する
function focus_last(){
	var node = document.querySelector("#qa_input");
	node.focus();
	var textNode = null;
	textNode = node.lastChild;
	var caret = 0; // insert caret after the 10th character say
	var range = document.createRange();
	range.setStart(textNode, caret);
	range.setEnd(textNode, caret);
	var sel = window.getSelection();
	sel.removeAllRanges();
	sel.addRange(range);
}

// contenteditableはそのままformでsubmitできないためいったん非表示のテキストエリアにコピー
function copy_to_hidden () {
	// 空白タグを取り除く
	$('#qa_input').children('span').each(function () {
		// ゼロ幅のスペース
		if ($(this).html().trim() == "\u200B")
	    {
	    	$(this).remove();
	    }
	});
	var content = $("#qa_input").html();
	$("#qa_input_hidden").html(content);
    return true;
}

// 問題登録押下時、リロードせずにAjaxで登録と再検索を行う
function ajax_reload ()
{
	var qa_input = $("#qa_input_hidden").html();
	var decoded = $("#qa_input_hidden").html(qa_input).text();
	var yomudake_flg = $("#yomudake_flg").val();
	var reversible_flg = $("#reversible_flg").val();
	jQuery.ajax({
		url: "../register_qa.html?qa_input=" + decoded + 
				"&yomudake_flg=" + yomudake_flg +
				"&reversible_flg=" + reversible_flg,
		dataType: "html",
		cache: false,
		success: function(data)
		{				
			$("#qa_area").html(data);
			$("#qa_input").html("");
			$("#qa_input").focus();
			id = 2;
		},
		error: function(data)
		{
			alert("ajax error");
		}
	});	
}

function change_seitou_color(obj)
{
	var qa_id = $(obj).parent().attr("id");
	var s_id = $(obj).attr("id");
	var attr = $(obj).attr('onmouseout');
	var is_seikai_now = 0;
	if (typeof attr !== typeof undefined && attr !== false) {
	    is_seikai_now = 1;
	}
	jQuery.ajax({
		url: "../change_seitou_color.html?qa_id="+qa_id+"&s_id="+s_id+"&is_seikai_now="+is_seikai_now,
		dataType: "html",
		cache: false,
		success: function(data)
		{			
			if (data == '0')
			{
				$(obj).css("opacity","1");
				$(obj).removeAttr('onmouseout');
				$("#seikai_sum").text(Number($("#seikai_sum").text())+1);
				document.getElementById("seikai_se").play();
			}
			else
			{
				$(obj).css("opacity","0");
				$(obj).attr("onmouseout","this.style.opacity='0'");
				$("#seikai_sum").text(Number($("#seikai_sum").text())-1);
				document.getElementById("huseikai_se").play();
			}
		},
		error: function(data)
		{
			alert("ajax error");
		}
	});
}

// TODO 常にoffになってしまう
function change_val(chk_box)
{
	if($("this").val() == "off")
	{
		$("this").val() == "on"
	}
	else
	{
		$("this").val() == "off";
	}
}

function to_right_page()
{
	if (document.getElementById("qa_area").offsetHeight < document.getElementById("qa_area").scrollHeight ||
		document.getElementById("qa_area").offsetWidth < document.getElementById("qa_area").scrollWidth) {
		    // your element have overflow
//			var hiddenElements = $( "qa_area" ).find( ":hidden" );
//			alert(hiddenElements.html());
			var h = $("#qa_area").height();
			var hiddenEls = new Array();

			$("#qa_area").find(".qa").each(function(){
			    if ($(this).position().top > h)
			        hiddenEls.push($(this));
			});

			var qa = "";
			for (var i = 0; i < hiddenEls.length; i++)
			{
				var qa = qa + $('<span>').append(hiddenEls[i].clone()).html();
				//alert(hiddenEls[i].html());
				//alert(aa);
				
			}
			
			$("#qa_area_right").html(qa);
			
		} else {
		    // your element doesn't have overflow
			//alert("not overflow")
	}
}

var mode = "default";

function key_event() {
	// 解答を全て赤くするショートカット
    // altKey + R
    if (window.event.altKey == true && window.event.keyCode == 82)
    {
	    event.preventDefault();
    	
        if (mode != "red")
        {
        	$(".a").css("opacity","1");
            mode = "red";
        }
        else
        {
            // もう一度altKey＋Rを押すともとに戻る
            for(i = 0; i < document.getElementsByClassName("a").length; i++)
            {
            	var attr = document.getElementsByClassName("a")[i].getAttribute('onmouseout');
            	if (attr != null) {
            		document.getElementsByClassName("a")[i].style.opacity = 0;
            	}
            }
            mode = "default";
        }
    }

    // 解答を全て白くするショートカット
    // altKey + W
    if (window.event.altKey == true && window.event.keyCode == 87)
    {
	    event.preventDefault();

	    if (mode != "white")
        {
        	$(".a").css("opacity","0");
            mode = "white";
        }
        else
        {
            // もう一度altKey＋Wを押すともとに戻る
            for(i = 0; i < document.getElementsByClassName("a").length; i++)
            {
            	var attr = document.getElementsByClassName("a")[i].getAttribute('onmouseout');
            	if (attr == null) {
            		document.getElementsByClassName("a")[i].style.opacity = 1;
            	}
            }
            mode = "default";
        }
    }
}
/*
var pointerX;
var pointerY;
$("#loope").draggable({
  start : function(evt, ui) {
    pointerY = (evt.pageY - $('#canvas').offset().top) / zoom - parseInt($(evt.target).css('top'));
    pointerX = (evt.pageX - $('#canvas').offset().left) / zoom - parseInt($(evt.target).css('left'));
  },
  drag : function(evt, ui) {
    var canvasTop = $('#canvas').offset().top;
    var canvasLeft = $('#canvas').offset().left;
    var canvasHeight = $('#canvas').height();
    var canvasWidth = $('#canvas').width();

    // Fix for zoom
    ui.position.top = Math.round((evt.pageY - canvasTop) / zoom - pointerY); 
    ui.position.left = Math.round((evt.pageX - canvasLeft) / zoom - pointerX); 

    // Check if element is outside canvas
    if (ui.position.left < 0) ui.position.left = 0;
    if (ui.position.left + $(this).width() > canvasWidth) ui.position.left = canvasWidth - $(this).width();  
    if (ui.position.top < 0) ui.position.top = 0;
    if (ui.position.top + $(this).height() > canvasHeight) ui.position.top = canvasHeight - $(this).height();  

    // Finally, make sure offset aligns with position
    ui.offset.top = Math.round(ui.position.top + canvasTop);
    ui.offset.left = Math.round(ui.position.left + canvasLeft);
  }
});*/

$(function() {
	
	var scale = 2;
	
	var $magnifyingGlass = $('<div class="magnifying_glass"></div>');
	var $magnifiedContent = $('<div class="magnified_content"></div>');
	var $magnifyingLens = $('<div class="magnifying_lens"></div>');
	
	//setup
	$magnifiedContent.css({
	    backgroundColor: $("html").css("background-color") || $("body").css("background-color"),
	    backgroundImage: $("html").css("background-image") || $("body").css("background-image"),
	    backgroundAttachment: $("html").css("background-attachment") || $("body").css("background-attachment"),
	    backgroundPosition: $("html").css("background-position") || $("body").css("background-position")
	});
	
	//$magnifiedContent.html(innerShiv($(document.body).html())); //fix html5 for ie<8, must also include script
	$magnifiedContent.html($(document.body).html());
	
	$magnifyingGlass.append($magnifiedContent);
	$magnifyingGlass.append($magnifyingLens); //comment this line to allow interaction
	$(document.body).append($magnifyingGlass);
	
	//funcs
	
	
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
	
	    }
	}).mouseup(function() {
	    $magnifyingGlass.removeData("drag");
	});
});
