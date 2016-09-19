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
		}
		else if (last == "a_input")
		{
			$("#qa_input").append(q_parts);						
			focus_last();
			id++;
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