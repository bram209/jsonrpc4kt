# JsonRpc4Kt


## Overview
```kotlin
fun main(args: Array<String>) {

    val client = jsonRpcClient("localhost", 3051) {
        authentication {
            basicAuth("testuser", "pass123")
        }

        conversion {
            jackson {
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            }
        }
    }

    val service = client.createService<MyRpcService>()
    val number = service.add(3, 5)

    println("3 + 5 = $number")
}

data class HealthReport(val message: String, val stamina: Int, val healthy: Boolean)

interface MyRpcService : JsonRpcService {

    fun add(n1: Int, n2: Int): Int

    @JsonRpcMethodName("health_check")
    fun healthCheck(): HealthReport

    fun someOtherMethod()

    // ,,,
}
```


## Getting started
JsonRpc4Kt consists of the following modules:
* Core - The core module that provides a json rpc client
* Service - Adds the ability to create json rpc services
* Jackson - Jackson serialization / deserialization support
* Gson - Gson serialization / deserialization support

### Core

#### Setup gradle
```groovy
repositories {
    jcenter()
    maven { url 'https://jitpack.io'}
}

dependencies {
    compile 'com.github.bram209.jsonrpc4kt:jsonrpc4kt-core:0.1'
}
```

#### Build a JsonRpcClient
```kotlin
val client = jsonRpcClient("hostname", 1234, JsonRpcClient::setup)

...

fun JsonRpcClient.setup() {
    authentication {
        basicAuth("username", "password")
    }

    convertion {
        jackson { // jackson module required
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }
    }
}
```

or 

```kotlin
val client = jsonRpcClient("hostname", 1234) {
    authentication {
        basicAuth("username", "password")
    }

    convertion {
        jackson { // jackson module required
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }
    }
}
```

#### Make a request

```kotlin
// By default, it will return a JsonObject
val result = client.makeRequest("testmethod")

// The makeRequest method makes use of type inference
val result : JsonObject = client.makeRequest("testmethod")
val result = client.makeRequest<JsonObject>("testmethod")
fun testMethod(): JsonObject = client.makeRequest("testmethod")

// For complex types, use the jackson or gson module (instructions are listed below)
data class Test(val testInfo: String)
val result = client.makeRequest<Test>("testmethod")
```

### Service
TODO

### Jackson
TODO

### Gson
TODO