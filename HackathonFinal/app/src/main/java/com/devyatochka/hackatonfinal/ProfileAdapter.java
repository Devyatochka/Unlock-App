package com.devyatochka.hackatonfinal;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by alexbelogurow on 01.04.17.
 */

class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    private List<Profile> profiles;

    public ProfileAdapter(List<Profile> profiles) {
        this.profiles = profiles;

    }

    public static class ProfileViewHolder extends RecyclerView.ViewHolder{

        private CardView mCardView;
        private TextView mTextViewLogin;
        private TextView mTextViewPassword;
        private ImageButton mImageButtonEye;

        public ProfileViewHolder(View itemView) {
            super(itemView);

            mCardView = (CardView) itemView.findViewById(R.id.cv_profile);
            mTextViewLogin = (TextView) itemView.findViewById(R.id.textViewCardLogin);
            mTextViewPassword = (TextView) itemView.findViewById(R.id.textViewCardPassword);
            mImageButtonEye = (ImageButton) itemView.findViewById(R.id.imageButtonEye);
        }


    }

    @Override
    public ProfileAdapter.ProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_item, parent, false);
        ProfileViewHolder countryViewHolder = new ProfileViewHolder(view);
        return countryViewHolder;
    }

    @Override
    public void onBindViewHolder(final ProfileAdapter.ProfileViewHolder holder, int position) {
        holder.mTextViewLogin.setText(profiles.get(position).getLogin());
        holder.mTextViewPassword.setText(profiles.get(position).getPassword());

        holder.mImageButtonEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences mIDpreferences = v.getContext().getSharedPreferences("id", Context.MODE_PRIVATE);
                SharedPreferences mpreferences = v.getContext().getSharedPreferences("pin_code", Context.MODE_PRIVATE);

                String key = mIDpreferences.getString("uuid","-1");
                key += mpreferences.getString("pin", "-1");
                DESedeEncryption encryption = null;
                try {
                    encryption = new DESedeEncryption(key);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

                holder.mTextViewPassword.setText(encryption.decrypt(holder.mTextViewPassword.getText().toString()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }
}
