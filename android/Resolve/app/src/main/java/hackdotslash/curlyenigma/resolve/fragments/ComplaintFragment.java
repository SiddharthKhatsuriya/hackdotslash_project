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

public class ComplaintFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(R.string.title_home);
        if(container != null)
            container.removeAllViews();
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_home);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new HomeAdapter());

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment f = new CreateComplaintFragment();
                getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.frame_layout_main, f)
                        .addToBackStack("create")
                        .commit();
            }
        });

        return view;
    }
}
