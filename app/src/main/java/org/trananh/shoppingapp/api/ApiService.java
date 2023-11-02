package org.trananh.shoppingapp.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.trananh.shoppingapp.util.Constants;
import org.trananh.shoppingapp.util.MyHttpResponse;
import org.trananh.shoppingapp.util.MyHttpResponseArray;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();
    ApiService apiService = new Retrofit.Builder()
            .baseUrl(Constants.apiURL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService.class);

    @GET("/region/")
    Call<MyHttpResponseArray> getAllRegions();

    //Auth-------------------------------------------------------------------------------------------------------------------------------------
    @POST("/auth/login_mobile")
    Call<MyHttpResponse> login(@Body Map<String, String> data);
    @POST("/auth/register")
    Call<MyHttpResponse> register(@Body Map<String, String> data);

    //User-------------------------------------------------------------------------------------------------------------------------------------
    @GET("/user/phoneNumber/{phone_number}")
    Call<MyHttpResponse> getUserByPhoneNumber(@Path("phone_number") String phoneNumber);
    @GET("/user/login_name/{login_name}")
    Call<MyHttpResponse> getUserByLoginName(@Path("login_name") String loginName);
    @GET("/user/{id}")
    Call<MyHttpResponse> getUserById(@Path("id") String id);

    //Category---------------------------------------------------------------------------------------------------------------------------------
    @GET("/structure_value/category/")
    Call<Map<String, Object>> getAllCategory();
    @GET("/structure_value/category/parent_id/{parentId}")
    Call<Map<String, Object>> getCategoryByParentId(@Path("parentId") String parentId);
    @GET("structure_value/category/level/{level}")
    Call<Map<String, Object>> getCategoryByLevel(@Path("level") int level);

    //Address
    @GET("structure_value/type/1/level/{level}")
    Call<MyHttpResponseArray> getAddressByLevel(@Path("level") int level);
    @GET("/structure_value/type/1")
    Call<MyHttpResponseArray> getAllAddress();

    //Product----------------------------------------------------------------------------------------------------------------------------------------
    @GET("/product/")
    Call<Map<String, Object>> getAllProducts();
    @GET("/product/category/{categoryId}")
    Call<Map<String, Object>> getProductByCategory(@Path("categoryId") String categoryId);
    @GET("/product/detail/{id}")
    Call<Map<String, Object>> getProductDetail(@Path("id") String productId);

    //UnitOfMeasure-------------------------------------------------------------------------------------------------------------------------------
    @GET("/unit_of_measure/")
    Call<MyHttpResponseArray> getAllUnitOfMeasure();
    @GET("/unit_of_measure/lowest/product_id/{productId}")
    Call<MyHttpResponse> getLowestUnitByProductId(@Path("productId") String productId);
    @GET("/unit_of_measure/product_id/{productId}")
    Call<MyHttpResponseArray> getUnitsByProductId(@Path("productId") String productId);

    //PriceDetail
    @GET("/price_detail/")
    Call<MyHttpResponseArray> getAllPriceDetail();

    //Cart
    @GET("/cart/")
    Call<Map<String, Object>> getAllCart(@Header("token") String token);
    @POST("/cart/add/")
    Call<Map<String, Object>> addCart(@Header("token") String token, @Body Map<String, Object> map);
    @POST("/cart/sub/")
    Call<Map<String, Object>> subCart(@Header("token") String token, @Body Map<String, Object> map);
    @DELETE("/cart/{id}")
    Call<Map<String, Object>> deleteCart(@Header("token") String token, @Path("id") int id);
}
