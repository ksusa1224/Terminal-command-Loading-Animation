// グローバル変数
var server_error = "GORORON堂のサーバーが混みあってるみたい";
var tag_search_conditions_uri = "";
var pulun = "false";
var qa_id_for_contextmenu = "";
var qa_husen_global = "";
var dragged_husen_left = 0;
var refresh_by_date = "";
var tag_id_for_contextmenu = "";
var firstTime = localStorage.getItem("first_time");
var qa_husen_junban_card = 1;

function body_load()
{	
	// Windows対策
	if (window.navigator.userAgent.indexOf("Windows")!= -1)
	{
		$(".note-line").css("height","18.5px");
		$("#qa_area").css("height","486px");
		$("#lines-left").append("<tr><td class='note-line'></td></tr>");
		$("#qa_area_right").css("height","486px");
		$("#lines-right").append("<tr><td class='note-line'></td></tr>");
		$(".husen").css("height","25px");
	}	
	
    $('#note_area').contextmenu({
        target: "#note_context-menu"
    });
//    $('.qa').contextmenu({
//        target: "#qa_context-menu"
//    });
    $('.date').contextmenu({
        target: "#date_context-menu"
    });
    $('#crystal_board').contextmenu({
        target: "#husen_board_context-menu"
//	$("#husen_custom").click();
	//    event.preventDefault();
    });
    $('.husen').not('.blue').contextmenu({
        target: "#husen_context-menu"
    });
//    $("#husen_board_context-menu").css("z-index","9999999999");

    refresh();

//	$("#serif").on("click", function () { // click event
//		$("#serif").hide();
//		$("#baloon").hide();
//		$("#balloon").css("z-index","99998");
//		$("#serif").css("z-index","99999");
//		
//	});	
    
    var query = window.location.href.split("/")[4].split("?")[1];
//    alert(window.location.href.split("/")[4].split("?")[1]);
    if (query == "plan=open" || query == "plan=open#")
    {
    	show_plan_modal();
    	$("#plan_open").click();
    }
    
    $("#popup3").draggable();    
    
    var account = window.location.href.split("/")[3];
    if (account == 'sample')
    {
    	if(firstTime) {
    		alert("SAMPLEアカウントは、セッションが切れると最初の状態に戻ります。");
    	    localStorage.setItem("first_time","1");
    	    $.ajax({
    	        url: '../sample.html',
    			dataType: "html",
    	        type: 'GET',
    	        success: function(data) {
    	        	PopupCenter('../manual/index.html', '暗記ノート 使い方', 650, 450);
    	        }
    	    });
//    	    return false;
    	    //$("#manual").click();  	    	
    	}
//	    show_tutorial_modal();
//	    $("#tutorial_link").click();
//	    $("#popup2").draggable();
    }
    
    $(".dropdown-menu").css("margin-top", "-15px");
    $(".dropdown-menu").css("opacity", "0.9");	
	
	$( "#sortable" ).sortable();
    $( "#sortable" ).disableSelection();    
    
	show_husen_modal();
	show_plan_modal();
	
	$("#qa_input")
	  .focusout(function() {
		$("#blue_pen").removeClass("rotate_pen");		
		$("#red_pen").removeClass("rotate_pen");		
	  })
	  .blur(function() {
	  });
	
	function touchHandler(event) {
	    var touch = event.changedTouches[0];

	    var simulatedEvent = document.createEvent("MouseEvent");
	        simulatedEvent.initMouseEvent({
	        touchstart: "mousedown",
	        touchmove: "mousemove",
	        touchend: "mouseup"
	    }[event.type], true, true, window, 1,
	        touch.screenX, touch.screenY,
	        touch.clientX, touch.clientY, false,
	        false, false, false, 0, null);

	    touch.target.dispatchEvent(simulatedEvent);
	    event.preventDefault();
	}

	function init() {
	    document.addEventListener("touchstart", touchHandler, true);
	    document.addEventListener("touchmove", touchHandler, true);
	    document.addEventListener("touchend", touchHandler, true);
	    document.addEventListener("touchcancel", touchHandler, true);
	}	
	
	var iOS = /iPad|iPhone|iPod/.test(navigator.userAgent) && !window.MSStream;
	if (iOS == true)
	{
//		var $dummySeikai = $("<audio />", {
//			  id: "seikai_se",
//			  src: "../sound/seikai.mp3",
//			  preload: "none",
//			  width: "1",
//			  height: "2"
//			});
//		var $dummyHuseikai = $("<audio />", {
//			  id: "huseikai_se",
//			  src: "../sound/huseikai2.mp3",
//			  preload: "none",
//			  width: "1",
//			  height: "2"
//			});
//		$(".a").on("click", function() {
//			//alert(this.style.opacity);
//			if (this.style.opacity == 1)
//			{
//			  var url = $(this).data("stream-url");
//			  $dummyHuseikai.attr("src", url);
//			  $dummyHuseikai.get(0).load(); // required if src changed after page load
//			  $dummyHuseikai.get(0).play();
//			}
//			else
//			{
//				  var url = $(this).data("stream-url");
//				  $dummySeikai.attr("src", url);
//				  $dummySeikai.get(0).load(); // required if src changed after page load
//				  $dummySeikai.get(0).play();		
//			}
//		});
	}
	
	
//	function initAudio() {
//	    var audio = new Audio('../sound/huseikai2.mp3');
//	    audio.addEventListener('play', function () {
//	        // When the audio is ready to play, immediately pause.
//	        audio.pause();
//	        audio.removeEventListener('play', arguments.callee, false);
//	    }, false);
//	    document.addEventListener('click', function () {
//	        // Start playing audio when the user clicks anywhere on the page,
//	        // to force Mobile Safari to load the audio.
//	        document.removeEventListener('click', arguments.callee, false);
//	        audio.play();
//	    }, false);
//	}	
//	document.getElementById("wood_table").ontouchmove = function(event){
////		alert("wood");
//	    event.preventDefault();
//	}	
	
//	var w = window.innerWidth;
//	alert(w);
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
//	    		event.preventDefault();
	            break;
	        default:
//	            alert('You have a strange Mouse!');
	    }
	});	
	$('.husen').not(".blue").mousedown(function(event) {
	    if (event.which == 3) {
	    	tag_id_for_contextmenu = this.id;
	    }
	});	

	
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
//    $( "#blue_pen" ).draggable();
//    $( "#red_pen" ).draggable();
    //$( "#qa_panel" ).draggable();
//    $( "#note_area" ).draggable();
    $( "#loupe" ).draggable();
    
    husen_draggable();
    
    var qa_husen_junban = 1;
    $('#husen_paste').droppable({
        accept:'.husen',
        drop: function(event,ui){
        	if ($(ui.draggable).hasClass("blue"))
        	{
        		return false;
        	}
//        	var scroll_left = $(document).scrollLeft();
//        	$(ui.draggable).css("position", "absolute");
//        	$(ui.draggable).css("left", "10px !important");
//        	$dragging = $(".husen.ui-draggable.ui-draggable-handle.ui-droppable.ui-draggable-dragging");
//        	var left = Number($dragging.css("left").replace(/px/g,""));
//        	left = left + scroll_left;
//        	$dragging.css("left", left);
        	var husen_name = $(ui.draggable).text();
        	$("#qa_husen").html("<span data-junban='"+ qa_husen_junban +"'>" + husen_name +"</span>");
        	qa_husen_global = qa_husen_global + $("#qa_husen").text();
        	//alert(qa_husen_global);
        	qa_husen_junban++;
        }
    });    

    $('#crystal_board').droppable({
        accept:'.husen',
        drop: function(event,ui){
        	// deleteを呼んでいるが、タグIDに空文字を指定しているのでタグは消さずにリフレッシュするだけ
			jQuery.ajax({
				url: "../husen_delete.html?tag_id=&owner_id=" + owner_id,
				dataType: "json",
				cache: false,
				success: function(data)
				{
					$("#husen_wrapper").html("");
					$("#husen_wrapper").prepend(data[0]);
				    $( ".husen" ).draggable({
				    	revert: 'true', 
				    	scroll: true,
				    	helper: 'clone',
						stop : function(e, ui){
					         $('.husen').draggable().data()["ui-draggable"].cancelHelperRemoval = true;
					         this.style.opacity=0;
					    },
						drag : function(e, ui){
					        this.style.opacity=0;
					    }    		
				    });    					
				},
				error: function(data)
				{
					$("#balloon").css("display","inline");
					$("#balloon").css("z-index","100002");
					$("#serif").css("z-index","100003");
					$("#serif").text(server_error);
				}
			});
        }
    });    

    $('#husen_paste').droppable({
        accept:'.husen',
        drop: function(event,ui){
        	if ($(ui.draggable).hasClass("blue"))
        	{
        		return false;
        	}
//        	var scroll_left = $(document).scrollLeft();
//        	$(ui.draggable).css("position", "absolute");
//        	$(ui.draggable).css("left", "10px !important");
//        	$dragging = $(".husen.ui-draggable.ui-draggable-handle.ui-droppable.ui-draggable-dragging");
//        	var left = Number($dragging.css("left").replace(/px/g,""));
//        	left = left + scroll_left;
//        	$dragging.css("left", left);
        	var husen_name = $(ui.draggable).text();
        	$("#qa_husen").html("<span data-junban='"+ qa_husen_junban +"'>" + husen_name +"</span>");
        	qa_husen_global = qa_husen_global + $("#qa_husen").text();
        	//alert(qa_husen_global);
        	qa_husen_junban++;
        }
    });    

    $('#slime').droppable({
        accept:'#erasor',
        drop: function(event,ui){
	    	$("#slime").animate({width: '500px', height:'150px', left:'0px'}, 50);
	    	$("#slime").animate({width: '150px', height:'150px', left:'0px'}, 300);
	    	$("#slime").animate({width: '150px', height:'150px', left:'870px'}, 1200);
	    	$("#erasor").css("left","1070px");
	    	$("#erasor").css("top","420px");
        }
    });    

    $('.husen').droppable({
        accept:'#erasor',
        drop: function(event,ui){
//        	alert(this.id);
        	var tag_id = this.id;
        	var tag_name = this.innerText;
        	if ($(this).hasClass("blue"))
        	{
				$("#balloon").css("display","inline");
				$("#balloon").css("z-index","100002");
				$("#serif").css("z-index","100003");
				$("#serif").text("青いふせんは特別なふせんだから、消せないよ〜");
        		tag_id = "";
        	}
        	else
        	{
	        	var ret = confirm("付箋「" + tag_name + "」を削除しますか？");
	        	if (ret == false)
	        	{
	        		tag_id = "";
	        	}
        	}
        	var url = window.location.href;
            owner_id = url.split('/')[3];        	
        	var url = window.location.href;
            owner_id = url.split('/')[3];        			
			jQuery.ajax({
				url: "../husen_delete.html?tag_id=" + tag_id + "&owner_id=" + owner_id,
				dataType: "json",
				cache: false,
				success: function(data)
				{
					$("#husen_wrapper").html("");
					$("#husen_wrapper").prepend(data[0]);
				    $( ".husen" ).draggable({
				    	revert: 'true', 
				    	scroll: true,
				    	helper: 'clone',
						stop : function(e, ui){
					         $('.husen').draggable().data()["ui-draggable"].cancelHelperRemoval = true;
					         this.style.opacity=0;
					    },
						drag : function(e, ui){
					        this.style.opacity=0;
					    }    		
				    });    					
				},
				error: function(data)
				{
					$("#balloon").css("display","inline");
					$("#balloon").css("z-index","100002");
					$("#serif").css("z-index","100003");
					$("#serif").text(server_error);
				}
			});
        }
    });    
    
    $('#erasor').droppable({
        accept:'.husen',
        drop: function(event,ui){
        	var tag_id = $(ui.draggable).attr("id");
        	var tag_name = $(ui.draggable).text();
        	if ($(ui.draggable).hasClass("blue"))
        	{
				$("#balloon").css("display","inline");
				$("#balloon").css("z-index","100002");
				$("#serif").css("z-index","100003");
				$("#serif").text("青いふせんは特別なふせんだから、消せないよ〜");
        		tag_id = "";
        	}
        	else
        	{
	        	var ret = confirm("付箋「" + tag_name + "」を削除しますか？");
	        	if (ret == false)
	        	{
	        		tag_id = "";
	        	}
        	}
        	var url = window.location.href;
            owner_id = url.split('/')[3];        	
			jQuery.ajax({
				url: "../husen_delete.html?tag_id=" + tag_id + "&owner_id=" + owner_id,
				dataType: "json",
				cache: false,
				success: function(data)
				{
					$("#husen_wrapper").html("");
					$("#husen_wrapper").prepend(data[0]);
				    $( ".husen" ).draggable({
				    	revert: 'true', 
				    	scroll: true,
				    	helper: 'clone',
						stop : function(e, ui){
					         $('.husen').draggable().data()["ui-draggable"].cancelHelperRemoval = true;
					         this.style.opacity=0;
					    },
						drag : function(e, ui){
					        this.style.opacity=0;
					    }    		
				    });    	
				    $("#" + tag_id).remove();
				},
				error: function(data)
				{
					$("#balloon").css("display","inline");
					$("#balloon").css("z-index","100002");
					$("#serif").css("z-index","100003");
					$("#serif").text(server_error);
				}
			});
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
                	var url = window.location.href;
                    owner_id = url.split('/')[3];        			
        			jQuery.ajax({
        				url: "../qa_delete.html?qa_id=" + qa_id +"&husen_str=" + tag_search_conditions_uri + "&owner_id=" + owner_id,
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

        					$('.qa').contextmenu({
        				        target: "#qa_context-menu"
        				    });
        				},
        				error: function(data)
        				{
        					$("#balloon").css("display","inline");
        					$("#balloon").css("z-index","100002");
        					$("#serif").css("z-index","100003");
        					$("#serif").text(server_error);
        				}
        			});
        		}
        		else
        		{
					$("#balloon").css("display","inline");
					$("#balloon").css("z-index","100002");
					$("#serif").css("z-index","100003");
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

    $('#qa_panel').droppable({
        accept:'.husen',
        drop: function(event,ui){
        	var id = $(ui.draggable).attr("id");
        	if ($(ui.draggable).hasClass("blue") && $(ui.draggable).text() != '読むだけ問題')
        	{
    			$("#balloon").css("display","inline");
    			$("#balloon").css("z-index","100002");
    			$("#serif").css("z-index","100003");
    			$("#serif").text("青いふせんは特別なふせんだから、Q&Aに貼ることはできないよ〜");  
    			$(ui.draggable).attr( 'style', 'position: relative;' );
    			$(".husen.blue.ui-draggable.ui-draggable-handle.ui-droppable.ui-draggable-dragging").remove();
//    			$(".husen.blue.ui-draggable.ui-draggable-handle.ui-droppable.ui-draggable-dragging").css('position','relative');
//    			$(ui.draggable).remove();
//    			$(ui.draggable).removeAttr('style');
        	}
        },
    	out: function(event, ui){
			$("#balloon").css("display","none");
			$("#serif").text("");   			    		
    	}
    });
    
    loupe_drop();
        
	$('#play_qa').on('ended', function() {
	  	$("#balloon").css("display","none");
		$("#serif").text("");		
	});
	
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

// 初期表示時およびAjax通信の後に呼び出す
function refresh()
{
	// スマホの場合
	if( /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) ) {
		 // クリスタルボードと付箋と虫眼鏡を表示しない
		 $("#crystal_board").hide();
		 $(".husen").hide();
		 $("#loupe").hide();
		 // テーブルを使わずに罫線を引く
		 $(".qa").css("border-top","1px solid #d9d9d9");
		 $(".qa").css("font-size","18px");
		 $(".qa").css("line-height","24.5px");
		 $(".date").css("border-top","1px solid #d9d9d9");
		 $(".date").css("font-size","18px");
		 $(".date").css("line-height","24.5px");
		 $(".note-line").css("display","none");
		 $("#note_menu_context").css("margin-top", "-700px");
		 $("#note_menu_context").css("font-size", "16px");
		 $("#husen_search_mobile").show();
		 $("#mobile_loupe").show();
		 
  		faint_to_clear();
		clear_to_faint();
		faint_to_clear_search();
		clear_to_faint_search();
		 
		 var device_width = (window.innerWidth > 0) ? window.innerWidth : screen.width;

		 // iPhone6 Plus
		 if (device_width == 414)
		 {
			 document.querySelector("meta[name=viewport]").setAttribute(
			          'content', 
			          'width=1275px, initial-scale=0.9, maximum-scale=0.9, minimum-scale=0.9, user-scalable=no');	 
		 }
		 // iPad(縦)
		 if (device_width == 768)
	     {
			 document.querySelector("meta[name=viewport]").setAttribute(
			          'content', 
			          'width=1275px, initial-scale=1.6, maximum-scale=1.6, minimum-scale=1.6, user-scalable=no');	 
	     }
		 // iPad(横)
		 if (device_width == 1024)
	     {
			 document.querySelector("meta[name=viewport]").setAttribute(
			          'content', 
			          'width=1275px, initial-scale=0.5, maximum-scale=0.5, minimum-scale=1.15, user-scalable=no');	 
	     }		 
	}
	else
	{
		if (window.screen.availWidth < 1280)
		{
			 $("body").css("overflow","scroll");		
		}
		else
		{
			 $("body").css("overflow","hidden");					
		}
	}
			
	if(Number($("#page_left").text()) == 1)
	{
		$("#prev_link").hide();
	}
	else
	{
		$("#prev_link").show();		
	}

	if(Number($("#page_right").text()) >= Number($("#total_pages").text()))
	{
		$("#next_link").hide();
	}
	else
	{
		$("#next_link").show();		
	}

	if (window.screen.availWidth < 1280)
	{
	}
	
	$('.qa').contextmenu({
	    target: "#qa_context-menu"
	});
	var margin_top = $(".dropdown-menu").css("margin-top");					
	
	if ($("#edit_mode").attr("src") == "../img/register_mode.png")
	{
		$("#edit_mode").attr("data-mode","register");
	}	
	else
	{
		$("#edit_mode").attr("data-mode","edit");		
	}
	
	$('.date').mousedown(function(event) {
	    if (event.which == 3) {
	    	refresh_by_date = this.id;
	    	$("#refesh_by_date").text($("#refesh_by_date").text().replace(/この日/g,refresh_by_date));
	    	$("#refesh_by_date2").text($("#refesh_by_date2").text().replace(/この日/g,refresh_by_date));
	    }
	});	
	
	$('.qa').mousedown(function(event) {
	    if (event.which == 3) {
			qa_id_for_contextmenu = this.id;
			var question = "\"" + $("#"+qa_id_for_contextmenu).children(".q").text() + "\"の例文を表示";
			var answer = "\"" + $("#"+qa_id_for_contextmenu).children(".a").text() + "\"の例文を表示";
			var q_lang = $("#"+qa_id_for_contextmenu).children(".q").data("language");
			var a_lang = $("#"+qa_id_for_contextmenu).children(".a").data("language");			
			$("#q_example").text(question);
			$("#a_example").text(answer);
			$("#q_image_search").text(question.replace(/の例文を表示/g, "の画像を検索"));
			$("#a_image_search").text(answer.replace(/の例文を表示/g, "の画像を検索"));
			if (q_lang == "日本語")
			{
				$("#q_example_menu").hide();
			}
			if (a_lang == "日本語")
			{
				$("#a_example_menu").hide();
			}
			if (q_lang == "日本語" && a_lang == "日本語")
			{
				$("#example_menu_devider").hide();				
			}
	    }
	});	

	$("#note_menu_context").draggable();
}

function faint_to_clear()
{
	$(".husen_card.faint").click(function(){
		$(this).removeClass("faint");
		$(this).addClass("clear");		
    	var husen_span = "<span data-junban='"+ qa_husen_junban_card +"'>" + $(this).text() +"</span>";
    	qa_husen_global = qa_husen_global + husen_span;
    	clear_to_faint();
	})
}

function clear_to_faint()
{
	$(".husen_card.clear").click(function(){
		$(this).removeClass("clear");
		$(this).addClass("faint");		
    	var husen_span = "<span data-junban='"+ qa_husen_junban_card +"'>" + $(this).text() +"</span>";
    	var replace = husen_span;
    	var re = new RegExp(replace,"g");
    	qa_husen_global = qa_husen_global.replace(re, "");
    	faint_to_clear();
	});
}

function faint_to_clear_search()
{
	$(".husen_search.faint").click(function(){
		$(this).removeClass("faint");
		$(this).addClass("clear");		
    	husen_names.push($(this).text());
    	mobile_search();
    	clear_to_faint_search();
	})
}

function clear_to_faint_search()
{
	$(".husen_search.clear").click(function(){
		$(this).removeClass("clear");
		$(this).addClass("faint");
//		removeA(husen_names, $(this).text());
		husen_names.remove($(this).text());
		mobile_search();
    	faint_to_clear_search();
	})
}

Array.prototype.remove = function() {
    var what, a = arguments, L = a.length, ax;
    while (L && this.length) {
        what = a[--L];
        while ((ax = this.indexOf(what)) !== -1) {
            this.splice(ax, 1);
        }
    }
    return this;
};

function removeA(arr) {
    var what, a = arguments, L = a.length, ax;
    while (L > 1 && arr.length) {
        what = a[--L];
        while ((ax= arr.indexOf(what)) !== -1) {
            arr.splice(ax, 1);
        }
    }
    return arr;
}

function mobile_search()
{
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
	var url = window.location.href;
	owner_id = url.split('/')[3];
	tag_search_conditions_uri = husens_str;
	jQuery.ajax({
		url: "../tag_search.html?husen_names=" + husens_str + "&owner_id=" + owner_id,
		dataType: "json",
		cache: false,
		success: function(data)
		{
			$("#qa_area").html(data[0]);
			$("#qa_area_right").html(data[1]);
			$("#seitou_sum").html(data[2]);
			$("#seikai_sum").html(data[3]);
			$(".total_pages").html(data[4]);
		    refresh();			
		},
		error: function(data)
		{
			$("#balloon").css("display","inline");
			$("#balloon").css("z-index","100002");
			$("#serif").css("z-index","100003");
			$("#serif").text(server_error);
		}
	});
}

function husen_draggable()
{
    $( ".husen" ).draggable({
    	revert: 'true', 
    	//appendTo: 'body',
    	//containment: 'window',
    	scroll: true,
    	helper: 'clone',
		stop : function(e, ui){
	        var scroll_left = $(document).scrollLeft();
	        $(ui.draggable).css("position", "absolute");
	        $(ui.draggable).css("left", "10px !important");
	        $dragging = $(".husen.ui-draggable.ui-draggable-handle.ui-droppable.ui-draggable-dragging");
	        var left = Number($dragging.css("left").replace(/px/g,""));
	        left = left + scroll_left;
	        $dragging.css("left", left);
//	         alert("stop");
	         $('.husen').draggable().data()["ui-draggable"].cancelHelperRemoval = true;
	         //this.style.opacity=0;
//	         alert($(ui.helper));
//	         $draged_husen = $(ui.helper);
//	         alert($draged_husen.attr("class"));
	    },
		drag : function(e, ui){
	        this.style.opacity=0;
//	        var scroll_left = $(document).scrollLeft();
//	        $(ui.draggable).css("position", "absolute");
//	        $(ui.draggable).css("left", "10px !important");
//	        $dragging = $(".husen.ui-draggable.ui-draggable-handle.ui-droppable.ui-draggable-dragging");
//	        var left = Number($dragging.css("left").replace(/px/g,""));
//	        left = left + scroll_left;
//	        $dragging.css("left", left);
	    },
	    scroll: 'true'
    });
	
}

//$(function () {
//    $("#husen_wrapper").sortable({
//        items: '> .husen'
//    });
//});

function show_husen_modal()
{
	var appendthis =  ("<div class='modal-overlay js-modal-close'></div>");
	
	$('a[data-modal-id]').click(function(e) {
		e.preventDefault();
    $("body").append(appendthis);
    $(".modal-overlay").fadeTo(500, 0.7);
    //$(".js-modalbox").fadeIn(500);
		var modalBox = $(this).attr('data-modal-id');
		$('#'+modalBox).fadeIn($(this).data());
	});  	  
  
	$(".js-modal-close, .modal-overlay").click(function() {
	    $(".modal-box, .modal-overlay").fadeOut(500, function() {
	        $(".modal-overlay").remove();
	    });
	 
	});
	 
	$(window).resize(function() {
	    $(".modal-box").css({
	        top: ($(window).height() - $(".modal-box").outerHeight()) / 2,
	        left: ($(window).width() - $(".modal-box").outerWidth()) / 2
	    });
	});
	 
	$(window).resize();
}

function show_tutorial_modal()
{
	var appendthis =  ("<div class='modal-overlay js-modal-close'></div>");
	
	$('a[data-modal-id]').click(function(e) {
		e.preventDefault();
    $("body").append(appendthis);
    $(".modal-overlay").fadeTo(500, 0.7);
    //$(".js-modalbox").fadeIn(500);
		var modalBox = $(this).attr('data-modal-id');
		$('#'+modalBox).fadeIn($(this).data());
	});  	  
  
	$(".js-modal-close, .modal-overlay").click(function() {
	    $(".modal-box, .modal-overlay").fadeOut(500, function() {
	        $(".modal-overlay").remove();
	    });
	 
	});
	 
	$(window).resize(function() {
	    $(".modal-box").css({
	        top: ($(window).height() - $(".modal-box").outerHeight()) / 2,
	        left: ($(window).width() - $(".modal-box").outerWidth()) / 2
	    });
	});
	 
	$(window).resize();
}

function show_plan_modal()
{
	var appendthis =  ("<div class='modal-overlay js-modal-close'></div>");
	
	$('a[data-modal-id]').click(function(e) {
		e.preventDefault();
    $("body").append(appendthis);
    $(".modal-overlay").fadeTo(500, 0.7);
    //$(".js-modalbox").fadeIn(500);
		var modalBox = $(this).attr('data-modal-id');
		$('#'+modalBox).fadeIn($(this).data());
	});  	  
  
	$(".js-modal-close, .modal-overlay").click(function() {
	    $(".modal-box, .modal-overlay").fadeOut(500, function() {
	        $(".modal-overlay").remove();
	    });
	 
	});
	 
	$(window).resize(function() {
	    $(".modal-box").css({
	        top: ($(window).height() - $(".modal-box").outerHeight()) / 2,
	        left: ($(window).width() - $(".modal-box").outerWidth()) / 2
	    });
	});
	 
	$(window).resize();
}

function delete_qa()
{
	var qa_id = qa_id_for_contextmenu;
	if (qa_id != "")
	{
    	var url = window.location.href;
        owner_id = url.split('/')[3];        			
		jQuery.ajax({
			url: "../qa_delete.html?qa_id=" + qa_id +"&husen_str=" + tag_search_conditions_uri + "&owner_id=" + owner_id,
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
				refresh();
			},
			error: function(data)
			{
				$("#balloon").css("display","inline");
				$("#balloon").css("z-index","100002");
				$("#serif").css("z-index","100003");
				$("#serif").text(server_error);
			}
		});
	}
}

function change_opacity (seitou) {
	seitou.mouseout(function(){ return false;});
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


function slime_pulupulu()
{
	$("#slime").animate({width: '150px', height:'145px', top:'445px'}, 600);
	$("#slime").animate({width: '150px', height:'150px', top:'440px'}, 600);
	if (pulun == "true")
	{
//		slime_pulupulu();
	}
}


var husen_names = [];
function loupe_drop()
{
    $('#loupe').droppable({
        accept:'.husen',
        out: function (event, ui) {
//        	var scroll_left = $(document).scrollLeft();
//        	$(ui.draggable).css("position", "absolute");
//        	$(ui.draggable).css("left", "10px !important");
//        	$dragging = $(".husen.ui-draggable.ui-draggable-handle.ui-droppable.ui-draggable-dragging");
//        	var left = Number($dragging.css("left").replace(/px/g,""));
//        	left = left + scroll_left;
//        	$dragging.css("left", left);
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
        	var url = window.location.href;
        	owner_id = url.split('/')[3];
        	tag_search_conditions_uri = husens_str;
			jQuery.ajax({
				url: "../tag_search.html?husen_names=" + husens_str + "&owner_id=" + owner_id,
				dataType: "json",
				cache: false,
				success: function(data)
				{
					$("#qa_area").html(data[0]);
					$("#qa_area_right").html(data[1]);
					$("#seitou_sum").html(data[2]);
					$("#seikai_sum").html(data[3]);
					$(".total_pages").html(data[4]);
				    refresh();

//					$drop_husen = $(".husen.ui-draggable.ui-draggable-handle.ui-draggable-dragging");
//					$drop_husen.hide();

//				    $("#loupe").remove();
//				    $(".magnifying_glass").remove();
//				    $(".magnified_content").remove();
//				    $(".magnifying_lens").remove();
//				    loupe();
//				    $("#loupe").remove();
////				    $(".magnified_content").html($(document.body));
//				    $(".magnifying_glass").remove();
//				    $(".magnified_content").remove();
//				    $(".magnifying_lens").remove();
//				    loupe();
//				    loupe_drop();

//				    $hide_husen = $(".magnified_content").find(".husen.ui-draggable.ui-draggable-handle.ui-draggable-dragging");
//				    $hide_husen.hide();
					
//				    $body_html = $("document.body").html();
//				    $(".magnified_content").html($body_html);
//		        	$(".husen.ui-draggable.ui-draggable-handle.ui-draggable-dragging").eq(0).show();
					
				},
				error: function(data)
				{
					$("#balloon").css("display","inline");
					$("#balloon").css("z-index","100002");
					$("#serif").css("z-index","100003");
					$("#serif").text(server_error);
				}
			});
        },
        drop: function(event,ui){
//        	var scroll_left = $(document).scrollLeft();
//        	$(ui.draggable).css("position", "absolute");
//        	$(ui.draggable).css("left", "10px !important");
//        	$dragging = $(".husen.ui-draggable.ui-draggable-handle.ui-droppable.ui-draggable-dragging");
//        	var left = Number($dragging.css("left").replace(/px/g,""));
//        	left = left + scroll_left;
//        	$dragging.css("left", left);

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
        	var url = window.location.href;
        	owner_id = url.split('/')[3];
			jQuery.ajax({
				url: "../tag_search.html?husen_names=" + husens_str + "&owner_id=" + owner_id,
				dataType: "json",
				cache: false,
				success: function(data)
				{
					$("#qa_area").html(data[0]);
					$("#qa_area_right").html(data[1]);
					$("#seitou_sum").html(data[2]);
					$("#seikai_sum").html(data[3]);
					$(".total_pages").html(data[4]);
				    refresh();

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
//					$("#loupe").remove();
//				    $(".magnifying_glass").remove();
//				    $(".magnified_content").remove();
//				    $(".magnifying_lens").remove();
//				    loupe();
//				    loupe_drop();
				    
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
					$("#balloon").css("z-index","100002");
					$("#serif").css("z-index","100003");
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
//    $(".qa").hover(function() {
    	if (speech_mode == "true")
    	{
    		var id = qa_id_for_contextmenu;
        	var s_id = $("#"+id).children(".a").attr("id");
        	serif = $("#"+id).children(".a").text();
        	var path = "../speech/" + id + "/" + s_id + ".m4a";
	        path = "../speech/" + s_id + ".m4a";
        	$.ajax({
        	    url:path,
        	    type:'HEAD',
        	    error: function()
        	    {
        	        path = "../speech/" + s_id + ".m4a";
        	    },
        	    success: function()
        	    {
        	        //file exists
        	    }
        	});      	
//        	alert(path);
        	$("#play_qa").attr("src",path);
	        document.getElementById("play_qa").play();    	
	    	$("#balloon").css("display","inline");
	    	$("#balloon").css("z-index","100002");
	    	$("#serif").css("z-index","100003");
	    	$("#serif").text(serif);
	    	$("#slime").animate({width: '150px', height:'145px', top:'445px'}, 600);
	    	$("#slime").animate({width: '150px', height:'150px', top:'440px'}, 600);
	    	speech_mode = "false";
    	}
//  }, function() {
//  	$("#balloon").css("display","none");
//	$("#serif").text("");
//
//  });	


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
    	var url = window.location.href;
        owner_id = url.split('/')[3];        			
		jQuery.ajax({
			url: "../tag_touroku.html?tag_name=" + tag_name + "&owner_id=" + owner_id,
			dataType: "json",
			cache: false,
			success: function(data)
			{
				if (data == 'deplicate')
				{
					$("#balloon").css("display","inline");
					$("#balloon").css("z-index","100002");
					$("#serif").css("z-index","100003");
					$("#serif").text("「" + tag_name + "」の付箋はすでにあるよ〜");					
				}
				else
				{
					$("#husen_wrapper").prepend(data[0]);
//					$("#husen_wrapper:first-child").removeAttr("contenteditable");
//					$("#husen_wrapper:first-child").removeAttr("onkeypress");
//					$("#husen_wrapper").prepend('<div class="husen" contenteditable="true" onkeypress="javascript:husen_touroku(this);"></div>');
					$("#balloon").css("display","inline");
					$("#balloon").css("z-index","100002");
					$("#serif").css("z-index","100003");
					$("#serif").text("付箋「" + tag_name + "」を作ったよ");
					$("#qa_input").focus();
				}
				refresh();
			},
			error: function(data)
			{
				$("#balloon").css("display","inline");
				$("#balloon").css("z-index","100002");
				$("#serif").css("z-index","100003");
				$("#serif").css("z-index","100003");
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
		if (is_ie_or_edge() == false)
		{
			$("#qa_input").append(a_parts);	
		}
		focus_last();
		id++;
		$("#blue_pen").addClass("rotate_pen");		
	}
	// QA登録ショートカット
	if (window.event.keyCode == 13 && window.event.shiftKey == true)
	{
		var qa_mojisu = $.trim($("#qa_input").text()).length;
		if (qa_mojisu > 100)
		{
			alert("QAの文字数は100文字までにしてください。");
			return false;
		}
		copy_to_hidden();
		register_qa_ajax();
		event.preventDefault();
	}
	else if (window.event.keyCode == 13)
	{
		if (last == "q_input")
		{
			$("#qa_input").append(a_parts);	
//			placeCaretAtEnd($("#qa_input span:last"));
			focus_last();
			id++;
			if (is_ie_or_edge() == false)
			{
				$("#red_pen").removeClass("rotate_pen");		
				$("#blue_pen").addClass("rotate_pen");		
			}
			else
			{
				$("#blue_pen").removeClass("rotate_pen");		
				$("#red_pen").addClass("rotate_pen");		
				$("#balloon").css("display","none");
				$("#serif").text("");				
			}
						
        	var url = window.location.href;
            owner_id = url.split('/')[3];        			
			jQuery.ajax({
				url: "../serif.html?a=" + last_a + "&owner_id=" + owner_id,
				dataType: "html",
				cache: false,
				success: function(data)
				{					
					if (last_a.trim() != '\u200B')
					{
						$("#balloon").css("display","inline");
						$("#balloon").css("z-index","100002");
						$("#serif").css("z-index","100003");
						$("#serif").text(data);
						setTimeout(function(){
							$("#balloon").css("display","none");
							$("#serif").text("");		
							$("#balloon").css("z-index","99998");
							$("#serif").css("z-index","99999");
						},5000);
					}
				},
				error: function(data)
				{
					$("#balloon").css("display","inline");
					$("#balloon").css("z-index","100002");
					$("#serif").css("z-index","100003");
					$("#serif").text(server_error);
				}
			});
			
		}
		else if (last == "a_input")
		{
			$("#qa_input").append(q_parts);						
			//			placeCaretAtEnd($("#qa_input span:last"));
			focus_last();
			id++;
			if (is_ie_or_edge() == false)
			{
				$("#blue_pen").removeClass("rotate_pen");		
				$("#red_pen").addClass("rotate_pen");		
				$("#balloon").css("display","none");
				$("#serif").text("");
			}
			else
			{
				$("#red_pen").removeClass("rotate_pen");		
				$("#blue_pen").addClass("rotate_pen");						
			}
		}		
		event.preventDefault();
	}
}

function is_ie_or_edge()
{
    var ua = window.navigator.userAgent;

    var msie = ua.indexOf('MSIE ');
    if (msie > 0) {
    	return true;
    }

    var trident = ua.indexOf('Trident/');
    if (trident > 0) {
    	return true;
    }

    var edge = ua.indexOf('Edge/');
    if (edge > 0) {
    	return true;
    }

    // other browser
    return false;
}

//最後の要素にカーソルを移動する
function focus_last(){
	var node = document.querySelector("#qa_input");
	node.focus();
	var textNode = null;
	textNode = node.lastChild;
//	alert(textNode);
	//textNode = $("#qa_input span:last");
	var caret = 0; // insert caret after the 10th character say
	var range = document.createRange();
	range.setStart(textNode, caret);
	range.setEnd(textNode, caret);
	var sel = window.getSelection();
	sel.removeAllRanges();
	sel.addRange(range);
	
	//$("#1").focus();
}

function placeCaretAtEnd(el) {
    el.focus();
    if (typeof window.getSelection != "undefined"
            && typeof document.createRange != "undefined") {
        var range = document.createRange();
        range.selectNodeContents(el);
        range.collapse(false);
        var sel = window.getSelection();
        sel.removeAllRanges();
        sel.addRange(range);
    } else if (typeof document.body.createTextRange != "undefined") {
        var textRange = document.body.createTextRange();
        textRange.moveToElementText(el);
        textRange.collapse(false);
        textRange.select();
    }
}

// 青ペン押下
function append_blue()
{
	var q_parts = "<span class='q_input' id='" + id + "'>&#8203;</span>";
	$("#qa_input").append(q_parts);						
	focus_last();
	id++;
}

//赤ペン押下
function append_red()
{
	var a_parts = "<span class='a_input' id='" + id + "'>&#8203;</span>";
	$("#qa_input").append(a_parts);
//	this.selectionStart = this.selectionEnd = this.value.length;
	focus_last();
	id++;
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
	var mode = $("#edit_mode").data("mode");
	var qa_mojisu = $.trim($("#qa_input").text()).length;
	if (qa_mojisu > 100)
	{
		alert("QAの文字数は100文字までにしてください。");
		return false;
	}

	var qa_input = $("#qa_input_hidden").html();
	var decoded = encodeURIComponent($("#qa_input_hidden").html(qa_input).text());
	var yomudake_flg = "";//$("#yomudake_flg").val();
	var reversible_flg = "";//$("#reversible_flg").val();
	var qa_husen = qa_husen_global;
	var qa_id = $("#qa_id").val();
	var url = window.location.href;
    owner_id = url.split('/')[3];        			
	jQuery.ajax({
		url: "../register_qa.html?qa_input_hidden=" + decoded + 
				"&yomudake_flg=" + yomudake_flg +
				"&reversible_flg=" + reversible_flg +
				"&qa_id=" + qa_id +
				"&qa_husen=" + qa_husen +
				"&husen_str=" + tag_search_conditions_uri +
				"&owner_id=" + owner_id +
				"&mode=" + mode,
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
				if (data[5] == "overlimit")
				{
					alert("無料会員の登録問題数制限を超えました。さらに登録するには、プレミアムユーザーにアップグレードさい。");
				}
			

				if (mode == 'edit') 
				{
					change_mode();
				}
			refresh();
			$("#blue_pen").addClass("rotate_pen");
			$("#red_pen").removeClass("rotate_pen");

		},
		error: function(data)
		{
			$("#balloon").css("display","inline");
			$("#balloon").css("z-index","100002");
			$("#serif").css("z-index","100003");
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
	
	var url = window.location.href;
    owner_id = url.split('/')[3];        			
	jQuery.ajax({
		url: "../change_seitou_color.html?qa_id="+qa_id+"&s_id="+s_id+"&is_seikai_now="+is_seikai_now + "&owner_id=" + owner_id,
		dataType: "html",
		cache: false,
		success: function(data)
		{			
			if (data == '0')
			{
				$(obj).css("opacity","1");
				$(obj).prepend("<img src='../img/check.png' class='check' />");
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
			$("#balloon").css("z-index","100002");
			$("#serif").css("z-index","100003");
			$("#serif").text(server_error);
		}
	});
}

// TODO 常にoffになってしまう
function change_val(chk_box)
{
	if($("this").val() == "off")
	{
		$("this").val() == "on";
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
    
    // 虫眼鏡と付箋ボードを移動
    if (window.event.keyCode == 39 && window.event.altKey == true)
    {
    	$("#loupe").css("left", "550px");
    	$("#crystal_board").css("left", "430px");
    }
    
    // 虫眼鏡と付箋ボードを移動
    if (window.event.keyCode == 37 && window.event.altKey == true)
    {
    	$("#loupe").css("left", "80px");
    	$("#crystal_board").css("left", "-10px");
    }
    
    var now_page = 0;
    
    // 右矢印 次のページ
    if (window.event.keyCode == 39 && window.event.shiftKey == true)
    {
    	now_page = Number($("#page_left").text());
    	paging(now_page, "next");
    	now_page++;
    }
    // 左矢印　前のページ
    if (window.event.keyCode == 37 && window.event.shiftKey == true)
    {
    	now_page = Number($("#page_left").text());
    	paging(now_page, "prev");
    	now_page--;
    }
    
    if (tag_id_for_contextmenu != "")
    {
    	if (window.event.keyCode == 13)
    	{
        	event.preventDefault();		
    		var tag_name = $("#" + tag_id_for_contextmenu).text();
        	var url = window.location.href;
            owner_id = url.split('/')[3];        			
    		jQuery.ajax({
    			url: "../edit_husen.html?tag_name=" + tag_name + "&tag_id=" + tag_id_for_contextmenu + "&owner_id=" + owner_id,
    			dataType: "html",
    			cache: false,
    			success: function(data)
    			{
    				if (data == 'deplicate')
    				{
    					$("#balloon").css("display","inline");
    					$("#balloon").css("z-index","100002");
    					$("#serif").css("z-index","100003");
    					$("#serif").text("「" + tag_name + "」の付箋はすでにあるよ〜");					
    				}
    				else
    				{
    					$("#balloon").css("display","inline");
    					$("#balloon").css("z-index","100002");
    					$("#serif").css("z-index","100003");
    					$("#serif").text("付箋「" + tag_name + "」を作ったよ");
    					$("#qa_input").focus();
    				}
    			},
    			error: function(data)
    			{
    				$("#balloon").css("display","inline");
    				$("#balloon").css("z-index","100002");
    				$("#serif").css("z-index","100003");
    				$("#serif").text(server_error);
    			}
    		});		
    		$("#" + tag_id_for_contextmenu).removeAttr('contenteditable');
    	}
    }    
}

function edit_husen()
{
	$("#" + tag_id_for_contextmenu).attr('contenteditable','true');
}

var is_note_open = true;

function paging(page,next_or_prev)
{
//	alert(page);
//	alert(next_or_prev);
//	alert(is_note_open);
	if (page == null)
	{
		page = Number($("#page_left").text());
	}
	
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
	
	var url = window.location.href;
    owner_id = url.split('/')[3];        			
	jQuery.ajax({
		url: "../paging.html?now_page="+page+"&next_or_prev="+next_or_prev+"&husen_str="+tag_search_conditions_uri + "&owner_id=" + owner_id,
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
			refresh();
		},
		error: function(data)
		{
			$("#balloon").css("display","inline");
			$("#balloon").css("z-index","100002");
			$("#serif").css("z-index","100003");
			$("#serif").text(server_error);
		}
	});
}


$(function() {

	loupe();
	//$('.a').attr('onMouseOver', 'this.style.opacity=1;');
///	$('.a').attr('onClick', 'change_seitou_color(this);');
	
});

//function remove_loupe()
//{
//	$loope.remove();
//	$magnifyingGlass.remove();
//	$magnifiedContent.remove();
//	$magnifyingLens.remove();
//}

function loupe()
{
	// 虫眼鏡
//	var scale = 1.2;
//	
//	var $magnifyingGlass = $('<div class="magnifying_glass"></div>');
//	var $magnifiedContent = $('<div class="magnified_content"></div>');
//	var $magnifyingLens = $('<div class="magnifying_lens"></div>');
//	var $loope = $('<div id="loupe"></div>');
	
	//setup
//	$magnifiedContent.css({
//	    backgroundColor: $("html").css("background-color") || $("body").css("background-color"),
//	    backgroundImage: $("html").css("background-image") || $("body").css("background-image"),
//	    backgroundAttachment: $("html").css("background-attachment") || $("body").css("background-attachment"),
//	    backgroundPosition: $("html").css("background-position") || $("body").css("background-position")
//	});
//	
//	$loope.css({
//		position: "absolute",
//		left:550,
//		top:100
//	});
//	
//	$magnifyingGlass.css({
//		position: "absolute",
//		left:562,
//		top:107
//	});	
//	
//    $magnifiedContent.css({
//        left: -562 * scale,
//        top: -107 * scale
//    });
//	
//	//$magnifiedContent.html(innerShiv($(document.body).html())); //fix html5 for ie<8, must also include script
////	$magnifiedContent.html($(document.body).not('.husen').html());
//	$magnifiedContent.html($(document.body).html());
//	$magnifyingGlass.append($magnifiedContent);
//	$magnifyingGlass.append($magnifyingLens); //comment this line to allow interaction
//	$(document.body).append($magnifyingGlass);
//	$(document.body).append($loope);
//	
//	function updateViewSize() {
//	    $magnifiedContent.css({
//	        width: $(document).width(),
//	        height: $(document).height()
//	    });
//	}
//	
//	//begin
//	updateViewSize();
//	
//	//events
//	$(window).resize(updateViewSize);
//	
//	$magnifyingGlass.mousedown(function(e) {
//	    e.preventDefault();
//	    $(this).data("drag", {
//	        mouse: {
//	            top: e.pageY,
//	            left: e.pageX
//	        },
//	        offset: {
//	            top: $(this).offset().top,
//	            left: $(this).offset().left
//	        }
//	    });
//	});
//	
//	$loope.mousedown(function(e) {
//	    e.preventDefault();
//	    $(this).data("drag", {
//	        mouse: {
//	            top: e.pageY,
//	            left: e.pageX
//	        },
//	        offset: {
//	            top: $(this).offset().top,
//	            left: $(this).offset().left
//	        }
//	    });
//	});
//	
//	
//	
//	$(document.body).mousemove(function(e) {
//	    if ($loope.data("drag")) {
//	        var drag = $loope.data("drag");
//	
//	        var left = drag.offset.left + (e.pageX - drag.mouse.left);
//	        var top = drag.offset.top + (e.pageY - drag.mouse.top);
//	
//	        $magnifyingGlass.css({
//	            left: left,
//	            top: top
//	        });
//	        $magnifiedContent.css({
//	            left: -left * scale,
//	            top: -top * scale
//	        });
//	        
//	        var loupe = jQuery('#loupe');
//	    	//var offset = $(".magnifying_glass").offset();
//
//	        loupe.css({
//	            left: left-10,
//	            top: top-8,
//	        });	
//	    }
//	}).mouseup(function() {
//		$loope.removeData("drag");
//	});	
//	
//	
//	
//	$(document.body).mousemove(function(e) {
//	    if ($magnifyingGlass.data("drag")) {
//	        var drag = $magnifyingGlass.data("drag");
//	
//	        var left = drag.offset.left + (e.pageX - drag.mouse.left);
//	        var top = drag.offset.top + (e.pageY - drag.mouse.top);
//	
//	        $magnifyingGlass.css({
//	            left: left,
//	            top: top
//	        });
//	        $magnifiedContent.css({
//	            left: -left * scale,
//	            top: -top * scale
//	        });
//	        
//	        var loupe = jQuery('#loupe');
//	    	//var offset = $(".magnifying_glass").offset();
//
//	        loupe.css({
//	            left: left-15,
//	            top: top-10,
//	        });	
//	    }
//	}).mouseup(function() {
//	    $magnifyingGlass.removeData("drag");
//	});	
	
}

function slime_speak()
{	
	$("#slime").animate({width: '150px', height:'145px', top:'445px'}, 600);
	$("#slime").animate({width: '150px', height:'150px', top:'440px'}, 600);
	var url = window.location.href;
    owner_id = url.split('/')[3];        			
	jQuery.ajax({
		url: "../serif.html?args_num=0" + "&owner_id=" + owner_id,
		dataType: "html",
		cache: false,
		success: function(data)
		{					
			$("#balloon").css("display","inline");
			$("#balloon").css("z-index","100002");
			$("#serif").css("z-index","100003");
			$("#serif").text(data);
			setTimeout(function(){
				$("#balloon").css("display","none");
				$("#serif").text("");		
				$("#balloon").css("z-index","99998");
				$("#serif").css("z-index","99999");
			},5000);
		},
		error: function(data)
		{
			$("#balloon").css("display","inline");
			$("#balloon").css("z-index","100002");
			$("#serif").css("z-index","100003");
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
	$("#qa_input").removeAttr("data-ph");
	var qa_id = $(q_obj).parent().attr('id');
	if(q_obj == null)
	{
		qa_id = qa_id_for_contextmenu;
	}
	var url = window.location.href;
    owner_id = url.split('/')[3];        			
	jQuery.ajax({
		url: "../edit_qa.html?qa_id="+qa_id + "&owner_id=" + owner_id,
		dataType: "json",
		cache: false,
		success: function(data)
		{
			$("#qa_input").html(data[0]);
			$("#husen_paste").html(data[1]);
		    $("#qa_id").val(qa_id);
		    $("#edit_mode").attr("src", "../img/edit_mode2.png");
		    $("#edit_mode").data("mode", "edit");
		    refresh();
		},
		error: function(data)
		{
			$("#balloon").css("display","inline");
			$("#balloon").css("z-index","100002");
			$("#serif").css("z-index","100003");
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
	var url = window.location.href;
    owner_id = url.split('/')[3];        			
	jQuery.ajax({
		url: "../to_miseikai.html?husen_names=" + tag_search_conditions_uri + "&refresh_by_date=" + refresh_by_date + "&now_page_left="+now_page_left + "&owner_id=" + owner_id,
		dataType: "json",
		cache: false,
		success: function(data)
		{
			$("#qa_area").html(data[0]);
			$("#qa_area_right").html(data[1]);
			$("#seitou_sum").html(data[2]);
			$("#seikai_sum").html(data[3]);
			$(".total_pages").html(data[4]);
			refresh();
		},
		error: function(data)
		{
			$("#balloon").css("display","inline");
			$("#balloon").css("z-index","100002");
			$("#serif").css("z-index","100003");
			$("#serif").text(server_error);
		}
	});
}

//検索中の全QAを正解の状態にする
function to_seikai()
{
	var now_page_left = $("page_left").text();
	var url = window.location.href;
    owner_id = url.split('/')[3];        			
	jQuery.ajax({
		url: "../to_seikai.html?husen_names=" + tag_search_conditions_uri + "&refresh_by_date=" + refresh_by_date + "&now_page_left="+now_page_left + "&owner_id=" + owner_id,
		dataType: "json",
		cache: false,
		success: function(data)
		{
			$("#qa_area").html(data[0]);
			$("#qa_area_right").html(data[1]);
			$("#seitou_sum").html(data[2]);
			$("#seikai_sum").html(data[3]);
			$(".total_pages").html(data[4]);	
			refresh();
		},
		error: function(data)
		{
			$("#balloon").css("display","inline");
			$("#balloon").css("z-index","100002");
			$("#serif").css("z-index","100003");
			$("#serif").text(server_error);
		}
	});
}

// ランダムなタイミングで勝手にしゃべりだす
function doSomething() {
	
	if($("#balloon").css("display") == "none")
	{
		slime_speak();
	}
}

(function loop() {
    var rand = Math.round(Math.random() * (100000 - 500)) + 500;
    setTimeout(function() {
            doSomething();
            loop();  
    }, rand);
}());

function change_mode()
{
	// 登録モードなら編集モードにする
	if ($("#edit_mode").attr("src") == "../img/register_mode.png")
	{
		$("#edit_mode").attr("src","../img/edit_mode2.png");
		$("#edit_mode").data("mode","edit");
		$("#qa_input").attr("data-ph","ここに問題と解答を入力");

	}
	// 編集モードなら登録モードにする
	else
	{
		$("#edit_mode").attr("src","../img/register_mode.png");
		$("#edit_mode").data("mode","regist");
		$("#qa_input_hidden").html("");
		$("#qa_input").html("");
		id = 2;
		qa_husen_global = "";
		$("#husen_paste").html("付箋をドラッグ");
		$("#qa_input").focus();
	}
}

function qa_mouseover(obj)
{
//	var qa_id = obj.id;
//	jQuery.ajax({
//		url: "../edit_qa.html?qa_id="+qa_id,
//		dataType: "json",
//		cache: false,
//		success: function(data)
//		{
//		    //$("#google_image").html("");
//			$("#qa_input").html(data[0]);
//			$("#husen_paste").html(data[1]);
//		    $("#qa_id").val(qa_id);
//		    $("#edit_mode").attr("src", "../img/edit_mode2.png");
//		    
//			//var keyword = "mountains";
//		    var keyword = $("#"+qa_id).children(".a").text().replace(/\u200B/g,'');
////		    alert(keyword);
//		    //keyword = "apple";
//		    //alert(typeof(keyword));
////			$.getJSON("http://api.flickr.com/services/feeds/photos_public.gne?jsoncallback=?",
////			        {
////			            tags: keyword,
////			            tagmode: "any",
////			            format: "json"
////			        },
////			        function(data) {
////			            var rnd = Math.floor(Math.random() * data.items.length);
////
////			            var image_src = data.items[0]['media']['m'].replace("_m", "_b");
////
//////			            $('body').css('background-image', "url('" + image_src + "')");
////					    $("#google_image").html("<img height='100px' src='" + image_src +"' />");
////			        });	
//		    
//		},
//		error: function(data)
//		{
//			$("#balloon").css("display","inline");
//			$("#serif").text(server_error);
//		}
//	});	
	
//	var keywords = $("#"+qa_id).children(".a").text().replace(/\u200B/g,'');
//	$.ajax ({
//		url: "../image_search.html?keywords=" + keywords,
//		type:"GET",
//       cache: false,
//       dataType: "json",
//       success: function (data) {
//		    $("#google_image").html("<img height='100px' src='" + data +"' />");
//       },
//       error: function(xhr, status, error) {
////    	   alert(status);
////    	   alert(error);
////    	   var err = eval("(" + xhr.responseText + ")");
////    	   alert(err.Message);
//    	 }
//       });

}

$(function () {
	$('#husen_wrapper').sortable({
	    tolerance: 'touch',
	    drop: function () {
	        alert('delete!');
	    }
	});
	$('.husen').sortable();
});

function husen_order()
{
	var husen_ids_in_order = "";
	var idx = 0;
	$('#sortable').children('li').each(function () {
		if (this.id != "" && this.id != "blank_husen")
		{
			idx++;
			husen_ids_in_order = husen_ids_in_order + this.id + ":" + idx +",";
		}
	});
	husen_ids_in_order = husen_ids_in_order.substring(0,husen_ids_in_order.length - 1);
	var url = window.location.href;
    owner_id = url.split('/')[3];        			
	jQuery.ajax({
		url: "../husen_order.html?husen_ids_in_order=" + husen_ids_in_order + "&owner_id=" + owner_id,
		dataType: "json",
		cache: false,
		success: function(data)
		{
//			alert(data[0]);
			$("#husen_wrapper").html("");
			$("#husen_wrapper").html(data[0]);
		},
		error: function(data)
		{
			$("#balloon").css("display","inline");
			$("#balloon").css("z-index","100002");
			$("#serif").css("z-index","100003");
			$("#serif").text(server_error);
		}
	});	
}

// 問題をリピート再生
function play_q()
{
	var id = qa_id_for_contextmenu;
	var q_id = $("#"+id).children(".q").attr("id");
	var path = "../speech/" + id + "/" + q_id + "_q.m4a";
    path = "../speech/" + q_id + "_q.m4a";
	$.ajax({
	    url:path,
	    type:'HEAD',
	    error: function()
	    {
	        path = "../speech/" + q_id + "_q.m4a";
	    },
	    success: function()
	    {
	        //file exists
	    }
	});      	
//	alert(path);
    $("#play_qa").attr("src",path);
    $("#play_qa").attr("loop");
    document.getElementById("play_qa").loop = true;
    document.getElementById("play_qa").play();
}

//解答をリピート再生
function play_a()
{
	var id = qa_id_for_contextmenu;
	var a_id = $("#"+id).children(".a").attr("id");
	var path = "../speech/" + id + "/" + a_id + "_a.m4a";
    path = "../speech/" + a_id + "_a.m4a";
	$.ajax({
	    url:path,
	    type:'HEAD',
	    error: function()
	    {
	        path = "../speech/" + a_id + "_a.m4a";
	    },
	    success: function()
	    {
	        //file exists
	    }
	});      	
//	alert(path);
	$("#play_qa").attr("src",path);
    $("#play_qa").attr("loop");
    document.getElementById("play_qa").loop = true;
    document.getElementById("play_qa").play();
}

//問題と解答をリピート再生
function play_qa()
{
	var id = qa_id_for_contextmenu;
	var q_id = $("#"+id).children(".q").attr("id");
	var q_path = "../speech/" + q_id + "_q.m4a";
	myAudio = new Audio(q_path);
	myAudio.setAttribute("src",q_path);
	myAudio.play();
	myAudio.addEventListener('ended', function() {
		play_qa2();
	});
}

function play_qa2()
{
	var id = qa_id_for_contextmenu;
	var a_id = $("#"+id).children(".a").attr("id");
	var a_path = "../speech/" + a_id + "_a.m4a";
	myAudio = new Audio(a_path);
	myAudio.setAttribute("src",a_path);
	myAudio.play();
	myAudio.addEventListener('ended', function() {
		play_qa();
	});
}

// 再生を停止
function stop_audio()
{
    document.getElementById("play_qa").loop = false;
}

// 問題を画像検索
function search_q_image()
{
	var id = qa_id_for_contextmenu;
	var question = $("#"+id).children(".q").text();
	var language = $("#"+id).children(".q").data("language");
	if (language == "日本語")
	{
		question = encodeURIComponent(question);
	}
	window.open("http://www.google.com/search?q=" + question + "&tbm=isch");
}

// 解答を画像検索
function search_a_image()
{
	var id = qa_id_for_contextmenu;
	var answer = $("#"+id).children(".a").text();
	var language = $("#"+id).children(".a").data("language");
	if (language == "日本語")
	{
		answer = encodeURIComponent(question);
	}
	window.open("http://www.google.com/search?q=" + answer + "&tbm=isch");
}

// 問題の例文を表示
function show_q_example()
{
	var id = qa_id_for_contextmenu;
	var question = $("#"+id).children(".q").text();
	question = question.replace(/ /g,"+");
	window.open("http://ejje.weblio.jp/sentence/content/" + question);
}

// 解答の例文を表示
function show_a_example()
{
	var id = qa_id_for_contextmenu;
	var answer = $("#"+id).children(".a").text();
	answer = answer.replace(/ /g,"+");
	window.open("http://ejje.weblio.jp/sentence/content/" + answer);
}

function PopupCenter(url, title, w, h) {
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

function to_premium(owner_id) {		
	
	if(!document.getElementById('agreement').checked) {
	    alert("利用規約に同意してください。");
	    return false;
	}
	
	jQuery.ajax({
		url: "../regist_premium.html?owner_id=" + owner_id,
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

function create_workbook()
{
  PopupCenter('./create_workbook.html', '暗記ノート 問題集作成', 650, 450);
}

function fullscreen()
{
	var params = [
	    'height='+screen.height,
	    'width='+screen.width,
	    'fullscreen=yes' // only works in IE, but here for completeness
	].join(',');
	     // and any other options from
	     // https://developer.mozilla.org/en/DOM/window.open

	var popup = window.open('note.html?husen_str=' + tag_search_conditions_uri); 
	popup.moveTo(0,0);
}

function to_general(owner_id)
{
	if(!confirm("General Ownerになると、これまでのデータはクリアされます。General Ownerは、問題が１００問までしか登録できません。\n本当によろしければ、OKボタンを押してください。"))
	{
		return false;
	}
	
//	jQuery.ajax({
//		url: "../to_general.html?owner_id=" + owner_id,
//		dataType: "text",
//		cache: false,
//		success: function(data)
//		{
//			alert(data);
			location.href= "../to_general.html?owner_id=" + owner_id;
//			return false;
//		},
//		error: function(data)
//		{
//			alert("ajax error");
//			return false;
//		}
//	});			
}
