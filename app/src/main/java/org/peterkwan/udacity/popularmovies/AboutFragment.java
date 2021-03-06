package org.peterkwan.udacity.popularmovies;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import lombok.NoArgsConstructor;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnListItemClickListener} interface
 * to handle interaction events.
 */
@NoArgsConstructor
public class AboutFragment extends Fragment implements AboutItemListAdapter.ListItemClickListener {

    private OnListItemClickListener mListener;

    @BindArray(R.array.about_list)
    String[] aboutList;

    @BindView(R.id.aboutListView)
    RecyclerView aboutListView;

    private Unbinder unbinder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);

        unbinder = ButterKnife.bind(this, rootView);

        AboutItemListAdapter listAdapter = new AboutItemListAdapter(aboutList);
        listAdapter.setItemClickListener(this);
        aboutListView.setAdapter(listAdapter);
        aboutListView.addItemDecoration(new DividerItemDecoration(aboutListView.getContext(), DividerItemDecoration.VERTICAL));

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListItemClickListener) {
            mListener = (OnListItemClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListItemClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onItemClick(int position) {
        if (mListener != null)
            mListener.onAboutItemSelected(position);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListItemClickListener {
        void onAboutItemSelected(int index);
    }
}
