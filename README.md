# transfer-platform
Test task for a finance company

Java/Scala​ ​Test

Design and implement a RESTful API (including data model and the backing implementation) for money
transfers between accounts.

Explicit requirements:

1. keep it simple and to the point (e.g. no need to implement any authentication, assume the APi is
invoked by another internal system/service)
1. use whatever frameworks/libraries you like (except Spring, sorry!) but don't forget about the
requirement #1
1. the datastore should run in-memory for the sake of this test
1. the final result should be executable as a standalone program (should not require a pre-installed
container/server)
1. demonstrate with tests that the API works as expected

Implicit requirements:

1. the code produced by you is expected to be of high quality.
1. there are no detailed requirements, use common sense.


How to run:


To start an application just clone the repo and run `./gradlew run`

API definition can be found in `api/swagger.yaml`