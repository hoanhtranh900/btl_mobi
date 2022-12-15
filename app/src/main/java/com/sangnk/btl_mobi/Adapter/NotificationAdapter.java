package com.sangnk.btl_mobi.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.sangnk.btl_mobi.Fragments.ProfileFragment;
import com.sangnk.btl_mobi.Model.Notification;
import com.sangnk.btl_mobi.Model.User;
import com.sangnk.btl_mobi.R;
import com.sangnk.btl_mobi.network.ApiClient;
import com.sangnk.btl_mobi.network.ApiInterface;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private static final String TAG = "NotificationAdapter";
    private Context mContext;
    private List<Notification> mNotifications;
    private boolean isFragment;

    public NotificationAdapter(Context mContext, List<Notification> mNotifications, boolean isFragment) {
        this.mContext = mContext;
        this.mNotifications = mNotifications;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: Started");
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false);
        Log.d(TAG, "onCreateViewHolder: ");
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: Started");
//        firebaseUser  = FirebaseAuth.getInstance().getCurrentUser();

        Notification notification = mNotifications.get(position);
        holder.time.setText(notification.getCreateTime());
        holder.content.setText(notification.getContent());
    }



    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView time;
        public TextView content;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            init();
        }

        private void init() {
            content = itemView.findViewById(R.id.notification_content);
            time = itemView.findViewById(R.id.notification_time);
        }
    }
}
