# Arrowhead-Tools
Produce and Consume AH Services were been never before so easy. Arrowhead AIO (All In One) allows to create Ah Services with no extra dependencies and in a simpel way.
##### Version 0.1
##### How to use it
First Initialize AhAIO object:
```java
AhAIO ahAIO = new AhAIO(trustStoreFile, trustStorePassword, keyStoreFile, keyStorePassword, tsigFile);
```
To register a new Producer: (AhAIO will unregister all services at exit)
```java
ahAIO.addProducer(serviceName, serviceType, servicePath, serviceHost, servicePort, serviceSecure);
```
To list all the Registered Services:
```java
ahAIO.printServiceDiscovery();
```
To find a Service to consume:
```java
AhService service= ahAIO.findService(serviceName);
```
To get all the info of the service:
```java
service.getName();
service.getType();
service.getHost();
service.getPort();
service.getPath();
service.isSecure();
```
For CoAP resources it able to generate directly the URL (Feel free to add other protocols, I'm not going to add support for others):
```java
service.getCoapURI();
```

 
