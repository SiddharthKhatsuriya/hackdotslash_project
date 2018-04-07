package hackdotslash.curlyenigma.resolve.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hackdotslash.curlyenigma.resolve.R;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeAdapterViewHolder> {

    class HomeAdapterViewHolder extends RecyclerView.ViewHolder{
        public HomeAdapterViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public HomeAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_complaint, parent, false);
        return new HomeAdapterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(HomeAdapterViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 6;
    }
}
