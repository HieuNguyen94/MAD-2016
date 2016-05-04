package com.restful_client_android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.faradaj.blurbehind.BlurBehind;
import com.faradaj.blurbehind.OnBlurCompleteListener;


public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private int itemsCount = 0;
    private Activity activity;

    public FeedAdapter(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_feed, parent, false);

        return new CellFeedViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final CellFeedViewHolder holder = (CellFeedViewHolder) viewHolder;
//        bindDefaultFeedItem(position, holder);
        //TODO bind event
        ((CellFeedViewHolder) viewHolder).btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context, "Like", Toast.LENGTH_SHORT).show();
                Snackbar.make(view, "This feature is not available at the moment", Snackbar.LENGTH_LONG).show();
            }
        });
        ((CellFeedViewHolder) viewHolder).btnComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context, "Comment", Toast.LENGTH_SHORT).show();
//                Snackbar.make(view, "This feature is not available at the moment", Snackbar.LENGTH_LONG).show();
                Intent intent = new Intent(activity, PostDetails.class);
                context.startActivity(intent);
            }
        });
        ((CellFeedViewHolder) viewHolder).ivUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context, "Profile", Toast.LENGTH_SHORT).show();
//                Snackbar.make(view, "This feature is not available at the moment", Snackbar.LENGTH_LONG).show();
                Intent intent = new Intent(activity, ProfileActivity.class);
                context.startActivity(intent);
            }
        });
        ((CellFeedViewHolder) viewHolder).tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context, "Profile", Toast.LENGTH_SHORT).show();
//                Snackbar.make(view, "This feature is not available at the moment", Snackbar.LENGTH_LONG).show();
                Intent intent = new Intent(activity, ProfileActivity.class);
                context.startActivity(intent);
            }
        });
        ((CellFeedViewHolder) viewHolder).ivCardImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "This feature is not available at the moment", Snackbar.LENGTH_LONG).show();
                BlurBehind.getInstance().execute(activity, new OnBlurCompleteListener() {
                    @Override
                    public void onBlurComplete() {
                        Intent intent = new Intent(activity, ViewImageActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        context.startActivity(intent);
                    }
                });
            }
        });
    }

    private void bindDefaultFeedItem(int position, CellFeedViewHolder holder) {
        if (position % 2 == 0) {
            holder.ivCardImage.setImageResource(R.drawable.img_feed_center_1);
//            holder.ivFeedBottom.setImageResource(R.drawable.img_feed_bottom_1);
        } else {
            holder.ivCardImage.setImageResource(R.drawable.img_feed_center_2);
//            holder.ivFeedBottom.setImageResource(R.drawable.img_feed_bottom_2);
        }

        holder.btnComments.setTag(position);
        holder.btnMore.setTag(position);
        holder.ivCardImage.setTag(holder);
        holder.btnLike.setTag(holder);

    }

    public void updateItems() {
        itemsCount = 10;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getItemCount() {
        return itemsCount;
    }

    public static class CellFeedViewHolder extends RecyclerView.ViewHolder {
        ImageView ivUserProfile;
        TextView tvUsername;
        ImageView ivCardImage;
//        ImageView ivFeedBottom;
        TextView tvDescription;
        ImageButton btnComments;
        ImageButton btnLike;
        ImageButton btnMore;
        TextSwitcher tsLikesCounter;
//        FrameLayout vImageRoot;

        public CellFeedViewHolder(View view) {
            super(view);
            ivUserProfile = (ImageView) view.findViewById(R.id.ivUserProfile);
            tvUsername = (TextView) view.findViewById(R.id.tvUsername);
            ivCardImage = (ImageView) view.findViewById(R.id.ivCardImage);
//            ivFeedBottom = (ImageView) view.findViewById(R.id.ivFeedBottom);
            tvDescription = (TextView) view.findViewById(R.id.tvDescription);
            btnComments = (ImageButton) view.findViewById(R.id.btnComments);
            btnLike = (ImageButton) view.findViewById(R.id.btnLike);
            btnMore = (ImageButton) view.findViewById(R.id.btnMore);
            tsLikesCounter = (TextSwitcher) view.findViewById(R.id.tsLikesCounter);
//            vImageRoot = (FrameLayout) view.findViewById(R.id.vImageRoot);
        }
    }
}
