class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(view:"/index")
		"/controllerList"(view:"/controllerList")
		"500"(view:'/error')
	}
}
