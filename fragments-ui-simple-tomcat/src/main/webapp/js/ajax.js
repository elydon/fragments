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
			
			xmlHttp.open(options.method ? options.method : "GET", options.url, true);
			if (options.method == 'POST') {
		        xmlHttp.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
		    }
			xmlHttp.send(options.data);
		}
	};
})(window);
