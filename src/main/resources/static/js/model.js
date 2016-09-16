//
// Copyright (c) Necotech.org 2014, All rights reserved.
//

String.prototype.endsWith = function (suffix) {
	return this.indexOf(suffix, this.length - suffix.length) !== -1;
};

var modelManager = new ModelManager();

function ModelManager() {
	this.models = [];

	this.addModels = function (model_defines) {
		for (var name in model_defines) {
			this.models.push(model_defines[name]);
		}
	};
	
	this.currentLoadingIndex = 0;
	this.model_finished = false;
	this.texture_finished = false;

	this.startLoad = function () {
		if (this.currentLoadingIndex >= this.models.length) {
			log('load finished');

			this.initModelsBuffer();
			return;
		}
		else {
			log('loading ' + this.currentLoadingIndex + ' of ' + this.models.length + '...');
		}

		this.model_finished = false;
		this.texture_finished = false;
		
		var modelObj = this.models[this.currentLoadingIndex];

		this.loadModel('model/' + modelObj.mod, function (model) {
			modelObj.model = model;
			modelObj.model.color = modelObj.color;
			modelManager.model_finished = true;
			modelManager.checkForLoadNextModel();
		});

		if (modelObj.img) {
			this.loadTexture('mat/' + modelObj.img, function (texture) {
				modelObj.texture = texture;
				modelManager.texture_finished = true;
				modelManager.checkForLoadNextModel();
			});
		} else {
			this.texture_finished = true;
		}
	};

	this.loadModel = function (url, onresponse) {
		var oReq = new XMLHttpRequest();
		oReq.open("GET", url, true);
		oReq.responseType = "arraybuffer";

		oReq.onload = function (oEvent) {
			var buffer = oReq.response; // Note: not oReq.responseText
			if (buffer) {

				if (url.endsWith('.zip')) {

					var i = url.lastIndexOf('/');
					var filename = url.substr(i + 1, url.length - i - 4) + 'mod';

					var zip = new JSZip(buffer);
					buffer = zip.file(filename).asArrayBuffer();
				}

				onresponse(createModel(buffer));
			}
		};

		oReq.send(null);
	};

	this.loadTexture = function (url, onloaded) {
		var texture = gl.createTexture();
		texture.image = new Image();
		texture.image.onload = function () {
			handleLoadedTexture(texture);
			onloaded(texture);
		}
		texture.image.src = url;
	};

	this.checkForLoadNextModel = function () {
		if (this.model_finished && this.texture_finished) {
			this.currentLoadingIndex++;
			this.startLoad();
		}
	};

	this.initModelsBuffer = function () {
		for (var i = 0; i < this.models.length; i++) {
			bindModelBuffer(this.models[i].model);
			this.models[i].model.texture = this.models[i].texture;
		}

		if (this.onfinish != null) {
			this.onfinish();
		}
	};

	this.drawObject = function (obj, before, after) {
		if (obj.model_define)
		{
			for (var name in obj.model_define) {
				var modeldef = obj.model_define[name];

				if (before != null) before(modeldef);
				drawModel(modeldef.model);
				if (after != null) after(modeldef);
			}
		}
	};
}

function createModel(buffer) {
	// header
	var header = new Int32Array(buffer);

	var model = {
		vertexCount: header[0],
		normalCount: header[1],
		texCoordCount: header[2],
	};

	// body
	var stream = new Float32Array(buffer);
	var offset = 3;
	model.vertexs = stream.subarray(offset, offset + model.vertexCount * 3);
	offset += model.vertexCount * 3;
	model.normals = stream.subarray(offset, offset + model.normalCount * 3);
	offset += model.normalCount * 3;
	model.texcoords = stream.subarray(offset, offset + model.texCoordCount * 2);
	offset += model.texCoordCount * 2;

	return model;
}

function bindModelBuffer(model) {

	// vertex
	model.vertexBuffer = gl.createBuffer();
	gl.bindBuffer(gl.ARRAY_BUFFER, model.vertexBuffer);
	gl.bufferData(gl.ARRAY_BUFFER, model.vertexs, gl.STATIC_DRAW);

	model.vertexBuffer.itemSize = 3;
	model.vertexBuffer.numItems = model.vertexCount;

	// normal
	model.normalBuffer = gl.createBuffer();
	gl.bindBuffer(gl.ARRAY_BUFFER, model.normalBuffer);
	gl.bufferData(gl.ARRAY_BUFFER, model.normals, gl.STATIC_DRAW);

	model.normalBuffer.itemSize = 3;
	model.normalBuffer.numItems = model.normalCount;

	// txtcoord
	if (model.texCoordCount > 0) {
		model.texcoordBuffer = gl.createBuffer();
		gl.bindBuffer(gl.ARRAY_BUFFER, model.texcoordBuffer);
		gl.bufferData(gl.ARRAY_BUFFER, model.texcoords, gl.STATIC_DRAW);

		model.texcoordBuffer.itemSize = 2;
		model.texcoordBuffer.numItems = model.texCoordCount;
	}

}

function drawModel(model) {
	//if (model.vertexBuffer == null) return;

	gl.bindBuffer(gl.ARRAY_BUFFER, model.vertexBuffer);
	gl.vertexAttribPointer(shaderProgram.vertexPositionAttribute, model.vertexBuffer.itemSize, gl.FLOAT, false, 0, 0);

	gl.bindBuffer(gl.ARRAY_BUFFER, model.normalBuffer);
	gl.vertexAttribPointer(shaderProgram.vertexNormalAttribute, model.normalBuffer.itemSize, gl.FLOAT, false, 0, 0);

	if (shaderProgram.textureCoordAttribute != null) {
		if (model.texcoordBuffer != null) {
			gl.enableVertexAttribArray(shaderProgram.textureCoordAttribute);

			gl.bindBuffer(gl.ARRAY_BUFFER, model.texcoordBuffer);
			gl.vertexAttribPointer(model.texcoordBuffer, model.texcoordBuffer.itemSize, gl.FLOAT, false, 0, 0);
		}
		else {
			gl.disableVertexAttribArray(shaderProgram.textureCoordAttribute);
		}
	}

	if (model.color != null) {
		gl.uniform4fv(shaderProgram.ambientLight, model.color);
	}
	else {
		gl.uniform4fv(shaderProgram.ambientLight, [0.2, 0.2, 0.2, 1.0]);
	}
	
	if (model.texture) {
		gl.activeTexture(gl.TEXTURE0);
		gl.bindTexture(gl.TEXTURE_2D, model.texture);
	}

	mvPushMatrix();
	setMatrixUniforms();
	gl.drawArrays(gl.TRIANGLES, 0, model.vertexBuffer.numItems);
	mvPopMatrix();
}

