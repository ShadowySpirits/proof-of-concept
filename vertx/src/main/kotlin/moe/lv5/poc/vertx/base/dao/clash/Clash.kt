package moe.lv5.poc.vertx.base.dao.clash

import io.vertx.core.json.JsonArray

data class Clash(
    var port: Int = 7890,
    var socksPort: Int = 7891,
    var redirPort: Int = 7891,
    var allowLan: Boolean = false,
    var mode: ClashMode = ClashMode.RULE,
    var logLevel: LogLevel = LogLevel.SILENT,
    var externalController: String = "0.0.0.0:9090",
    var secret: String = "",
    var ipv6: Boolean = false,
    var dns: DNS = DNS(),
    var proxies: JsonArray,
    var rules: MutableList<String> = mutableListOf(),
)

data class DNS(
    var enable: Boolean = true,
    var listen: String = "0.0.0.0:53",
    var defaultNameserver: MutableList<String> = mutableListOf(),
    var enhancedMode: EnhancedMode = EnhancedMode.FAKE_IP,
    var fakeIpRange: String = "198.18.0.1/16",
    var fakeIpFilter: MutableList<String> = mutableListOf(),
    var nameserver: MutableList<String> = mutableListOf(),
)

data class Proxy(
    var name: String = "",
    var type: ProxyType = ProxyType.UNKNOWN,
    var level: Float = 0.0f,
    var raw: String = "",
)

data class ProxyGroup(
    var name: String = "",
    var type: ProxyGroupType = ProxyGroupType.SELECT,
    var proxies: MutableList<String> = mutableListOf(),
    var url: String = "http://www.gstatic.com/generate_204",
    var interval: Int = 300,
)

data class Rule(
    var type: RuleType = RuleType.DOMAIN,
    var config: String = "",
    var direction: String = "",
    var noResolve: Boolean = false,
)
