package ws.wolfsoft.shoeshappiness;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ws.wolfsoft.shoeshappiness.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {


    public RequestsFragment() {
        // Constructeur public vide requis
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Gonflez la disposition de ce fragment
        return inflater.inflate(R.layout.fragment_requests, container, false);
    }

}
