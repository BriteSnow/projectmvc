var dx = dx || {};

// --------- bind --------- //
(function($) {

	var pushers = [
		[":checkbox", function(value){
			var iptValue = this.attr("value");
			if (iptValue){
				if ($.isArray(value)){
					if ($.inArray(iptValue,value) > -1){
						this.prop("checked",true);
					}
				}else{
					if (iptValue == value){
						this.prop("checked",true);
					}					
				}
			}else{
				if (value){
					this.prop("checked",true);
				}
			}
		}],
		["input", function(value){
			this.val(value);
		}],
		["select", function(value){
			this.val(value);
		}],
		["textarea", function(value){
			this.val(value);
		}],
		["*", function(value){
			this.html(value);
		}]
	];

	var pullers = [
		[":checkbox", function(existingValue){
			var iptValue = this.attr("value");
			var newValue;
			if (this.prop("checked")){
				newValue = (iptValue)?iptValue:true;
				if (typeof existingValue !== "undefined"){
					// if we have an existingValue for this property, we create an array
					var values = $.isArray(existingValue)?existingValue:[existingValue];
					values.push(newValue);
					newValue = values;
				}				
			}
			return newValue;
		}],
		["input, select", function(existingValue){
			return this.val();
		}],
		["textarea", function(existingValue){
			return this.val();
		}],
		["*", function(existingValue){
			return this.html();
		}]
	];

	dx.addPusher = function(selector,func){
		pushers.unshift([selector,func]);
	};

	dx.addPuller = function(selector,func){
		pullers.unshift([selector,func]);
	};

	/**
	 * 
	 */
	$.fn.dxPush = function(data) {
		// iterate and process each matched element
		return this.each(function() {
			var $e = $(this);

			$e.find(".dx").each(function(){
				var $dx = $(this);
				var propPath = getPropPath($dx);
				var value = val(data,propPath);
				var i = 0, selector, fun, l = pushers.length;
				for (; i<l ; i++){
					selector = pushers[i][0];
					if ($dx.is(selector)){
						fun = pushers[i][1];
						fun.call($dx,value);
						break;
					}
				}
			});
		});

	};

	$.fn.dxPull= function(){
		var obj = {};
		// iterate and process each matched element
		this.each(function() {
			var $e = $(this);

			$e.find(".dx").each(function(){
				var $dx = $(this);
				var propPath = getPropPath($dx);
				var i = 0, selector, fun, l = pullers.length;
				for (; i<l ; i++){
					selector = pullers[i][0];
					if ($dx.is(selector)){
						fun = pullers[i][1];
						var existingValue = val(obj,propPath);
						var value = fun.call($dx,existingValue);
						if (typeof value !== "undefined"){
							val(obj,propPath,value);	
						}
						break;
					}					
				}
			});
		});		
		
		return obj;
	};

	/** 
	 * Return the variable path of the first dx-. "-" is changed to "."
	 * 
	 * @param classAttr: like "row dx dx-contact.name"
	 * @returns: will return "contact.name"
	 **/
	function getPropPath($dx){
		var classAttr = $dx.attr("class");
		var path = null;
		var i =0, classes = classAttr.split(" "), l = classes.length, name;
		for (; i < l; i++){
			name = classes[i];
			if (name.indexOf("dx-") === 0){
				path = name.split("-").slice(1).join(".");
			}
		}
		// if we do not have a path in the css, try the data-dx attribute
		if (!path){
			path = $dx.attr("data-dx");
		}
		if (!path){
			path = $dx.attr("name"); // last fall back, assume input field
		}
		return path;
	}

	// Method that allow to get or set a value to a root object with a path.to.value
	// for example val({},"contact.job.title","vp"), will popuplate {} as {contact:{job:{title:"vp"}}};
	// and val({contact:{job:{title:"vp"}}}, "cotnact.job.title") === "vp"
	function val(rootObj, pathToValue, value){
		var setMode = (typeof value !== "undefined");
		var firstNode = rootObj; // value if get mode, rootObj if setMode;

		if (!rootObj) {
			return rootObj;
		}
		// for now, return the rootObj if the pathToValue is empty or null or undefined
		if (!pathToValue) {
			return rootObj;
		}

		var i, names = pathToValue.split(".");
		var l = names.length;
		var lIdx = l  -1;
		var name, currentNode = firstNode, nextNode;
		for (i = 0; i < l; i++) {
			name = names[i];
			nextNode = currentNode[name];
			if (setMode){
				// if last index, set the value
				if (i === lIdx){
					currentNode[name] = value;
					currentNode = value;
				}else{
					if (typeof nextNode === "undefined") {
						nextNode = {};
					} 
					currentNode[name] = nextNode;
					currentNode = nextNode;
				}
			}else{
				currentNode = nextNode;
				if (typeof currentNode === "undefined") {
					currentNode = undefined;
					break;
				}			
			}

		} // /for
		if (setMode){
			return firstNode;
		}else{
			return currentNode;
		}
	}

	dx.val = val;

})(jQuery);
// --------- /bind --------- //
