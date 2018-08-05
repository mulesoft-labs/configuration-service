%dw 1.0
%output application/json
%var httpUrl = inboundProperties."http.scheme" ++ '://' ++ inboundProperties.host
				++ inboundProperties.'http.relative.path'

%function createResourceLocation(app, version, env) httpUrl ++ '/' ++ app ++ '/' ++ version ++ '/'  ++ env
---
payload default [] map (config) -> {
	application : config.application,
	version : config.version,
	environment : config.environment,
	links : [
		{
			rel : "self",
			href : createResourceLocation(config.application, config.version, config.environment)
		}
	]
} unless payload == null otherwise []
