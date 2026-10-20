package dao.protocol;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.DecoderException;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class FrameCodecTest {

    private static Envelope ping(long clientTimeMs) {
        return Envelope.newBuilder()
                .setPing(Ping.newBuilder().setClientTimeMs(clientTimeMs))
                .build();
    }

    private static EmbeddedChannel newChannel() {
        EmbeddedChannel ch = new EmbeddedChannel();
        FrameCodec.configure(ch.pipeline());
        return ch;
    }


    private static byte[] encode(Envelope msg) {
        EmbeddedChannel ch = newChannel();
        assertTrue(ch.writeOutbound(msg));

        ByteBuf all = Unpooled.buffer();
        ByteBuf part;
        while ((part = ch.readOutbound()) != null) {
            all.writeBytes(part);
            part.release();
        }
        ch.finishAndReleaseAll();

        byte[] bytes = new byte[all.readableBytes()];
        all.readBytes(bytes);
        all.release();
        return bytes;
    }

    @Test
    void ping_onTheWire_hasLengthPrefixThenProtobufBytes() {
        byte[] wire = encode(ping(123));


        assertArrayEquals(new byte[] {0, 0, 0, 4, 0x2A, 0x02, 0x08, 0x7B}, wire);
    }

    @Test
    void roundTrip_decodeGivesBackTheSameEnvelope() {
        Envelope original = ping(123);

        EmbeddedChannel receiver = newChannel();
        assertTrue(receiver.writeInbound(Unpooled.wrappedBuffer(encode(original))));

        Envelope decoded = receiver.readInbound();
        assertEquals(original, decoded);
        assertNull(receiver.readInbound(), "chỉ được ra đúng một gói");
        receiver.finishAndReleaseAll();
    }

    @Test
    void splitFrame_isDeliveredOnlyWhenComplete() {
        byte[] wire = encode(ping(123));
        EmbeddedChannel receiver = newChannel();

        receiver.writeInbound(Unpooled.wrappedBuffer(Arrays.copyOfRange(wire, 0, 3)));
        assertNull(receiver.readInbound(), "chưa đủ byte thì chưa được ra gói");

        receiver.writeInbound(Unpooled.wrappedBuffer(Arrays.copyOfRange(wire, 3, wire.length)));
        Envelope decoded = receiver.readInbound();
        assertEquals(ping(123), decoded);
        receiver.finishAndReleaseAll();
    }

    @Test
    void twoFramesGluedTogether_areDeliveredAsTwoEnvelopes() {
        byte[] a = encode(ping(1));
        byte[] b = encode(ping(2));
        byte[] glued = new byte[a.length + b.length];
        System.arraycopy(a, 0, glued, 0, a.length);
        System.arraycopy(b, 0, glued, a.length, b.length);

        EmbeddedChannel receiver = newChannel();
        receiver.writeInbound(Unpooled.wrappedBuffer(glued)); // MỘT lần đọc, HAI gói dính nhau

        Envelope first = receiver.readInbound();
        Envelope second = receiver.readInbound();
        assertEquals(ping(1), first);
        assertEquals(ping(2), second);
        assertNull(receiver.readInbound());
        receiver.finishAndReleaseAll();
    }

    @Test
    void oversizedFrame_isRejected() {
        EmbeddedChannel receiver = newChannel();

        ByteBuf evil = Unpooled.buffer().writeInt(2_000_000);

        assertThrows(DecoderException.class, () -> receiver.writeInbound(evil));
        receiver.finishAndReleaseAll();
    }
}
