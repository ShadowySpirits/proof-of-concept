package mq.rocketmq.auth;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.rocketmq.common.constant.LoggerName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AuthSigner {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static final SigningAlgorithm DEFAULT_ALGORITHM = SigningAlgorithm.HmacSHA1;
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerName.CLIENT_LOGGER_NAME);
    private static final int CAL_SIGNATURE_FAILED = 10015;
    private static final String CAL_SIGNATURE_FAILED_MSG =
            "[%s:signature-failed] unable to calculate a request signature. error=%s";

    private AuthSigner() {
    }

    public static String calSignature(String data, String key) throws AuthException {
        return calSignature(data, key, DEFAULT_ALGORITHM, DEFAULT_CHARSET);
    }

    public static String calSignature(String data, String key, SigningAlgorithm algorithm, Charset charset)
            throws AuthException {
        return signAndBase64Encode(data, key, algorithm, charset);
    }

    private static String signAndBase64Encode(String data, String key, SigningAlgorithm algorithm, Charset charset)
            throws AuthException {
        try {
            byte[] signature = sign(data.getBytes(charset), key.getBytes(charset), algorithm);
            return new String(Base64.getEncoder().encode(signature), DEFAULT_CHARSET);
        } catch (Exception e) {
            String message = String.format(CAL_SIGNATURE_FAILED_MSG, CAL_SIGNATURE_FAILED, e.getMessage());
            LOGGER.error(message, e);
            throw new AuthException("CAL_SIGNATURE_FAILED", CAL_SIGNATURE_FAILED, message, e);
        }
    }

    private static byte[] sign(byte[] data, byte[] key, SigningAlgorithm algorithm) throws AuthException {
        try {
            Mac mac = Mac.getInstance(algorithm.toString());
            mac.init(new SecretKeySpec(key, algorithm.toString()));
            return mac.doFinal(data);
        } catch (Exception e) {
            String message = String.format(CAL_SIGNATURE_FAILED_MSG, CAL_SIGNATURE_FAILED, e.getMessage());
            LOGGER.error(message, e);
            throw new AuthException("CAL_SIGNATURE_FAILED", CAL_SIGNATURE_FAILED, message, e);
        }
    }

    public static String calSignature(byte[] data, String key) throws AuthException {
        return calSignature(data, key, DEFAULT_ALGORITHM, DEFAULT_CHARSET);
    }

    public static String calSignature(byte[] data, String key, SigningAlgorithm algorithm, Charset charset)
            throws AuthException {
        return signAndBase64Encode(data, key, algorithm, charset);
    }

    private static String signAndBase64Encode(byte[] data, String key, SigningAlgorithm algorithm, Charset charset)
            throws AuthException {
        try {
            byte[] signature = sign(data, key.getBytes(charset), algorithm);
            return new String(Base64.getEncoder().encode(signature), DEFAULT_CHARSET);
        } catch (Exception e) {
            String message = String.format(CAL_SIGNATURE_FAILED_MSG, CAL_SIGNATURE_FAILED, e.getMessage());
            LOGGER.error(message, e);
            throw new AuthException("CAL_SIGNATURE_FAILED", CAL_SIGNATURE_FAILED, message, e);
        }
    }
}
