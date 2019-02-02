package ws.wolfsoft.shoeshappiness;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class Settings_activity extends AppCompatActivity {

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    private ProgressDialog mProgressDialog;
    private ProgressBar mProgressBar;

    //Pour notre Layout de activity_settings on crée des instances pour le statut, la pp et le nom utilisateur

    private CircleImageView mDisplayImage;
    private TextView mName;
    private TextView mStatus;
    private TextView mStatustbn;
    private TextView mImagebtn;

    //Notre Stockage firebase
    private StorageReference mImageStorage;

    //On déclare un entier qui vaut un et qu'on va assigner dans la fonction du clic du bouton changer d'image de profil
    private static final int GALLERY_PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_activity);

       //notre progress bar
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar_pp);

        //pour notre progressBar déclarée là haut
        mProgressDialog = new ProgressDialog(this);

       // Déclarez une référence de stockage et l'initialisez dans la méthode onCreate.
        mImageStorage = FirebaseStorage.getInstance().getReference();

//////////////////////////////////////synchroniser le nom d'utilisateur, le statut et la photo de profile avec notre database firebase (DEBUT)////////////////////////////////////////////////////////////////
/////////////////////////////////////                                                                                                          ////////////////////////////////////////////////////////////////

        mDisplayImage = (CircleImageView) findViewById(R.id.profile_image );
        mName = (TextView) findViewById(R.id.profile_displayName );
        mStatus = (TextView) findViewById(R.id.settings_status);
        mStatustbn = (TextView) findViewById(R.id.settings_statut_btn);
        mImagebtn = (TextView) findViewById(R.id.settings_image_btn);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_uid = mCurrentUser.getUid();

        //ne pas oublier d'inclure les données dans "Users" de notre database firebase
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        //Afficher les données de cette Database lorsqu'il ny'a pas de connexion (mémoire cache). ( voir WeMoney.Class)
        mUserDatabase.keepSynced( true );

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //synchroniser les champs statuts, nom d'utilisateur, image de profile entre ceux du projet et de la BD firebase
                String nom = dataSnapshot.child("nom").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String profil_image = dataSnapshot.child("profil_image").getValue().toString();

                //désormais les noms et les status sont attribués à l'utilisateur grace à cette fonction
                mName.setText(nom);
                mStatus.setText(status);
                //charger l'image téléchargée qui se trouve dans notre storage firebase
                mProgressBar.setVisibility(View.VISIBLE);

                if(!image.equals("default")) {

                    //charger l'image téléchargée qui se trouve dans notre storage firebase
                    mProgressBar.setVisibility(View.VISIBLE);

                    //sans sauvegarder l'image de profil dans la mémoire cache on aura utilisé ce code
                    //Picasso.with(Settings_activity.this).load(image).placeholder(R.drawable.default_avatar).into(mDisplayImage);

                    //Mais si nous voulons voir la photo même en étant hors ligne nous utilisons ceci pour la mettre dans la mémoire cache
                    Picasso.with(Settings_activity.this).load(image).networkPolicy( NetworkPolicy.OFFLINE )
                            .placeholder(R.drawable.default_avatar).into( mDisplayImage, new Callback() {
                        @Override
                        public void onSuccess() {


                        }

                        @Override
                        public void onError() {

                            Picasso.with(Settings_activity.this).load(image).placeholder(R.drawable.default_avatar).into(mDisplayImage);

                        }
                    });

                }
                if (!image.equals( "image" )){
                    mProgressBar.setVisibility(View.INVISIBLE);
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

      ////////////////ICI nous pouvons egalement modifier le statut en cliquant sur le statut proprement dit///////////////////////////////////////
        mStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status_value = mStatus.getText().toString();
                Intent status_intent = new Intent(Settings_activity.this, StatusActivity.class);
                status_intent.putExtra("status_value", status_value);
                startActivity(status_intent);
            }
        });
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        /////////Action sur le Click du bouton changer de statut/////////
        mStatustbn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String status_value = mStatus.getText().toString();

                Intent status_intent = new Intent(Settings_activity.this, StatusActivity.class);
                status_intent.putExtra("status_value", status_value);
                startActivity(status_intent);
            }
        });

        /////////Action sur le Click du bouton changer d'image de profil/////////////
        mImagebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //pointer vers la galerie de téléphone de l'utilisateur afin de prendre une photos
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), 1);

                // ce code ci bas donne un sélecteur d'app qualifié dans la gestion de vos images, et envoit dans l'activité de recadrage une fois l'image sélectionnée
               /* CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(Settings_activity.this);
                 */
            }

        });
    }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == GALLERY_PICK && resultCode == RESULT_OK){

                // ce code ci bas  envoit dans l'activité de recadrage une fois l'image sélectionnée
                String imageuri = data.getDataString();
                // démarrer l'activité de recadrage pour l'image pré-acquise enregistrée sur l'appareil
                Uri imageUri = data.getData();
                CropImage.activity(imageUri).setAspectRatio(1,1).start(this);

               // Toast.makeText(Settings_activity.this, imageuri, Toast.LENGTH_SHORT).show();

            }

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {

                   //lorsque la tâche est un succès (choix et rognage de l'image) une progressDialog apparaît
                    mProgressDialog = new ProgressDialog(Settings_activity.this);
                    mProgressDialog.setTitle("Téléchargement de l'image...");
                    mProgressDialog.setMessage("Veuillez patienter pendant que nous téléchargeons et traitons l'image");
                    //SetCanceledonTouchOutside(false): ne pas enlever la barre de progression au touché à l'exterieure de celle ci
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();

                    Uri resultUri = result.getUri();

                    //
                    String current_user_id = mCurrentUser.getUid();

                    //télécharger l'image sélectionné par l'user vers le dossier "profile_images" du stockage firebase et créé un dossier quelqconque avec jpg à la fin
                    //le terme "random" à la fin de la ligne de code ci bas sera expliqué dans la fonction staique  String random
                    // StorageReference filepath = mImageStorage.child("profile_images").child(random() +".jpg");

                    //Mais bon nous allons laisser la fonction random pour donner comme nom à l'image de profil l'id de l'utilisateur courant
                    //grace à la fonction ci haut : String current_user_id = mCurrentUser.getUid()
                    StorageReference filepath = mImageStorage.child("profile_images").child(current_user_id +".jpg");
                    filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                           //Si l'image a été bien téléchargé dans le stockage fire base
                            if (task.isSuccessful()){
                            //Un message apparaiît

                                String download_url = task.getResult().getDownloadUrl().toString();


                                mUserDatabase.child("image").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful()){

                                            mProgressDialog.dismiss();
                                            mProgressBar.setVisibility(View.VISIBLE);
                                            Toast.makeText(Settings_activity.this, "Image téléchargée", Toast.LENGTH_SHORT).show();
                                            mProgressBar.setVisibility(View.INVISIBLE);
                                        }


                                    }
                                });

                            }
                            //Si le téléchargement de l'image dans notre stockage firebase échoue
                            else{
                            //Une message d'erreur apparaît
                                Toast.makeText(Settings_activity.this, "Erreur lors du téléchargement de l'image", Toast.LENGTH_SHORT).show();
                                mProgressDialog.dismiss();
                            }
                        }
                    });

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                    Exception error = result.getError();
                }
            }
        }

        //voici donc notre fonction statique qui va nous permettre de créer des noms aléatoires pour les photos de profiles sélectionnés par l'utilisateur
        //là haut nous ne pouvions pas juste à la fin de la ligne .child("image_name.jpg") sinon toutes les photos sélectionnées par les users
    //devaient porter le même nom et pourraient se prêter à confusion.
    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
    }


