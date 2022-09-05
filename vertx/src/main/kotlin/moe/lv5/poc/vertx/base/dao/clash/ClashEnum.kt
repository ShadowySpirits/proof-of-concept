package moe.lv5.poc.vertx.base.dao.clash

enum class ClashMode {
    DIRECT, RULE, GLOBAL
}

enum class LogLevel {
    SILENT, ERROR, WARN, INFO, DEBUG, TRACE
}

enum class EnhancedMode {
    FAKE_IP, REDIR_HOST
}
