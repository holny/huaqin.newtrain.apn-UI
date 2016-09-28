package huaqin.houlinyuan.a0920_apnsui.sim;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import huaqin.houlinyuan.a0920_apnsui.R;
import huaqin.houlinyuan.a0920_apnsui.ui.fragments.NotesListFragment;

/**
 * Created by ubuntu on 16-9-22.
 */
public class SimBFragment  extends NotesListFragment {
    private static final String TAG = "hlySimBFragment";
    private static final String TAGA = "FFAASimBFragment";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private ContentResolver resolver;

    private final int PHONEID = 1;
    private final int SOLTID = 1;
    private int SUBID;
    public static SimBFragment newInstance() {
        return new SimBFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getActivity().getSharedPreferences("initdatabasecount",getActivity().MODE_PRIVATE);
        editor = preferences.edit();
        Log.d(TAG , "-----onCreate------");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_simb, container, false);
        Log.d(TAG , "-----onCreateView------");
        return initView(view);
    }
    private View initView(View view)
    {

        try {
            //利用反射获取mtk代码里的getSubId方法。获取subId
            Class<?> MtkSubscriptionManager = Class.forName("android.telephony.SubscriptionManager");
            Constructor MtkSubscriptionManagerConstructor = MtkSubscriptionManager.getConstructor(Context.class);
            Object mtkSubscriptionManagerObject = MtkSubscriptionManagerConstructor.newInstance(getActivity());
            Method mtkSubscriptionManagergetSubId = MtkSubscriptionManager.getMethod("getSubId", int.class);
            int[] result = (int[])mtkSubscriptionManagergetSubId.invoke(mtkSubscriptionManagerObject,SOLTID);
            SUBID = result[0];
            //根据subId获取subscriptionInfo
            SubscriptionManager subscriptionManager = SubscriptionManager.from(getContext());
            SubscriptionInfo subscriptionInfo = subscriptionManager.getActiveSubscriptionInfo(SUBID);
            if (subscriptionInfo != null) {

                String carrierName = subscriptionInfo.getCarrierName().toString();
                String countryIso = subscriptionInfo.getCountryIso();
                int dataRoaming = subscriptionInfo.getDataRoaming();
                String displayName = subscriptionInfo.getDisplayName().toString();
                String iccId = subscriptionInfo.getIccId();
                int iconTint = subscriptionInfo.getIconTint();
                String number = subscriptionInfo.getNumber();
                int simSlotIndex = subscriptionInfo.getSimSlotIndex();
                int subscriptionId = subscriptionInfo.getSubscriptionId();
                String mnc = String.valueOf(subscriptionInfo.getMnc());
                String mcc = String.valueOf(subscriptionInfo.getMcc());
                if (mnc.trim().length() == 1)
                {

                    mnc = "0" + mnc.trim();
                }

                TextView carrierName_textview = (TextView)view.findViewById(R.id.fragment2_sim_carrierName);
                TextView countryIso_textview = (TextView)view.findViewById(R.id.fragment2_sim_countryIso);
                TextView dataRoaming_textview = (TextView)view.findViewById(R.id.fragment2_sim_dataRoaming);
                TextView displayName_textview = (TextView)view.findViewById(R.id.fragment2_sim_displayName);
                TextView iccId_textview = (TextView)view.findViewById(R.id.fragment2_sim_iccId);
                TextView iconTint_textview = (TextView)view.findViewById(R.id.fragment2_sim_iconTint);
                TextView number_textview = (TextView)view.findViewById(R.id.fragment2_sim_number);
                TextView simSlotIndex_textview = (TextView)view.findViewById(R.id.fragment2_sim_simSlotIndex);
                TextView subscriptionId_textview = (TextView)view.findViewById(R.id.fragment2_sim_subscriptionId);
                TextView mnc_textview = (TextView)view.findViewById(R.id.fragment2_sim_mnc);
                TextView mcc_textview = (TextView)view.findViewById(R.id.fragment2_sim_mcc);

                carrierName_textview.setText(carrierName);
                countryIso_textview.setText(countryIso);
                switch (dataRoaming)
                {
                    case SubscriptionManager.DATA_ROAMING_ENABLE:
                        dataRoaming_textview.setText("enable");
                        break;
                    case SubscriptionManager.DATA_ROAMING_DISABLE:
                        dataRoaming_textview.setText("disable");
                        break;
                    default:
                        dataRoaming_textview.setText("other");
                }
                displayName_textview.setText(displayName);
                iccId_textview.setText(iccId);
                iconTint_textview.setText("" + iconTint);
                number_textview.setText(number);
                simSlotIndex_textview.setText("" + simSlotIndex);
                subscriptionId_textview.setText("" + subscriptionId);
                mnc_textview.setText(mnc);
                mcc_textview.setText(mcc);



                //TelephonyManager
                Class<?> MtkTelephonyManager = Class.forName("android.telephony.TelephonyManager");
                Constructor MtkTelephonyManagerConstructor = MtkTelephonyManager.getConstructor(Context.class);
                Object MtkTelephonyManagerObject = MtkTelephonyManagerConstructor.newInstance(getActivity());

                Method mtkTelephonyManagergetDeviceId = MtkTelephonyManager.getMethod("getDeviceId", int.class);
                String deviceId = (String) mtkTelephonyManagergetDeviceId.invoke(MtkTelephonyManagerObject, SOLTID);

                Method mtkTelephonyManagergetDataActivity = MtkTelephonyManager.getMethod("getDataActivity", int.class);
                int dataActivity = (int) mtkTelephonyManagergetDataActivity.invoke(MtkTelephonyManagerObject, SUBID);

                Method mtkTelephonyManagergetDataState = MtkTelephonyManager.getMethod("getDataState", int.class);
                int dataState = (int) mtkTelephonyManagergetDataState.invoke(MtkTelephonyManagerObject, SUBID);

                Method mtkTelephonyManagergetNetworkOperatorForSubscription = MtkTelephonyManager.getMethod("getNetworkOperatorForSubscription", int.class);
                String networkOperatorForSubscription = (String) mtkTelephonyManagergetNetworkOperatorForSubscription.invoke(MtkTelephonyManagerObject, SUBID);

                Method mtkTelephonyManagergetNetworkOperatorName = MtkTelephonyManager.getMethod("getNetworkOperatorName", int.class);
                String networkOperatorName = (String) mtkTelephonyManagergetNetworkOperatorName.invoke(MtkTelephonyManagerObject, SUBID);

                Method mtkTelephonyManagergetSimOperator = MtkTelephonyManager.getMethod("getSimOperator", int.class);
                String simOperator = (String) mtkTelephonyManagergetSimOperator.invoke(MtkTelephonyManagerObject, SUBID);

                Method mtkTelephonyManagergetSimOperatorNameForPhone = MtkTelephonyManager.getMethod("getSimOperatorNameForPhone", int.class);
                String simOperatorNameForPhone = (String) mtkTelephonyManagergetSimOperatorNameForPhone.invoke(MtkTelephonyManagerObject, PHONEID);

                Method mtkTelephonyManagergetSimSerialNumber = MtkTelephonyManager.getMethod("getSimSerialNumber", int.class);
                String simSerialNumber = (String) mtkTelephonyManagergetSimSerialNumber.invoke(MtkTelephonyManagerObject, SUBID);

                Method mtkTelephonyManagergetSimState = MtkTelephonyManager.getMethod("getSimState", int.class);
                int simState = (int) mtkTelephonyManagergetSimState.invoke(MtkTelephonyManagerObject, SUBID);

                Method mtkTelephonyManagergetSubscriberId = MtkTelephonyManager.getMethod("getSubscriberId", int.class);
                String subscriberId = (String) mtkTelephonyManagergetSubscriberId.invoke(MtkTelephonyManagerObject, SUBID);

                Method mtkTelephonyManagerisNetworkRoaming = MtkTelephonyManager.getMethod("isNetworkRoaming", int.class);
                boolean isNetworkRoaming = (boolean) mtkTelephonyManagerisNetworkRoaming.invoke(MtkTelephonyManagerObject, SUBID);

                Method mtkTelephonyManagerhasIccCard = MtkTelephonyManager.getMethod("hasIccCard", int.class);
                boolean hasIccCard = (boolean) mtkTelephonyManagerhasIccCard.invoke(MtkTelephonyManagerObject, SOLTID);

                TextView deviceId_textview = (TextView)view.findViewById(R.id.fragment2_sim_deviceId);
                TextView dataActivity_textview = (TextView)view.findViewById(R.id.fragment2_sim_dataActivity);
                TextView dataState_textview = (TextView)view.findViewById(R.id.fragment2_sim_dataState);
                TextView networkOperatorForSubscription_textview = (TextView)view.findViewById(R.id.fragment2_sim_networkOperatorForSubscription);
                TextView networkOperatorName_textview = (TextView)view.findViewById(R.id.fragment2_sim_networkOperatorName);
                TextView simOperator_textview = (TextView)view.findViewById(R.id.fragment2_sim_simOperator);
                TextView simOperatorNameForPhone_textview = (TextView)view.findViewById(R.id.fragment2_sim_simOperatorNameForPhone);
                TextView simSerialNumber_textview = (TextView)view.findViewById(R.id.fragment2_sim_simSerialNumber);
                TextView simState_textview = (TextView)view.findViewById(R.id.fragment2_sim_simState);
                TextView subscriberId_textview = (TextView)view.findViewById(R.id.fragment2_sim_subscriberId);
                TextView isNetworkRoaming_textview = (TextView)view.findViewById(R.id.fragment2_sim_isNetworkRoaming);
                TextView hasIccCard_textview = (TextView)view.findViewById(R.id.fragment2_sim_hasIccCard);

                deviceId_textview.setText(deviceId);
                dataActivity_textview.setText("" + dataActivity);
                dataState_textview.setText("" + dataState);
                networkOperatorForSubscription_textview.setText(networkOperatorForSubscription);
                networkOperatorName_textview.setText(networkOperatorName);
                simOperator_textview.setText(simOperator);
                simOperatorNameForPhone_textview.setText(simOperatorNameForPhone);
                if(simOperatorNameForPhone != null)
                {
                    simOperatorNameForPhone_textview.setText(simOperatorNameForPhone);
                }
                else {
                    simOperatorNameForPhone_textview.setText("未知");
                }
                simSerialNumber_textview.setText(simSerialNumber);
                simState_textview.setText("" + simState);
                subscriberId_textview.setText(subscriberId);
                isNetworkRoaming_textview.setText("" + isNetworkRoaming);
                hasIccCard_textview.setText("" + hasIccCard);
            }


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return view;
    }
    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_simb;
    }

    @Override
    protected int getNumColumns() {
        return 0;
    }
}
