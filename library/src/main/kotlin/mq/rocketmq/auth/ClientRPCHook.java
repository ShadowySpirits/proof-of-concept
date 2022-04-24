package mq.rocketmq.auth;

import java.lang.reflect.Field;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.rocketmq.remoting.CommandCustomHeader;
import org.apache.rocketmq.remoting.RPCHook;
import org.apache.rocketmq.remoting.protocol.RemotingCommand;

public class ClientRPCHook implements RPCHook {
    public static final int GET_DIRECT_ROUTE_INFO_BY_TOPIC = 364;

    private final SessionCredentials sessionCredentials;
    protected ConcurrentHashMap<Class<? extends CommandCustomHeader>, Field[]> fieldCache = new ConcurrentHashMap<>();

    public ClientRPCHook(SessionCredentials sessionCredentials) {
        this.sessionCredentials = sessionCredentials;
    }

    @Override
    public void doBeforeRequest(String remoteAddr, RemotingCommand request) {
        byte[] total = AuthUtils.combineRequestContent(request,
                                                       parseRequestContent(request, sessionCredentials.getAccessKey()
                                                               , sessionCredentials.getSecurityToken()));
        String signature = AuthUtils.calSignature(total, sessionCredentials.getSecretKey());
        request.addExtField(SessionCredentials.SIGNATURE, signature);
        request.addExtField(SessionCredentials.ACCESS_KEY, sessionCredentials.getAccessKey());

        /* The SecurityToken value is unnecessary, user can choose this one. */
        if (sessionCredentials.getSecurityToken() != null) {
            request.addExtField(SessionCredentials.SECURITY_TOKEN, sessionCredentials.getSecurityToken());
        }
    }

    @Override
    public void doAfterResponse(String remoteAddr, RemotingCommand request, RemotingCommand response) {
    }

    public void doAfterRpcFailure(String remoteAddr, RemotingCommand request, Boolean remoteTimeout) {
    }

    protected SortedMap<String, String> parseRequestContent(RemotingCommand request, String ak, String securityToken) {
        CommandCustomHeader header = request.readCustomHeader();
        /* Sort property */
        SortedMap<String, String> map = new TreeMap<>();
        map.put(SessionCredentials.ACCESS_KEY, ak);
        if (securityToken != null) {
            map.put(SessionCredentials.SECURITY_TOKEN, securityToken);
        }
        try {
            /* Add header properties */
            if (null != header) {
                Field[] fields = fieldCache.get(header.getClass());
                if (null == fields) {
                    fields = header.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                    }
                    Field[] tmp = fieldCache.putIfAbsent(header.getClass(), fields);
                    if (null != tmp) {
                        fields = tmp;
                    }
                }

                for (Field field : fields) {
                    Object value = field.get(header);
                    if (null != value && !field.isSynthetic()) {
                        map.put(field.getName(), value.toString());
                    }
                }
            }
            return map;
        } catch (Exception e) {
            throw new RuntimeException("incompatible exception.", e);
        }
    }

    public SessionCredentials getSessionCredentials() {
        return sessionCredentials;
    }
}
