package info.texnoman.virtualdars.activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.*
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.airbnb.lottie.Lottie
import com.airbnb.lottie.LottieAnimationView
import info.texnoman.virtualdars.R
import info.texnoman.virtualdars.api.RetrofitClient
import info.texnoman.virtualdars.model.BuyCourse
import info.texnoman.virtualdars.model.DetailData
import info.texnoman.virtualdars.model.TransactionModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import uz.click.mobilesdk.BuildConfig
import uz.click.mobilesdk.core.ClickMerchant
import uz.click.mobilesdk.core.ClickMerchantConfig
import uz.click.mobilesdk.core.ClickMerchantManager
import uz.click.mobilesdk.core.callbacks.ClickMerchantListener
import uz.click.mobilesdk.impl.paymentoptions.PaymentOptionEnum
import uz.click.mobilesdk.impl.paymentoptions.ThemeOptions

class BuyCourseActivity : AppCompatActivity() {
    lateinit var tvContent: TextView
    lateinit var ivBack: ImageView
    lateinit var click: Button
    lateinit var lottie: LottieAnimationView
    lateinit var linearLayout: LinearLayout
    private lateinit var themeMode: ThemeOptions
    lateinit var data:DetailData
     var transationId=0
    //dgQwuBPH2r
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_course)

        checkDarkThemeMode(this)
        data =CourseActivity.data

        getTransaction()
        linearLayout =findViewById(R.id.layout)
        tvContent = findViewById(R.id.tvContent)
        ivBack = findViewById(R.id.ivBack)
        click =findViewById(R.id.click)
         lottie =findViewById<LottieAnimationView>(R.id.lottie)
        click.setOnClickListener {
            if (transationId==0){
                Toast.makeText(this, "Iltimos biroz kuting", Toast.LENGTH_SHORT).show()
               return@setOnClickListener
            }
            val config = ClickMerchantConfig.Builder()
                .serviceId(15411L)
                .merchantId(7400L)
                .amount(data.price.toDouble())
                //transaction param is optional (if you not have your billing system)
                .transactionParam(transationId.toString())
//              .returnUrl("https://www.youtube.com/")
                .locale("UZ")
                .option(PaymentOptionEnum.CLICK_EVOLUTION)
                .theme(themeMode)
                .productName(data.title)
                //.productDescription(data.requirements[0].toString())
                .merchantUserId(15592L)
               // .requestId("")
                .build()

            ClickMerchantManager.logs = BuildConfig.DEBUG

            ClickMerchant.init(
                supportFragmentManager, config,
                object : ClickMerchantListener {
                    override fun onReceiveRequestId(id: String) {
                      /*  currentUser.requestId = id*/
                    }

                    override fun onSuccess(paymentId: Long) {
                       Log.e("succes",paymentId.toString())
                        startActivity(Intent(this@BuyCourseActivity,MainActivity::class.java))
                        /*  currentUser.paymentId = paymentId
                          currentUser.paid = true*/
                    }

                    override fun onFailure() {
                      //  currentUser.requestId = ""
                    }
                    override fun onInvoiceCancelled() {
                      //  currentUser.requestId = ""
                    }

                    override fun closeDialog() {
                       // findNavController(req).navigate(R.id.navigation_my_course)
                        ClickMerchant.dismiss()

                    }
                }
            )

        }
        ivBack.setOnClickListener {
            finish()
        }
    }
    private fun checkDarkThemeMode(context: Context) {
        val mode = context.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)
        when (mode) {
            Configuration.UI_MODE_NIGHT_NO -> {
                themeMode = ThemeOptions.LIGHT
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                themeMode = ThemeOptions.NIGHT
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                themeMode = ThemeOptions.LIGHT
            }
        }
    }
    private fun getTransaction(){
        RetrofitClient.instance_2.getTransactionParams("uz",data.id).enqueue(object :Callback<TransactionModel>{
            override fun onResponse(call: Call<TransactionModel>, response: Response<TransactionModel>) {
                lottie.visibility = View.VISIBLE
                linearLayout.visibility =View.GONE
                if (response.isSuccessful){
                    lottie.visibility = View.GONE
                    linearLayout.visibility =View.VISIBLE
                    val gettransaction =response.body()
                    transationId = gettransaction?.data?.id!!
                    if (gettransaction.success == true){
                        Log.e("params",gettransaction.data.toString())
                    }else{
                        lottie.visibility = View.GONE
                        linearLayout.visibility =View.VISIBLE
                        Toast.makeText(applicationContext, gettransaction?.message, Toast.LENGTH_SHORT).show()
                    }
                }else{

                    lottie.visibility = View.GONE
                    linearLayout.visibility =View.VISIBLE
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()

                }
            }
            override fun onFailure(call: Call<TransactionModel>, t: Throwable) {
            }

        })
    }

    private fun setContent() {
        RetrofitClient.instance.buyCourse().enqueue(object: Callback<BuyCourse> {
            override fun onResponse(call: Call<BuyCourse>, response: Response<BuyCourse>) {
                if (response.isSuccessful) {
                    val buyCourse = response.body()
                    if (buyCourse!!.success) {
                        tvContent.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Html.fromHtml(buyCourse.data, Html.FROM_HTML_MODE_COMPACT)
                        } else {
                            Html.fromHtml(buyCourse.data)
                        }
                    } else {
                        Toast.makeText(applicationContext, buyCourse.message, Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(applicationContext, response.message(), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BuyCourse>, t: Throwable) {

            }
        })
    }
}