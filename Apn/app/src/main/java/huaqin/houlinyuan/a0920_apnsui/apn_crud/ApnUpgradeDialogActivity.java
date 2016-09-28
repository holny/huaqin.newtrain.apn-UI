package huaqin.houlinyuan.a0920_apnsui.apn_crud;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import huaqin.houlinyuan.a0920_apnsui.R;
import huaqin.houlinyuan.a0920_apnsui.database.Apns;

public class ApnUpgradeDialogActivity extends AppCompatActivity {
    private ContentResolver resolver;
    private final int RESULT_CODE = 4321;
    private final int ERROR_CODE = 1001;
    private final static String TAG = "hlyApnUpgradeDActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apnupgrade_dialog);
        TextView attrName_textView = (TextView)findViewById(R.id.apnupgrade_attrname);
        final EditText attrValue_editText = (EditText)findViewById(R.id.apnupgrade_newattrvalue);
        Button confrim_button = (Button)findViewById(R.id.apnupgrade_confirm);
        Button cancel_button = (Button)findViewById(R.id.apnupgrade_cancel);
        resolver = getContentResolver();
        Bundle bundle = getIntent().getExtras();
        final String attrName = bundle.getString("attrName");
        final String attrValue = bundle.getString("attrValue");
        final int _id = bundle.getInt("_id");
        Log.d(TAG, "------attrName=" + attrName+"--attrValue=" + attrValue);
        if ((attrName != null) && (attrValue!=null))
        {
            attrName_textView.setText(attrName);
            if(!"".equals(attrValue.trim()))
            {
                attrValue_editText.setText(attrValue);
            }
        }

        confrim_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((attrValue_editText.getText() !=null) && (!"".equals(attrValue_editText.getText().toString().trim())))
                {
                    Uri uri = ContentUris.withAppendedId(Apns.Apn.APN_CONTENT_URI,_id);
                    ContentValues contentValuesa = new ContentValues();
                    contentValuesa.put(attrName, "" + attrValue_editText.getText().toString().trim());
                    resolver.update(uri, contentValuesa, null, null);
                    Toast toast = Toast.makeText(getApplicationContext(),"更新成功,attrName=" + attrName + "--attrValue= " + attrValue_editText.getText().toString().trim(),Toast.LENGTH_SHORT);
                    toast.show();
                    setResult(RESULT_CODE);
                    finish();
                }
                else
                {
                    Uri uri = ContentUris.withAppendedId(Apns.Apn.APN_CONTENT_URI,_id);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(attrName, "");
                    resolver.update(uri, contentValues, null, null);
                    Toast toast = Toast.makeText(getApplicationContext(),"更新成功,为空,attrName=" + attrName + "--attrValue= " + attrValue_editText.getText().toString().trim(),Toast.LENGTH_SHORT);
                    toast.show();
                    setResult(RESULT_CODE);
                    finish();
                }
            }
        });
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
}
