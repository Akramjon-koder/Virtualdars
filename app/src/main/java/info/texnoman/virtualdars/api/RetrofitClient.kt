package info.texnoman.virtualdars.api

import info.texnoman.virtualdars.utils.Utils
import io.paperdb.Paper
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val  original = chain.request()
            val token = Paper.book().read<String>("token")
            var token2 = ""

            if (token != null) {
                token2 = token
            }
            val requestBuilder = original.newBuilder()
                .addHeader("token", token2)
                .method(original.method, original.body)

            val requset = requestBuilder.build()
            chain.proceed(requset)
        }.build()

    val instance: Api by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(Utils.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(Api::class.java)
    }

    val instance_2: Api by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(Utils.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        retrofit.create(Api::class.java)
    }
}