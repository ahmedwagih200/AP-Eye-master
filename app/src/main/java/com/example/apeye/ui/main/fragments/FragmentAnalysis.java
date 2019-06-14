package com.example.apeye.ui.main.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.apeye.R;
import com.example.apeye.api.ApiService;
import com.example.apeye.api.RetrofitBuilder;
import com.example.apeye.model.ApiResultResponse;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Abdel-Rahman El-Shikh on 14-Jun-19.
 */
public class FragmentAnalysis extends Fragment implements Callback<ApiResultResponse> {
    private static final String TAG = "FragmentAnalysis";
    private String time = "";
    private String plant = "";
    private ApiService apiService;
    private BarChart barChart;

    private ImageView imgAnalysis;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analysis, container, false);

        final MaterialButton buttonTime = view.findViewById(R.id.spinnerTime);
        final MaterialButton buttonPlant = view.findViewById(R.id.spinnerPlant);
        MaterialButton buttonAnalysis = view.findViewById(R.id.buttonAnalysis);
        barChart = view.findViewById(R.id.chart);
        imgAnalysis = view.findViewById(R.id.imgAnalysis);
        progressBar = view.findViewById(R.id.progressBar);

        apiService = RetrofitBuilder.createService(ApiService.class);

        final String[] spinnerTimeItems = new String[]{
                "Last Day",
                "Last 15 Days",
                "Last 1 Month",
                "Last 3 Months",
                "Last 6 Months",
                "Last Year",
                "All Times"
        };
        final String[] spinnerPlantItems = new String[]{
                "apple",
                "cherry",
                "corn",
                "grape",
                "peach",
                "pepper",
                "potato",
                "strawberry",
                "tomato"
        };

        final ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(
                Objects.requireNonNull(getActivity()),
                android.R.layout.simple_spinner_dropdown_item,
                spinnerTimeItems);

        final ArrayAdapter plantAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                spinnerPlantItems
        );

        buttonTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Time")
                        .setAdapter(timeAdapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position) {
                                time = spinnerTimeItems[position];
                                buttonTime.setText(time);
                                dialog.dismiss();
                            }
                        }).create().show();
            }
        });
        buttonPlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Choose Plant")
                        .setAdapter(plantAdapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int position) {
                                plant = spinnerPlantItems[position];
                                buttonPlant.setText(plant);
                                dialog.dismiss();
                            }
                        }).create().show();
            }
        });

        buttonAnalysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(time) && !TextUtils.isEmpty(plant)) {
                    barChart.setVisibility(View.GONE);
                    analysis();
                } else {
                    showErrorDialog("Please chose both time and plant to make analysis.");
                }
            }
        });

        return view;
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setMessage(message)
                .setCancelable(true)
                .setTitle("We are Sorry")
                .setIcon(R.drawable.ic_confused);
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void analysis() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            progressBar.setVisibility(View.VISIBLE);
            String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

            firebaseFirestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {

                        if (Objects.requireNonNull(task.getResult()).exists()) {

                            String userName = task.getResult().getString("name");

                            doAnalysis(userName);
                        }

                    } else {
                        progressBar.setVisibility(View.GONE);
                        String error = Objects.requireNonNull(task.getException()).getMessage();
                        Log.e(TAG, "onComplete: " + error);
                        Toast.makeText(getActivity(), "(FIRE_STORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }else
        {
            showErrorDialog("Please SignIn to use the analysis Feature");
        }
    }

    private void doAnalysis(String userName) {
        Log.e(TAG, "doanalysis: plant = " + plant + " userName = " + userName + " time = " + time);
        Call<ApiResultResponse> call = apiService.MakeAnalysis(plant, userName, time);
        call.enqueue(this);
    }

    @Override
    public void onResponse(@NonNull Call<ApiResultResponse> call, @NonNull Response<ApiResultResponse> response) {
        if (response.isSuccessful()) {
            assert response.body() != null;
            String mResponse = response.body().getResult();
            if (!TextUtils.isEmpty(mResponse)) {
                makeChartByResponse(mResponse);
            } else {
                showErrorDialog("Yo can't view this plant analysis as there are no data available for you.");
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    private void makeChartByResponse(String mResponse) {
         ArrayList<String> xAxis = new ArrayList<>();
         ArrayList<BarEntry> chartData = new ArrayList<>();
        int p = 0;
        String[] separated = mResponse.split(",");
        for (String s : separated) {
            String[] record = s.split(":");
            for (int i = 0; i < record.length; i++) {
                if (i == 0) {
                    xAxis.add(record[0]);
                } else {
                    chartData.add(new BarEntry(Integer.parseInt(record[1]), p));
                    p++;
                }
            }
        }
        BarDataSet barDataSet = new BarDataSet(chartData, "Analysis");
        BarData data = new BarData(xAxis, barDataSet);
        imgAnalysis.setVisibility(View.GONE);
        barChart.setVisibility(View.VISIBLE);
        XAxis mXAxis = barChart.getXAxis();
        mXAxis.setLabelsToSkip(0);
        Log.e(TAG, "makeChartByResponse: " + xAxis);
        barChart.setData(data);
        progressBar.setVisibility(View.GONE);

    }

    @Override
    public void onFailure(@NonNull Call<ApiResultResponse> call, @NonNull Throwable t) {
        Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
        progressBar.setVisibility(View.GONE);
    }
}
