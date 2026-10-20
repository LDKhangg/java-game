package dao.protocol;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

public class FrameCodec {
  public static void configure(ChannelPipeline pipeline){
    pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(1_048_576, 0, 4, 0, 4));
    pipeline.addLast("protobufDecoder", new ProtobufDecoder(Envelope.getDefaultInstance()));
    pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
    pipeline.addLast("protobufEncoder", new ProtobufEncoder());
  }
}
