package com.combintech.baojili.network;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.combintech.baojili.apphelper.PreferenceLoginHelper;
import com.combintech.baojili.application.BaseURL;
import com.combintech.baojili.model.BJItem;
import com.combintech.baojili.model.BJLocation;
import com.combintech.baojili.model.BJStock;
import com.combintech.baojili.model.BJTrHistory;
import com.combintech.baojili.model.CodeAndSizeList;
import com.combintech.baojili.model.ItemInOut;
import com.combintech.baojili.model.LoginResponse;
import com.combintech.baojili.model.MessageResponse;
import com.combintech.baojili.model.StockByItem;
import com.combintech.baojili.model.StockByLocation;
import com.combintech.baojili.model.UpdatePasswordResponse;
import com.combintech.baojili.model.User;
import com.combintech.baojili.network.errormodel.ErrorResponse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Hendry Setiadi
 */
public class RestClient {

    public static final String MULTIPART_FORM_DATA = "multipart/form-data";
    private static MyAPI service;

    public static MyAPI getClient() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .readTimeout(25, TimeUnit.SECONDS)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request();
                        Request.Builder reqBuilder;
                            if (PreferenceLoginHelper.isLoggedIn()) {
                            reqBuilder = request.newBuilder()
                                    .header("X-Authorization", PreferenceLoginHelper.getClientKey());
                        } else {
                            reqBuilder = request.newBuilder();
                        }
                        Request req = reqBuilder.build();
                        return chain.proceed(req);
                    }
                })
                .addInterceptor(new ErrorResponseInterceptor(ErrorResponse.class))
                .build();

        if (service == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BaseURL.API_BASE_URL)
                    .build();
            service = retrofit.create(MyAPI.class);
            return service;
        } else return service;

    }

    public interface MyAPI {
        @FormUrlEncoded
        @POST("login.php")
        Observable<LoginResponse> login(@Field("user_id") String userId, @Field("password") String password);

        @Headers({"Content-Type:application/json"})
        @GET("myinfo.php")
        Observable<LoginResponse> myinfo();

        @Multipart
        @POST("add_member.php")
        Observable<MessageResponse> addMember(@Part("username") RequestBody userName,
                                              @Part("name") RequestBody name,
                                              @Part("email") RequestBody email,
                                              @Part("password") RequestBody password,
                                              @Part("role") RequestBody role,
                                              @Part MultipartBody.Part photo); /* KEY = photo */

        @Headers({"Content-Type:application/json"})
        @GET("member.php")
        Observable<ArrayList<User>> getMemberList();

        @Multipart
        @POST("update_member.php")
        Observable<MessageResponse> updateMember(@Part("username") RequestBody userName,
                                                 @Part("name") RequestBody name,
                                                 @Part("email") RequestBody email,
                                                 @Part("role") RequestBody role,
                                                 @Part MultipartBody.Part photo); /* KEY = photo */

        @FormUrlEncoded
        @POST("update_password.php")
        Observable<UpdatePasswordResponse> updatePassword(@Field("username") String userId,
                                                          @Field("password") String password,
                                                          @Field("old_password") String oldPassword);

        @FormUrlEncoded
        @POST("delete_member.php")
        Observable<MessageResponse> deleteMember(@Field("username") String userId);

        @Headers({"Content-Type:application/json"})
        @GET("location.php")
        Observable<ArrayList<BJLocation>> getLocationlist(@Query("location_name") String locationName);

        @Headers({"Content-Type:application/json"})
        @GET("item_code_size_list.php")
        Observable<CodeAndSizeList> getCodeAndSizeList();

        @Headers({"Content-Type:application/json"})
        @GET("item.php")
        Observable<ArrayList<BJItem>> getItemlist(@Query("item_id") String itemId,
                                                  @Query("size") String size,
                                                  @Query("code") String code);

        @Headers({"Content-Type:application/json"})
        @GET("stock.php")
        Observable<ArrayList<BJStock>> getStockList(@Query("stock_id") String stockId,
                                                    @Query("location_id") String locationId,
                                                    @Query("item_id") String itemId);

        @Headers({"Content-Type:application/json"})
        @GET("stock_by_location.php")
        Observable<ArrayList<StockByLocation>> getStockListByLoc();

        @Headers({"Content-Type:application/json"})
        @GET("stock_by_item.php")
        Observable<ArrayList<StockByItem>> getStockListByItem();

        @FormUrlEncoded
        @POST("add_location.php")
        Observable<MessageResponse> addLocation(@Field("name") String locationName,
                                                @Field("type") String type,
                                                @Field("address") String address,
                                                @Field("latitude") String latitude,
                                                @Field("longitude") String longitude);

        @FormUrlEncoded
        @POST("delete_location.php")
        Observable<MessageResponse> deleteLocation(@Field("location_id") String locationId);

        @FormUrlEncoded
        @POST("update_location.php")
        Observable<MessageResponse> updateLocation(@Field("location_id") String locationId,
                                                   @Field("name") String locationName,
                                                   @Field("type") String type,
                                                   @Field("address") String address,
                                                   @Field("latitude") String latitude,
                                                   @Field("longitude") String longitude);

        @Headers({"Content-Type:application/json"})
        @GET("item_by_location.php")
        Observable<ArrayList<BJItem>> getItemByLocation(@Query("location_id") String locationId,
                                                        @Query("size") String size,
                                                        @Query("code") String code);

        @Multipart
        @POST("add_item.php")
        Observable<MessageResponse> addItem(@Part("description") RequestBody description,
                                            @Part("type") RequestBody type,
                                            @Part("code") RequestBody code,
                                            @Part("size") RequestBody size,
                                            @Part("price") RequestBody price,
                                            @Part("cost") RequestBody cost,
                                            @Part MultipartBody.Part photo); /* KEY = photo */

        @FormUrlEncoded
        @POST("delete_item.php")
        Observable<MessageResponse> deleteItem(@Field("item_id") String itemId);

        @Multipart
        @POST("update_item.php")
        Observable<MessageResponse> updateItem(@Part("item_id") RequestBody itemId,
                                               @Part("description") RequestBody description,
                                               @Part("type") RequestBody type,
                                               @Part("code") RequestBody code,
                                               @Part("size") RequestBody size,
                                               @Part("price") RequestBody price,
                                               @Part("cost") RequestBody cost,
                                               @Part MultipartBody.Part photo); /* KEY = photo */

        @Headers({"Content-Type:application/json"})
        @GET("item_in.php")
        Observable<ArrayList<ItemInOut>> getItemIn(@Query("location_id") String locationId,
                                                   @Query("start") String start,
                                                   @Query("row") String row,
                                                   @Query("is_deleted") String isDeleted);

        @Headers({"Content-Type:application/json"})
        @GET("item_out.php")
        Observable<ArrayList<ItemInOut>> getItemOut(@Query("location_id") String locationId,
                                                    @Query("start") String start,
                                                    @Query("row") String row,
                                                    @Query("is_deleted") String isDeleted);

        @FormUrlEncoded
        @POST("add_item_in.php")
        Observable<MessageResponse> addItemIn(@Field("location_id") String locationId,
                                              @Field("source_location_id") String sourceLocationId,
                                              @Field("item_id") String itemId,
                                              @Field("in_type") String inType,
                                              @Field("quantity") String quantity);

        @FormUrlEncoded
        @POST("add_item_out.php")
        Observable<MessageResponse> addItemOut(@Field("location_id") String locationId,
                                               @Field("target_location_id") String targetLocationId,
                                               @Field("item_id") String itemId,
                                               @Field("out_type") String outType,
                                               @Field("quantity") String quantity);

        @FormUrlEncoded
        @POST("delete_item_tr.php")
        Observable<MessageResponse> deleteItemTr(@Field("item_tr_id") String itemTrId);

        @Headers({"Content-Type:application/json"})
        @GET("item_history.php")
        Observable<ArrayList<BJTrHistory>> itemHistory(@Query("tr_item_id") String trItemId);

        @FormUrlEncoded
        @POST("update_item_in.php")
        Observable<MessageResponse> updateItemIn(@Field("tr_item_id") String trItemId,
                                                 @Field("location_id") String locationId,
                                                 @Field("source_location_id") String sourceLocationId,
                                                 @Field("item_id") String itemId,
                                                 @Field("in_type") String inType,
                                                 @Field("quantity") String quantity);

        @FormUrlEncoded
        @POST("update_item_out.php")
        Observable<MessageResponse> updateItemOut(@Field("tr_item_id") String trItemId,
                                                  @Field("location_id") String locationId,
                                                  @Field("target_location_id") String targetLocationId,
                                                  @Field("item_id") String itemId,
                                                  @Field("out_type") String outType,
                                                  @Field("quantity") String quantity);

    }

    public static MultipartBody.Part getMultiPartBodyPartFromPath(String key, String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        File file = new File(path);
        if (file.exists()) {
            // create RequestBody instance from file
            RequestBody requestFile =
                    RequestBody.create(
                            MediaType.parse(MULTIPART_FORM_DATA),
                            file
                    );
            return MultipartBody.Part.createFormData(key, file.getName(), requestFile);
        } else {
            return null;
        }
    }

    public static RequestBody getRequestBodyFromUri(String stringToPut) {
        return RequestBody.create(
                MediaType.parse(MULTIPART_FORM_DATA), stringToPut);
    }

}
