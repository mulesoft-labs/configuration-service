%dw 1.0
%output application/java
---
(payload default [] map (document) -> {
	configId : flowVars.toVersionObjectId,
	key : document.key,
	type : document.type,
	value : document.value
} as :object {class : 'org.bson.Document'}) unless payload == null otherwise []