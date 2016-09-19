//QA内でのパーツの順番
var id = 2;

function qa_focus(obj)
{
	if (id == 2)
	{
		enter();
	}
}

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

function focus_next(){
	var node = document.querySelector("#qa_input");
	node.focus();
	var textNode = node.firstChild;
	
	var caret = 0; // insert caret after the 10th character say
	var range = document.createRange();
	range.setStart(textNode, caret);
	range.setEnd(textNode, caret);
	var sel = window.getSelection();
	sel.removeAllRanges();
	sel.addRange(range);	
}


function focus_id(_id)
{
	//var node = document.getElementById("#"+_id);
	//node.focus();
	var textNode = document.getElementById("#"+_id);
	//alert(textNode);
	
	var caret = 0; // insert caret after the 10th character say
	var range = document.createRange();
	range.setStart(textNode, caret);
	range.setEnd(textNode, caret);
	var sel = window.getSelection();
	sel.removeAllRanges();
	sel.addRange(range);	
}
/*
//JQuery plugin:
$.fn.textWidth = function(_text, _font){//get width of text with font.  usage: $("div").textWidth();
        var fakeEl = $('<span>').hide().appendTo(document.body).text(_text || this.val() || this.text()).css('font', _font || this.css('font')),
            width = fakeEl.width();
        fakeEl.remove();
        return width;
    };

$.fn.autoresize = function(options){//resizes elements based on content size.  usage: $('input').autoresize({padding:10,minWidth:0,maxWidth:100});
  options = $.extend({padding:10,minWidth:0,maxWidth:10000}, options||{});
  $(this).on('input', function() {
    $(this).css('width', Math.min(options.maxWidth,Math.max(options.minWidth,$(this).textWidth() + options.padding)));
  }).trigger('input');
  return this;
}
*/



var gl; // A global variable for the WebGL context
var canvas;

function initWebGL(canvas) {
	gl = null;

	try {
		// Try to grab the standard context. If it fails, fallback to experimental.
		gl = canvas.getContext("webgl") || canvas.getContext("experimental-webgl");
	}
	catch (e) { }

	// If we don't have a GL context, give up now
	if (!gl) {
		alert("Unable to initialize WebGL. Your browser may not support it.");
		gl = null;
	}

	return gl;
}

function start() {
	canvas = document.getElementById("glcanvas");

	gl = initWebGL(canvas);      // Initialize the GL context

	// Only continue if WebGL is available and working
	if (gl) {
		initShaders();
		if (window.initBuffers) initBuffers();
		if (window.initTexture) initTexture();
		if (window.initScene) initScene();
	}

	canvas.onresize = function () {
		gl.viewport(0, 0, canvas.width, canvas.height);
	};
}
//function loadIdentity() {
//	mvMatrix = Matrix.I(4);
//}

//function multMatrix(m) {
//	mvMatrix = mvMatrix.x(m);
//}

//function mvTranslate(v) {
//	multMatrix(Matrix.Translation($V([v[0], v[1], v[2]])).ensure4x4());
//}
//function setMatrixUniforms() {
//	gl.uniformMatrix4fv(shaderProgram.pMatrixUniform, false, pMatrix);
//	gl.uniformMatrix4fv(shaderProgram.mvMatrixUniform, false, mvMatrix);
//}

var mvMatrix = mat4.create();
var pMatrix = mat4.create();

var mvMatrixStack = [];

function mvPushMatrix() {
	var copy = mat4.create();
	mat4.set(mvMatrix, copy);
	mvMatrixStack.push(copy);
}

function mvPopMatrix() {
	if (mvMatrixStack.length == 0) {
		throw "Invalid popMatrix!";
	}
	mvMatrix = mvMatrixStack.pop();
}

function handleLoadedTexture(texture) {
	gl.bindTexture(gl.TEXTURE_2D, texture);
	gl.pixelStorei(gl.UNPACK_FLIP_Y_WEBGL, true);
	gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGBA, gl.RGBA, gl.UNSIGNED_BYTE, texture.image);
	gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.NEAREST);
	gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.NEAREST);
	gl.bindTexture(gl.TEXTURE_2D, null);
}

function vec3(x, y, z) {
	this.x = x;
	this.y = y;
	this.z = z;

	this.toArray = function () {
		return [x, y, z];
	}
}