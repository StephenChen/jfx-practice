package jfx.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * HttpServer
 *
 * 启动类，负责启动(Bootstrap) 和 main
 *
 * 1. Handler 需要声明泛型为 <FullHttpRequest>，声明之后，只有 msg 为 FullHttpRequest 的消息才能进来。
 * 由于泛型的过滤比较简单，就不改代码来验证了，但是在这里我们可以利用泛型的特性另外做个小测试，将泛型去掉，
 * 并将 HttpServer 中 .addLast("aggregator", new HttpObjectAggregator(512 * 1024)) 注释掉，观察注释前后的log。
 * 注释前：
 * initChannel ch:[id: 0xcb9d8e9e, L:/0:0:0:0:0:0:0:1:8888 - R:/0:0:0:0:0:0:0:1:58855]
 * class:io.netty.handler.codec.http.HttpObjectAggregator$AggregatedFullHttpRequest
 * channelReadComplete
 * 注释后：
 * initChannel ch:[id: 0xc5415409, L:/0:0:0:0:0:0:0:1:8888 - R:/0:0:0:0:0:0:0:1:58567]
 * class:io.netty.handler.codec.http.DefaultHttpRequest
 * class:io.netty.handler.codec.http.LastHttpContent$1
 * channelReadComplete
 * channelReadComplete
 * 从中可以看出，如果没有 aggregator，那么一个 http 请求就会通过多个 Channel 被处理，这对业务开发是不方便的。
 * 2. 生成 response，使用 FullHttpResponse，同 FullHttpRequest 类似，不用将 response 拆分成多个 channel 返回给请求端了。
 * 3. 添加 header 描述 length。这一步很重要，如果没有，会发现用 postman 发出请求之后就一直在刷新，
 * 因为 http 请求方不知道返回的数据到底有多长。
 * 4. channel 读取完成之后需要输出缓冲流。如果没有这一步，会发现 postman 同样会一直在刷新。
 *
 * @author cxy
 * @date 2021/01/07
 */
public class HttpServer {

    private final int port;

    public HttpServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: " + HttpServer.class.getSimpleName() + " <port>");
            return;
        }
        int port = Integer.parseInt(args[0]);
        new HttpServer(port).start();
    }

    public void start() throws Exception {
        ServerBootstrap b = new ServerBootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        b.group(group)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        System.out.println("initChannel ch:" + ch);
                        ch.pipeline()
                                .addLast("decoder", new HttpRequestDecoder())       // 1
                                .addLast("encoder", new HttpResponseEncoder())      // 2
                                .addLast("aggregator", new HttpObjectAggregator(512 * 1024))    // 3
                                .addLast("handler", new HttpHandler());     // 4
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128) // determining the number of connections queued
                .childOption(ChannelOption.SO_KEEPALIVE, Boolean.TRUE);

        b.bind(port).sync();
    }
}
