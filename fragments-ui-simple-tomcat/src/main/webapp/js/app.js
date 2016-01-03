(function(ns, ajax, utils) {
	"use strict";
	
	var container = document.getElementById('fragment'),
		form = document.getElementById('fragment-form'),
		formError = document.getElementById('fragment-form-error'),
		formButton = document.getElementById('fragment-form-button'),
		formShown = false,
		layer = document.getElementById('layer'),
		saveButton = document.getElementById('fragment-form-save'),
		imageUploadProgressBarContainer = document.getElementById('fragment-form-image-progressbar'),
		imageUploadProgressBar = document.getElementById('progress'),
		imageUploadFile = document.getElementById('fragment-form-image'),
		imagePreview = document.getElementById('fragment-form-image-preview')
		;
	
	ns.Fragment = {
		show: function(fragment) {
			if (typeof fragment != 'object') {
				// got ID, first ask for the fragment object
				ajax.request({
					url: "/webservice/fragments.service?id=" + fragment,
					success: function(xmlHttp) {
						// parse the fragment object and call the show() function again
						var json = JSON.parse(xmlHttp.responseText);
						if (json.status === 'okay') {
							ns.Fragment.show(json.result);
						}
					}
				});
			} else {
				// clear
				container.innerHTML = '';
				
				// header
				var header = document.createElement('h1');
				header.textContent = fragment.header;
				container.appendChild(header);
				
				// text
				var text = document.createElement('div');
				text.innerHTML = fragment.text;
				container.appendChild(text);
				
				// source
				var source = document.createElement('div');
				source.innerHTML = '<a href="' + fragment.source + '">' + fragment.source + '</a>';
				container.appendChild(source);
				
				// TODO: optional attribute: image
			}
		},
		
		toggleForm: function() {
			if (formShown) {
				formButton.textContent = '+';
				form.style.display = 'none';
				layer.style.display = 'none';
				layer.style.zIndex = '-10';
			} else {
				formButton.textContent = 'x';
				form.style.display = 'block';
				layer.style.display = 'block';
				layer.style.zIndex = '5';
			}
			
			formShown = !formShown;
		},
		
		clearForm: function() {
			var i,
				elements = form.querySelectorAll('input[type="text"],input[type="url"],input[type="hidden"],input[type="file"],textarea')
				;
			
			for (i = 0; i < elements.length; i++) {
				elements[i].value = '';
			}
			
			formError.textContent = '';
			formError.style.display = 'none';
			imagePreview.style.display = 'none';
		}
	};
	
	document.addEventListener('DOMContentLoaded', function () {
		ns.Fragment.show(1);
		
		formButton.addEventListener('click', function() {
			ns.Fragment.toggleForm();
		}, false);
		
		imageUploadFile.addEventListener('change', function() {
			if (imageUploadFile.value != '') {
				imageUploadProgressBarContainer.style.display = 'block';
				// TODO: disable form
				ajax.upload(imageUploadFile, {
					url: '/webservice/images.service',
					method: 'POST',
					success: function(xmlHttp) {
						var json = JSON.parse(xmlHttp.responseText);
						imageUploadProgressBarContainer.style.display = 'none';
						// TODO: enable form
						
						if (json.status === 'okay') {
							imagePreview.querySelector('img').src = '/webservice/images.service?key=' + json.result.key;
							imagePreview.querySelector('input').value = json.result.key;
							imagePreview.style.display = 'block';
						} else {
							formError.textContent = json.message;
							formError.style.display = 'block';
						}
					}
				}, imageUploadProgressBar);
			}
		});
		imagePreview.querySelector('a').addEventListener('click', function() {
			imageUploadFile.value = '';
			imagePreview.querySelector('input').value = '';
			imagePreview.style.display = 'none';
		});
		
		saveButton.addEventListener('click', function(e) {
			e.preventDefault();
			
			ajax.request({
				url: '/webservice/fragments.service',
				data: utils.serialize(form.getElementsByTagName('form')[0]),
				method: 'POST',
				success: function(xmlHttp) {
					var json = JSON.parse(xmlHttp.responseText);
					if (json.status === 'okay') {
						ns.Fragment.show(json.result);
						ns.Fragment.toggleForm();
						ns.Fragment.clearForm();
					} else {
						formError.textContent = json.message;
						formError.style.display = 'block';
					}
				}
			});
		}, false);
	}, false);
})(window, window.Ajax, window.Utils);
