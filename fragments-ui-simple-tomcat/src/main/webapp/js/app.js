(function(ns, ajax) {
	"use strict";
	
	var container = document.getElementById('fragment'),
		form = document.getElementById('fragment-form'),
		formButton = document.getElementById('fragment-form-button'),
		formShown = false
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
						ns.Fragment.show(json.result);
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
				
				// TODO: optional attributes
			}
		}
	};
	
	document.addEventListener('DOMContentLoaded', function () {
		ns.Fragment.show(1);
		
		formButton.addEventListener('click', function() {
			if (formShown) {
				formButton.textContent = '+';
				form.style.display = 'none';
			} else {
				formButton.textContent = 'x';
				form.style.display = 'block';
			}
			
			formShown = !formShown;
		}, false);
	}, false);
})(window, window.Ajax);
