package com.offical.shopz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.core.OrderBy;
import com.google.firebase.firestore.core.OrderBy.Direction;

public class YourOrdersActivity extends AppCompatActivity {
    DatabaseReference yourOrdersReference;
    RecyclerView orderList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_orders);
        orderList = findViewById(R.id.orders_list);
        LinearLayoutManager orderListLayoutManager = new LinearLayoutManager(YourOrdersActivity.this);
        orderList.setLayoutManager(orderListLayoutManager);
        orderList.setHasFixedSize(true);
        yourOrdersReference = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        Query query = yourOrdersReference.child("yourOrders").orderByChild("orderTime");
        FirebaseRecyclerOptions<YourOrdersList> options = new FirebaseRecyclerOptions.Builder<YourOrdersList>()
                .setQuery(query, YourOrdersList.class)
                .build();
        FirebaseRecyclerAdapter<YourOrdersList, YourOrdersViewHolder> yourordersAdapter = new FirebaseRecyclerAdapter<YourOrdersList, YourOrdersViewHolder>(options){

            @NonNull
            @Override
            public YourOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.individual_your_order,parent,false);
                return new YourOrdersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull YourOrdersViewHolder holder, int position, @NonNull YourOrdersList model) {
                final String orderId = getRef(position).getKey();
                Log.d("data",orderId);

                yourOrdersReference.child("yourOrders").child(orderId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        holder.setOrderId(snapshot.child("orderId").getValue().toString());
                        holder.setOrderTime(snapshot.child("orderTime").getValue().toString());
                        holder.setOrderStatus(snapshot.child("orderStatus").getValue().toString());
                        holder.setOrderTotal(snapshot.child("orderTotal").getValue().toString());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        };
        yourordersAdapter.startListening();
        orderList.setAdapter(yourordersAdapter);
    }

    private static class YourOrdersViewHolder extends RecyclerView.ViewHolder {
        public YourOrdersViewHolder(@NonNull View itemView) {
            super(itemView);
        }
        public void setOrderId(String order_id) {
            TextView orderId = itemView.findViewById(R.id.order_id);
            orderId.setText(order_id);
        }
        public void setOrderStatus(String order_status) {
            TextView orderId = itemView.findViewById(R.id.order_status);
            orderId.setText(order_status);
        }
        public void setOrderTime(String order_time) {
            TextView orderId = itemView.findViewById(R.id.order_time);
            orderId.setText(order_time);
        }
        public void setOrderTotal(String order_total) {
            TextView orderId = itemView.findViewById(R.id.order_total);
            orderId.setText(order_total);
        }
    }
}