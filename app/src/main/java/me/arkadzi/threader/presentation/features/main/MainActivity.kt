package me.arkadzi.threader.presentation.features.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import me.arkadzi.threader.R
import me.arkadzi.threader.app.layout.utils.setProgressChangeListener
import me.arkadzi.threader.app.layout.utils.textStr
import me.arkadzi.threader.domain.model.Page
import me.arkadzi.threader.domain.processing.BfsPageAnalyzer
import me.arkadzi.threader.presentation.adapter.Adapter
import org.koin.android.scope.currentScope


class MainActivity : AppCompatActivity(), IMainView {
    val presenter: IMainPresenter by currentScope.inject()
    private lateinit var adapter: Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        presenter.onCreate(this)
    }

    override fun onDestroy() {
        presenter.releaseView()
        super.onDestroy()
    }

    override fun updateState(state: BfsPageAnalyzer.State) {
        val isRunning = state == BfsPageAnalyzer.State.RUNNING
        btStart.isEnabled = !isRunning
        btStop.isEnabled = isRunning
        sbThread.isEnabled = !isRunning
        sbMaxLoad.isEnabled = !isRunning
        etUrl.isEnabled = !isRunning
        etQuery.isEnabled = !isRunning
    }

    override fun updateProgress(progress: Int, max: Int) {
        pbAnalyze.max = max
        pbAnalyze.progress = progress
    }

    override fun updateList(data: List<Page>) {
        Log.e("asd", "data")
        adapter.updateData(data)
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun initViews() {
        adapter = Adapter(layoutInflater)
        rv.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            itemAnimator = null
            adapter = this@MainActivity.adapter
        }
        sbMaxLoad.apply {
            setProgressChangeListener {
                tvMaxLoadHint.text = getString(R.string.max_load, seekBarProgress(this))
            }
            max = seekBarPagesMax()
            progress = max / 2
        }
        sbThread.apply {
            setProgressChangeListener {
                tvThreadsHint.text = getString(R.string.max_threads, seekBarProgress(this))
            }
            max = seekBarThreadMax()
            progress = max / 2
        }
        btStart.setOnClickListener {
            presenter.onStartClick(
                    etUrl.textStr,
                    etQuery.textStr,
                    seekBarProgress(sbThread),
                    seekBarProgress(sbMaxLoad)
            )
        }
        btStop.setOnClickListener {
            presenter.onStopClick()
        }
    }

    private fun seekBarProgress(seekBar: SeekBar) = seekBar.progress + 1

    private fun seekBarThreadMax() = BfsPageAnalyzer.MAX_THREADS_COUNT - 1

    private fun seekBarPagesMax() = BfsPageAnalyzer.MAX_ANALYZE_COUNT - 1

}
