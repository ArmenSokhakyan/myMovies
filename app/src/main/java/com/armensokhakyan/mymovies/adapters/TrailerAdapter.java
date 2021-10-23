package com.armensokhakyan.mymovies.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.armensokhakyan.mymovies.R;
import com.armensokhakyan.mymovies.data.Trailer;

import java.util.ArrayList;
import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterVieHolder> {

    private List<Trailer> trailers;
    private OnClickTrailerAdapterListener onClickTrailerAdapterListener;

    public TrailerAdapter() {
        trailers = new ArrayList<>();
    }

    @NonNull
    @Override
    public TrailerAdapterVieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_item, parent, false);
        return new TrailerAdapterVieHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerAdapterVieHolder holder, int position) {
        Trailer trailer = trailers.get(position);
        holder.textViewTrailerName.setText(trailer.getName());
    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    public interface OnClickTrailerAdapterListener{
        void onClickTrailer(String url);
    }

    class TrailerAdapterVieHolder extends RecyclerView.ViewHolder{

        private TextView textViewTrailerName;

        public TrailerAdapterVieHolder(@NonNull View itemView) {
            super(itemView);
            textViewTrailerName = itemView.findViewById(R.id.textViewTrailerName);
            textViewTrailerName.setOnClickListener(view -> {
                if(onClickTrailerAdapterListener != null){
                    onClickTrailerAdapterListener.onClickTrailer(trailers.get(getAdapterPosition()).getKey());
                }
            });
        }
    }

    public void setTrailers(ArrayList<Trailer> trailers) {
        this.trailers = trailers;
        notifyDataSetChanged();
    }

    public void setOnClickTrailerAdapterListener(OnClickTrailerAdapterListener onClickTrailerAdapterListener) {
        this.onClickTrailerAdapterListener = onClickTrailerAdapterListener;
    }
}
