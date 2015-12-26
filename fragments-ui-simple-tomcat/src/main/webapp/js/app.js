(function(ns, ajax) {
	"use strict";
	
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
				var container = document.getElementById('fragment');
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
	}, false);
})(window, window.Ajax);
