package com.example.android.covidhack;

import android.content.res.Resources;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

public class ResultAdapter extends FirestoreAdapter<ResultAdapter.ViewHolder> {


    public ResultAdapter(Query query){
        super(query);
    }

    @NonNull
    @Override
    public ResultAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater=LayoutInflater.from(viewGroup.getContext());
        return new ViewHolder(inflater.inflate(R.layout.result_list_item,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ResultAdapter.ViewHolder viewHolder, int i) {
        viewHolder.bind(getSnapshot(i));
    }

    static class ViewHolder extends RecyclerView.ViewHolder{


        TextView lattitude,longitude,macaddress,time,mobile;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lattitude=(TextView)itemView.findViewById(R.id.list_lattitude);
            longitude=(TextView)itemView.findViewById(R.id.list_longitude);
            macaddress=(TextView)itemView.findViewById(R.id.list_macaddress);
            time=(TextView)itemView.findViewById(R.id.list_timestamp);
            mobile=(TextView)itemView.findViewById(R.id.list_number);
        }

        public void bind(final DocumentSnapshot snapshot){
            /**Result result=snapshot.toObject(Result.class);
            Resources resources=itemView.getResources();

            lattitude.setText(""+result.getLattitude());
            longitude.setText(""+result.getLongitude());
            macaddress.setText(""+result.getMacaddress());
            time.setText(""+result.getTimestamp());
            mobile.setText(""+result.getPhnumber());**/
        }

    }
}
