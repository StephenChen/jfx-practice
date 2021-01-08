# Netty


## 什么是 Netty

摘自 《Essential Netty In Action》
```text
Netty 是一个利用 Java 的高级网络的能力，隐藏其背后的复杂性而提供一个
易于使用的 API 的客户端/服务器框架。
Netty 是一个广泛使用的 Java 网络编程框架（Netty 在 2011 年获得了
Duke's Choice Award，见https://www.java.net/dukeschoice/2011）。
它活跃和成长于用户社区，像大型公司 Facebook 和 Instagram 以及流行
开源项目如 Infinispan, HornetQ, Vert.x, Apache Cassandra 和
Elasticsearch 等，都利用其强大的对于网络抽象的核心代码。
```


## Netty 和 Tomcat 的区别

- 通信协议
    - TomCat 是基于 Http 协议的，本质是一个基于 http 协议的 web 容器。
    - Netty 能通过编程自定义各种协议，因为 netty 能通过 codec 来编码/解码字节流，
    完成类似 redis 访问的功能。

- 性能
    - netty 性能不一定比 tomcat 高。
    - tomcat 从6.x开始就支持了 nio 模式，并且后续还有 APR 模式（通过 jni 调用
    apache 网络库），相比 bio 模式，并发性能得到了很大提高，特别是 APR 模式。
    - netty 是否比 tomcat 性能更高，取决于 netty 程序作者的技术实力。
    

## 五种 IO 模型

- BIO，同步阻塞IO，阻塞整个步骤，如果连接少，他的延迟是最低的，因为一个线程只处理一个连接，
适用于少连接且延迟低的场景，比如说数据库连接。
- NIO，同步非阻塞IO，阻塞业务处理但不阻塞数据接收，适用于高并发且处理简单的场景，比如聊天软件。
- 多路复用IO，他的两个步骤处理是分开的，也就是说，一个连接可能他的数据接收是线程a完成的，
数据处理是线程b完成的，他比BIO能处理更多请求。
- 信号驱动IO，这种IO模型主要用在嵌入式开发，不参与讨论。
- 异步IO，他的数据请求和数据处理都是异步的，数据请求一次返回一次，适用于长连接的业务场景。

[Linux IO模式](https://segmentfault.com/a/1190000003063859)


## Netty 受欢迎

- 并发高：基于NIO，通过 Selector 处理多个 Socket。

- 传输快：依赖了NIO的一个特性——零拷贝。
Java的内存有堆内存、栈内存和字符串常量池等等，其中堆内存是占用内存空间最大的一块，
也是Java对象存放的地方，一般我们的数据如果需要从IO读取到堆内存，中间需要经过Socket缓冲区，
也就是说一个数据会被拷贝两次才能到达他的的终点，如果数据量大，就会造成不必要的资源浪费。
Netty针对这种情况，使用了NIO中的另一大特性——[零拷贝](https://www.ibm.com/developerworks/cn/linux/l-cn-zerocopy1/index.html) ，
当他需要接收数据的时候，他会在堆内存之外开辟一块内存，数据就直接从IO读到了那块内存中去，
在netty里面通过ByteBuf(Netty的一个重要概念，他是netty数据处理的容器，也是Netty封装好
的一个重要体现)可以直接对这些数据进行直接操作，从而加快了传输速度。

- 封装好：查看 [io](io/) 中的对比。

- Channel：数据传输流
    - Channel，表示一个连接，可以理解为每一个请求，就是一个 Channel。
    - ChannelHandler，核心处理业务就在这里，用于处理业务请求。
    - ChannelHandlerContext，用于传输业务数据。
    - ChannelPipeline，用于保存处理过程需要用到的 ChannelHandler 和 ChannelHandlerContext。
    
- ByteBuf：存储字节的容器，使用方便，有读索引和写索引，方便对整段字节缓存进行读写，支持get/set，方便对其中每一个字节进行读写。  
三种使用模式：
    - Heap Buffer 堆缓冲区  
    堆缓冲区是 ByteBuf 最常用的模式，将数据存储在堆空间。
    - Direct Buffer 直接缓冲区  
    直接缓冲区是 ByteBuf 另一种常用模式，内存分配不发生在堆，jdk1.4引入的 nio 的 ByteBuffer 类允许 jvm
    通过本地方法调用分配内存，这样做有两个好处：
        - 通过免去中间交换的内存拷贝，提升 IO 处理速度；直接缓冲区的内容可以驻留在垃圾回收扫描的堆区以外。
        - DirectBuffer 在 -XX:MaxDirectMemorySize=xxM 大小限制下，使用 Heap 之外的内存，GC 对此“无能为力”，
        也就意味着规避了在高负载下频繁的 GC 过程对应用线程的中断影响。
    - Composite Buffer 符合缓冲区  
    符合缓冲区相当于多个不同 ByteBuf 的视图，这是 netty 提供的，jdk不提供。
    
- Codec  
Netty 中的编码/解码器，通过它能完成字节与pojo、pojo与pojo 的相互转换，从而达到自定义协议的目的。  
在 Netty 中最有名的就是 HttpRequestDecoder 和 HttpResponseEncoder 了。
    

## 搭建 HttpServer

- FullHttpRequest
    - 1.HttpRequest 第一部分包含头信息
    - 2.HttpContent 包含数据，可以后续有多个 HTTPContent 部分
    - 3.LastHttpContent 标记 HTTP Request 的结束，同时可能包含头的尾部信息
    - 4.完整的 HTTP request，由1,2,3组成
    
- FullHttpResponse
    - 1.HttpResponse 第一部分包含头信息
    - 2.HttpContent 包含数据，可以后续有多个 HTTPContent 部分
    - 3.LastHttpContent 标记 HTTP Request 的结束，同时可能包含头的尾部信息
    - 4.完整的 HTTP response，由1,2,3组成
    
从 request 的介绍我们可以看出来，一次 http 请求并不是通过一次对话完成的，他中间可能有很次的连接。
通过对 netty 的了解，每一次对话都会建立一个 channel，并且**一个 ChannelInboundHandler 一般是
不会同时去处理多个 Channel 的**。
如何在一个 Channel 里面处理一次完整的 Http 请求？使用上面提到的 FullHttpRequest，只需要在使用
netty 处理 channel 的时候，只处理消息是 FullHttpRequest 的 Channel，就能在一个 ChannelHandler
中处理一个完整的 Http 请求。

搭建一个Netty服务器，具体过程查看代码[http](http/)。


## 构建 HTTPS 服务

构建 HTTPS 服务需要 SSL 证书  
```text
SSL 证书就是遵守 SSL 协议，由受信任的数字证书颁发机构 CA，在验证服务器身份后颁发，具有服务器身份验证
和数据传输加密功能。
也就是说，HTTPS 相比于 HTTP 服务能够防止网络劫持，同时具备一定的安全加密作用。
一般来说，证书可以在阿里云、腾讯云这种云服务上申请。申请下来后，证书只能用于指定的域名和服务器上。
```
Netty 有提供 SSL 加密的工具包，只需要通过添加 SslHandler，就能快速搭建。见[ChannelInitializer](http/SSLChannelInitializer.java)


## Decoder 和 Encoder

[Decoder和Encoder](https://www.jianshu.com/p/fd815bd437cd)


## 使用 Netty 实现长连接

背景知识，[《如何使用Socket实现长连接》](https://www.jianshu.com/p/b36632165de5)

一个简单的长连接 demo 分为以下步骤：
1. 创建连接(Channel)
2. 发心跳包
3. 发消息，并通知其他用户
4. 一段时间没收到心跳包或者用户主动关闭之后关闭连接

两个技术难点：
1. 如何保存已创建的 Channel  
这里我们是将 Channel 放在一个 Map 中，以 Channel.hashCode() 作为 key。
这样做有一个劣势，就是不适合水平扩展，每个机器都有一个连接数的上限，如果需要实现多用户实时在线，对机器的数量要求会很高，
在这里不多做讨论。不同的业务场景，设计方案也是不同的，可以在长连接方案和客户端轮询方案中进行选择。

2. 如何自动关闭没有心跳的连接  
Netty 有一个比较好的 Feature，就是 ScheduledFuture，他可以通过 ChannelHandlerContext.executor().schedule() 创建，
支持延时提交，也支持取消任务，这就给心跳包的自动关闭提供了一个很好的实现方案。

开始编写：


