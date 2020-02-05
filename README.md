# grpc-client-kotlin
tutorial and template [gRPC](https://grpc.io/) project
# How to start
1. install [AndroidStudio](https://developer.android.com/studio/?hl=ja).
2. open project with Gradle.
3. add dependency and set about protocol buffers.

   - grpc-client-kotlin/build.gradle
   ```groovy
   dependencies {
	   classpath "com.google.protobuf:protobuf-gradle-plugin:0.8.8"
   }
   ```
   - grpc-client-kotlin/app/build.gradle
   ```groovy
   apply plugin: 'com.google.protobuf'

   protobuf {
       protoc { artifact = 'com.google.protobuf:protoc:3.11.0' }
       plugins {
           grpc { artifact = 'io.grpc:protoc-gen-grpc-java:1.28.0-SNAPSHOT' }
       }
       generateProtoTasks {
           all().each { task ->
               task.builtins {
                   java { option 'lite' }
               }
               task.plugins {
                   grpc { // Options added to --grpc_out
                       option 'lite' }
               }
           }
       }
   }

   dependencies {
	   implementation 'io.grpc:grpc-okhttp:1.28.0-SNAPSHOT'
	   implementation 'io.grpc:grpc-protobuf-lite:1.28.0-SNAPSHOT'
	   implementation 'io.grpc:grpc-stub:1.28.0-SNAPSHOT'
	   implementation "javax.annotation:javax.annotation-api"
   }
   ```
4. create ```.proto``` file for service at ```app/src/main```
   ```golang
   syntax = "proto3";

   package greet;

   option java_package = "tutorial.grpc.client.kotlin";
   option java_outer_classname = "GreetService";


   service Greet {
       rpc Say(Question) returns (Reply) {}
   }

   message Question {
       string message = 1;
   }

   message Reply {
       string message = 1;
   }
   ```
5. generate support gRPC service file. protobuf generate service file from ```.proto```.
   ```shell
   $ run ./gradlew build
   ```
   - output at ```build/generated/source/{debug||release}/{java_package}```
6. start app.