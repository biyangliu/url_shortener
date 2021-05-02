# URL Shortener #

This is a simple URL Shortener for converting a long URL to short URL like [TinyURL](https://tinyurl.com/app).

It supports:
1. CREATE: Take a URL and emit a short URL.
2. LOOKUP: Visiting the short URL (i.e. a GET request) should redirect to the original long URL.

# How to use 
+ With Docker and docker-compose: 
```sh
$ git clone https://github.com/biyangliu/url_shortener.git
$ cd url_shortener
$ docker-compose up 
```
+ Open `localhost:8080` to see CREATE API page.
+ Visit the generated short URL to redirect to the original long URL.


# Features
+ Built with Maven/Spring Boot/Docker. Easily built and executed.
+ Database: MongoDB chosen for enhanced scalability. Can easily scale up to multiple shards and replicas.
+ Cache Layer: Redis. Maximize the throughput.
+ Highly available algorithm for short URL generation with Snowflake-like ID generator.
+ JUnit5/Mockito is used for unit testing.
+ Basic Slf4j logging is added for further analysis.

# Basic Algorithm Walk Through
```
.......... .......... .......... .......... ........
|<   31 bits of timestamp       >||Sev||<   Seq   >|
```

+ For a short URL, we use 8 characters (a-z, A-Z and 0-9). So we can have Log<sub>2</sub>(62<sup>8</sup>) ≈ 48 bits.
+ The Snowflake ID: total 48 bits = 31 bits timestamp + 5 bits server id + 12 bits sequence integer (can be further adjusted).
+ When running the application in parallel, different threads use AtomicLong sequence number to avoid concurrency problems.
+ Then this generated ID will be converted into 8 characters of 62-base.
+ Comparing to other approaches, Snowflake ID generation does NOT have a dependency on database or Redis or ZooKeeper. So the availability is maximized.
+ 31 bits of timestamp with base unit of .1s will last about 3.4 years, which is good for a short URL service.
+ 5 bits server ID and 12 bits sequence number will allow more than 1.3M URLs generated in one second, good for handling extreme cases.
+ Redis is introduced for both CREATE and LOOKUP calls, so that the same long URLs will generate the same result, and greatly reduce database loads.


# Things to Do
+ Support variable short URL length.
+ Batch the database writes, and reduce the dependency on database.
+ Add ElasticSearch/Kibana or Grafana to access pattern analysis, for further optimization
+ To scale out, set up Redis Cluster with replicas and MongoDB Cluster with auto-sharding and auto-replication, as well as adding more web servers.
+ Add more tests to improve code coverage.