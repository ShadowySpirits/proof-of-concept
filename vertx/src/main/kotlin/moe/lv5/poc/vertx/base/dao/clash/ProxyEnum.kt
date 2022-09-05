package moe.lv5.poc.vertx.base.dao.clash

enum class ProxyType {
    HTTP,
    HTTPS,
    SOCKS4,
    SOCKS5,
    TROJAN,
    VMESS,
    UNKNOWN
}

enum class ProxyGroupType {
    SELECT,
    URL_TEST,
    LOAD_BALANCE,
}
