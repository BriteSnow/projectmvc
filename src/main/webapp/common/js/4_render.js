// --------- Render --------- //
// Just a little indirection to render a template using handlebars.
// This simple indirection allows much flexibility later one, 
// when using pre-compiling or other templating engine are needed.
Handlebars.templates = Handlebars.templates || {};

$(function(){
	// Make all templates partials (no reasons why they should not)
	// Note: We put this in a jQuery.ready to make sure the Handlebars.templates where loaded. 
	//       This assumes the "templates.js" is loaded in the <head></head> (which is the case in our best practice)
	Handlebars.partials =  Handlebars.templates;	
});

// Global scope is acceptable for this very generic function (could be namespaced if it is the developer preference)
function render(templateName,data){
	var tmpl = Handlebars.templates[templateName];

	if (!tmpl){
		var html = $("#" + templateName).html();
		if (!html){
			throw "Not template found in pre-compiled and in DOM for " + templateName;
		}
		tmpl = Handlebars.compile(html);
		Handlebars.templates[templateName] = tmpl;
	}
	return tmpl(data);
}
// --------- /Render --------- //