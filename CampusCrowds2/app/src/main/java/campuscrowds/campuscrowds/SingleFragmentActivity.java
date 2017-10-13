package campuscrowds.campuscrowds;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Matthew on 9/29/2017.
 * Subclasses of this SingleFragmentActivity will implement this method to return an instance of the fragment
 * that the activity is hosting.
 */

public abstract class SingleFragmentActivity extends AppCompatActivity {
    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment f = fragmentManager.findFragmentById(R.id.fragment_container);
        if(f == null){
            f = createFragment();
            fragmentManager.beginTransaction().add(R.id.fragment_container, f).commit();
        }
    }
}
