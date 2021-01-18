package com.app.perk99driver.networking;


import android.util.Log;
import com.app.perk99driver.BuildConfig;
import com.app.perk99driver.utils.SharedPrefUtil;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import static okhttp3.logging.HttpLoggingInterceptor.Level.BODY;
import static okhttp3.logging.HttpLoggingInterceptor.Level.NONE;

/**
 * Created by 123 on 25-09-2017.*/

public class Injector {

    private static Retrofit provideRetrofit(String baseUrl)
    {
        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl)
                .client(provideOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private static OkHttpClient provideOkHttpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(provideHttpLoggingInterceptor())
                .addInterceptor(provideHeaderInterceptor())
                .addInterceptor(new ForbiddenInterceptor())
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES)
                .build();
    }

    private static HttpLoggingInterceptor provideHttpLoggingInterceptor()
    {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger()
          {
               @Override
               public void log(String message)
               {
               Log.e("Injector","ss "+message);
               }
          });
        httpLoggingInterceptor.setLevel(BuildConfig.DEBUG ? BODY : NONE);
        return httpLoggingInterceptor;
    }

    public static InterfaceApi provideApi()
    {
        return provideRetrofit(InterfaceApi.BASE_URL).create(InterfaceApi.class);
    }

    private static Interceptor provideHeaderInterceptor()
    {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException
            {

                SharedPrefUtil helper = SharedPrefUtil.getInstance();
                Request request;
                Log.e("auth","aaa "+helper.getAuthToken());
//                Log.e("ProvideHeaderInterceptor ",intercept(chain))
                if (!helper.getString(SharedPrefUtil.AUTH_TOKEN).equals("")) {
                    request = chain.request().newBuilder()
                           // .header("Accept", "application/json")
                            .header("authorization", "Bearer "+helper.getString(SharedPrefUtil.AUTH_TOKEN))
                            .build();
                } else {
                    request = chain.request().newBuilder().build();
              }
                return chain.proceed(request);
            }
        };
    }

    public static class ForbiddenInterceptor implements Interceptor {

        @Override
        public Response intercept(final Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);
            if (response.code() == 401) {
                SharedPrefUtil.getInstance().setLogin(false);
                //MyApplication.tokenExpired();
            }
            return response;
        }
    }
}