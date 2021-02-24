package com.alf.preciosaspromessas.utils;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.alf.preciosaspromessas.FavoriteItemActivity;
import com.alf.preciosaspromessas.R;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> implements Animation.AnimationListener {

    private final List<Verse> mDataList;
    private final LayoutInflater layoutInflater;
    private final Context mContext;
    protected final ListHelper db;

    public ListAdapter(Context context, List<Verse> dataList) {
        mDataList = dataList;
        mContext = context;
        layoutInflater = LayoutInflater.from(context);
        db = new ListHelper(context);
    }

    @NonNull
    @Override
    public ListAdapter.ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_fav_list, parent, false);
        return new ListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapter.ListViewHolder holder, int position) {
        String verse = mDataList.get(position).getVerse();
        holder.mVerse.setText(verse);

        holder.mVerse.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, FavoriteItemActivity.class);
            intent.putExtra("verse", verse);
            mContext.startActivity(intent);
        });

        holder.mRemove.setOnClickListener(view -> {

            final Animator animatorSet = AnimatorInflater.loadAnimator(mContext, R.animator.list_item_out);
            animatorSet.setTarget(holder.itemView);
            animatorSet.start();

            db.deleteVerse(mDataList.get(position));

            mDataList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mDataList.size());

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                animatorSet.end();
                holder.itemView.clearAnimation();

                final Animator animatorSet2 = AnimatorInflater.loadAnimator(mContext, R.animator.list_item_up);
                animatorSet2.setTarget(holder.itemView);
                animatorSet2.start();
                animatorSet2.end();
                holder.itemView.clearAnimation();

                notifyDataSetChanged();
            }, 500);


        });
    }

    @Override
    public int getItemCount() {
        return  mDataList.size();
    }

    static class ListViewHolder extends RecyclerView.ViewHolder {

        private final TextView mVerse;
        private final ImageButton mRemove;

        private ListViewHolder(@NonNull View itemView) {
            super(itemView);

            mVerse = itemView.findViewById(R.id.text_fav_list_item);
            mRemove = itemView.findViewById(R.id.btn_delete_item);

        }
    }

    @Override
    public void onAnimationStart(Animation animation) { }

    @Override
    public void onAnimationEnd(Animation animation) { }

    @Override
    public void onAnimationRepeat(Animation animation) { }
}
