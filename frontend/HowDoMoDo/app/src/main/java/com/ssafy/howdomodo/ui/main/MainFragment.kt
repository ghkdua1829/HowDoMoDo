package com.ssafy.howdomodo.ui.main

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.howdomodo.R
import com.ssafy.howdomodo.`object`.ObjectMovie
import com.ssafy.howdomodo.ui.Loading
import com.ssafy.howdomodo.ui.selectArea.SelectAreaActivity
import kotlinx.android.synthetic.main.fragment_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment : Fragment() {
    companion object {
        var movieDataBool = false
        var postDataBool = false
    }

    private val vm: MainViewModel by viewModel()
    private val mvm: MovieViewModel by viewModel()
//    var movieList: List<Movie> = listOf(
//        Movie("공작", "액션", 3.4, "url/image1"),
//        Movie("미스터빈", "코미디", 2.1, "url/image2"),
//        Movie("암살", "액션", 4.9, "url/image3"),
//        Movie("괴물", "스릴러", 4.6, "url/image4")
//    )

    var postingList = arrayOf("영화 테넷", "영화 인턴", "영화 공작")
    var postList = arrayListOf<String>()
    lateinit var postingAdapter: PostingAdapter
    lateinit var mainAdapter: MainAdapter

    var univIdx = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Loading.goLoading(view.context)
        mvm.getMovieData()


        Log.d("TAEK", postList.size.toString())
        // RecyclerView Apapter
        mainAdapter = MainAdapter(object : MainViewHolder.ClickListener {
            override fun movieClick(position: Int) {
                val movieTitle = mainAdapter.movieData[position].title
                val intent = Intent(activity, SelectAreaActivity::class.java)
                ObjectMovie.movieTitle = movieTitle
                startActivity(intent)
            }
        })
        movieObserve(mainAdapter)
        act_main_rv_movie.adapter = mainAdapter
        act_main_rv_movie.layoutManager = LinearLayoutManager(this.context).also {
            it.orientation = LinearLayoutManager.HORIZONTAL
        }
        act_main_rv_movie.setHasFixedSize(true)

        // Blog Posting Adapter
        postingAdapter = PostingAdapter(object : PostingViewHolder.ItemClickListener {
            override fun onItemClick(position: Int) {
                val intent = Intent(activity, WebviewActivity::class.java)
                intent.putExtra("url", postingAdapter.blogData[position].link)
                startActivity(intent)

            }

        })
        observe(postingAdapter)
        act_main_rv_posting.adapter = postingAdapter
        act_main_rv_posting.layoutManager = LinearLayoutManager(this.context).also {
            it.orientation = LinearLayoutManager.HORIZONTAL
        }
        act_main_rv_posting.setHasFixedSize(true)

        // Spinner (Dropdown) Adapter
//        act_main_spinner_posting.adapter = ArrayAdapter(
//            this.requireContext(),
//            android.R.layout.simple_spinner_dropdown_item,
//            postList
//        )
//
//        act_main_spinner_posting.setSelection(0)
        Log.e("asdsad", postList.size.toString())

        //아이템 선택 리스너


    }

    fun movieObserve(mainAdapter: MainAdapter) {
        mvm.loading.observe(this, Observer {
            movieDataBool = it

            if (!movieDataBool && !postDataBool) {
                Loading.exitLoading()
            }
        })

        vm.loading.observe(this, Observer {
            postDataBool = it
            if (!movieDataBool && !postDataBool) {
                Loading.exitLoading()
            }
        })

        mvm.movieData.observe(this, Observer {
            val movieList = it
            Log.d("TEST", movieList.size.toString())
            mainAdapter.setMovieItemList(movieList)
        })
        mvm.spinnerCopyData.observe(this, Observer {
            postList = it
            act_main_spinner_posting.adapter = ArrayAdapter(
                this.requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                postList
            )
            act_main_spinner_posting.setSelection(0)
            act_main_spinner_posting.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        println("영화를 선택하세요")
                    }

                    override fun onItemSelected(
                        p0: AdapterView<*>?,
                        p1: View?,
                        position: Int,
                        p3: Long
                    ) {
                        (p0!!.getChildAt(0) as TextView).setTextColor(Color.WHITE)
                        println("영화 제목: " + postList[position])
                        vm.getBlogData(postList[position])
                    }
                }
        })
    }

    fun observe(postingAdapter: PostingAdapter){
        vm.blogData.observe(this, Observer {
            val blogList = it
            postingAdapter.setBlogItemList(blogList)
        })

        vm.errorToast.observe(this, Observer {
            Toast.makeText(this.context, "Error", Toast.LENGTH_SHORT).show()
        })
    }

}