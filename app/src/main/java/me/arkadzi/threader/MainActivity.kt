package me.arkadzi.threader

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import me.arkadzi.threader.adapter.Adapter
import me.arkadzi.threader.model.Page
import me.arkadzi.threader.model.PageProcessor
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    val subject = PublishSubject.create<List<Page>>().toSerialized()

    private lateinit var adapter: Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val url = "https://www.24video.porn"

        adapter = Adapter(layoutInflater)
        rv.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            itemAnimator = null
            adapter = this@MainActivity.adapter
        }

        val pageProcessor = PageProcessor()
        val page = Page(url, pageProcessor, "anal", null)
        subject
            .doOnNext {
                Log.e("asd", "next")
            }
            .sample(1000, TimeUnit.MILLISECONDS)
//            .flatMapIterable { it }
//            .cast(Page::class.java)
//            .toList()
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::updateData, { Log.e("asd", "$it") })


        Observable.create<Boolean> {
            bfs(page, 1000, 10)
        }.subscribeOn(Schedulers.computation())
            .subscribe()
//        thread(start = true) {
//            try {
//                bfs(page)
//            } catch (e: InterruptedException) {
//                e.printStackTrace()
//            }
//        }
    }

    private fun updateData(data: List<Page>) {
        Log.e("asd", "data")
        adapter.updateData(data)
    }

    private class ProcessPriorityThreadFactory(private val threadPriority: Int) : ThreadFactory {

        override fun newThread(r: Runnable): Thread {
            val thread = Thread(r)
            thread.priority = threadPriority
            return thread
        }

    }

    @Throws(InterruptedException::class)
    fun bfs(start: Page, maxAnalyzePageCount: Int, threadsCount: Int): List<Page>? {
        val service =
            Executors.newFixedThreadPool(threadsCount, ProcessPriorityThreadFactory(Thread.MIN_PRIORITY)).apply {
            }
        val used = LinkedList<Page>()
        val queue = LinkedList<Page>()
        queue.add(start)
        while (!queue.isEmpty()) {
            val layerPart = mutableListOf<Page>()
            val node = queue.pop()
            layerPart.add(node)
            while (queue.peek()?.layer == node.layer && layerPart.size < threadsCount) {
                layerPart.add(queue.pop())
            }
            used.addAll(layerPart)
            notifyUpdates(used)
            service.invokeAll(layerPart.map { generateCallable(it) })
            notifyUpdates(used)
            if (queue.size + used.size < maxAnalyzePageCount) {
                queue.addAll(layerPart.flatMap { it.getStates() }.distinct().filter { it !in used })
                while (queue.size + used.size > maxAnalyzePageCount) {
                    queue.removeLast()
                }
            }

            if (finalState(used, maxAnalyzePageCount)) {
                return null
            }
        }
        return null
    }

    private fun notifyUpdates(used: LinkedList<Page>) {
        subject.onNext(used)
    }

    private fun generateCallable(queueElement: Page): Callable<Page> {
        return Callable {
            queueElement.process()
            queueElement as Page
            println("f " + queueElement.layer + " " + queueElement.analyzeTime + " " + queueElement.url)
            queueElement
        }
    }


    private fun finalState(queueElement: LinkedList<Page>, maxAnalyzePageCount: Int): Boolean {
        return queueElement.size >= maxAnalyzePageCount
    }

}
