package tw.jojordan.drone.m100;

import dji.midware.data.manager.P3.ServiceManager;
import android.app.Activity;

public class DemoBaseActivity extends Activity{
    
    @Override
    protected void onResume() {
        super.onResume();
        ServiceManager.getInstance().pauseService(false);
    }
    
    @Override
    protected void onPause() {
        super.onPause();

        ServiceManager.getInstance().pauseService(true);
    }
    
}
