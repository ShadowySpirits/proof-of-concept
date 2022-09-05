package moe.lv5.poc.vertx.base

import io.vertx.core.impl.logging.Logger
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.launch
import moe.lv5.poc.vertx.annotation.*
import moe.lv5.poc.vertx.util.AnnotationUtils
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import kotlin.reflect.KFunction
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.isAccessible

open class CustomVerticle : CoroutineVerticle(), KoinComponent {
    protected val logger: Logger = LoggerFactory.getLogger(this::class.java)

    private val router: Router by lazy { Router.router(vertx) }
    private lateinit var route: Route

    override suspend fun start() {
        var routeCount = 0
        var rootPath = "/"

        this::class.findAnnotation<Service>()?.let {
            rootPath = it.mountPoint
        }

        this::class.memberFunctions.forEach { func ->
            val requestMap = AnnotationUtils.findAnnotation(func, Path::class) ?: return@forEach
            routeCount++
            func.isAccessible = true
            val httpMethod = io.vertx.core.http.HttpMethod.valueOf(requestMap.method.name)
            val path = when (requestMap.method) {
                HttpMethod.GET -> func.findAnnotation<Get>()?.path ?: requestMap.path
                HttpMethod.POST -> func.findAnnotation<Post>()?.path ?: requestMap.path
                HttpMethod.PUT -> func.findAnnotation<Put>()?.path ?: requestMap.path
                HttpMethod.DELETE -> func.findAnnotation<Delete>()?.path ?: requestMap.path
                else -> requestMap.path
            }

            getVerticleRouter().route(httpMethod, path).coroutineHandler(func)
            logger.debug("add route: ${requestMap.method.name} ${rootPath}${path}")
        }

        if (routeCount > 0) {
            mountServiceRouter(rootPath)
            logger.debug("mount route to $rootPath")
        }
    }

    override suspend fun stop() {
        unmountServiceRouter()
    }

    private fun Route.coroutineHandler(process: KFunction<*>) {
        handler { ctx ->
            launch(ctx.vertx().dispatcher()) {
                try {
                    process.callSuspend(this@CustomVerticle, ctx)
                } catch (e: Exception) {
                    val req = ctx.request()
                    logger.error("${req.method()} ${req.absoluteURI()}", e)
                    ctx.fail(e)
                }
            }
        }
    }

    protected fun getRootRouter(): Router {
        return get { parametersOf(vertx) }
    }

    private fun getVerticleRouter(): Router {
        return router
    }

    private fun mountServiceRouter(mountPoint: String) {
        require(!mountPoint.endsWith("*")) { "Don't include * when mounting a sub router" }
        route = getRootRouter()
            .route("$mountPoint*")
            .apply {
                subRouter(router)
            }
    }

    private fun unmountServiceRouter() {
        route.remove()
    }
}
