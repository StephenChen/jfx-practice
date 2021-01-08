package jfx.netty.live;

/**
 * LiveMessage
 *
 * @author cxy
 * @date 2021/01/08
 */
public class LiveMessage {
    static final byte TYPE_HEART = 1;
    static final byte TYPE_MESSAGE = 2;

    /**
     * 表示消息的类型，有心跳类型和内容类型
     */
    private byte type;
    /**
     * 表示消息的长度
     */
    private int length;
    /**
     * 表示消息的内容（心跳包在这里没有内容）
     */
    private String content;

    public LiveMessage() {
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "LiveMessage{" +
                "type=" + type +
                ", length=" + length +
                ", content='" + content + '\'' +
                '}';
    }
}
