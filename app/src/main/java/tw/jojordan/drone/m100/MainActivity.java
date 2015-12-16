package tw.jojordan.drone.m100;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.*;
import android.os.Process;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import dji.sdk.api.DJIDrone;
import dji.sdk.api.DJIDroneTypeDef;
import dji.sdk.api.DJIError;
import dji.sdk.api.MainController.DJIMainControllerSystemState;
import dji.sdk.interfaces.DJIDroneTypeChangedCallback;
import dji.sdk.interfaces.DJIExecuteResultCallback;
import dji.sdk.interfaces.DJIExecuteStringResultCallback;
import dji.sdk.interfaces.DJIGeneralListener;
import dji.sdk.interfaces.DJIMcuUpdateStateCallBack;

public class MainActivity extends DemoBaseActivity implements View.OnClickListener{

    private Button myCheckBtn ;
    private Button myTurnOnBtn ;
    private Button myTurnOffBtn ;
    private Button myInfoBtn ;
    private TextView myMCIInfoTextview;


    private DJIMcuUpdateStateCallBack mMcuUpdateStateCallBack = null;

    private static final String TAG = "MainActivity" ;
    private String McStateString = "";
    private final int SHOWDIALOG = 1;
    private final int SHOWTOAST = 2;
    private static final int SHOW_ALTER_DIALOG = 0;

    private DJIDroneTypeDef.DJIDroneType mType;


    private android.os.Handler handler = new android.os.Handler(new android.os.Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case SHOWDIALOG:
                    showMessage(getString(R.string.demo_activation_message_title),(String)msg.obj);
                    break;
                case SHOWTOAST:
                    Toast.makeText(MainActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myCheckBtn      = (Button) findViewById(R.id.check_button);
        myTurnOnBtn     = (Button) findViewById(R.id.turn_on_button) ;
        myTurnOffBtn    = (Button) findViewById(R.id.turn_off_button) ;
        myInfoBtn       = (Button) findViewById(R.id.info_button) ;
        myMCIInfoTextview = (TextView) findViewById(R.id.mcinfo_textview) ;

        myCheckBtn.setOnClickListener(this);
        myTurnOnBtn.setOnClickListener(this);
        myTurnOffBtn.setOnClickListener(this);
        myInfoBtn.setOnClickListener(this);

        new Thread(){
            public void run(){
                try {
                    DJIDrone.checkPermission(getApplicationContext(), new DJIGeneralListener() {
                        @Override
                        public void onGetPermissionResult(int result) {
                            if (result == 0) {
                                // show success
                                Log.e(TAG, "onGetPermissionResult =" + result);
                                Log.e(TAG, "onGetPermissionResultDescription=" + DJIError.getCheckPermissionErrorDescription(result));
                            } else {
                                // show error
                                Log.e(TAG, "onGetPermissionResult =" + result);
                                Log.e(TAG, "onGetPermissionResultDescription=" + DJIError.getCheckPermissionErrorDescription(result));
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        DJIDrone.initAPPManager(this.getApplicationContext(), new DJIDroneTypeChangedCallback() {

            @Override
            public void onResult(DJIDroneTypeDef.DJIDroneType type) {
                mType = type;
                handler.sendEmptyMessage(SHOW_ALTER_DIALOG);
            }

        });
        DJIDrone.initWithType(this.getApplicationContext(), DJIDroneTypeDef.DJIDroneType.DJIDrone_M100);





    }
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.check_button:
                if(DJIDrone.connectToDrone()){
                    String resultStr = "connect ok!" ;
                    handler.sendMessage(handler.obtainMessage(SHOWTOAST,resultStr)) ;
                }else {
                    String resultStr = "connect fail!" ;
                    handler.sendMessage(handler.obtainMessage(SHOWTOAST,resultStr)) ;
                }


                break;
            case R.id.turn_on_button:
                DJIDrone.getDjiMC().turnOnMotor(new DJIExecuteResultCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        String resultStr = "action turn on motor:" + djiError.errorDescription;
                        handler.sendMessage(handler.obtainMessage(SHOWTOAST,resultStr)) ;
                    }
                });
                break;

            case R.id.turn_off_button:
               DJIDrone.getDjiMC().turnOffMotor(new DJIExecuteResultCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        String resultStr = "action turn off motor:" + djiError.errorDescription;
                        handler.sendMessage(handler.obtainMessage(SHOWTOAST, resultStr));
                    }
                });
                break;
            case R.id.info_button:


                break;
        }
    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub

        super.onResume();
        DJIDrone.getDjiMC().startUpdateTimer(1000);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub

        super.onPause();
        DJIDrone.getDjiMC().stopUpdateTimer();
    }
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        // The following codes are used to kill the application process.
        android.os.Process.killProcess(Process.myPid());
    }



    public void showMessage(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
