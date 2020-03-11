package io.devbeans.swyft.network;

import android.accounts.NetworkErrorException;
import android.util.Log;

import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import io.devbeans.swyft.Databackbone;
import io.devbeans.swyft.interface_retrofit_delivery.delivery_earnings;
import io.devbeans.swyft.interface_retrofit_delivery.delivery_wallet;
import io.devbeans.swyft.interface_retrofit_delivery.history;
import io.devbeans.swyft.interface_retrofit_delivery.swift_api_delivery;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ApiController {

    private static final String TAG = ApiController.class.getSimpleName();

    private static final ApiController ourInstance = new ApiController();

    public static ApiController getInstance() {

        return ourInstance;
    }

    private ApiController() {
    }
    public void getEarnings() {
        Retrofit retrofit = Databackbone.getinstance().getRetrofitbuilder();
        swift_api_delivery riderapidata = retrofit.create(swift_api_delivery.class);

        Call<delivery_earnings> call = riderapidata.deliveryEarning(Databackbone.getinstance().rider.getId(),(Databackbone.getinstance().rider.getUserId()));
        call.enqueue(new Callback<delivery_earnings>() {
            @Override
            public void onResponse(Call<delivery_earnings> call, Response<delivery_earnings> response) {
                if(response.isSuccessful()){

                    delivery_earnings dailyearning = response.body();
                    Databackbone.getinstance().delivery_driver_earning = dailyearning;

                  //  DisableLoading();
                   // load_Data();
                   // update_view();
                }
                else{
                    //DisableLoading();
                }

            }

            @Override
            public void onFailure(Call<delivery_earnings> call, Throwable t) {
                System.out.println(t.getCause());

                //DisableLoading();
               // load_Data();
            }
        });
    }
    public void getwallet() {
        Retrofit retrofit = Databackbone.getinstance().getRetrofitbuilder();
        swift_api_delivery riderapidata = retrofit.create(swift_api_delivery.class);

        Call<delivery_wallet> call = riderapidata.deliverywallet(Databackbone.getinstance().rider.getId(),(Databackbone.getinstance().rider.getUserId()));
        call.enqueue(new Callback<delivery_wallet>() {
            @Override
            public void onResponse(Call<delivery_wallet> call, Response<delivery_wallet> response) {
                if(response.isSuccessful()){

                    delivery_wallet wallet = response.body();
                    Databackbone.getinstance().wallet = wallet;

                    //  DisableLoading();
                    // load_Data();
                    // update_view();
                }
                else{
                    //DisableLoading();
                }

            }

            @Override
            public void onFailure(Call<delivery_wallet> call, Throwable t) {
                System.out.println(t.getCause());

                //DisableLoading();
                // load_Data();
            }
        });
    }
    public void gethistory() {
        Retrofit retrofit = Databackbone.getinstance().getRetrofitbuilder();
        swift_api_delivery riderapidata = retrofit.create(swift_api_delivery.class);

        Call<List<history>> call = riderapidata.deliveryhistory(Databackbone.getinstance().rider.getId(),(Databackbone.getinstance().rider.getUserId()));
        call.enqueue(new Callback<List<history>>() {
            @Override
            public void onResponse(Call<List<history>> call, Response<List<history>> response) {
                if(response.isSuccessful()){

                    List<history> history = response.body();
                    Databackbone.getinstance().history = history;

                    //  DisableLoading();
                    // load_Data();
                    // update_view();
                }
                else{
                    //DisableLoading();
                }

            }

            @Override
            public void onFailure(Call<List<history>> call, Throwable t) {
                System.out.println(t.getCause());

                //DisableLoading();
                // load_Data();
            }
        });
    }
    /*
    public void getStatus(final long id, final String orderStatus) {
        Call<WebResponse> call = getService().getOrderStatus(id);
        call.enqueue(new Callback<WebResponse>() {
            @Override
            public void onResponse(Call<WebResponse> call,
                                   final Response<WebResponse> response) {
                if (response.isSuccessful()) {
                    boolean status = response.body().isStatus();
                    if (status) {
                        if (!orderStatus.equalsIgnoreCase(response.body().getMessage())) {
                            EventBus.getDefault().post(new UpdateOrderStatusEvent(id, response.body().getMessage()));
                        }
//                        EventBus.getDefault().post(new SuccessApiResponseEvent(ApiResponseEvent.REQUEST_PUSH_ORDER_API_SUCCESS, response.body()));
                    } else {
//                        EventBus.getDefault().post(new SuccessApiResponseEvent(ApiResponseEvent.REQUEST_PUSH_ORDER_API_FAILURE, response.body()));
                    }
                }
            }

            @Override
            public void onFailure(Call<WebResponse> call, Throwable t) {
//                EventBus.getDefault().post(new SuccessApiResponseEvent(ApiResponseEvent.REQUEST_PUSH_ORDER_API_FAILURE, handleError(t)));
            }
        });
    }
    */
    public String handleError(Throwable t) {

        String error = somethingWentWrong();
        if (t == null)
            return error;

        if (t instanceof SocketTimeoutException)
            error = t.getLocalizedMessage();
        else if (t instanceof IOException)
            error = t.getLocalizedMessage();
        else if (t instanceof IllegalStateException)
            error = t.getLocalizedMessage();
        else if (t instanceof NetworkErrorException)
            error = t.getLocalizedMessage();
        else if (t instanceof JsonSyntaxException)
            error = t.getLocalizedMessage();

        if (error != null)
            Log.e(TAG, error);
        else
            error = somethingWentWrong();

        return error;
    }

    public String somethingWentWrong() {
        return "Something went wrong. Please try again later!";
    }


}
