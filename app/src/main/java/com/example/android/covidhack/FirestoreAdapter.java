package com.example.android.covidhack;

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public abstract class FirestoreAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>
        implements com.google.firebase.firestore.EventListener<QuerySnapshot>{
    private static String TAG="Firestore Adapter";

    private Query mQuery;
    private ListenerRegistration mRegistration;
    private ArrayList<DocumentSnapshot> mSnapshots=new ArrayList<>();

    public FirestoreAdapter(Query query){
        mQuery=query;
    }

    @Override
    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
        if(e!=null){
            Log.v(TAG,"Error is "+e);
        }
        for(DocumentChange change:queryDocumentSnapshots.getDocumentChanges()){
            DocumentSnapshot snapshot=change.getDocument();
            switch (change.getType()){
                case ADDED:
                    onDocumentAdded(change);
                    break;
                case MODIFIED:
                    onDocumentModified(change);
                    break;
                case REMOVED:
                    onDocumentRemoved(change);
                    break;
            }
        }
        onDatachanged();
    }

    public void startListening(){
        if(mQuery!=null && mRegistration==null){
            mRegistration=mQuery.addSnapshotListener(this);
        }
    }

    public void stopListening(){
        if(mRegistration!=null){
            mRegistration.remove();
            mRegistration=null;
        }
        mSnapshots.clear();
        notifyDataSetChanged();
    }

    public void setQuery(Query query){
        stopListening();

        mSnapshots.clear();
        notifyDataSetChanged();

        mQuery=query;
        startListening();
    }

    @Override
    public int getItemCount() {
        return mSnapshots.size();
    }

    protected DocumentSnapshot getSnapshot(int position){
        return mSnapshots.get(position);
    }

    protected void onError(FirebaseFirestoreException e){}

    protected void onDatachanged(){}

    protected void onDocumentAdded(DocumentChange change){
        mSnapshots.add(change.getNewIndex(),change.getDocument());
        notifyItemInserted(change.getNewIndex());
    }

    protected void onDocumentModified(DocumentChange change){
        if(change.getOldIndex()==change.getNewIndex()){
            mSnapshots.set(change.getOldIndex(),change.getDocument());
            notifyItemChanged(change.getOldIndex());
        }else {
            mSnapshots.remove(change.getOldIndex());
            mSnapshots.add(change.getNewIndex(),change.getDocument());
            notifyItemMoved(change.getOldIndex(),change.getNewIndex());
        }
    }

    protected void onDocumentRemoved(DocumentChange change){
        mSnapshots.remove(change.getOldIndex());
        notifyItemRemoved(change.getOldIndex());
    }

}
