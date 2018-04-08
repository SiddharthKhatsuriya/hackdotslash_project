package hackdotslash.curlyenigma.resolve.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hackdotslash.curlyenigma.resolve.R;
import hackdotslash.curlyenigma.resolve.adapters.HomeAdapter;

public class RegisterFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(R.string.title_register);
        if(container != null)
            container.removeAllViews();
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        return view;
    }
}
