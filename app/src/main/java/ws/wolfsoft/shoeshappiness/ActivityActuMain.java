package ws.wolfsoft.shoeshappiness;

/**
 * Created by Mekongo ABANDA on 04/10/17.
 */
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.florent37.materialviewpager.MaterialViewPager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import filactualite.RecyclerViewFragment;


public class ActivityActuMain extends AppCompatActivity { //implements View.OnClickListener{

    MaterialViewPager materialViewPager;
    View headerLogo;
    ImageView headerLogoContent;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actualite_main);

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();


//////////////////////////     ACTION SUR LE ACTUALITE_MAIN.XML (DEBUT) (n'oublions pas de déplier les regions pour voir)   ///////////////////////////////////////////////////////////////////////////////

        //4 onglets
        final int tabCount = 4;

        //les vues définies dans @layout/header_logo
        headerLogo = findViewById(R.id.headerLogo);
        headerLogoContent = (ImageView) findViewById(R.id.headerLogoContent);

        //le MaterialViewPager
        this.materialViewPager = (MaterialViewPager) findViewById(R.id.materialViewPager);
        this.materialViewPager.getViewPager().setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                //je créé pour chaque onglet un RecyclerViewFragment
                return RecyclerViewFragment.newInstance();
            }

            @Override
            public int getCount() {
                return tabCount;
            }

            //le titre à afficher pour chaque page
            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return getResources().getString(R.string.Actu_WeMoney);
                    case 1:
                        return getResources().getString(R.string.Musique);
                    case 2:
                        return getResources().getString(R.string.technologie);
                    case 3:
                        return getResources().getString(R.string.international);
                    default:
                        return "Page " + position;
                }
            }

            int oldItemPosition = -1;
            @Override
            public void setPrimaryItem(ViewGroup container, int position, Object object) {
                super.setPrimaryItem(container, position, object);

                //seulement si la page est différente
                if (oldItemPosition != position) {
                    oldItemPosition = position;

                    //définir la nouvelle couleur et les nouvelles images
                    String imageUrl = null;
                    int color = Color.BLACK;
                    Drawable newDrawable = null;

                    switch (position) {
                        case 0:
                            imageUrl = "https://previews.123rf.com/images/zven0/zven01010/zven0101000032/8063871-fleur-abstraite-vert-sur-un-fond-blanc--Banque-d'images.jpg";
                            color = getResources().getColor(R.color.green);
                            newDrawable = getResources().getDrawable(R.drawable.logo1n);
                            break;
                        case 1:
                            imageUrl = "http://media.torah-box.com/note-de-musique-juive-1724.jpg";
                            color = getResources().getColor(R.color.orange);
                            newDrawable = getResources().getDrawable(R.drawable.music);
                            break;
                        case 2:
                            imageUrl = "https://dailygeekshow.com/wp-content/uploads/2014/03/8-facettes-de-notre-cerveau-qui-ont-evolue-avec-la-technologie8.jpg";
                            color = getResources().getColor(R.color.cyan);
                            newDrawable = getResources().getDrawable(R.drawable.light);
                            break;
                        case 3:
                            imageUrl = "http://www.newpaltz.edu/media/international-student-services/MAP.jpg";
                            color = getResources().getColor(R.color.green_teal);
                            newDrawable = getResources().getDrawable(R.drawable.earth);
                            break;
                    }

                    //puis modifier les images/couleurs
                    int fadeDuration = 400;
                    materialViewPager.setColor(color, fadeDuration);
                    materialViewPager.setImageUrl(imageUrl, fadeDuration);
                    toggleLogo(newDrawable,color,fadeDuration);

                }
            }
        });

        //permet au viewPager de garder 4 pages en mémoire (à ne pas utiliser sur plus de 4 pages !)
        this.materialViewPager.getViewPager().setOffscreenPageLimit(tabCount);
        //relie les tabs au viewpager
        this.materialViewPager.getPagerTitleStrip().setViewPager(this.materialViewPager.getViewPager());
    }

    private void toggleLogo(final Drawable newLogo, final int newColor, int duration){

        //animation de disparition
        final AnimatorSet animatorSetDisappear = new AnimatorSet();
        animatorSetDisappear.setDuration(duration);
        animatorSetDisappear.playTogether(
                ObjectAnimator.ofFloat(headerLogo, "scaleX", 0),
                ObjectAnimator.ofFloat(headerLogo, "scaleY", 0)
        );

        //animation d'apparition
        final AnimatorSet animatorSetAppear = new AnimatorSet();
        animatorSetAppear.setDuration(duration);
        animatorSetAppear.playTogether(
                ObjectAnimator.ofFloat(headerLogo, "scaleX", 1),
                ObjectAnimator.ofFloat(headerLogo, "scaleY", 1)
        );

        //après la disparition
        animatorSetDisappear.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                //modifie la couleur du cercle
                ((GradientDrawable) headerLogo.getBackground()).setColor(newColor);

                //modifie l'image contenue dans le cercle
                headerLogoContent.setImageDrawable(newLogo);

                //démarre l'animation d'apparition
                animatorSetAppear.start();
            }
        });

        //démarre l'animation de disparition
        animatorSetDisappear.start();
   }
////////////////////////////////     ACTION SUR LE ACTUALITE_MAIN.XML (FIN)   ///////////////////////////////////////////////////////////////////////////////

//////////////////////////////  FireBase déconnexion, appel de la fonction (DEBUT) /////////////////////////////////////////////////////////
   //action sur la déconnexion
  //  @Override
  //  public void onClick(View view) {
   //    if(view == buttondeconnexion){
   //        firebaseAuth.signOut();
   //        finish();
   //        startActivity(new Intent(this, MainActivity.class));
   //    }
   // }
//////////////////////////////////  FireBase déconnexion, appel de la fonction (FIN) /////////////////////////////////////////////////////////



}

