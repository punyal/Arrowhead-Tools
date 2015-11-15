# Arrowhead-Tools
Produce and Consume AH Services were been never before so easy. Arrowhead AIO (All In One) allows to create Ah Services with no extra dependencies and in a simpel way.
##### Version 0.1
##### How to use it
1. Initialize AhAIO object:
```java
AhAIO ahAIO = new AhAIO(trustStoreFile, trustStorePassword, keyStoreFile, keyStorePassword, tsigFile);
```
2. If you want to register a new Producer: (AhAIO will unregister all services at exit)
```java
ahAIO.addProducer(serviceName, serviceType, servicePath, serviceHost, servicePort, serviceSecure);
```
3. To list all the Registered Services:
```java
ahAIO.printServiceDiscovery();
```
4. To find a Service to consume:
```java
AhService service= ahAIO.findService(serviceName);
```
5. To get all the info of the service:
```java
service.getName();
service.getType();
service.getHost();
service.getPort();
service.getPath();
service.isSecure();
```
6. For CoAP resources it able to generate directly the URL (Feel free to add other protocols, I'm not going to add support for others):
```java
service.getCoapURI();
```

 
