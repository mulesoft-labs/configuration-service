%dw 1.0
%output application/java
---
{
	application: flowVars.application,
	version : flowVars.configVersion,
	environment: flowVars.env

}