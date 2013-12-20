package com.home

class MenuController {
	static navigation = [
		[group:"home",action:"index", title:"Mac Fin Home"]	
	]
    def index() { render(uri: "/") }
}
