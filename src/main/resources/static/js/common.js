var page = window.location.href.split("/")[3];

$(function() {
	init();
});

function init()
{
	var url = '../chat.html?page=' + page + "&type=init"
	ajax(url);
}

function submit_comment(obj)
{
	$next = $(obj);
	var client_comment = $next.prev().val();
	var url = '../chat.html?page=' + page + '&client_comment=' + client_comment;
	ajax(url);
}

function submit()
{
	var url = '../chat.html?page=' + page;
	ajax(url);
}

function yes()
{
	var url = '../chat.html?page=' + page + '&type=yes';
	ajax(url);
}

function no()
{
	var url = '../chat.html?page=' + page + '&type=no';
	ajax(url);
}

function ajax(url)
{
    $.ajax({
        url: url,
		dataType: "json",
        type: 'GET',
        success: function(data) {
        	$("#comments").append(data[0]);
        }
    });
}