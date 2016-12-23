package com.example.user.cheeseburgeryummy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

/**
 * Created by user on 12/23/16.
 */

public class LoginActivity extends Activity {
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        Timber.tag("LiftCycles");
        Timber.d("Activity Created");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    @OnClick(R.id.facebookLoginButton) void actionFacebookLogin() {
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        if (AccessToken.getCurrentAccessToken() != null) {
            // 이미 로그인 한 경우
        } else {
            // 로그인 안한 경우
            callbackManager = CallbackManager.Factory.create();

            Set<String> set = new HashSet<String>();
            set.add("email");
            set.add("public_profile");
            set.add("user_friends");

            LoginManager.getInstance().logInWithReadPermissions(this, set);

            LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("AAAA", loginResult.getAccessToken().getToken());
                        Log.d("AAAA", loginResult.getRecentlyDeniedPermissions().toString());
                        Log.d("AAAA", loginResult.getRecentlyGrantedPermissions().toString());

                        AccessToken accessToken = loginResult.getAccessToken();

                        GraphRequest request = GraphRequest.newMeRequest(
                                accessToken,
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {
                                        // Application code

                                        try {
                                            HashMap<String, Object> resultMap = (HashMap<String, Object>) jsonToMap(object);
                                            Log.d("DDDDDDD", resultMap.get("name").toString());

                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            // intent.putExtra(String key, Serializable obj) 는 Map을 지원하지 않는다. HashMap으로 변경 후 값 전달.
                                            intent.putExtra("resultMap", resultMap);
                                            startActivity(intent);
                                            LoginActivity.this.finish();

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        Log.d("DDDDDD", response.toString());
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id, link, email, name, age_range, cover, locale, gender");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        Log.d("AAAA", "cancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d("AAAA", "error");
                    }
                }
            );

        }
    }

    @OnClick(R.id.userLoginButton) void actionUserLogin() {

    }

    public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if(json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for(int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if(value instanceof JSONArray) {
                value = toList((JSONArray) value);
            }

            else if(value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }
}
