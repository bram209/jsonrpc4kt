
```
val client = jsonRpcClient("chain1@localhost", 2906, JsonRpcClient::setup)
```

```
fun JsonRpcClient.setup() {
    authentication {
        basicAuth("multichainrpc", "5eUirCwpukq2YkUShbUYrk8tsH7BgDhSeLtRDCAK2z7f")
    }

    convertion {
        jackson {
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }
    }
}
```