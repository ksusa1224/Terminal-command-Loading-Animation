var server_error = "GORORON堂のサーバーが混みあってるみたい";

function change_opacity (seitou) {
	seitou.mouseout(function(){ return false});
	if (seitou.style.opacity == 1)
	{
		seitou.style.opacity = 1;
	}
	else
	{
		seitou.style.opacity = 0;		
	}
}

function mouseout (seitou) {
	if (seitou.style.opacity == 1)
	{
		seitou.style.opacity = 0;
	}
}

// QA再生制御関数　動作仕様：
// ①１回目にクリックしたときリピート再生する
// ②止めたいときはその問題文かどこかしらの問題文をクリックする
// ③もう１度リピート再生するにはまた問題文をどこかしらクリックする
var play_mode_cnt = 0;
function control_qa_saisei(mondaibun__,kaitou__,q_gengo,a_gengo)
{
	play_mode_cnt ++;

	// 奇数回目の問題文クリックの場合
	if (play_mode_cnt % 2 == 1)
	{
		play_mode = "repeat";			
	}
	// 偶数回目の問題文クリックの場合
	else
	{
		play_mode = "stop";
	}

	if (play_mode == "repeat")
	{
		qa_saisei(mondaibun__,kaitou__,q_gengo,a_gengo,play_mode);
	}
	else if (play_mode == "stop")
	{
		window.speechSynthesis.cancel();			
	}
}

function qa_saisei(mondaibun_, kaitou_,q_gengo_,a_gengo_,play_mode)
{
	var q_msg = new SpeechSynthesisUtterance(mondaibun_);
	var voices = window.speechSynthesis.getVoices();
 	if (q_gengo_ == "日本語")
	{
//        q_msg.default = true;
 		q_msg.voice = voices[0]; // Otoya
		q_msg.volume = 0.4;
		q_msg.pitch = 0.9;
		q_msg.lang = "ja-JP";
	}else
	{
//        q_msg.default = true;
		// q_msg.voice = voices[6];//Vicky
		q_msg.volume = 1;
		q_msg.lang = "en-US";
	}
	window.speechSynthesis.speak(q_msg);

	var a_msg = new SpeechSynthesisUtterance(kaitou_);
	if (a_gengo_ == "日本語")
	{
		a_msg.lang = "ja-JP";
 		//a_msg.voice = voices[46]; // Otoya
		a_msg.volume = 0.4;
		a_msg.pitch = 1;
		window.speechSynthesis.speak(a_msg);
	}
//	else if (a_gengo == "読むだけ")
//	{
		// 読むだけ問題の正答は読み上げない
//	}
	else
	{
//        a_msg.default = true;
		a_msg.voice = voices[62];//Vicky
		a_msg.volume = 1;
		a_msg.lang = "en-US";
		window.speechSynthesis.speak(a_msg);		
	}
	
	if (play_mode == "repeat")
	{
		qa_saisei(mondaibun_,kaitou_,q_gengo_,a_gengo_,play_mode);

	}
}

(function($){
    $.fn.extend({
         center: function (options) {
              var options =  $.extend({ // Default values
                   inside:window, // element, center into window
                   transition: 0, // millisecond, transition time
                   minX:0, // pixel, minimum left element value
                   minY:0, // pixel, minimum top element value
                   withScrolling:false, // booleen, take care of the scrollbar (scrollTop)
                   vertical:false, // booleen, center vertical
                   horizontal:true // booleen, center horizontal
              }, options);
              return this.each(function() {
                   var props = {position:'absolute'};
                   if (options.vertical) {
                        var top = ($(options.inside).height() - $(this).outerHeight()) / 2;
                        if (options.withScrolling) top += $(options.inside).scrollTop() || 0;
                        top = (top > options.minY ? top : options.minY);
                        $.extend(props, {top: top+'px'});
                   }
                   if (options.horizontal) {
                         var left = ($(options.inside).width() - $(this).outerWidth()) / 2;
                         if (options.withScrolling) left += $(options.inside).scrollLeft() || 0;
                         left = (left > options.minX ? left : options.minX);
                         $.extend(props, {left: left+'px'});
                   }
                   if (options.transition > 0) $(this).animate(props, options.transition);
                   else $(this).css(props);
                   return $(this);
              });
         }
    });
})(jQuery);

var tag_search_conditions_uri = "";

var pulun = "false";

function slime_pulupulu()
{
	$("#slime").animate({width: '150px', height:'145px', top:'445px'}, 600);
	$("#slime").animate({width: '150px', height:'150px', top:'440px'}, 600);
	if (pulun == "true")
	{
//		slime_pulupulu();
	}
}

$draged_husen = null;

var qa_id_for_contextmenu = "";

function body_load()
{	
	$('.qa').mousedown(function(event) {
	    switch (event.which) {
	        case 1:
//	            alert('Left Mouse button pressed.');
	            break;
	        case 2:
//	            alert('Middle Mouse button pressed.');
	            break;
	        case 3:
//	            alert('Right Mouse button pressed.');
	    		qa_id_for_contextmenu = this.id;
	            break;
	        default:
//	            alert('You have a strange Mouse!');
	    }
	});	
//	$( ".qa" ).mouseover(function() {
//		qa_id_for_contextmenu = this.id;
////		alert(qa_id_for_contextmenu);
//	});
//	countLines("qa_area");
	
	$("#qa_input").on("click", function () { // click event
//		  alert( "Handler for .focus() called." );
		id = 2;
		enter();
	});	
	
	//$("#entire_page").center();
    $( "#crystal_board" ).draggable();
    $( "#dialog" ).draggable();
    //$( "#slime" ).draggable();
    $( "#erasor" ).draggable();
    $( "#blue_pen" ).draggable();
    $( "#red_pen" ).draggable();
    //$( "#qa_panel" ).draggable();
    $( "#note_area" ).draggable();
    $( ".husen" ).draggable({
    	revert: 'true', 
    	//appendTo: 'body',
    	//containment: 'window',
    	scroll: true,
    	helper: 'clone',
		stop : function(e, ui){
	         $('.husen').draggable().data()["ui-draggable"].cancelHelperRemoval = true;
	         this.style.opacity=0;
//	         alert($(ui.helper));
//	         $draged_husen = $(ui.helper);
//	         alert($draged_husen.attr("class"));
	    },
		drag : function(e, ui){
	        this.style.opacity=0;
	    }    		
    });
    $( "#loupe" ).draggable();
    
    var qa_husen_junban = 1;
    $('#husen_paste').droppable({
        accept:'.husen',
        drop: function(event,ui){
        	var husen_name = $(ui.draggable).text();
        	$("#qa_husen").html("<span data-junban='"+ qa_husen_junban +"'>" + husen_name +"</span>")
        	qa_husen_junban++;
        }
    });    

    $('#qa_panel').droppable({
        accept:'#erasor',
        drop: function(event,ui){
        	var id = $(ui.draggable).attr("id");
        	if (id == 'erasor')
        	{
        		var qa_id = $("#qa_id").val();
        		if (qa_id != "")
        		{
        			jQuery.ajax({
        				url: "../qa_delete.html?qa_id=" + qa_id +"&husen_str=" + tag_search_conditions_uri,
        				dataType: "json",
        				cache: false,
        				success: function(data)
        				{
        					$("#qa_input_hidden").html("");
        					$("#qa_input").html("");
        					id = 2;
        					$("#qa_input").focus();
        					
        					$("#qa_area").html(data[0]);
        					$("#qa_area_right").html(data[1]);
        					$("#seitou_sum").html(data[2]);
        					$("#seikai_sum").html(data[3]);
        					$(".total_pages").html(data[4]);

        				    $("#loupe").remove();
        				    $(".magnifying_glass").remove();
        				    $(".magnified_content").remove();
        				    $(".magnifying_lens").remove();
        				    loupe();
        				    loupe_drop();

//        					$('.qa').contextmenu({
//        				        target: "#qa_context-menu"
//        				    });
//        				    var margin_top = $(".dropdown-menu").css("margin-top");	
        				},
        				error: function(data)
        				{
        					$("#balloon").css("display","inline");
        					$("#serif").text(server_error);
        				}
        			});
        		}
        		else
        		{
					$("#balloon").css("display","inline");
					$("#serif").text("新しいQAカードは消せないよ〜");        			
        		}
        	}
        },
    	out: function(event, ui){
    		$("#qa_id").val("");
			$("#balloon").css("display","none");
			$("#serif").text("");   			    		
    	}
    });
    
    loupe_drop();
    
    $('#crystal_board').droppable({
        accept:'.husen',
        drop: function(event,ui){
        	//alert($(ui.draggable).html());
        	//$("#husen_wrapper").append($(ui.draggable).parent().html());
        	//$('#husen_wrapper').append($(ui.draggable).clone()).html();
        	//        	alert($(ui.draggable).text());
//        	alert($(this).attr('id'));
        }
    });    


    
    $(".qa").hover(function() {
    	var id = $(this).attr("id");
    	var s_id = $(this).children(".a").attr("id");
    	var path = "../speech/" + s_id + ".m4a";
    	$("#play_qa").attr("src",path);

      $(document).keydown(function(obj) {
    	  // スペースキー
    	  if (window.event.keyCode == 32)
    	  {
//    		  document.getElementById("play_qa").play();    	
    	  }
      });

  }, function() {
	  	$("#play_qa").attr("src","");

       // unbind the keydown handler on mouseleave
     $(document).unbind("keydown");
		document.getElementById("play_qa").stop();
  });
    
	$('#play_qa').on('ended', function() {
	  	$("#balloon").css("display","none");
		$("#serif").text("");		
	});
	
	var factor = 2;

	$('#slime').hover(
	    function(){
	    	pulun = "true";
	    	slime_pulupulu();
	    },        
	    function(){
	    	pulun = "false";
	    }
	 );	
}

var husen_names = [];
function loupe_drop()
{
    $('#loupe').droppable({
        accept:'.husen',
        out: function (event, ui) {
        	var removeItem = $(ui.draggable).text();
        	husen_names = jQuery.grep(husen_names, function(value) {
        		  return value != removeItem;
        	});
        	var husens_str = "";
        	var loop_length = husen_names.length;
        	for (var i = 0; i < loop_length; i++)
        	{
        		husens_str += husen_names[i];
        		if (i < loop_length - 1)
        		{
        			husens_str += ",";
        		}
        	}
        	husens_str = encodeURIComponent(husens_str);
        	tag_search_conditions_uri = husens_str;
			jQuery.ajax({
				url: "../tag_search.html?husen_names=" + husens_str,
				dataType: "json",
				cache: false,
				success: function(data)
				{
					$("#qa_area").html(data[0]);
					$("#qa_area_right").html(data[1]);
					$("#seitou_sum").html(data[2]);
					$("#seikai_sum").html(data[3]);
					$(".total_pages").html(data[4]);

//					$drop_husen = $(".husen.ui-draggable.ui-draggable-handle.ui-draggable-dragging");
//					$drop_husen.hide();

//				    $("#loupe").remove();
//				    $(".magnifying_glass").remove();
//				    $(".magnified_content").remove();
//				    $(".magnifying_lens").remove();
//				    loupe();
				    $("#loupe").remove();
//				    $(".magnified_content").html($(document.body));
				    $(".magnifying_glass").remove();
				    $(".magnified_content").remove();
				    $(".magnifying_lens").remove();
				    loupe();
				    loupe_drop();

				    $hide_husen = $(".magnified_content").find(".husen.ui-draggable.ui-draggable-handle.ui-draggable-dragging");
				    $hide_husen.hide();
					

					$('.qa').contextmenu({
				        target: "#qa_context-menu"
				    });
				    var margin_top = $(".dropdown-menu").css("margin-top");	
					
//				    $body_html = $("document.body").html();
//				    $(".magnified_content").html($body_html);
//		        	$(".husen.ui-draggable.ui-draggable-handle.ui-draggable-dragging").eq(0).show();
					
				},
				error: function(data)
				{
					$("#balloon").css("display","inline");
					$("#serif").text(server_error);
				}
			});
        },
        drop: function(event,ui){
//		    $(ui.draggable).remove();

        	husen_names.push($(ui.draggable).text());
        	var husens_str = "";
        	var loop_length = husen_names.length;
        	for (var i = 0; i < loop_length; i++)
        	{
        		husens_str += husen_names[i];
        		if (i < loop_length - 1)
        		{
        			husens_str += ",";
        		}
        	}
        	husens_str = encodeURIComponent(husens_str);
        	tag_search_conditions_uri = husens_str;
			jQuery.ajax({
				url: "../tag_search.html?husen_names=" + husens_str,
				dataType: "json",
				cache: false,
				success: function(data)
				{
					$("#qa_area").html(data[0]);
					$("#qa_area_right").html(data[1]);
					$("#seitou_sum").html(data[2]);
					$("#seikai_sum").html(data[3]);
					$(".total_pages").html(data[4]);

//				    $("#loupe").remove();
//				    $(".magnifying_glass").remove();
//				    $(".magnified_content").remove();
//				    $(".magnifying_lens").remove();
//				    loupe();
					$drop_husen = $(".husen.ui-draggable.ui-draggable-handle.ui-draggable-dragging");
					//$drop_husen.hide();

//					$("#loupe").remove();
//				    $(".magnifying_glass").remove();
//				    $(".magnified_content").remove();
//				    $(".magnifying_lens").remove();
//				    loupe();
//				    loupe_drop();
//				   // alert($drop_husen.length);
				    //$("#loupe").remove();
//					$body = $(document.body);
//					$body = $body.find("#loupe").remove();
//					$body = $body.find(".magnifying_glass").remove();
//					$body = $body.find("magnified_content").remove();
//					$body = $body.find("magnifying_lens").remove();
//				    $(".magnified_content").html($(document.body).html());
//				    $(".magnifying_glass").remove();
//				    $(".magnified_content").remove();
//				    $(".magnifying_lens").remove();
//				    loupe();
				    //loupe_drop();
					$("#loupe").remove();
				    $(".magnifying_glass").remove();
				    $(".magnified_content").remove();
				    $(".magnifying_lens").remove();
				    loupe();
				    loupe_drop();
				    
				    $hide_husen = $(".magnified_content").find(".husen.ui-draggable.ui-draggable-handle.ui-draggable-dragging");
				    $hide_husen.hide();
				    //alert($hide_husen.length);
				    
				    
		        	//$(".husen.ui-draggable.ui-draggable-handle.ui-draggable-dragging").eq(0).show();

				    $('.qa').contextmenu({
				        target: "#qa_context-menu"
				    });
				    var margin_top = $(".dropdown-menu").css("margin-top");	
//				    
//				    $("#loupe").remove();
//				    $(".magnifying_glass").remove();
//				    $(".magnified_content").remove();
//				    $(".magnifying_lens").remove();
//				    loupe();
				    
				},
				error: function(data)
				{
					$("#balloon").css("display","inline");
					$("#serif").text(server_error);
				}
			});
        }
    });    	
}

var speech_mode = "false";
function slime_speech()
{	
	speech_mode = "true";
	var serif = "";
    $(".qa").hover(function() {
    	if (speech_mode == "true")
    	{
//        	var id = $(this).attr("id");
    		var id = qa_id_for_contextmenu;
        	var s_id = $("#"+id).children(".a").attr("id");
        	//alert(s_id);
        	serif = $("#"+id).children(".a").text();
        	var path = "../speech/" + s_id + ".m4a";
        	$("#play_qa").attr("src",path);
	        document.getElementById("play_qa").play();    	
	    	$("#balloon").css("display","inline");
	    	$("#serif").text(serif);
	    	$("#slime").animate({width: '150px', height:'145px', top:'445px'}, 600);
	    	$("#slime").animate({width: '150px', height:'150px', top:'440px'}, 600);
	    	speech_mode = "false";
    	}
  }, function() {
  	$("#balloon").css("display","none");
	$("#serif").text("");

  });	


}

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

function husen_touroku(obj)
{
	if (window.event.keyCode == 13)
	{
		var tag_name = obj.innerHTML;
		jQuery.ajax({
			url: "../tag_touroku.html?tag_name=" + tag_name,
			dataType: "html",
			cache: false,
			success: function(data)
			{
				if (data == 'deplicate')
				{
					$("#balloon").css("display","inline");
					$("#serif").text("「" + tag_name + "」の付箋はすでにあるよ〜");					
				}
				else
				{
					$("#crystal_board").prepend('<div class="husen" contenteditable="true" onkeypress="javascript:husen_touroku(this);"></div>');
					$("#balloon").css("display","inline");
					$("#serif").text("付箋「" + tag_name + "」を作ったよ");
					$("#qa_input").focus();
				}
			},
			error: function(data)
			{
				$("#balloon").css("display","inline");
				$("#serif").text(server_error);
			}
		});		
		event.preventDefault();
	}
}

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
						$("#balloon").css("display","inline");
						$("#serif").text(data);
					}
				},
				error: function(data)
				{
					$("#balloon").css("display","inline");
					$("#serif").text(server_error);
				}
			});
			
		}
		else if (last == "a_input")
		{
			$("#qa_input").append(q_parts);						
			focus_last();
			id++;
			$("#balloon").css("display","none");
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
function register_qa_ajax ()
{
	var qa_input = $("#qa_input_hidden").html();
	var decoded = encodeURIComponent($("#qa_input_hidden").html(qa_input).text());
	var yomudake_flg = "";//$("#yomudake_flg").val();
	var reversible_flg = "";//$("#reversible_flg").val();
	var qa_husen = ($("#qa_husen").val());
	var qa_id = $("#qa_id").val();
//	alert(qa_id);
	jQuery.ajax({
		url: "../register_qa.html?qa_input_hidden=" + decoded + 
				"&yomudake_flg=" + yomudake_flg +
				"&reversible_flg=" + reversible_flg +
				"&qa_id=" + qa_id +
				"&qa_husen=" + qa_husen +
				"&husen_str=" + tag_search_conditions_uri,
		dataType: "json",
		cache: false,
		success: function(data)
		{				
			$("#qa_input_hidden").html("");
			$("#qa_input").html("");
			id = 2;
			$("#qa_area").html(data[0]);
			$("#qa_area_right").html(data[1]);
			$("#seitou_sum").html(data[2]);
			$("#seikai_sum").html(data[3]);
			$(".total_pages").html(data[4]);
			$("#qa_input").focus();

		    $('.qa').contextmenu({
		        target: "#qa_context-menu"
		    });
		    var margin_top = $(".dropdown-menu").css("margin-top");					
		},
		error: function(data)
		{
			$("#balloon").css("display","inline");
			$("#serif").text(server_error);
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
				$("#slime").animate({width: '150px', height:'405px', top:'185px'}, 200);
				$("#slime").animate({width: '150px', height:'150px', top:'440px'}, 200);
				document.getElementById("huseikai_se").play();
				
			}
		},
		error: function(data)
		{
			$("#balloon").css("display","inline");
			$("#serif").text(server_error);
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
    
    // 右矢印 次のページ
    if (window.event.keyCode == 39)
    {
    	var now_page = Number($("#page_left").text());
    	paging(now_page, "next");
    }
    // 左矢印　前のページ
    if (window.event.keyCode == 37)
    {
    	var now_page = Number($("#page_left").text());
    	paging(now_page, "prev");
//    	alert(now_page);
    }
}

var is_note_open = true;

function paging(page,next_or_prev)
{
//	alert(page);
//	alert(next_or_prev);
//	alert(is_note_open);
	
	var total_pages = Number($("#total_pages").text());

	// 最後のページで⇒を押した場合
	if (next_or_prev == "next" && is_note_open == true &&
		(total_pages == page || total_pages == (page + 1)))
	{
		// TODO ノートを閉じる
		return false;
	}
	// 最初のページで⇦を押した場合
	else if (next_or_prev == "prev" && page == 1)
	{
		is_note_open = false;
		// TODO ノートを閉じる
		$("#notebook").hide();
		$("#qa_area").hide();
		$("#qa_area_right").hide();
		$("#logo").hide();
		$("#score").hide();
		$(".note-line").hide();
		$(".page").hide();
		$("#notebook_close").show();
		$("#loupe").hide();
	    $(".magnifying_glass").hide();
	    $(".magnified_content").hide();
	    $(".magnifying_lens").hide();
	    $(".total_pages").hide();
	    $(".page_right").hide();
//	    $("#notebook").focus();
	    $("#crystal_board").css("left","-30px");
	    $("#crystal_board").css("top","200px");
//	    loupe();
//	    loupe_drop();
		return false;
	}
	else if (next_or_prev == "next")
	{
//		alert("a");
		if (is_note_open == false)
		{
			$("#notebook").show();
			$("#qa_area").show();
			$("#qa_area_right").show();
			$("#logo").show();
			$("#score").show();
			$(".note-line").show();
			$(".page").show();
			$("#notebook_close").hide();
			$("#loupe").show();
		    $(".magnifying_glass").show();
		    $(".magnified_content").show();
		    $(".magnifying_lens").show();
		    $(".total_pages").show();
		    $(".page_right").show();
		    $("#crystal_board").css("left","430px");
		    $("#crystal_board").css("top","200px");
		    is_note_open = true;
			return false;
		}
	}
	
	//TODO 検索条件
	jQuery.ajax({
		url: "../paging.html?now_page="+page+"&next_or_prev="+next_or_prev+"&husen_str="+tag_search_conditions_uri,
		dataType: "json",
		cache: false,
		success: function(data)
		{
			$("#qa_area").html(data[0]);
			$("#qa_area_right").html(data[1]);
			$("#seitou_sum").html(data[2]);
			$("#seikai_sum").html(data[3]);
			$(".total_pages").html(data[4]);
			
			if (next_or_prev == 'next')
			{
				$("#page_left").text(page + 2);
				$("#page_right").text(page + 3);
			}
			else
			{
				$("#page_left").text(page - 2);				
				$("#page_right").text(page - 1);
			}
		    $('.qa').contextmenu({
		        target: "#qa_context-menu"
		    });
		    var margin_top = $(".dropdown-menu").css("margin-top");					

//			alert("a");
			//alert($(document.body).html());

//		    $body_html = $(document.body).html();
//		    prompt("",$body_html);
//		    //alert($body_html);
//		    var qa_area_before = $("#qa_area").html();
//		    //alert(qa_area_before);
//		    var aa = $("#qa_area").live(function() {return $("this").html();});
//		    //alert(aa);
//		    var qa_area_after = data[0];
//		    //alert(qa_area_after);
////		    $body_html.replaceWith(function(){
////		        return $("<pre />", {html: $(this).html()});
////		    });
//		    
////		    $("span, p").each(function() {
////		        var text = $(this).text();
////		        text = text.replace("lollypops", "marshmellows");
////		        $(this).text(text);
////		    });
//		    
////		    $("#qa_area").each(function () {
////		    	alert("a");
////		    	$body_html($(document.body).html().replace(qa_area_before,qa_area_after));
////		    });
//		    //alert(document.getElementsByClassName("magnified_content").length);
//		    var lens2 = document.getElementsByClassName("magnified_content")[0].outerHTML;
//		    //alert(lens2);
//		    var re5 = new RegExp(lens2,"g");
//		    $body_html = $body_html.replace(re5, "");
//
//		    var re = new RegExp(qa_area_before,"g");
//		    $body_html = $body_html.replace(re, qa_area_after);
//		    
//		    var qa_area_before_right = $("#qa_area_right").html();
//		    var qa_area_after_right = data[1];
////		    alert(qa_area_after_right);
//		    var re4 = new RegExp(qa_area_before_right,"g");
//		    $body_html = $body_html.replace(re4, qa_area_after_right);
//
//		    
//		    var loupe = document.getElementById("loupe").outerHTML;
//		    //alert(loupe);
//		    var re2 = new RegExp(loupe,"g");
//		    $body_html = $body_html.replace(re2, "");
//
//
//		    var lens = document.getElementsByClassName("magnifying_glass")[0].outerHTML;
////		    alert(lens);
//		    var re3 = new RegExp(lens,"g");
//		    $body_html = $body_html.replace(re3, "");
//		    //$body_html.find('.magnifying_lens').first().remove();
//		    
//		    
//		    //$body_html.find('.magnified_content').first().remove();
//		    
//		    //alert($body_html);
//		    
//			$(".magnified_content").html($body_html);
//			//remove_loupe();
//			
////			loupe();
//			
////			var $magnifiedContent = $('<div class="magnified_content"></div>');
//
//			
////			$magnifiedContent.html($(document.body).html());			
//			//alert(data);
////			$("#qa_input").html(data);
////			$("#qa_id").val(qa_id);
		   // alert($("#loupe").attr("id"));
//		    $body_html = $(document.body).html();
//		    var glass = document.getElementsByClassName("magnifying_glass")[0].outerHTML;
//		    var reg = new RegExp(glass,"g");
//		    $body_html = $body_html.replace(reg, "");		    
//		    $(".magnified_content").html($body_html);
			$drop_husen = $(".husen.ui-draggable.ui-draggable-handle.ui-draggable-dragging");
			$drop_husen.hide();
//		    $hide_husen = $(".magnified_content").find(".husen.ui-draggable.ui-draggable-handle.ui-draggable-dragging");
//		    $hide_husen.hide();

			$("#loupe").remove();
		    $(".magnifying_glass").remove();
		    $(".magnified_content").remove();
		    $(".magnifying_lens").remove();
		    loupe();
		    $drop_husen.show();
		    loupe_drop();
		},
		error: function(data)
		{
			$("#balloon").css("display","inline");
			$("#serif").text(server_error);
		}
	});
}


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
//	$magnifiedContent.html($(document.body).not('.husen').html());
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

function slime_speak()
{	
	$("#slime").animate({width: '150px', height:'145px', top:'445px'}, 600);
	$("#slime").animate({width: '150px', height:'150px', top:'440px'}, 600);
	jQuery.ajax({
		url: "../serif.html?args_num=0",
		dataType: "html",
		cache: false,
		success: function(data)
		{					
			$("#balloon").css("display","inline");
			$("#serif").text(data);
			//sleep(5000);
			//$("#balloon").css("display","none");			
		},
		error: function(data)
		{
			$("#balloon").css("display","inline");
			$("#serif").text(server_error);
		}
	});
}

function sleep(milliseconds) {
	  var start = new Date().getTime();
	  for (var i = 0; i < 1e7; i++) {
	    if ((new Date().getTime() - start) > milliseconds){
	      break;
	    }
	  }
}

function edit_qa(q_obj)
{
	var qa_id = $(q_obj).parent().attr('id');
	if(q_obj == null)
	{
		qa_id = qa_id_for_contextmenu;
	}
	//alert(qa_id);
	jQuery.ajax({
		url: "../edit_qa.html?qa_id="+qa_id,
		dataType: "html",
		cache: false,
		success: function(data)
		{
			$("#qa_input").html(data);
			$("#qa_id").val(qa_id);
		},
		error: function(data)
		{
			$("#balloon").css("display","inline");
			$("#serif").text(server_error);
		}
	});
}


function show_red() {
	$(".a").css("opacity","1");
}

function hide_red()
{
	$(".a").css("opacity","0");
}

function reset_red()
{
    for(i = 0; i < document.getElementsByClassName("a").length; i++)
    {
    	var attr = document.getElementsByClassName("a")[i].getAttribute('onmouseout');
    	if (attr == null) {
    		document.getElementsByClassName("a")[i].style.opacity = 1;
    	}
    }

    for(i = 0; i < document.getElementsByClassName("a").length; i++)
    {
    	var attr = document.getElementsByClassName("a")[i].getAttribute('onmouseout');
    	if (attr != null) {
    		document.getElementsByClassName("a")[i].style.opacity = 0;
    	}
    }
}

// 検索中の全QAを未正解の状態に戻す
function to_miseikai()
{
	var now_page_left = $("page_left").text();
	
	jQuery.ajax({
		url: "../to_miseikai.html?husen_names=" + tag_search_conditions_uri + "&now_page_left="+now_page_left,
		dataType: "json",
		cache: false,
		success: function(data)
		{
			$("#qa_area").html(data[0]);
			$("#qa_area_right").html(data[1]);
			$("#seitou_sum").html(data[2]);
			$("#seikai_sum").html(data[3]);
			$(".total_pages").html(data[4]);
			
		    $("#loupe").remove();
		    $(".magnifying_glass").remove();
		    $(".magnified_content").remove();
		    $(".magnifying_lens").remove();
		    loupe();			
		},
		error: function(data)
		{
			$("#balloon").css("display","inline");
			$("#serif").text(server_error);
		}
	});
}

// ランダムなタイミングで勝手にしゃべりだす
function doSomething() {
	
	if($("#balloon").css("display") == "none")
	{
		slime_speak();
		setTimeout(function(){
			$("#balloon").css("display","none");
			$("#serif").text("");		
		},5000);
	}
}

(function loop() {
    var rand = Math.round(Math.random() * (100000 - 500)) + 500;
    setTimeout(function() {
            doSomething();
            loop();  
    }, rand);
}());

//動作不良
function countLines(id) {
	  var divHeight = document.getElementById(id).offsetHeight;
	  alert(divHeight);
	  var spanHeight = document.getElementsByClassName("qa")[0].offsetHeight;
	  //alert(divHeight / spanHeight + 1);
	  var lineHeight = parseInt(document.getElementsByClassName("qa")[0].style.lineHeight);
	  var lines = divHeight / lineHeight;
	  alert("Lines: " + lines);
}