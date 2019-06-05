package me.arkadzi.threader.domain.processing

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import me.arkadzi.threader.domain.model.Page
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class BfsPageAnalyzer(val pageLoader : PageLoader) {
    private val dataSubject = PublishSubject.create<Data>().toSerialized()
    private val runningSubject = BehaviorSubject.createDefault(State.IDLE)
    private var service: ExecutorService? = null
    private var bfsDisposable: Disposable? = null

    fun startAnalyzing(analyzeParameters: AnalyzeParameters) {
        if (runningSubject.value!! == State.RUNNING) {
            throw IllegalArgumentException("startAnalyzing: BfsPageAnalyzer is already running")
        }

        service = Executors.newFixedThreadPool(
            analyzeParameters.threadsCount,
            ProcessPriorityThreadFactory(Thread.MIN_PRIORITY)
        )

        bfsDisposable = getBfsCompletable(analyzeParameters)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                runningSubject.onNext(State.RUNNING)
            }
            .doFinally {
                runningSubject.onNext(State.IDLE)
            }
            .subscribe()
    }

    fun getStateObservable(): Observable<State> = runningSubject

    fun getDataObservable(minUpdatePeriod: Long, timeUnit: TimeUnit): Observable<Data> =
        dataSubject.sample(minUpdatePeriod, timeUnit)

    fun stopAnalyzing() {
        service?.shutdownNow()
    }

    private fun getBfsCompletable(analyzeParameters: AnalyzeParameters): Completable {
        return Completable
            .create { emitter ->
                val executorService = service
                try {
                    executorService?.let {
                        bfs(it, analyzeParameters)
                    }
                    if (!emitter.isDisposed) {
                        emitter.onComplete()
                    }
                } catch (e: Throwable) {
                    if (!emitter.isDisposed) {
                        emitter.onError(e)
                    }
                } finally {
                    if (executorService?.isAvailable() == true) {
                        executorService.shutdown()
                    }
                }
            }
    }

    fun bfs(service: ExecutorService, analyzeParameters: AnalyzeParameters) {
        val (startPage, maxAnalyzePageCount, threadsCount) = analyzeParameters

        val used = LinkedList<Page>()
        val queue = LinkedList<Page>()
        queue.add(startPage)

        while (!queue.isEmpty() && service.isAvailable()) {
            val layerPart = extractLayerPart(queue, threadsCount)
            used.addAll(layerPart)
            notifyUpdates(used, maxAnalyzePageCount)
            service.invokeAll(layerPart.map { generateCallable(it) })
            notifyUpdates(used, maxAnalyzePageCount)
            appendToQueue(queue, used, maxAnalyzePageCount, layerPart)

            if (finalState(used, maxAnalyzePageCount)) {
                return
            }
        }
    }

    private fun appendToQueue(
        queue: LinkedList<Page>,
        used: LinkedList<Page>,
        maxAnalyzePageCount: Int,
        layerPart: MutableList<Page>
    ) {
        if (queue.size + used.size < maxAnalyzePageCount) {
            queue.addAll(layerPart.flatMap { it.getStates() }.distinct().filter { it !in used })
            while (queue.size + used.size > maxAnalyzePageCount) {
                queue.removeLast()
            }
        }
    }

    private fun extractLayerPart(
        queue: LinkedList<Page>,
        threadsCount: Int
    ): MutableList<Page> {
        val layerPart = mutableListOf<Page>()
        val node = queue.pop()
        layerPart.add(node)
        while (queue.peek()?.layer == node.layer && layerPart.size < threadsCount) {
            layerPart.add(queue.pop())
        }
        return layerPart
    }

    private fun notifyUpdates(used: LinkedList<Page>, maxAnalyze: Int) {
        dataSubject.onNext(Data(used, maxAnalyze))
    }

    private fun finalState(queueElement: LinkedList<Page>, maxAnalyzePageCount: Int): Boolean {
        return queueElement.size >= maxAnalyzePageCount
    }

    private fun generateCallable(queueElement: Page): Callable<Page> {
        return Callable {
            queueElement.process(pageLoader)
//            println("f " + queueElement.layer + " " + queueElement.analyzeTime + " " + queueElement.url)
            queueElement
        }
    }

    fun ExecutorService.isAvailable() = !isShutdown && !isTerminated

    data class Data(
            val pages: List<Page>,
            val max: Int
    )

    data class AnalyzeParameters(
        val startPage: Page,
        val maxAnalyzePageCount: Int,
        val threadsCount: Int
    )

    enum class State {
        RUNNING, IDLE
    }

    companion object {
        const val MAX_THREADS_COUNT = 100
        const val MAX_ANALYZE_COUNT = 10000
    }
}