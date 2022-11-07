package info.texnoman.virtualdars.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import info.texnoman.virtualdars.R
import info.texnoman.virtualdars.activity.LoginActivity
import info.texnoman.virtualdars.activity.SearchActivity
import info.texnoman.virtualdars.adapter.WishlistAdapter
import info.texnoman.virtualdars.api.RetrofitClient
import info.texnoman.virtualdars.model.IsWish
import info.texnoman.virtualdars.model.Wishlist
import info.texnoman.virtualdars.model.WishlistData
import io.paperdb.Paper
import maes.tech.intentanim.CustomIntent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WishlistFragment : Fragment(), WishlistAdapter.OnItemClickListener {
    lateinit var lottieAnimationView: LottieAnimationView
    lateinit var rlSignPlaceholder: RelativeLayout
    lateinit var btnLogin: Button
    lateinit var ivSearch: ImageView
    lateinit var llEmptyFavourite: LinearLayout
    lateinit var rlWishlist: RelativeLayout
    lateinit var rvWishlist: RecyclerView
    lateinit var adapter: WishlistAdapter
    var wishlists = mutableListOf<WishlistData>()
    lateinit var wishlist: Wishlist

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_wishlist, container, false)

        initView(root)

        ivSearch.setOnClickListener {
            startActivity(Intent(context, SearchActivity::class.java))
        }

        val token = Paper.book().read<String>("token")
        if (token != null) {
            rlWishlist.isVisible = true
            getWishlist()
        } else {
            rlSignPlaceholder.isVisible = true
        }

        btnLogin.setOnClickListener {
            startActivity(Intent(context, LoginActivity::class.java))
            CustomIntent.customType(context, "fadein-to-fadeout")
        }

        return root
    }

    private fun initView(view: View) {
        ivSearch = view.findViewById(R.id.ivSearch)
        lottieAnimationView = view.findViewById(R.id.lottieAnimationView)
        llEmptyFavourite = view.findViewById(R.id.llEmptyFavourite)
        rlWishlist = view.findViewById(R.id.rlWishlist)
        btnLogin = view.findViewById(R.id.btnLogin)
        rlSignPlaceholder = view.findViewById(R.id.rlSignPlaceholder)
        rvWishlist = view.findViewById(R.id.rvWishlist)
        rvWishlist.layoutManager = LinearLayoutManager(context)
    }

    private fun getWishlist() {
        RetrofitClient.instance_2.getWishlist().enqueue(object: Callback<Wishlist> {
                override fun onResponse(call: Call<Wishlist>, response: Response<Wishlist>) {
                    if (response.isSuccessful) {
                        wishlist = response.body()!!

                        if (wishlist.success) {
                            wishlists = wishlist.data as MutableList<WishlistData>

                            adapter = WishlistAdapter(this@WishlistFragment, context, wishlist.data)
                            rvWishlist.adapter = adapter

                            rvWishlist.isVisible = true
                            lottieAnimationView.isVisible = false

                            if (wishlists.isEmpty()) {
                                llEmptyFavourite.isVisible = true
                            }

                        } else {
                            Toast.makeText(context, wishlist.message, Toast.LENGTH_SHORT).show()
                            rlWishlist.isVisible = false
                            rlSignPlaceholder.isVisible = true
                        }

                    } else {
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                        rlWishlist.isVisible = false
                        rlSignPlaceholder.isVisible = true
                    }
                }

                override fun onFailure(call: Call<Wishlist>, t: Throwable) {

                }
            })
    }

    override fun onItemAddClick(position: Int) {
        RetrofitClient.instance_2.isWish(wishlists[position].id)
                .enqueue(object: Callback<IsWish> {
                    override fun onResponse(call: Call<IsWish>, response: Response<IsWish>) {
                        if (response.isSuccessful) {
                            val isWish = response.body()

                            if (isWish!!.success) {
                                wishlists.removeAt(position)
                                adapter.notifyDataSetChanged()
                                Toast.makeText(context, isWish.message, Toast.LENGTH_SHORT).show()

                                if (wishlists.isEmpty()) {
                                    llEmptyFavourite.isVisible = true
                                }

                            } else {
                                Toast.makeText(context, isWish.message, Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                        }

                    }

                    override fun onFailure(call: Call<IsWish>, t: Throwable) {

                    }
                })
    }
}