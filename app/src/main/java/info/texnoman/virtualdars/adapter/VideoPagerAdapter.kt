package info.texnoman.virtualdars.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import info.texnoman.virtualdars.fragment.VideoFragment
import info.texnoman.virtualdars.model.VideoData
class VideoPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    var showDetails = listOf<VideoData>()
    override fun getItemCount(): Int {
        return showDetails.size
    }
    override fun createFragment(position: Int): Fragment {
        return VideoFragment().apply {
            arguments = bundleOf(
                VideoFragment.KEY_POSITION to position
            )

        }
    }
}
