package com.sawatruck.loader.view.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sawatruck.loader.BaseApplication;
import com.sawatruck.loader.Constant;
import com.sawatruck.loader.R;
import com.sawatruck.loader.entities.TruckBodyType;
import com.sawatruck.loader.entities.TruckClass;
import com.sawatruck.loader.entities.TruckType;
import com.sawatruck.loader.entities.User;
import com.sawatruck.loader.utils.HttpHelper;
import com.sawatruck.loader.utils.HttpResponseListener;
import com.sawatruck.loader.utils.HttpUtil;
import com.sawatruck.loader.utils.Misc;
import com.sawatruck.loader.utils.Notice;
import com.sawatruck.loader.utils.StringUtil;
import com.sawatruck.loader.utils.UserManager;
import com.sawatruck.loader.view.activity.BaseActivity;
import com.sawatruck.loader.view.activity.MainActivity;
import com.sawatruck.loader.view.adapter.PackageAdapter;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by royal on 8/19/2017.
 */


public class FragmentPostLoad2 extends BaseFragment implements View.OnClickListener {
    public static final String TAG = FragmentPostLoad2.class.getSimpleName();
    @Bind(R.id.btn_back) View btnBack;
    @Bind(R.id.btn_next) View btnNext;
    @Bind(R.id.spinner_vehicle_class) BetterSpinner spinnerVehicleClass;
    @Bind(R.id.spinner_vehicle_bodywork) BetterSpinner spinnerVehicleBodyWork;
    @Bind(R.id.spinner_vehicle_type) BetterSpinner spinnerVehicleType;
    @Bind(R.id.view_take_photo) View btnTakePhoto;
    @Bind(R.id.view_upload_photo) View btnUploadPhoto;
    private JSONArray photoJSONArray = new JSONArray();
    private static final int PICK_Camera_IMAGE = 2;
    private int SELECT_IMAGE = 1000;
    private static String photoUrl;
    private int loadTypeID = 0;
    File destination;
    private String postLoadContent;

    private TruckClass currentTruckClass;
    private TruckBodyType currentTruckBodyType;
    private TruckType currentTruckType;
    private ArrayList<TruckBodyType> truckBodyTypes = new ArrayList<>();
    private ArrayList<TruckClass> truckClasses = new ArrayList<>();
    private ArrayList<TruckType> truckTypes = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_post_loads_step_two, container, false);
        ButterKnife.bind(this,view);

        btnBack.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnTakePhoto.setOnClickListener(this);
        btnUploadPhoto.setOnClickListener(this);

//        spinnerVehicleClass.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                currentTruckClass = truckClasses.get(position);
//            }
//        });
//
//        spinnerVehicleBodyWork.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                currentTruckBodyType  = truckBodyTypes.get(position);
//            }
//        });

        spinnerVehicleType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentTruckType = truckTypes.get(position);
            }
        });

        BaseActivity baseActivity = (BaseActivity) getActivity();
        baseActivity.setAppTitle(getResources().getString(R.string.title_post_loads));


        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        super.onResume();
        initSpinners();
        getTruckTypes();
//        getAllTruckClasses();
//        getTruckBodywork();
    }

    @Override
    public void onClick(View v) {
        int id =v.getId();
        MainActivity mainActivity = (MainActivity)getActivity();
        switch (id) {
            case R.id.btn_back:
                FragmentPostLoad1 fragmentPostLoad1 = new FragmentPostLoad1();
                fragmentPostLoad1.setPostLoadContent(postLoadContent);
                mainActivity.hideFragmentWithTag(FragmentPostLoad2.TAG);

                if(loadTypeID==6 || loadTypeID ==3)
                    mainActivity.changeFragmentWithTag(fragmentPostLoad1, FragmentPostLoad1.TAG);
                else {
                    FragmentPostLoad fragmentPostLoad = new FragmentPostLoad();
                    mainActivity.changeFragmentWithTag(fragmentPostLoad, FragmentPostLoad.TAG);
                }


                break;
            case R.id.btn_next:
                nextForm();
                break;
            case R.id.view_take_photo:
                alertSelectImageType();
                break;
            case R.id.view_upload_photo:
                uploadPhoto();
                break;
        }
    }

    private void nextForm(){
        MainActivity mainActivity = (MainActivity)getActivity();


        if(currentTruckType == null) {
            Notice.show(getString(R.string.error_select_truck_type));
            return;
        }

//        if(currentTruckClass == null) {
//            Notice.show(getString(R.string.error_select_truck_class));
//            return;
//        }
//        if(currentTruckBodyType == null) {
//            Notice.show(getString(R.string.error_select_truck_body_type));
//            return;
//        }
        try {
            JSONObject postLoadObject = new JSONObject(postLoadContent);
            postLoadObject.put("LoadPhotos",photoJSONArray);
            postLoadObject.put("TruckTypeID",currentTruckType.getID());
            postLoadObject.put("TruckBodyworkTypeID","");
//            postLoadObject.put("TruckBodyworkTypeID",currentTruckBodyType.getTypeID());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        FragmentPostLoad3 fragmentPostLoad3 = new FragmentPostLoad3();
        fragmentPostLoad3.setPostLoadContent(postLoadContent.toString());


        mainActivity.hideFragmentWithTag(FragmentPostLoad2.TAG);
        fragmentPostLoad3.setLoadTypeID(this.loadTypeID);
        mainActivity.changeFragmentWithTag(fragmentPostLoad3, FragmentPostLoad3.TAG);
    }
    private void uploadPhoto() {
        final LinkedHashMap<String, Object> param = new LinkedHashMap<String, Object>();
        if(photoUrl==null) {
            Notice.show(getString(R.string.error_add_truck_photo));
            return;
        }
        else{
            try {
                File file = new File(photoUrl);
                if(file.exists())
                    param.put("img",file);
                else {
                    Notice.show(getString(R.string.error_file_not_exist));
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        btnTakePhoto.setEnabled(false);
        btnUploadPhoto.setEnabled(false);
        HttpHelper.makeMultipartPostRequest(Constant.UPLOAD_USER_PHOTO_API, param, new HttpResponseListener() {

            @Override
            public void OnSuccess(Object response) {
                btnTakePhoto.setEnabled(true);
                btnUploadPhoto.setEnabled(true);
                String strRet = (String) response;
                try {
                    JSONObject jsonObject = new JSONObject(strRet);
                    final String photoPath = jsonObject.getString("PhotoPath");
                    JSONObject pathObject = new JSONObject();
                    pathObject.put("PhotoPath", photoPath);
                    photoJSONArray.put(pathObject);

                } catch (JSONException e) {

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnTakePhoto.setEnabled(true);
                            btnUploadPhoto.setEnabled(true);
                        }
                    });

                    e.printStackTrace();
                }
            }

            @Override
            public void OnFailure(final Object error) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnTakePhoto.setEnabled(true);
                        btnUploadPhoto.setEnabled(true);
                        String strRet = (String) error;
                        Misc.showResponseMessage(strRet);
                    }
                });
            }
        });

    }

    public void initSpinners(){
        ArrayList<String> emtpylist = new ArrayList<>();
        ArrayAdapter adapter = new ArrayAdapter(
                getContext(),
                R.layout.spinner_item,
                emtpylist);
        adapter.setDropDownViewResource(R.layout.spinner_select);
        spinnerVehicleType.setAdapter(adapter);
        spinnerVehicleBodyWork.setAdapter(adapter);
        spinnerVehicleClass.setAdapter(adapter);
    }

//    private void getAllTruckClasses(){
//        HttpUtil httpUtil = new HttpUtil();
//        User user  = UserManager.with(getContext()).getCurrentUser();
//        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());
//        httpUtil.get(Constant.GET_TRUCK_CLASSES_API, new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                String paramString = new String(responseBody);
//                paramString = StringUtil.escapeString(paramString);
//
//                truckClasses = new ArrayList<>();
//
//                try {
//                    JSONArray jsonArray = new JSONArray(paramString);
//                    for(int j=0; j<jsonArray.length(); j++) {
//                        TruckClass truckClass = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), TruckClass.class);
//                        truckClasses.add(truckClass);
//                    }
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            ArrayList<String> strTruckClasses = new ArrayList<>();
//                            for(int i = 0; i <truckClasses.size(); i++){
//                                strTruckClasses.add(truckClasses.get(i).getName());
//                            }
//
//
//                            ArrayAdapter truckClassAdapter = new ArrayAdapter(
//                                    getContext(),
//                                    R.layout.spinner_item,
//                                    strTruckClasses);
//                            truckClassAdapter.setDropDownViewResource(R.layout.spinner_select);
//                            spinnerVehicleClass.setAdapter(truckClassAdapter);
//                            truckClassAdapter.notifyDataSetChanged();
//                        }
//                    });
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//            }
//        });
//    }

    public void getTruckTypes(){
        HttpUtil httpUtil = new HttpUtil();
        User user  = UserManager.with(getContext()).getCurrentUser();
        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());
        httpUtil.get(Constant.GET_TRUCK_TYPE_API, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String paramString = new String(responseBody);
                paramString = StringUtil.escapeString(paramString);

                truckTypes = new ArrayList<>();

                try {
                    JSONArray jsonArray = new JSONArray(paramString);
                    for(int j=0; j<jsonArray.length(); j++) {
                        TruckType truckBrand = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), TruckType.class);
                        truckTypes.add(truckBrand);
                    }
                    PackageAdapter.truckTypes = truckTypes;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ArrayList<String> strTruckTypes = new ArrayList<>();
                            for(int i = 0; i <truckTypes.size(); i++){
                                strTruckTypes.add(truckTypes.get(i).getName());
                            }

                            ArrayAdapter truckTypeAdapter = new ArrayAdapter(
                                    getContext(),
                                    R.layout.spinner_item,
                                    strTruckTypes);
                            truckTypeAdapter.setDropDownViewResource(R.layout.spinner_select);
                            spinnerVehicleType.setAdapter(truckTypeAdapter);
                            truckTypeAdapter.notifyDataSetChanged();
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }


//    public void getTruckBodywork(){
//        HttpUtil httpUtil = new HttpUtil();
//        User user  = UserManager.with(getContext()).getCurrentUser();
//        httpUtil.getClient().addHeader(Constant.PARAM_AUTHORIZATION, user.getToken());
//        httpUtil.get(Constant.GET_TRUCK_TYPE_API, new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                String paramString = new String(responseBody);
//                paramString = StringUtil.escapeString(paramString);
//
//                truckBodyTypes = new ArrayList<>();
//
//                try {
//                    JSONArray jsonArray = new JSONArray(paramString);
//                    for(int j=0; j<jsonArray.length(); j++) {
//                        TruckBodyType truckBodyType = BaseApplication.getGson().fromJson(jsonArray.get(j).toString(), TruckBodyType.class);
//                        truckBodyTypes.add(truckBodyType);
//                    }
//
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            ArrayList<String> strBodyTypes = new ArrayList<>();
//                            for(int i = 0; i <truckBodyTypes.size(); i++){
//                                strBodyTypes.add(truckBodyTypes.get(i).getName());
//                            }
//                            ArrayAdapter truckBodyTypeAdapter = new ArrayAdapter(
//                                    getContext(),
//                                    R.layout.spinner_item,
//                                    strBodyTypes);
//                            truckBodyTypeAdapter.setDropDownViewResource(R.layout.spinner_select);
//                            spinnerVehicleBodyWork.setAdapter(truckBodyTypeAdapter);
//                            truckBodyTypeAdapter.notifyDataSetChanged();
//                        }
//                    });
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//            }
//        });
//    }


    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),SELECT_IMAGE);
    }

    public void alertSelectImageType(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final View viewSelLang = getActivity().getLayoutInflater().inflate(R.layout.dialog_get_image,null);
        final RadioButton radioCamera = (RadioButton)viewSelLang.findViewById(R.id.radio_camera);

        final Button btnOk = (Button)viewSelLang.findViewById(R.id.btn_ok);
        final Button btnCancel = (Button)viewSelLang.findViewById(R.id.btn_cancel);

        builder.setView(viewSelLang);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioCamera.isChecked())
                    openCamera();
                else
                    openGallery();
                alertDialog.dismiss();
            }
        });
    }

    public void openCamera(){
        Date nowTime = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH-mm-ss");
        String strTime = timeFormatter.format(nowTime);
        String strDate = dateFormatter.format(nowTime);
        String name= strDate+"-"+strTime;
        destination = new File(Environment.getExternalStorageDirectory(), name + ".jpg");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        Uri destinationURI = FileProvider.getUriForFile(
                getContext(),
                BaseApplication.getInstance().getApplicationContext()
                        .getPackageName() + ".provider", destination);


        intent.putExtra(MediaStore.EXTRA_OUTPUT, destinationURI );
        startActivityForResult(intent, PICK_Camera_IMAGE);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            if(requestCode == SELECT_IMAGE)
                if (data != null) {
                    photoUrl = Misc.getRealPathFromURI(data.getData());

                }
            if(requestCode == PICK_Camera_IMAGE){
                photoUrl = destination.getAbsolutePath();
            }
        }
    }
    public String getPostLoadContent() {
        return postLoadContent;
    }

    public void setPostLoadContent(String postLoadContent) {
        this.postLoadContent = postLoadContent;
    }

    public int getLoadTypeID() {
        return loadTypeID;
    }

    public void setLoadTypeID(int loadTypeID) {
        this.loadTypeID = loadTypeID;
    }
}
