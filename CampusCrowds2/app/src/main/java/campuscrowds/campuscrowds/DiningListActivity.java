package campuscrowds.campuscrowds;


import android.support.v4.app.Fragment;

/**
 * Created by Matthew on 9/29/2017.
 * This is the main activity, it is the first activity started, it hosts DiningListFragment
 */

public class DiningListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new DiningListFragment();
    }
}
