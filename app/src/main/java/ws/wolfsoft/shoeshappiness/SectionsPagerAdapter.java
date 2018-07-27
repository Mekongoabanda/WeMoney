package ws.wolfsoft.shoeshappiness;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by HP on 12/10/2017.
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        //on assigne chaque fragment pour chaque case (j'sais pas trop comment dire cela) bref, vous mêmes constatez)
        switch (position) {
            case 0:
                RequestsFragment requestsFragment = new RequestsFragment();
                return requestsFragment;

            case 1:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;

            case 2:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        //trois cases
        return 3;
    }

    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "DEMANDES";

            case 1:
                return "CHATS";

            case 2:
                return "AMI(E)S";

            default: return  null;
        }

    }
}
