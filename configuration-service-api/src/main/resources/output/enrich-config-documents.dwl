%dw 1.0
%output application/json
%var httpUrl = inboundProperties."http.scheme" ++ '://' ++ inboundProperties.host 
				++ inboundProperties.'http.relative.path' ++ '/dynamic/@resourceName'
%function createResourceLocation(resource) httpUrl replace '@resourceName' with resource
---
payload ++ {documents : flowVars.documents map (doc) -> {
	key : doc.key,
	type : doc.type,
	links:[
		{
			rel :"self",
			href: createResourceLocation(doc.key)
		}
	]
}}