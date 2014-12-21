var RemoteDaoHandler;

(function(){

	var defaultOpts = {
		contextPath: "/",
		create: "das-create",
		update: "das-update",
		"delete": "das-delete",
		get: "das-get",
		list: "das-list"
	};

	RemoteDaoHandler = function(entityName, opts){
		this._entityName = entityName;
		this._opts = $.extend({},defaultOpts,opts);
	};
	
	/**
	 * Returns the entyType name set on the constructor
	 **/
	RemoteDaoHandler.prototype.entityType = function(){
		return this._entityName;
	};	
	
	/**
	 * DAO Interface. Return value directly since it is in memory.
	 * Remote Specification: 
	 *   - will do HTTP GET as: "dao-get-{entityType}?id={id}"
	 *   - will expect a object back with {result:...} result as the prop with the result
	 * @param {String} objectType
	 * @param {Integer} id
	 * 
	 * @return the deferred that will resolve with the 
	 */
	RemoteDaoHandler.prototype.get = function(id) {
		var dfd = $.Deferred();
		var url = this._opts.contextPath + this._opts["get"] + "-" + this.entityType();
		var params = {
			id: id
		};
		return RemoteDaoHandler.executeAjax(dfd,url,params);
	};
	
	/**
	 * DAO Interface: Return a deferred object for this objectType and options
	 * @param {Object} opts
	 *           opts.pageIndex {Number}	Index of the page, starting at 0.
	 *           opts.pageSize  {Number}	Size of the page
	 *           opts.filter     {Object}	Object of matching items. If item is a single value, then, it is a ===, 
	 *																		Note implemented: advanced operation support with an operator in the key. 
	 *																		{"age,>":12,"age,<":18}
	 *						opts.orderBy   {String}
	 *						opts.orderType {String} "asc" or "desc"
	 */	
	RemoteDaoHandler.prototype.list = function(opts) {
		var dfd = $.Deferred();
		var url = this._opts.contextPath + this._opts["list"] + "-" + this.entityType();
		var data = {};
		if (opts){
			if (opts.filter){
				data.filter = JSON.stringify(opts.filter);
			}
		}
		return RemoteDaoHandler.executeAjax(dfd,url,data);
	};
	
	/**
	 * DAO Interface: Create new object, set new id, and add it.
	 *
	 * @param {Object} newEntity if null, does nothing (TODO: needs to throw exception)
	 */
	RemoteDaoHandler.prototype.create = function(newEntity) {
		var dfd = $.Deferred();
		var propsStr = JSON.stringify(newEntity);
		var data = {props:propsStr};
		var url = this._opts.contextPath + this._opts["create"] + "-" + this.entityType();
		return RemoteDaoHandler.executeAjax(dfd,url,data,true);
	};


	/**
	 * DAO Interface: update new object
	 *
	 * @param {Object} newEntity if null, does nothing (TODO: needs to throw exception)
	 */
	RemoteDaoHandler.prototype.update = function(id,props) {
		var dfd = $.Deferred();
		var url = this._opts.contextPath + this._opts["update"] + "-" + this.entityType();
		var data = {};
		data.id = id * 1; 
		data.props = JSON.stringify(props);
		return RemoteDaoHandler.executeAjax(dfd,url,data,true);
	};


	/**
	 * DAO Interface: remove an instance of objectType for a given type and id.
	 *
	 * Return the id deleted
	 *
	 * @param {Integer} id
	 *
	 */
	RemoteDaoHandler.prototype["delete"] = function(id) {
		var dfd = $.Deferred();
		var params = {id:id};
		var url = this._opts.contextPath + this._opts["delete"] + "-" + this.entityType();
		return RemoteDaoHandler.executeAjax(dfd,url,params,true);
	};
	// --------- Static Methods --------- //

	RemoteDaoHandler.executeAjax = function(resultDfd,url,params,post){
		var ajax = $.ajax(url,{		
			dataType: "json",
			data: params, 
			type: (post)?"POST":"GET"
		});
		
		ajax.done(function(data){
			if (document){
				var extra = {
					url: url,
					params: params,
					responseData: data
				};
				$(document).trigger("RemoteDaoHandler_ajax_done",extra);
			}
			resultDfd.resolve(data.result);
		});
		
		ajax.fail(function(ex){
			resultDfd.fail(ex);
		});
		
		return resultDfd.promise();	
	};
	// --------- /Static Methods --------- //	
	
})();