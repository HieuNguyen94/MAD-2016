package com.restful_client_android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import com.faradaj.blurbehind.BlurBehind;
import com.faradaj.blurbehind.OnBlurCompleteListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import de.hdodenhof.circleimageview.CircleImageView;


public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private Activity activity;
    public ArrayList<CommentData> commentDataList;
    private AsyncHttpClient client;

    public CommentAdapter(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        this.commentDataList = new ArrayList<CommentData>();
        client = new AsyncHttpClient();
    }

    public void deleteComment(final Context context, final CellFeedViewHolder holder, final CommentData comment) {
        JSONObject params = new JSONObject();
        StringEntity entity = null;
        try {
            params.put("id", comment.commentId);
            entity = new StringEntity(params.toString());
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        client.put(context, Variables.deleteCommentApiUrl, entity, "application/json", new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                System.out.println("Delete comment failed");
                Utils.showToast(context, "Delete comment failed");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Utils.showToast(context, "Delete comment failed");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Utils.showToast(context, "Delete comment failed");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String success = response.getString(Variables.apiSuccess);
                    String message = response.getString(Variables.apiMessage);
                    if (success.equals("true")) {
                        commentDataList.remove(comment);
                        notifyDataSetChanged();
                    } else {
                        System.out.println("Delete comment ERROR" + message);
                        Utils.showToast(context, Variables.messageOperationFailed);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);

        return new CellFeedViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        final CellFeedViewHolder holder = (CellFeedViewHolder) viewHolder;

        //bind data
        final CommentData comment = commentDataList.get(position);

        Picasso.with(context).load(comment.avatarUrl).transform(new CircleTransform()).into(holder.userAvatar);
        holder.content.setText(comment.content);
        holder.userName.setText(comment.username);
        holder.time.setText(comment.time);
        if (Variables.currentLoginUsername.equals(comment.username)) {
            holder.deleteButton.setVisibility(View.VISIBLE);
        } else {
            holder.deleteButton.setVisibility(View.GONE);
        }
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteComment(context, holder, comment);
            }
        });
    }

    public void updateItems() {
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getItemCount() {
        return commentDataList.size();
    }

    public void insertComment(int position, CommentData data) {
        commentDataList.add(position, data);
        notifyItemInserted(position);
    }

    public void insertCommentJSON(int position, JSONObject object) {
        try {
            CommentData comment = new CommentData(
                    object.getString("id"),
                    object.getString("userAvatar"),
                    object.getString("userName"),
                    object.getString("content"),
                    object.getString("postTime"));
            insertComment(position, comment);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void insertComment(CommentData data) {
        commentDataList.add(data);
    }

    public void insertCommentJSON(JSONObject object) {
        try {
            CommentData comment = new CommentData(
                    object.getString("id"),
                    object.getString("userAvatar"),
                    object.getString("userName"),
                    object.getString("content"),
                    object.getString("postTime"));
            insertComment(comment);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void removeCard(FeedCardData data) {
        int pos = commentDataList.indexOf(data);
        commentDataList.remove(pos);
        notifyItemRemoved(pos);
    }

    public void removeAllComment() {
        commentDataList.clear();
        notifyDataSetChanged();
    }

    public CommentData getCommentData(int position) {
        return commentDataList.get(position);
    }

    public static class CellFeedViewHolder extends RecyclerView.ViewHolder {
        ImageView userAvatar;
        TextView userName;
        TextView content;
        TextView time;
        ImageButton deleteButton;

        public CellFeedViewHolder(View view) {
            super(view);
            userAvatar = (ImageView) view.findViewById(R.id.userAvatarComment);
            userName = (TextView) view.findViewById(R.id.userNameComment);
            content = (TextView) view.findViewById(R.id.contentComment);
            time = (TextView) view.findViewById(R.id.dateComment);
            deleteButton = (ImageButton) view.findViewById(R.id.deleteButtonComment);
        }
    }

    class CommentData {
        public String commentId;
        public String avatarUrl;
        public String username;
        public String content;
        public String time;

        public CommentData(String commentId, String avatarUrl, String username, String content, String time) {
            this.commentId = commentId;
            if (avatarUrl.equals("")) {
                this.avatarUrl = Variables.defaultAvatarUrl;
            } else {
                this.avatarUrl = avatarUrl;
            }
            this.username = username;
            this.content = content;
            this.time = time;
        }
    }
}
