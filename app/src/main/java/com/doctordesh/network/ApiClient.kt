package com.doctordesh.network

import android.content.Context
import com.doctordesh.helpers.AppPreference
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.ArrayList
import java.util.concurrent.TimeUnit


class ApiClient {

    companion object {

        var retrofit: Retrofit? = null



        fun getClient(): Retrofit {

            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)



            val okHttpClient = OkHttpClient.Builder()

                .connectTimeout(260, TimeUnit.SECONDS)
                .writeTimeout(260, TimeUnit.SECONDS)
                .readTimeout(260, TimeUnit.SECONDS)
                .addInterceptor(logging)

                .build()





            if (retrofit == null) {


                retrofit = Retrofit.Builder()
                    .baseUrl(ApiList.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(okHttpClient)
                    .build()
            }





            return retrofit!!
        }


        fun getCustomClient(mContext: Context): Retrofit {

            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            var okHttpClient = OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(object : Interceptor {
                    override fun intercept(chain: Interceptor.Chain): Response {
                        val original = chain.request()
                        // Request customization: add request headers
                        val requestBuilder = original.newBuilder()

                            .header("x-auth", AppPreference.getInstance(mContext).getAuthToken())

                        val request = requestBuilder.build()
                        return chain.proceed(request)
                    }
                })
                .build()

            /* okHttpClient.interceptors(object : Interceptor {
                 override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                     val original = chain.request()
                     // Request customization: add request headers
                     val requestBuilder = original.newBuilder()

                         .header("x-auth", AppPreference.getInstance(mContext).getAuthToken())

                     val request = requestBuilder.build()
                     return chain.proceed(request)
                 }
             })*/



            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(ApiList.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(okHttpClient)
                    .build()
            }





            return retrofit!!
        }


    }

}