# Configuration Service Api

This is the implementation of api-spec with `MongoDB` as source of repository to store configurations. This implementation is ready to use with MongoDB. It  provides basic validation to avoid storing duplicate configurations. This api creates `database` in MongoDB as `configuration`. It also creates two collections `configuration.coordinates` to store configuration and `configuration.documents` to store documents in MongoDB.  It has a keystore which consists of three keys
 1) Encryption Key
 2) Signature key
 3) Wrapping Key
 
 To view keystore you can use password as `changeit`

# How to run api
You can simply clone the project and import in studio as `maven project`. To run api, you will need [MongoDB](https://www.mongodb.com/download-center#enterprise) running. By default it tries to connect local instance of MongoDB. However, you can edit connection setting in `properties` file.  To access api-console you can visit `http://127.0.0.1:8081/console/` 

