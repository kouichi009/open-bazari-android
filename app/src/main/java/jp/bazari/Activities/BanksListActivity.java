package jp.bazari.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import jp.bazari.Adapters.BanksListAdapter;
import jp.bazari.Apis.IntentKey;
import jp.bazari.R;

import java.util.ArrayList;


public class BanksListActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banks_list);

        ImageView backArrow = (ImageView) findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final ListView listView = (ListView) findViewById(R.id.listView);

        final ArrayList<String> list = new ArrayList<>();
        list.add("主要金融機関");
        list.add("三菱ＵＦＪ銀行");
        list.add("みずほ銀行");
        list.add("りそな銀行");
        list.add("埼玉りそな銀行");
        list.add("三井住友銀行");
        list.add("ジャパンネット銀行");
        list.add("楽天銀行");
        list.add("ゆうちょ銀行");
        list.add("50音順");
        list.add("あ行");
        list.add("か行");
        list.add("さ行");
        list.add("た行");
        list.add("な行");
        list.add("は行");
        list.add("ま行");
        list.add("や行");
        list.add("ら行");
        list.add("わ行");

        listView.setAdapter(new BanksListAdapter(this, list));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = list.get(position);

                if (str.equals("50音順") || str.equals("主要金融機関")) {
                    return;
                }
                if (str.equals("あ行") || str.equals("か行") || str.equals("さ行") || str.equals("た行") || str.equals("な行") || str.equals("は行")
                        || str.equals("ま行") || str.equals("や行") || str.equals("ら行") || str.equals("わ行")) {

                    Intent intent = new Intent(BanksListActivity.this, AgyouListActivity.class);
                    intent.putExtra(IntentKey.GOJUON1.name(), str);
                    startActivity(intent);
//                    startActivityForResult(intent, requestCode);

                } else {

                    // intentの作成
                    Intent intent = new Intent();
                    intent.putExtra(IntentKey.BANKNAME.name(), str);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });


//        LoginButton loginButton;
//        CallbackManager callback;
//        loginButton.registerCallback(callback, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                handleFacebookAccessToken(loginResult.getAccessToken());
//            }
//
//            @Override
//            public void onCancel() {
//
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//
//            }
//        });
//    }
//
//    private void handleFacebookAccessToken(AccessToken accessToken) {
//        FirebaseAuth firebaseAuth;
//        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
//        firebaseAuth.signInWithCredential(credential).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                //error
//            }
//        }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//            @Override
//            public void onSuccess(AuthResult authResult) {
//                // success
//                String email = authResult.getUser().getEmail();
//
//            }
//        });
    }
}
