package com.zemoga.apptvdemo.ui.feature.home

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.BrowseSupportFragment.HEADERS_ENABLED
import androidx.leanback.widget.*
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.palette.graphics.Palette
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import coil.size.Scale
import com.zemoga.apptvdemo.R
import com.zemoga.apptvdemo.data.local.Category
import com.zemoga.apptvdemo.data.remote.Movie
import com.zemoga.apptvdemo.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.log
import kotlin.properties.Delegates


@AndroidEntryPoint
class HomeFragment : BrowseSupportFragment() {

    private val viewModel: HomeViewModel by viewModels()
    private var sSelectedBackgroundColor: Int by Delegates.notNull()
    private var sDefaultBackgroundColor: Int by Delegates.notNull()

    private val backgroundManager by lazy {
        BackgroundManager.getInstance(requireActivity()).apply {
            attach(requireActivity().window)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            prepareEntranceTransition()
        }

        sDefaultBackgroundColor =
            ContextCompat.getColor(requireContext(), R.color.default_background)
        sSelectedBackgroundColor =
            ContextCompat.getColor(requireContext(), R.color.selected_background)

        // over title
        headersState = HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true

        title = getString(R.string.app_name)

        // set fastLane (or headers) background color
        //brandColor = ContextCompat.getColor(requireContext(), R.color.fastlane_background)
        // set search icon color
        searchAffordanceColor = ContextCompat.getColor(requireContext(), R.color.search_opaque)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeData()
        setOnItemViewClickedListener { _, item, _, _ ->
            item as Movie
            viewModel.onMovieClicked(item)
        }

        setOnSearchClickedListener {
            viewModel.onSearchClicked()
        }


        viewModel.toast.asLiveData().observe(viewLifecycleOwner) { stringRes ->
            Toast.makeText(requireContext(), stringRes, Toast.LENGTH_SHORT).show()
        }

        setDynamicBackground()
    }


    private fun observeData() {
        viewModel.moviesResponse.asLiveData().observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Idle -> {
                }
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    displayData(resource.data)
                    startEntranceTransition()
                }
                is Resource.Error -> TODO()
            }
        }

        viewModel.navigateToDetail.asLiveData().observe(viewLifecycleOwner) {
            findNavController().navigate(
                HomeFragmentDirections.actionHomeToDetail(
                    it.category,
                    it.movie
                )
            )
        }
    }

    private fun setDynamicBackground() {

        val outMetrics = DisplayMetrics()

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val display = requireActivity()?.display
            display?.getRealMetrics(outMetrics)
        } else {
            @Suppress("DEPRECATION")
            val display = requireActivity()?.windowManager.defaultDisplay
            @Suppress("DEPRECATION")
            display.getMetrics(outMetrics)
        }

        onItemViewSelectedListener =
            OnItemViewSelectedListener { itemViewHolder, item, rowViewHolder, row ->
                if (item is Movie) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        delay(BACKGROUND_UPDATE_DELAY)
                        val imageRequest = ImageRequest.Builder(requireContext())
                            .data(item.imageUrl)
                            .crossfade(true)
                            .placeholder(R.drawable.default_background)
                            .scale(Scale.FIT)
                            .build()

                        Log.d("----->LOG<-------", "viewed item in presenter")
                        requireContext().imageLoader.execute(imageRequest).drawable?.let {
                            backgroundManager.drawable = it
                        }

                        /* if ((itemViewHolder.view as ImageCardView).isFocused) {
                             (itemViewHolder.view as ImageCardView).setInfoAreaBackgroundColor(
                                 sSelectedBackgroundColor
                             )
                         } else {
                             (itemViewHolder.view as ImageCardView).setInfoAreaBackgroundColor(
                                 sDefaultBackgroundColor
                             )
                         }*/

                    }
                }
            }

        /* setOnItemViewSelectedListener { itemViewHolder, item, _, _ ->
             if (itemViewHolder?.view != null) {
                 val bitmapDrawable =
                     (itemViewHolder.view as ImageCardView).mainImageView.drawable as? BitmapDrawable
                 if (bitmapDrawable != null) {
                     Palette.from(bitmapDrawable.bitmap).generate { palette ->
                         // Priority for vibrantSwatch, if not available dominantSwatch
                         (palette?.vibrantSwatch ?: palette?.dominantSwatch)?.let { swatch ->
                             backgroundManager.color = swatch.rgb
                         }
                     }
                 }
             }
         }*/
    }

    private fun displayData(categories: List<Category>) {
        val adapter = ArrayObjectAdapter(ListRowPresenter())
        for (category in categories) {
            val headerItem = HeaderItem(category.id, category.genre)
            val rowAdapter = ArrayObjectAdapter(PosterPresenter())
            for (movie in category.movies) {
                rowAdapter.add(movie)
            }
            adapter.add(ListRow(headerItem, rowAdapter))
        }
        this.adapter = adapter

        // Scrolling to row/column
        viewModel.scrollPos?.let { (catPos, moviePos) ->
            rowsSupportFragment.setSelectedPosition(
                catPos,
                true,
                ListRowPresenter.SelectItemViewHolderTask(moviePos)
            )
            viewModel.resetScrollPos()
        }
    }

    companion object {

        private const val BACKGROUND_UPDATE_DELAY = 300L
        private const val SHARED_ELEMENT_NAME = "transition"
        const val REQUEST_AUTHORIZATION = 1001
        const val REQUEST_GOOGLE_PLAY_SERVICES = 1002
    }
}