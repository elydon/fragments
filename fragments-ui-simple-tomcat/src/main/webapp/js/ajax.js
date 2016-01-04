(function(ns) {
	"use strict";
	
	ns.Ajax = {
		request: function(options) {
			var xmlHttp = new XMLHttpRequest();
			
			xmlHttp.onreadystatechange = function() {
				if (xmlHttp.readyState == XMLHttpRequest.DONE) {
					if (xmlHttp.status == 200) {
						options.success(xmlHttp);
					} else {
						options.fail(xmlHttp);
					}
				}
			};
			
			if (typeof options.progress == 'function') {
				xmlHttp.upload.addEventListener('progress', options.progress);
			}
			
			xmlHttp.open(options.method ? options.method : "GET", options.url, true);
			if (options.method === 'POST' && typeof options.progress == 'undefined') {
				xmlHttp.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
			}
			xmlHttp.send(options.data);
		},
		
		upload: function(fileSelectElement, options, progressBarElement) {
			if (fileSelectElement.files.length === 0) {
				return;
			}
			
			options.data = new FormData();
			options.data.append('file', fileSelectElement.files[0]);
			
			if (typeof progressBarElement != 'undefined') {
				progressBarElement.style.width = 0;
				options.progress = function(e) {
					progressBarElement.style.width = Math.ceil(e.loaded / e.total) * 100 + '%';
				};
			}
			
			ns.Ajax.request(options);
		}
	};
})(window);
