var app = app || {};

// --------- Render --------- //
// Just a little indirection to render a template using handlebars.
// This simple indirection allows much flexibility later one, 
// when using pre-compiling or other templating engine are needed.
Handlebars.templates = Handlebars.templates || {}; 
// make all templates partials (no reasons why they should not)
Handlebars.partials =  Handlebars.templates;

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

