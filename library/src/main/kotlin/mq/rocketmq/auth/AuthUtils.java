package mq.rocketmq.auth;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.SortedMap;
import org.apache.rocketmq.remoting.protocol.RemotingCommand;

public final class AuthUtils {
    private AuthUtils() {
    }

    public static byte[] combineRequestContent(RemotingCommand request, SortedMap<String, String> fieldsMap) {
        try {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> entry : fieldsMap.entrySet()) {
                if (!SessionCredentials.SIGNATURE.equals(entry.getKey())) {
                    sb.append(entry.getValue());
                }
            }
            byte[] array = null;
            ByteBuffer body = request.getBody();
            if (null != body) {
                array = body.array();
            }
            return AuthUtils.combineBytes(sb.toString().getBytes(SessionCredentials.CHARSET), array);
        } catch (Exception e) {
            throw new RuntimeException("Incompatible exception.", e);
        }
    }

    public static byte[] combineBytes(byte[] b1, byte[] b2) {
        int size = (null != b1 ? b1.length : 0) + (null != b2 ? b2.length : 0);
        byte[] total = new byte[size];
        if (null != b1) {
            System.arraycopy(b1, 0, total, 0, b1.length);
        }
        if (null != b2) {
            assert b1 != null;
            System.arraycopy(b2, 0, total, b1.length, b2.length);
        }
        return total;
    }

    public static String calSignature(byte[] data, String secretKey) {
        return AuthSigner.calSignature(data, secretKey);
    }
}
