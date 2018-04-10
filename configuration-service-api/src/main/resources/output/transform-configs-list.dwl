%dw 1.0
%output application/json
---
payload default [] map (config) -> {
	application : config.application,
	version : config.version,
	environment : config.environment
} unless payload == null otherwise []
