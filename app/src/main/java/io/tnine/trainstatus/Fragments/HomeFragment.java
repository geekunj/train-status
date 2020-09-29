package io.tnine.trainstatus.Fragments;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.tnine.trainstatus.Adapters.StationAutoCompleteAdapter;
import io.tnine.trainstatus.MainActivity;
import io.tnine.trainstatus.Models.ModelCancelledTrains.CancelledTrainsModel;
import io.tnine.trainstatus.Models.ModelPnrStatus.PnrStatusModel;
import io.tnine.trainstatus.Models.ModelRescheduledTrains.RescheduledTrainsModel;
import io.tnine.trainstatus.Utils.ApiClient;
import io.tnine.trainstatus.Utils.Config;
import io.tnine.trainstatus.Utils.DelayAutoCompleteTextView;
import io.tnine.trainstatus.Interfaces.ApiInterface;
import io.tnine.trainstatus.Models.Station;
import io.tnine.trainstatus.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentTransaction ft;
    private Fragment cancelledTrains = new CancelledTrains();
    private FloatingActionButton btn_get_pnr_status;
    private EditText et_pnr_number;
    ApiInterface apiInterface;
    private ProgressDialog progressDialog;

    public HomeFragment() {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        CardView btnLiveTrain = (CardView) view.findViewById(R.id.btn_live_train);
        final CardView btnRescheduledTrain = (CardView) view.findViewById(R.id.btn_rescheduled);

        CardView btnCancelledTrains = (CardView) view.findViewById(R.id.btn_cancelled);
        CardView btnPnrStatus = view.findViewById(R.id.btn_pnr);
        CardView btnTrainSchedule = view.findViewById(R.id.btn_train_schedule);
        CardView btnLiveStationStatus = view.findViewById(R.id.btn_live_station);
        CardView btnSeatAvailability = view.findViewById(R.id.btn_seat_availability);
        CardView btnFareEnquiry = view.findViewById(R.id.btn_fair_enquiry);
        btn_get_pnr_status = (FloatingActionButton)view.findViewById(R.id.btn_get_pnr_status);
        et_pnr_number = (EditText)view.findViewById(R.id.et_pnr_number);

        final Calendar c = Calendar.getInstance();
        final SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());

        //TODO: set a common OnClickListener for all buttons
        Call<CancelledTrainsModel> call = apiInterface.getCancelledTrains(formattedDate, Config.myApiKey);
        call.enqueue(new Callback<CancelledTrainsModel>() {
            @Override
            public void onResponse(Call<CancelledTrainsModel> call, Response<CancelledTrainsModel> response) {
//                Toast.makeText(getActivity(),String.valueOf(response.body().getResponseCode()),Toast.LENGTH_LONG).show();
                    if (response.isSuccessful()) {
                    if (response.body().getResponseCode() == 200) {
                        Config.setCancelledTrainsModel(response.body());
                    }
                }
            }
            @Override
            public void onFailure(Call<CancelledTrainsModel> call, Throwable t) {
                Log.e("CancelTrain res fail", t.toString());
            }
        });

        Call <RescheduledTrainsModel> callReschTrains = apiInterface.getRescheduledTrains(formattedDate, Config.myApiKey);
        callReschTrains.enqueue(new Callback<RescheduledTrainsModel>() {
            @Override
            public void onResponse(Call<RescheduledTrainsModel> callReschTrains, Response<RescheduledTrainsModel> response) {
                if (response.isSuccessful()) {
                    if (response.body().getResponseCode() == 200) {
                        Config.setRescheduledTrainsModel(response.body());
                    }
                }
            }

            @Override
            public void onFailure(Call<RescheduledTrainsModel> callReschTrains, Throwable t) {
                Log.e("Resch. Trains Res. fail", t.toString());
            }
        });

        btnCancelledTrains.setOnClickListener(new View.OnClickListener() {                          //Cancelled Trains Button
            @Override
            public void onClick(View view) {
                if (Config.getCancelledTrainsModel()!=null) {
                    if (Config.getCancelledTrainsModel().getResponseCode() == 200) {
                        Fragment cancelledTrains = new CancelledTrains();
                        ((MainActivity) getActivity()).performTransaction(cancelledTrains);
                    }
                }else Toast.makeText(getActivity(),"Problem in fetching data!",Toast.LENGTH_SHORT).show();

            }
        });

        btnLiveTrain.setOnClickListener(new View.OnClickListener() {                                //Live Train Status button
            @Override
            public void onClick(View view) {

                Fragment liveTrain = new LiveTrainFragment();
                ((MainActivity)getActivity()).performTransaction(liveTrain);


            }
        });

        btnRescheduledTrain.setOnClickListener(new View.OnClickListener() {                         //Rescheduled Trains button
            @Override
            public void onClick(View view) {
                if (Config.getRescheduledTrainsModel()!=null) {
                    if (Config.getRescheduledTrainsModel().getResponseCode() == 200) {
                        Fragment rescheduledTrain = new RescheduledTrainsFragment();
                        ((MainActivity) getActivity()).performTransaction(rescheduledTrain);
                    }
                }else Toast.makeText(getActivity(),"Problem in fetching data!",Toast.LENGTH_SHORT).show();
            }
        });

        btnTrainSchedule.setOnClickListener(new View.OnClickListener() {                            //Train Schedule Button
            @Override
            public void onClick(View view) {
                Fragment trainRoute = new TrainRouteDetailsFragment();
                ((MainActivity)getActivity()).performTransaction(trainRoute);
            }
        });

        btnLiveStationStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment liveStationStatus = new LiveStationFragment();
                ((MainActivity)getActivity()).performTransaction(liveStationStatus);
            }
        });

        btnPnrStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment pnrDetails = new PnrDetailsFragment();
                ((MainActivity)getActivity()).performTransaction(pnrDetails);

            }
        });

        btnSeatAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment seatAvailabilityDetailsFragment = new SeatAvailabilityDetailsFragment();
                ((MainActivity)getActivity()).performTransaction(seatAvailabilityDetailsFragment);

            }
        });

        btnFareEnquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment fareDetailsFragment = new FareDetailsFragment();
                ((MainActivity)getActivity()).performTransaction(fareDetailsFragment);

            }
        });

//        editPnrNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
//                if (i == EditorInfo.IME_ACTION_DONE) {
//
//
//                    return true;
//                }
//                return false;
//            }
//
//        });


        btn_get_pnr_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_pnr_number.getText().length() == 10) {
//                    InputMethodManager imm = (InputMethodManager) ((MainActivity)getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setMessage("loading....");
                    progressDialog.setTitle("Pnr status");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.show();
                    Call <PnrStatusModel> call = apiInterface.getPnrStatus(et_pnr_number.getText().toString(), Config.myApiKey);
                    call.enqueue(new Callback<PnrStatusModel>() {
                        @Override
                        public void onResponse(Call<PnrStatusModel> call, Response<PnrStatusModel> response) {
                            if (response.isSuccessful()) {
                                Log.e("Request hit", "is successful");
                                Toast.makeText(getActivity(),String.valueOf(response.body().getResponseCode()),Toast.LENGTH_SHORT).show();

                                if (response.body().getResponseCode() == 200) {
                                    Log.e("PNR Response", response.body().getResponseCode() + "");
                                    Config.setPnrStatusModel(response.body());
                                    Fragment pnrStatus = new PnrStatusFragment();
                                    ((MainActivity)getActivity()).performTransaction(pnrStatus);
                                    progressDialog.dismiss();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<PnrStatusModel> call, Throwable t) {
                            Log.e("PNR response failure", t.toString());
                        }
                    });
                }
            }
        });

        return view;
    }

}
