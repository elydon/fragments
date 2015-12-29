(function(ns, ajax, utils) {
	"use strict";
	
	var container = document.getElementById('fragment'),
		form = document.getElementById('fragment-form'),
		formError = document.getElementById('fragment-form-error'),
		formButton = document.getElementById('fragment-form-button'),
		formShown = false,
		layer = document.getElementById('layer'),
		saveButton = document.getElementById('fragment-form-save')
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
				elements = form.querySelectorAll('input[type="text"],input[type="url"],textarea')
				;
			
			for (i = 0; i < elements.length; i++) {
				elements[i].value = '';
			}
			
			formError.textContent = '';
			formError.style.display = 'none';
		}
	};
	
	document.addEventListener('DOMContentLoaded', function () {
		ns.Fragment.show(1);
		
		formButton.addEventListener('click', function() {
			ns.Fragment.toggleForm();
		}, false);
		
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
