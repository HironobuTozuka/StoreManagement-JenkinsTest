![Release](https://github.com/RomsPolska/StoreManagement/workflows/Release/badge.svg?branch=master)

# RCS Store Management

## General guidelines for RcsStoreManagement developement
**Two kinds of services:**
* Base/Domain Service - low level of abstraction, each method is an atomic operation
* App Service - usually combines operations from domain services to achive complete functionality
  * Each method should take a single parameter as input, strongly encouraged creating **Request** param
  * Each method should return **Response** object.
        
**MHEOperator communication:**
* Ideally there should be one way of calling physical action in a shop:
    * Calling TaskService.execute(TaskBundle) which triggers an action either pick, move or switching leds on/off or opening/closing gate
* Currently communication with MachineOperatorService is direct, but ideally we should use TaskService to hide machine operator from other SM services
    
        
**Controllers:**
* Should not execute any logic other than redirects
* Each method should have just one service method call, all processing should be done in service

**Adapter/Api modules:**
* Each api version should have it's own package inside "api" top level package
* Controllers should have independent **Request/Response** classes used as input and output. These **Request/Response** classes could and should be translated into  **app services Request/Response** using Converter class which is part of specific api package.
* Of course if specific api version can directly use app service Request/Response classes, we don't have to create new classes inside "api" package
* Api specific **Request/Response** should be package protected and not accessible outside of specific api package!
    
**Packaging:**
* inc.roms.rcs.
app
* inc.roms.rcs.api
api controllers
* inc.roms.rcs.config
app configuraton
* inc.roms.rcs.security

* inc.roms.rcs.service
* inc.roms.rcs.service.cubing
* inc.roms.rcs.service.inventory
* inc.roms.rcs.service.location
* inc.roms.rcs.service.machineoperator
* inc.roms.rcs.service.omnichanel
* inc.roms.rcs.service.operatorpanel
* inc.roms.rcs.service.order
* inc.roms.rcs.service.order
* inc.roms.rcs.service.task
* inc.roms.rcs.tools
* inc.roms.rcs.vo
* inc.roms.rcs.web

	
	


## Testing strategy
- Business logic should be contained within "Services"
- Services with low level of abstraction (calculations, operations with high level of complexity) are required to have unit tests
- Services with high level of abstraction (combination of atomic operations from basic services to provide full functionality) are required to have at least few integration black box tests, and we encourage to create unit tests for reporting functionality (It is expected that such unit tests verify that a mocked reporting service is called, as usually this kind of functions doesn't return any value)
- Api translators should have unit tests
- JSON serialization and deserializations should be tested as follows:
    * Example Json request is saved to a *.json file
    * Test tries to deserialize content of the file into request
    * Test tries to serialize request object
    * assertion is made that serialized object has to be the same as content of a file
    
## FIXMEs
- entity vs requests / responses vs domain model objects (packaging and naming)
- validation in controllers or at service level?

## Logs
To start using ELK on localhost (https://www.elastic.co/what-is/elk-stack):
- clone repository https://github.com/RomsPolska/DockerELK/
- enter repository folder and execute docker-compose up
- start SM using command: docker-compose -f docker-compose.yml -f docker-compose.add_syslog.yml up
- SM will not start in with docker-compose.add_syslog.yml if DockerELK does not work.

**Important** 
- https://github.com/RomsPolska/DockerELK/ is a duplicate of https://github.com/deviantony/docker-elk. It is not hardened.
- docker-compose.add_syslog.yml sets up logging to localhost 
