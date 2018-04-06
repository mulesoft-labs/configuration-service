# Configuration Service

This is a project to bring a practical implementation of Configuration as a Service alias (CaaS), one improvement over existing technologies such as zool or spring cloud config, that has matured over time and running in production in big enterprises.

This project aims to provide 2 core elements:

* An API Specification, that encapsulates the IP of the service.
* A connector for MuleESB, currently on versions 3 and 4.

# Repository Structure
This repository consists on the following folders:

* `api-spec` Contains the RAML specification of the configuration service.
* `configuration-service-api` Contains a Sample implementation of the API specification as a Mule ESB application, ready to run in MongoDB.
* `configuration-service-cli` Contains a command line interface to perform administrative tasks on the configuration service.
* `configuration-service-common` Is a java library that makes easy to share common logic between the various components.
* `mule3` Is the connector implemented as a DevKit module, to work with Mule 3.x
* `mule4` Is the connector implemented as a MuleExtension, to work with Mule 4.x

Please read the individual README files found on each folder to get more details on how to use each artifact.
