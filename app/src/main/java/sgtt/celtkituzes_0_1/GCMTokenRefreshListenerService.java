package sgtt.celtkituzes_0_1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by Storg on 2016.08.15..
 */
public class GCMTokenRefreshListenerService extends InstanceIDListenerService{
    //Amikor a token refreshel,kezdődik egy uj service az új token végett
    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this,GCMRegistrationIntentService.class);
        startService(intent);
    }
}
