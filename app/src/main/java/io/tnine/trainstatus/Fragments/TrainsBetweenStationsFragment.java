package io.tnine.trainstatus.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import io.tnine.trainstatus.Adapters.TrainsBetweenStationsAdapter;
import io.tnine.trainstatus.Utils.ApiClient;
import io.tnine.trainstatus.Interfaces.ApiInterface;
import io.tnine.trainstatus.Models.ModelTrainsBetweenStations.TrainsBetweenStationsModel;
import io.tnine.trainstatus.R;
import io.tnine.trainstatus.Utils.Config;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrainsBetweenStationsFragment extends Fragment {

    RecyclerView recycleTrainsBetweenStations;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager mlayoutManager;
    ApiInterface apiInterface;
    private ProgressDialog progressDialog;

    public TrainsBetweenStationsFragment() {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("Test", "HEllo");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trains_between_stations, container, false);
        String passedSourceStationCode = "";
        String passedChosenDate = "";
        String passedDestinationStationCode = "";

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("loading....");
        progressDialog.setTitle("Trains between stations");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        progressDialog.show();


        passedSourceStationCode = getArguments().getString("src_stn_code");
        passedDestinationStationCode = getArguments().getString("dest_stn_code");
        passedChosenDate = getArguments().getString("trains_bw_date");

        Log.e("Hello", passedSourceStationCode);
        Log.e("Hello", passedDestinationStationCode);
        Log.e("Hello", passedChosenDate);

        recycleTrainsBetweenStations = (RecyclerView) view.findViewById(R.id.rv_trains_bw_stations);

        Call<TrainsBetweenStationsModel> call = apiInterface.getTrainsBetweenStations(passedSourceStationCode, passedDestinationStationCode, passedChosenDate, Config.myApiKey);
        call.enqueue(new Callback<TrainsBetweenStationsModel>() {
            @Override
            public void onResponse(Call<TrainsBetweenStationsModel> call, Response<TrainsBetweenStationsModel> response) {

                if (response.isSuccessful()) {
                 //   Toast.makeText(getActivity(),String.valueOf(response.body().getResponseCode()),Toast.LENGTH_SHORT).show();
                    if(response.body().getResponseCode() == 200) {
                        TrainsBetweenStationsModel model = response.body();

                        mAdapter = new TrainsBetweenStationsAdapter(model, getActivity());
                        recycleTrainsBetweenStations.setHasFixedSize(true);
                        mlayoutManager = new LinearLayoutManager(getActivity());
                        recycleTrainsBetweenStations.setLayoutManager(mlayoutManager);
                        recycleTrainsBetweenStations.setAdapter(mAdapter);

                        progressDialog.dismiss();
                    }
                    else {
                        Log.d("test", response.body().getResponseCode() + "");
                    }
                } else {
                    Log.d("Test", response.code() + "");
                }
            }

            @Override
            public void onFailure(Call<TrainsBetweenStationsModel> call, Throwable t) {
                Log.e("No Response Trains b/w", t.toString());
            }
        });


        return view;
    }
}
