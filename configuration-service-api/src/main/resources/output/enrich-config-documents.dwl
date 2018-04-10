%dw 1.0
%output application/json
---
payload ++ {documents : flowVars.documents map (doc) -> {
	key : doc.key,
	type : doc.type
}}