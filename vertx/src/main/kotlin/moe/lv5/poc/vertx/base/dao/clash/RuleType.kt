package moe.lv5.poc.vertx.base.dao.clash

enum class RuleType(val value: String) {
    DOMAIN("DOMAIN"),
    DOMAIN_SUFFIX("DOMAIN-SUFFIX"),
    DOMAIN_KEYWORD("DOMAIN-KEYWORD"),
    GEOIP("GEOIP"),
    IP_CIDR("IP-CIDR"),
    IP_CIDR6("IP-CIDR6"),
    SRC_IP_CIDR("SRC-IP-CIDR"),
    SRC_PORT("SRC-PORT"),
    DST_PORT("DST-PORT"),
    PROCESS_NAME("PROCESS-NAME"),
    MATCH("MATCH"),
}
